package ru.gsench.passwordmanager;

import account_system.AccountSystem;

/**
 * Created by grish on 26.02.2017.
 */

public interface MainView {

    public void viewAccounts(AccountSystem accounts);
    public void keyInputWindow();
    public void selectBaseWindow();
    public void newKeyWindow();

}
