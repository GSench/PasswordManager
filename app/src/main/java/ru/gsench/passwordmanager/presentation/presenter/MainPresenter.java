package ru.gsench.passwordmanager.presentation.presenter;

import java.util.ArrayList;

import ru.gsench.passwordmanager.domain.SystemInterface;
import ru.gsench.passwordmanager.domain.account_system.Account;
import ru.gsench.passwordmanager.domain.interactor.MainInteractor;
import ru.gsench.passwordmanager.presentation.view.MainView;

/**
 * Created by grish on 30.03.2017.
 */

public class MainPresenter {

    private MainInteractor interactor;
    private MainView view;

    public MainPresenter(MainView view, SystemInterface systemInterface){
        interactor=new MainInteractor(this, systemInterface);
        this.view=view;
    }

    public void start(){
        view.init();
        interactor.onStart();
    }

    public void selectBaseWindow() {
        view.selectBaseWindow(interactor);
    }

    public void unableToParseBase() {
        view.unableToParseBase();
    }

    public void unexpectedException() {
        view.unexpectedException();
    }

    public void newPINDialog() {
        view.newPINDialog();
    }

    public void closeCurrentView() {
        view.closeCurrentView();
    }

    public void keyInputWindow() {
        view.keyInputWindow(interactor);
    }

    public void unableToEditBaseFile() {
        view.unableToEditBaseFile();
    }

    public void unableToReadBaseFile() {
        view.unableToReadBaseFile();
    }

    public void newKeyWindow() {
        view.newKeyWindow(interactor);
    }

    public void onConfirmPINCreation(){
        view.newPINWindow(interactor);
    }

    public void openPINWindow() {
        view.openPINWindow(interactor);
    }

    public void viewAccounts(ArrayList<Account> accountSystem) {
        view.viewAccounts(accountSystem);
    }

    public void onEditAccountBtn(Account account){
        view.editAccountWindow(interactor, account);
    }

    public void onAddAccountBtn(){
        view.editAccountWindow(interactor, null);
    }

    public void onDeleteAccountBtn(Account account){
        interactor.removeAccount(account);
    }

    public void onBackPressed() {
        view.exit();
    }
}
