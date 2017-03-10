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

    private MainView view;
    private SystemInterface system;
    private AccountSystem accountSystem;

    public MainPresenter(MainView mainView, SystemInterface system){
        view=mainView;
        this.system=system;
    }

    public void onStart(){
        String basePath = system.getSavedString(ACCOUNT_BASE);
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

    public void onCorrectKeyPhraseInput(){
        saveAccountBase();
        openAccountBase();
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
        accountSystem = new AccountSystem(null);
        system.saveString(ACCOUNT_BASE, path);
        view.newKeyWindow();
    }

    //TODO Exception handling
    public void onBaseSelected(String path){
        try {
            system.createFileIfNotExist(path);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        byte[] base;
        try {
            base = system.readFileFromPath(path);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        accountSystem = new AccountSystem(base);
        system.saveString(ACCOUNT_BASE, path);
        if(base.length==0) view.newKeyWindow();
        else view.keyInputWindow();
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
            system.writeFileToPath(accountSystem.encryptSystem(), system.getSavedString(ACCOUNT_BASE));
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
