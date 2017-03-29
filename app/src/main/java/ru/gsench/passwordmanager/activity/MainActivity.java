package ru.gsench.passwordmanager.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Toast;

import account_system.Account;
import account_system.AccountSystem;
import ru.gsench.passwordmanager.AndroidInterface;
import ru.gsench.passwordmanager.R;
import ru.gsench.passwordmanager.aview.EditAccountAView;
import ru.gsench.passwordmanager.aview.KeyInputAView;
import ru.gsench.passwordmanager.aview.NewKeyAView;
import ru.gsench.passwordmanager.aview.NewPINAView;
import ru.gsench.passwordmanager.aview.PINInputAView;
import ru.gsench.passwordmanager.aview.SelectBaseAView;
import interactor.MainInteractor;
import ru.gsench.passwordmanager.utils.BaseActivity;
import ru.gsench.passwordmanager.utils.CustomKeyboard;
import ru.gsench.passwordmanager.view.MainView;
import ru.gsench.passwordmanager.view_etc.AccountListAdapter;
import ru.gsench.passwordmanager.view_etc.PermissionManager;
import ru.gsench.passwordmanager.viewholder.MainViewHolder;
import utils.function;

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

    MainInteractor interactor;
    AccountListAdapter accountListAdapter;
    MainViewHolder viewHolder;
    EditAccountAView accountWindow;
    CustomKeyboard keyboard;
    PermissionManager permissionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main, R.id.dialog_content);
        viewHolder = new MainViewHolder(this);
        setupKeyboard();
        setupAccountWindow();
        permissionManager = new PermissionManager(this);
        permissionManager.requestBasePermissions(this, new function() {
            @Override
            public void run(String... params) {
                interactor = new MainInteractor(MainActivity.this, new AndroidInterface(MainActivity.this));
                interactor.onStart();
            }
        });
    }

    private void setupKeyboard(){
        keyboard = new CustomKeyboard(this, (KeyboardView) findViewById(R.id.keyboard_view), true);
        keyboard.enableHapticFeedback(true);
    }

    private void setupAccountWindow(){
        accountWindow = new EditAccountAView(this, viewHolder.main);
        keyboard.registerEditText(accountWindow.aViewHolder.editLogin, true);
        keyboard.registerEditText(accountWindow.aViewHolder.editName, true);
        keyboard.registerEditText(accountWindow.aViewHolder.editPassword, true);
    }

    @Override
    public void viewAccounts(AccountSystem accounts) {
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
            accountListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void keyInputWindow() {
        KeyInputAView aView = (KeyInputAView) new KeyInputAView(this, viewHolder.main, interactor).closeOnBackPressed(false);
        keyboard.registerEditText(aView.viewHolder.keyEdit, true);
        aView.open();
    }

    @Override
    public void unableToParseBase() {
        Toast.makeText(this, R.string.unable_parse_base, Toast.LENGTH_SHORT).show();
        interactor.resetBase();
    }

    @Override
    public void unexpectedException() {
        Toast.makeText(this, R.string.unexpected_error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void unableToEditBaseFile() {
        Toast.makeText(this, R.string.unable_to_edit_file, Toast.LENGTH_SHORT).show();
        interactor.resetBase();
    }

    @Override
    public void unableToReadBaseFile() {
        Toast.makeText(this, R.string.unable_to_read_file, Toast.LENGTH_SHORT).show();
        interactor.resetBase();
    }

    @Override
    public void closeCurrentView() {
        closeView();
    }

    @Override
    public void selectBaseWindow() {
        new SelectBaseAView(this, viewHolder.main, interactor)
                .closeOnBackPressed(false)
                .open();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void newKeyWindow() {
        NewKeyAView aView = (NewKeyAView) new NewKeyAView(this, viewHolder.main, interactor).closeOnBackPressed(false);
        keyboard.registerEditText(aView.viewHolder.keyEdit, true);
        aView.open();
    }

    @Override
    public void openPINWindow() {
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
                        newPINWindow();
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

    private void newPINWindow(){
        new NewPINAView(MainActivity.this, viewHolder.main, interactor)
                .closeOnBackPressed(true)
                .open();
    }

    public void onAddClick(View v){
        accountWindow.clearAddAccDialog();
        accountWindow.setOnDialogOkClick(new function() {
            @Override
            public void run(String... params) {
                if(accountWindow.checkAccDialogFilling()) {
                    closeView();
                    interactor.addNewAccount(accountWindow.getAccount());
                }
            }
        });
        accountWindow.closeOnBackPressed(true);
        openView(accountWindow);
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
                        interactor.removeAccount(accountToDelete);
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

    private void editAccount(final Account account){
        accountWindow.clearAddAccDialog();
        accountWindow.setAccount(account);
        accountWindow.setOnDialogOkClick(new function() {
            @Override
            public void run(String... params) {
                if(accountWindow.checkAccDialogFilling()) {
                    closeView();
                    Account account1 = accountWindow.getAccount();
                    account.setName(account1.getName());
                    account.setLogin(account1.getLogin());
                    account.setPassword(account1.getPassword());
                    interactor.editAccount(account);
                }
            }
        });
        accountWindow.closeOnBackPressed(true);
        openView(accountWindow);
    }

    @Override
    public void onBackPressed() {
        if(keyboard.isCustomKeyboardVisible()){
            keyboard.hideCustomKeyboard();
            return;
        }
        super.onBackPressed();
    }

}
