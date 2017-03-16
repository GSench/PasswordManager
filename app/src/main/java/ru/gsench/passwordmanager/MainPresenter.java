package ru.gsench.passwordmanager;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.xml.parsers.ParserConfigurationException;

import account_system.Account;
import account_system.AccountSystem;

/**
 * Created by grish on 26.02.2017.
 */

public class MainPresenter {

    private static final String ACCOUNT_BASE = "base_path";
    private static final String KEY = "key";
    private static final String PIN = "pin";
    private static final String PIN_TRIES = "pin_tries";
    private static final String LAST_PIN_TRY = "last_pin_try";
    private static final long PIN_BLOCK = 30*1000;
    private static final int PIN_LOCK_TRIES = 3;

    private MainView view;
    private SystemInterface system;
    private AccountSystem accountSystem;

    private boolean newBaseSelected = false;

    public MainPresenter(MainView mainView, SystemInterface system){
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

    //TODO Exception handling
    //TODO To separate thread
    public boolean isKeyPhraseCorrect(String text){
        try {
            accountSystem.decryptSystem(text);
        } catch (SAXException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            return false;
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void afterCorrectKeyInput(){
        openAccountBase();
        if(newBaseSelected) view.newPINDialog();
    }

    public void afterNewKeyInput(String newKey){
        isKeyPhraseCorrect(newKey);
        saveAccountBase();
        afterCorrectKeyInput();
    }

    public void onNewPIN(String pin){
        system.saveString(PIN, pin);
        system.saveString(KEY, accountSystem.getKey());
    }

    public boolean isPINCorrect(String pin) throws BlockPINException {
        long block = isPINBlocked();
        if(block>0) throw new BlockPINException(block);

        boolean correct = pin.equals(system.getSavedString(PIN, null));
        if(!correct){
            system.saveInt(PIN_TRIES, system.getSavedInt(PIN_TRIES, 0)+1);
            system.saveLong(LAST_PIN_TRY, System.currentTimeMillis());
        } else {
            system.saveInt(PIN_TRIES, 0);
        }
        return correct;
    }

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

    public void afterCorrectPINInput(){
        String key = system.getSavedString(KEY, null);
        isKeyPhraseCorrect(key);
        afterCorrectKeyInput();
    }

    public void onResetPIN(){
        system.removeSaved(KEY);
        system.removeSaved(PIN);
        system.removeSaved(PIN_TRIES);
        system.removeSaved(LAST_PIN_TRY);
        view.keyInputWindow();
    }

    //TODO Exception handling
    public void onNewBaseSelected(String path){
        try {
            system.deleteFile(path);
            system.createFileIfNotExist(path);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        onExistingBaseSelected(path);
    }

    public void onExistingBaseSelected(String path){
        newBaseSelected = true;
        onBaseSelected(path);
        system.saveString(ACCOUNT_BASE, path);
    }

    //TODO Exception handling
    private void onBaseSelected(String path){
        byte[] base;
        try {
            base = system.readFileFromPath(path);
        } catch (IOException e) {
            e.printStackTrace();
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

    //TODO Exception handling
    //TODO To separate thread
    private void saveAccountBase(){
        try {
            system.writeFileToPath(accountSystem.encryptSystem(), system.getSavedString(ACCOUNT_BASE, null));
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
