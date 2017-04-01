package ru.gsench.passwordmanager.presentation.view.aview;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import ru.gsench.passwordmanager.R;
import ru.gsench.passwordmanager.domain.account_system.Account;
import ru.gsench.passwordmanager.domain.interactor.AccountListUseCase;
import ru.gsench.passwordmanager.domain.interactor.MainInteractor;
import ru.gsench.passwordmanager.presentation.presenter.AccountListPresenter;
import ru.gsench.passwordmanager.presentation.utils.AView;
import ru.gsench.passwordmanager.presentation.utils.AViewContainer;
import ru.gsench.passwordmanager.presentation.view.AccountListView;
import ru.gsench.passwordmanager.presentation.view.view_etc.AccountListAdapter;
import ru.gsench.passwordmanager.presentation.viewholder.AccountListViewHolder;

/**
 * Created by grish on 01.04.2017.
 */

public class AccountListAView extends AView implements AccountListView {

    public AccountListViewHolder viewHolder;
    private AccountListPresenter presenter;
    private AccountListAdapter accountListAdapter;

    public AccountListAView(AViewContainer container, AccountListUseCase interactor) {
        super(container);
        viewHolder = new AccountListViewHolder(context, parent);
        presenter = new AccountListPresenter(this, interactor);
    }

    @Override
    protected ViewGroup getView() {
        return viewHolder.main;
    }

    @Override
    protected void start() {
        presenter.start();
    }

    @Override
    public void init() {
        viewHolder.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onAddAccountBtn();
            }
        });
    }

    @Override
    public void exit() {
        closeSelf();
    }

    @Override
    public void viewAccounts(ArrayList<Account> accounts) {
        if(accountListAdapter==null){
            accountListAdapter=new AccountListAdapter(context, accounts, new AccountListAdapter.AccountListInterface() {
                @Override
                public void onAccountDelete(Account account) {
                    presenter.onDeleteAccountBtn(account);
                }

                @Override
                public void onAccountEdit(Account account) {
                    presenter.onEditAccountBtn(account);
                }
            });
            viewHolder.accountList.setAdapter(accountListAdapter);
        } else {
            accountListAdapter.notifyDataSetChanged(accounts);
        }
        accountListAdapter.closeAllItems();
    }

    @Override
    public void updateAccounts() {
        presenter.onUpdateAccounts();
    }

    @Override
    public void confirmDeleteDialog(final Account accountToDelete){
        new AlertDialog.Builder(context)
                .setTitle(R.string.delete_title)
                .setMessage(context.getString(R.string.delete_msg, accountToDelete.getName(), accountToDelete.getLogin()))
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        presenter.onDeleteAccountConfirmed(accountToDelete);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .create()
                .show();
    }
}
