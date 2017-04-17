package ru.gsench.passwordmanager.domain.usecase;

import java.util.ArrayList;

import ru.gsench.passwordmanager.domain.account_system.Account;

/**
 * Created by grish on 01.04.2017.
 */

public interface AccountListUseCase {

    public void onEditAccount(Account account);
    public void onAddAccount();
    public void removeAccount(Account account);
    public ArrayList<Account> getAccountsToView();
}
