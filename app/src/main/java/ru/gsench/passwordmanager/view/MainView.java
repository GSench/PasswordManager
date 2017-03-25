package ru.gsench.passwordmanager.view;

import account_system.AccountSystem;

/**
 * Created by grish on 26.02.2017.
 */

public interface MainView {

    public void viewAccounts(AccountSystem accounts);
    public void keyInputWindow();
    public void selectBaseWindow();
    public void newKeyWindow();
    public void openPINWindow();
    public void newPINDialog();
    public void onCorrectKeyInput();
    public void onIncorrectKeyInput();
    public void unableToParseBase();
    public void unexpectedException();
    public void unableToEditBaseFile();
    public void unableToReadBaseFile();

}
