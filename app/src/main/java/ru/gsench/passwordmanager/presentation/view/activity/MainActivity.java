package ru.gsench.passwordmanager.presentation.view.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import ru.gsench.passwordmanager.domain.account_system.Account;
import ru.gsench.passwordmanager.domain.account_system.AccountSystem;
import ru.gsench.passwordmanager.presentation.AndroidInterface;
import ru.gsench.passwordmanager.R;
import ru.gsench.passwordmanager.presentation.view.aview.EditAccountAView;
import ru.gsench.passwordmanager.presentation.view.aview.KeyInputAView;
import ru.gsench.passwordmanager.presentation.view.aview.NewKeyAView;
import ru.gsench.passwordmanager.presentation.view.aview.NewPINAView;
import ru.gsench.passwordmanager.presentation.view.aview.PINInputAView;
import ru.gsench.passwordmanager.presentation.view.aview.SelectBaseAView;
import ru.gsench.passwordmanager.domain.interactor.MainInteractor;
import ru.gsench.passwordmanager.presentation.presenter.MainPresenter;
import ru.gsench.passwordmanager.presentation.utils.BaseActivity;
import ru.gsench.passwordmanager.presentation.utils.CustomKeyboard;
import ru.gsench.passwordmanager.presentation.view.MainView;
import ru.gsench.passwordmanager.presentation.view.view_etc.AccountListAdapter;
import ru.gsench.passwordmanager.presentation.view.view_etc.PermissionManager;
import ru.gsench.passwordmanager.presentation.viewholder.MainViewHolder;
import ru.gsench.passwordmanager.domain.utils.function;

public class MainActivity extends BaseActivity implements MainView {

    /**TODO App Preferences
     * - keyboard option
     * - base reselection
     * - pin setting
     * - key setting
     * */
    //TODO Passwords' categories
    //TODO Search

    public static final String APP_PREFERENCES = "AppPreferences";

    AccountListAdapter accountListAdapter;
    MainViewHolder viewHolder;
    CustomKeyboard keyboard;
    PermissionManager permissionManager;

    private MainPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main, R.id.dialog_content);
        viewHolder = new MainViewHolder(this);
        permissionManager = new PermissionManager(this);
        permissionManager.requestBasePermissions(this, new function() {
            @Override
            public void run(String... params) {
                presenter = new MainPresenter(MainActivity.this, new AndroidInterface(MainActivity.this));
                presenter.start();
            }
        });
    }

    @Override
    public void init() {
        setupKeyboard();
    }

    @Override
    public void exit() {
        finish();
    }

    private void setupKeyboard(){
        keyboard = new CustomKeyboard(this, (KeyboardView) findViewById(R.id.keyboard_view), true);
        keyboard.enableHapticFeedback(true);
    }

    @Override
    public void viewAccounts(ArrayList<Account> accounts) {
        if(accountListAdapter==null){
            accountListAdapter=new AccountListAdapter(this, accounts, new AccountListAdapter.AccountListInterface() {
                @Override
                public void onAccountDelete(Account account) {
                    confirmDeleteDialog(account);
                }

                @Override
                public void onAccountEdit(Account account) {
                    editAccount(account);
                }
            });
            viewHolder.accountList.setAdapter(accountListAdapter);
        } else {
            accountListAdapter.notifyDataSetChanged(accounts);
        }
    }

    @Override
    public void keyInputWindow(MainInteractor interactor) {
        KeyInputAView aView = (KeyInputAView) new KeyInputAView(this, viewHolder.main, interactor).closeOnBackPressed(false);
        keyboard.registerEditText(aView.viewHolder.keyEdit, true);
        aView.open();
    }

    @Override
    public void unableToParseBase() {
        Toast.makeText(this, R.string.unable_parse_base, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void unexpectedException() {
        Toast.makeText(this, R.string.unexpected_error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void unableToEditBaseFile() {
        Toast.makeText(this, R.string.unable_to_edit_file, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void unableToReadBaseFile() {
        Toast.makeText(this, R.string.unable_to_read_file, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void closeCurrentView() {
        closeView();
    }

    @Override
    public void selectBaseWindow(MainInteractor interactor) {
        new SelectBaseAView(this, viewHolder.main, interactor)
                .closeOnBackPressed(false)
                .open();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void newKeyWindow(MainInteractor interactor) {
        NewKeyAView aView = (NewKeyAView) new NewKeyAView(this, viewHolder.main, interactor).closeOnBackPressed(false);
        keyboard.registerEditText(aView.viewHolder.keyEdit, true);
        aView.open();
    }

    @Override
    public void openPINWindow(MainInteractor interactor) {
        new PINInputAView(this, viewHolder.main, interactor)
                .closeOnBackPressed(false)
                .open();
    }

    @Override
    public void newPINDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.create_pin_title)
                .setMessage(R.string.create_pin_msg)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                        presenter.onConfirmPINCreation();
                    }
                })
                .setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .setCancelable(false)
                .create()
                .show();
    }

    @Override
    public void newPINWindow(MainInteractor interactor){
        new NewPINAView(MainActivity.this, viewHolder.main, interactor)
                .closeOnBackPressed(true)
                .open();
    }

    public void onAddClick(View v){
        presenter.onAddAccountBtn();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        permissionManager.onPermissionCallback(requestCode, permissions, grantResults);
    }

    private void confirmDeleteDialog(final Account accountToDelete){
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_title)
                .setMessage(getString(R.string.delete_msg, accountToDelete.getName(), accountToDelete.getLogin()))
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        presenter.onDeleteAccountBtn(accountToDelete);
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

    private void editAccount(Account account){
        presenter.onEditAccountBtn(account);
    }

    @Override
    public void editAccountWindow(MainInteractor interactor, Account account){
        EditAccountAView view = (EditAccountAView) new EditAccountAView(this, viewHolder.dialogContent, interactor, account)
                .closeOnBackPressed(true);
        keyboard.registerEditText(view.aViewHolder.editLogin, true);
        keyboard.registerEditText(view.aViewHolder.editName, true);
        keyboard.registerEditText(view.aViewHolder.editPassword, true);
        view.open();
    }

    @Override
    public void onBackPressed() {
        if(keyboard.isCustomKeyboardVisible()){
            keyboard.hideCustomKeyboard();
            return;
        }
        presenter.onBackPressed();
    }

}
