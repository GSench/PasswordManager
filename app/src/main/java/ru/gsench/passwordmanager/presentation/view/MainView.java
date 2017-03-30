package ru.gsench.passwordmanager.presentation.view;

import ru.gsench.passwordmanager.domain.account_system.Account;
import ru.gsench.passwordmanager.domain.account_system.AccountSystem;
import ru.gsench.passwordmanager.domain.interactor.MainInteractor;

/**
 * Created by grish on 26.02.2017.
 */

public interface MainView {

    public void init();
    public void exit();
    public void viewAccounts(AccountSystem accounts);
    public void keyInputWindow(MainInteractor interactor);
    public void selectBaseWindow(MainInteractor interactor);
    public void newKeyWindow(MainInteractor interactor);
    public void openPINWindow(MainInteractor interactor);
    public void newPINDialog();
    public void unableToParseBase();
    public void unexpectedException();
    public void unableToEditBaseFile();
    public void unableToReadBaseFile();
    public void closeCurrentView();
    public void editAccountWindow(MainInteractor interactor, Account account);
    public void newPINWindow(MainInteractor interactor);
}
