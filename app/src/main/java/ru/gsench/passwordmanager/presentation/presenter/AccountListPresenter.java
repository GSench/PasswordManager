package ru.gsench.passwordmanager.presentation.presenter;

import ru.gsench.passwordmanager.domain.account_system.Account;
import ru.gsench.passwordmanager.domain.interactor.AccountListUseCase;
import ru.gsench.passwordmanager.presentation.view.AccountListView;

/**
 * Created by grish on 01.04.2017.
 */

public class AccountListPresenter {

    private AccountListUseCase interactor;

    public void setView(AccountListView view) {
        this.view = view;
    }

    private AccountListView view;

    public AccountListPresenter(AccountListUseCase interactor){
        this.interactor=interactor;
    }

    public void start(){
        view.init();
        view.viewAccounts(interactor.getAccountsToView());
    }

    public void onEditAccountBtn(Account account){
        interactor.onEditAccount(account);
    }

    public void onAddAccountBtn(){
        interactor.onAddAccount();
    }

    public void onDeleteAccountBtn(Account account){
        view.confirmDeleteDialog(account);
    }

    public void onDeleteAccountConfirmed(Account account){
        interactor.removeAccount(account);
    }

    public void onUpdateAccounts(){
        view.viewAccounts(interactor.getAccountsToView());
    }

}
