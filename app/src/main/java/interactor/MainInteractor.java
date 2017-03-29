package interactor;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.xml.parsers.ParserConfigurationException;

import account_system.Account;
import account_system.AccountSystem;
import ru.gsench.passwordmanager.view.MainView;
import ru.gsench.passwordmanager.SystemInterface;
import utils.function;

/**
 * Created by grish on 26.02.2017.
 */

public class MainInteractor {

    private static final String ACCOUNT_BASE = "base_path";
    private static final String KEY = "key";
    private static final String PIN = "pin";
    private static final String PIN_TRIES = "pin_tries";
    private static final String LAST_PIN_TRY = "last_pin_try";
    private static final long PIN_BLOCK = 30*1000;
    private static final int PIN_LOCK_TRIES = 3;

    private MainView view;
    public SystemInterface system;
    private AccountSystem accountSystem;

    private boolean newBaseSelected = false;
    private boolean newKeyInput = false;

    public MainInteractor(MainView mainView, SystemInterface system){
        view=mainView;
        this.system=system;
    }

    public void onStart(){
        String basePath = system.getSavedString(ACCOUNT_BASE, null);
        if(basePath==null){
            view.selectBaseWindow();
            return;
        }
        onBaseSelected(basePath);
    }

    public void onKeyInput(final String key, final function onCorrectKeyInput, final function onIncorrectKeyInput){
        system.doOnBackground(new function() {
            @Override
            public void run(String... params) {
                try {
                    accountSystem.initSystem(key);
                } catch (SAXException | IOException e) {
                    system.doOnForeground(new function() {
                        @Override
                        public void run(String... params) {
                            view.unableToParseBase();
                        }
                    });
                    return;
                } catch (GeneralSecurityException e) {
                    system.doOnForeground(new function() {
                        @Override
                        public void run(String... params) {
                            onIncorrectKeyInput.run();
                        }
                    });
                    return;
                } catch (ParserConfigurationException e) {
                    system.doOnForeground(new function() {
                        @Override
                        public void run(String... params) {
                            view.unexpectedException();
                        }
                    });
                    return;
                }
                system.doOnForeground(new function() {
                    @Override
                    public void run(String... params) {
                        onCorrectKeyInput.run();
                        openAccountBase();
                        if(newKeyInput) saveAccountBase();
                        if(newBaseSelected) view.newPINDialog();
                    }
                });
            }
        });
    }

    private function closeViewFunc = new function() {
        @Override
        public void run(String... params) {
            view.closeCurrentView();
        }
    };

    public void afterNewKeyInput(String newKey){
        newKeyInput = true;
        onKeyInput(newKey, closeViewFunc, null);
    }

    public void onNewPIN(String pin){
        system.saveString(PIN, pin);
        system.saveString(KEY, accountSystem.getKey());
    }

    public void onPINInput(String pin) throws BlockPINException {
        long block = isPINBlocked();
        if(block>0) throw new BlockPINException(block);

        if(!pin.equals(system.getSavedString(PIN, null))){
            system.saveInt(PIN_TRIES, system.getSavedInt(PIN_TRIES, 0)+1);
            system.saveLong(LAST_PIN_TRY, System.currentTimeMillis());
        } else {
            system.saveInt(PIN_TRIES, 0);
            String key = system.getSavedString(KEY, null);
            onKeyInput(key, closeViewFunc, null);
        }
    }

    //TODO Make secure
    public long isPINBlocked(){
        int tries = system.getSavedInt(PIN_TRIES, 0);
        long current = System.currentTimeMillis();
        long lastTry = system.getSavedLong(LAST_PIN_TRY, current-PIN_BLOCK);
        if (tries>=PIN_LOCK_TRIES && current-lastTry <= PIN_BLOCK) return PIN_BLOCK - current + lastTry;
        else return -1;
    }

    public class BlockPINException extends Exception {
        public long getTimer() {
            return timer;
        }
        private long timer = 0;
        public BlockPINException(long timer){
            this.timer=timer;
        }
    }

    public void onResetPINBtn(){
        resetPIN();
        view.keyInputWindow();
    }

    private void resetPIN(){
        system.removeSaved(KEY);
        system.removeSaved(PIN);
        system.removeSaved(PIN_TRIES);
        system.removeSaved(LAST_PIN_TRY);
    }

    public void onNewBaseSelected(String path){
        try {
            system.deleteFile(path);
            system.createFileIfNotExist(path);
        } catch (IOException e) {
            view.unableToEditBaseFile();
            return;
        }
        onExistingBaseSelected(path);
    }

    public void onExistingBaseSelected(String path){
        newBaseSelected = true;
        onBaseSelected(path);
        system.saveString(ACCOUNT_BASE, path);
    }

    private void onBaseSelected(String path){
        byte[] base;
        try {
            base = system.readFileFromPath(path);
        } catch (IOException e) {
            view.unableToReadBaseFile();
            return;
        }
        accountSystem = new AccountSystem(base);
        if(base.length==0){
            view.newKeyWindow();
            return;
        }
        if(system.getSavedString(PIN, null)!=null)
            view.openPINWindow();
        else
            view.keyInputWindow();
    }

    public void resetBase(){
        system.removeSaved(ACCOUNT_BASE);
        resetPIN();
        onStart();
    }

    private void openAccountBase(){
        view.viewAccounts(accountSystem);
    }

    public void addNewAccount(Account account){
        accountSystem.addAccount(account);
        saveAccountBase();
        openAccountBase();
    }

    public void editAccount(Account account){
        saveAccountBase();
        openAccountBase();
    }

    public void removeAccount(Account account){
        accountSystem.deleteAccount(account);
        saveAccountBase();
        openAccountBase();
    }

    private final static Object synchronizationStub = new Object();
    private void saveAccountBase(){
        system.doOnBackground(new function() {
            @Override
            public void run(String... params) {
                synchronized (synchronizationStub){
                    try {
                        system.writeFileToPath(accountSystem.encryptSystem(), system.getSavedString(ACCOUNT_BASE, null));
                    } catch (GeneralSecurityException e) {
                        system.doOnForeground(new function() {
                            @Override
                            public void run(String... params) {
                                view.unexpectedException();
                            }
                        });
                    } catch (IOException e) {
                        system.doOnForeground(new function() {
                            @Override
                            public void run(String... params) {
                                view.unableToEditBaseFile();
                            }
                        });
                    }
                }
            }
        });
    }

}
