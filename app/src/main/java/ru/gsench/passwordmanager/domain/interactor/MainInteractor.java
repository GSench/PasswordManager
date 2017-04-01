package ru.gsench.passwordmanager.domain.interactor;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import ru.gsench.passwordmanager.domain.account_system.Account;
import ru.gsench.passwordmanager.domain.account_system.AccountSystem;
import ru.gsench.passwordmanager.domain.SystemInterface;
import ru.gsench.passwordmanager.presentation.presenter.CoordinatorPresenter;
import ru.gsench.passwordmanager.domain.utils.function;
import ru.gsench.passwordmanager.presentation.presenter.KeyInputPresenter;
import ru.gsench.passwordmanager.presentation.presenter.SelectBasePresenter;

/**
 * Created by grish on 26.02.2017.
 */

public class MainInteractor implements AccountListUseCase, EditAccountUseCase, KeyInputUseCase, NewKeyUseCase, NewPINUseCase, PINInputUseCase, SelectBaseUseCase {

    private static final String ACCOUNT_BASE = "base_path";
    private static final String KEY = "key";
    private static final String PIN = "pin";
    private static final String PIN_TRIES = "pin_tries";
    private static final String LAST_PIN_TRY = "last_pin_try";
    private static final long PIN_BLOCK = 30*1000;
    private static final int PIN_LOCK_TRIES = 3;

    private CoordinatorPresenter coordinator;
    public SystemInterface system;
    private AccountSystem accountSystem;

    private boolean newBaseSelected = false;
    private boolean newKeyInput = false;

    public MainInteractor(CoordinatorPresenter coordinator, SystemInterface system){
        this.coordinator = coordinator;
        this.system = system;
    }

    public void selectBase(){
        String basePath = system.getSavedString(ACCOUNT_BASE, null);
        if(basePath==null){
            coordinator.selectBaseView();
            return;
        }
        onBaseSelected(basePath);
    }

    @Override
    public void onKeyInput(final String key, final KeyInputPresenter presenter){
        system.doOnBackground(new function() {
            @Override
            public void run(String... params) {
                try {
                    accountSystem.initSystem(key);
                } catch (SAXException | IOException e) {
                    system.doOnForeground(new function() {
                        @Override
                        public void run(String... params) {
                            presenter.unableToParseBase();
                            resetBase();
                        }
                    });
                    return;
                } catch (GeneralSecurityException e) {
                    system.doOnForeground(new function() {
                        @Override
                        public void run(String... params) {
                            presenter.onIncorrectKeyInput();
                        }
                    });
                    return;
                } catch (ParserConfigurationException e) {
                    system.doOnForeground(new function() {
                        @Override
                        public void run(String... params) {
                            presenter.unexpectedException();
                        }
                    });
                    return;
                }
                system.doOnForeground(new function() {
                    @Override
                    public void run(String... params) {
                        presenter.onCorrectKeyInput();
                        afterCorrectKeyInput();
                    }
                });
            }
        });
    }

    private void inputCorrectKey(final String key, final function onException){
        system.doOnBackground(new function() {
            @Override
            public void run(String... params) {
                try {
                    accountSystem.initSystem(key);
                } catch (Exception e) {
                    system.doOnForeground(new function() {
                        @Override
                        public void run(String... params) {
                            onException.run();
                        }
                    });
                    return;
                }
                system.doOnForeground(new function() {
                    @Override
                    public void run(String... params) {
                        afterCorrectKeyInput();
                    }
                });
            }
        });
    }

    private void afterCorrectKeyInput(){
        coordinator.closeCurrentView();
        coordinator.openAccountList();
        if(newKeyInput) saveAccountBase(null);
        if(newBaseSelected) coordinator.newPINView();
    }

    @Override
    public void afterNewKeyInput(String newKey){
        newKeyInput = true;
        inputCorrectKey(newKey, null);
    }

    @Override
    public void onNewPIN(String pin){
        system.saveString(PIN, pin);
        system.saveString(KEY, accountSystem.getKey());
    }

    @Override
    public void onPINInput(String pin) {
        long block = isPINBlocked();
        if(block>0) return;

        if(!pin.equals(system.getSavedString(PIN, null))){
            system.saveInt(PIN_TRIES, system.getSavedInt(PIN_TRIES, 0)+1);
            system.saveLong(LAST_PIN_TRY, System.currentTimeMillis());
        } else {
            system.saveInt(PIN_TRIES, 0);
            String key = system.getSavedString(KEY, null);
            inputCorrectKey(key, null);
        }
    }

    //TODO Make secure
    @Override
    public long isPINBlocked(){
        int tries = system.getSavedInt(PIN_TRIES, 0);
        long current = System.currentTimeMillis();
        long lastTry = system.getSavedLong(LAST_PIN_TRY, current-PIN_BLOCK);
        if (tries>=PIN_LOCK_TRIES && current-lastTry <= PIN_BLOCK) return PIN_BLOCK - current + lastTry;
        else return -1;
    }

    @Override
    public void countDownTime(long time, long interval, function onTick, function onFinish) {
        system.countDown(time, interval, onTick, onFinish);
    }

    @Override
    public ArrayList<Account> getAccountsToView() {
        return accountSystem.getAccounts();
    }

    @Override
    public void onResetPIN(){
        resetPIN();
        coordinator.keyInputView();
    }

    private void resetPIN(){
        system.removeSaved(KEY);
        system.removeSaved(PIN);
        system.removeSaved(PIN_TRIES);
        system.removeSaved(LAST_PIN_TRY);
    }

    @Override
    public void onNewBaseSelected(String path, SelectBasePresenter presenter){
        try {
            system.deleteFile(path);
            system.createFileIfNotExist(path);
        } catch (IOException e) {
            presenter.unableToEditBaseFile();
            resetBase();
            return;
        }
        onExistingBaseSelected(path);
    }

    @Override
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
            //coordinator.unableToReadBaseFile();
            resetBase();
            return;
        }
        accountSystem = new AccountSystem(base);
        if(base.length==0){
            coordinator.newKeyView();
            return;
        }
        if(system.getSavedString(PIN, null)!=null)
            coordinator.openPINView();
        else
            coordinator.keyInputView();
    }

    private void resetBase(){
        system.removeSaved(ACCOUNT_BASE);
        resetPIN();
        selectBase();
    }

    @Override
    public void onEditAccount(Account account){
        coordinator.editAccountView(account);
    }

    @Override
    public void onAddAccount(){
        coordinator.editAccountView(new Account(accountSystem.getAccountsCount(), "", "", ""));
    }

    @Override
    public void editAccount(Account account){
        accountSystem.editAccount(account);
        saveAccountBase(null);
        coordinator.openAccountList();
    }

    @Override
    public void removeAccount(Account account){
        accountSystem.deleteAccount(account);
        saveAccountBase(null);
        coordinator.openAccountList();
    }

    private final static Object synchronizationStub = new Object();
    private void saveAccountBase(final function onException){
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
                                //coordinator.unexpectedException();
                            }
                        });
                    } catch (IOException e) {
                        system.doOnForeground(new function() {
                            @Override
                            public void run(String... params) {
                                //coordinator.unableToEditBaseFile();
                                resetBase();
                            }
                        });
                    }
                }
            }
        });
    }

}
