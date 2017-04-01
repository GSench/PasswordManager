package ru.gsench.passwordmanager.presentation.view;

import java.util.ArrayList;

import ru.gsench.passwordmanager.domain.account_system.Account;

/**
 * Created by grish on 01.04.2017.
 */

public interface AccountListView {

    public void init();
    public void exit();
    public void viewAccounts(ArrayList<Account> accounts);
    public void updateAccounts();
    public void confirmDeleteDialog(Account accountToDelete);

}
