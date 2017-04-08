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

import static ru.gsench.passwordmanager.domain.interactor.PINInteractor.PIN;

/**
 * Created by grish on 26.02.2017.
 */

public class MainInteractor implements AccountListUseCase, EditAccountUseCase, KeyInputUseCase, NewKeyUseCase, NewPINUseCase, PINInputUseCase, SelectBaseUseCase {

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
        String basePath = AccountBaseInteractor.getAccountBasePath(system);
        if(basePath==null){
            coordinator.selectBaseView();
            return;
        }
        onBaseSelected();
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
        if(newKeyInput) saveAccountBase();
        if(newBaseSelected) coordinator.newPINView();
    }

    @Override
    public void afterNewKeyInput(String newKey){
        newKeyInput = true;
        inputCorrectKey(newKey, null);
    }

    @Override
    public void onNewPIN(String pin){
        PINInteractor.onNewPIN(system, pin, accountSystem.getKey());
    }

    @Override
    public void onPINInput(final String pin) {
        PINInteractor.onPINInput(system, pin, new function(){
            @Override
            public void run(String... params) {
                inputCorrectKey(params[0], null);
            }
        });
    }

    @Override
    public long isPINBlocked(){
        return PINInteractor.isPINBlocked(system);
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
        PINInteractor.resetPIN(system);
        coordinator.keyInputView();
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
        AccountBaseInteractor.selectBase(path, system);
        onBaseSelected();
    }

    private void onBaseSelected(){
        try {
            accountSystem = AccountBaseInteractor.getAccountSystem(system);
        } catch (IOException e) {
            coordinator.unableToReadBaseFile();
            resetBase();
            return;
        } catch (AccountBaseInteractor.EmptyBaseException e) {
            coordinator.newKeyView();
            return;
        }
        if(system.getSavedString(PIN, null)!=null)
            coordinator.openPINView();
        else
            coordinator.keyInputView();
    }

    private void resetBase(){
        AccountBaseInteractor.resetBase(system);
        PINInteractor.resetPIN(system);
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
        saveAccountBase();
        coordinator.openAccountList();
    }

    @Override
    public void removeAccount(Account account){
        accountSystem.deleteAccount(account);
        saveAccountBase();
        coordinator.openAccountList();
    }

    private void saveAccountBase(){
        AccountBaseInteractor.saveAccountBase(system, accountSystem,
                new function() {
                    @Override
                    public void run(String... params) {
                        coordinator.unableToEditBaseFile();
                    }
                },
                new function() {
                    @Override
                    public void run(String... params) {
                        coordinator.unableToEditBaseFile();
                        resetBase();
                    }
                });
    }

    public void settingsChanged() {
        String basePath = AccountBaseInteractor.getAccountBasePath(system);
        if(basePath==null){
            coordinator.selectBaseView();
            return;
        }
        String oldKey = accountSystem.getKey();
        try {
            accountSystem = AccountBaseInteractor.getAccountSystem(system);
        } catch (IOException e) {
            coordinator.unableToReadBaseFile();
            resetBase();
            return;
        } catch (AccountBaseInteractor.EmptyBaseException e) {
            coordinator.newKeyView();
            return;
        }
        inputCorrectKey(oldKey, new function() {
            @Override
            public void run(String... params) {
                onBaseSelected();
            }
        });
    }
}
