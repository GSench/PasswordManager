package ru.gsench.passwordmanager;

import android.content.DialogInterface;
import android.content.Intent;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;

import account_system.Account;
import account_system.AccountSystem;
import ru.gsench.passwordmanager.windows.AccountListAdapter;
import ru.gsench.passwordmanager.windows.EditAccountWindow;
import ru.gsench.passwordmanager.windows.KeyInputWindow;
import ru.gsench.passwordmanager.windows.PINInputWindow;
import ru.gsench.passwordmanager.windows.PermissionManager;
import ru.gsench.passwordmanager.windows.SelectBaseWindow;
import ru.gsench.passwordmanager.windows.WindowListener;
import utils.CustomKeyboard;
import utils.function;

public class MainActivity extends AppCompatActivity implements MainView {

    private final String defaultBaseFileName = "base.xml";
    private final String defaultBaseFilePath = new File(Environment.getExternalStorageDirectory(), "Password Manager/"+defaultBaseFileName).getAbsolutePath();

    //TODO App Preferences
    public static final String APP_PREFERENCES = "AppPreferences";

    MainPresenter presenter;
    AccountListAdapter accountListAdapter;
    MainViewHolder viewHolder;
    EditAccountWindow accountWindow;
    CustomKeyboard keyboard;
    PermissionManager permissionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewHolder = new MainViewHolder(this);
        setupKeyboard();
        setupAccountWindow();
        permissionManager = new PermissionManager(this);
        permissionManager.requestBasePermissions(this, new function() {
            @Override
            public void run(String... params) {
                presenter = new MainPresenter(MainActivity.this, new AndroidInterface(MainActivity.this));
                presenter.onStart();
            }
        });
    }

    private void openWindow(ViewGroup viewGroup, boolean closeOnBackPressed){
        closeWindow();
        this.closeOnBackPressed=closeOnBackPressed;
        viewHolder.dialogContent.addView(viewGroup, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private void closeWindow(){
        viewHolder.dialogContent.removeAllViews();
    }

    private boolean closeOnBackPressed = true;
    private boolean windowOpened(){
        return viewHolder.dialogContent.getChildCount()!=0;
    }

    private void setupKeyboard(){
        keyboard = new CustomKeyboard(this, (KeyboardView) findViewById(R.id.keyboard_view));
        keyboard.enableHapticFeedback(true);
    }

    private void setupAccountWindow(){
        accountWindow = new EditAccountWindow(this, viewHolder.main, new WindowListener() {
            @Override
            public void onClose() {
                closeWindow();
            }
        });
        keyboard.registerEditText(accountWindow.aViewHolder.editLogin);
        keyboard.registerEditText(accountWindow.aViewHolder.editName);
        keyboard.registerEditText(accountWindow.aViewHolder.editPassword);
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
        KeyInputWindow window = new KeyInputWindow(this, viewHolder.main, new function() {
            @Override
            public void run(String... params) {
                if(presenter.isKeyPhraseCorrect(params[0])){
                    closeWindow();
                    presenter.afterCorrectKeyInput();
                }
            }
        });
        window.setMessage(getString(R.string.input_key));
        keyboard.registerEditText(window.viewHolder.keyEdit);
        openWindow(window.getView(), false);
    }

    @Override
    public void selectBaseWindow() {
        SelectBaseWindow window = new SelectBaseWindow(this, viewHolder.main,
                new function() {
                    @Override
                    public void run(String... params) {
                        closeWindow();
                        presenter.onBaseSelected(params[0]);
                    }
                },
                new function() {
                    @Override
                    public void run(String... params) {
                        closeWindow();
                        presenter.onNewBaseSelected(new File(new File(params[0]), defaultBaseFileName).getAbsolutePath());
                    }
                },
                new function() {
                    @Override
                    public void run(String... params) {
                        closeWindow();
                        presenter.onNewBaseSelected(defaultBaseFilePath);
                    }
                });
        openWindow(window.getView(), false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void newKeyWindow() {
        KeyInputWindow window = new KeyInputWindow(this, viewHolder.main, new function() {
            @Override
            public void run(String... params) {
                presenter.isKeyPhraseCorrect(params[0]);
                closeWindow();
                presenter.afterNewKeyInput();
            }
        });
        window.setMessage(getString(R.string.input_new_key));
        keyboard.registerEditText(window.viewHolder.keyEdit);
        openWindow(window.getView(), false);
    }

    @Override
    public void openPINWindow() {
        PINInputWindow window = new PINInputWindow(this, viewHolder.main, new function() {
            @Override
            public void run(String... params) {
                if(presenter.isPINCorrect(params[0])){
                    closeWindow();
                    presenter.afterCorrectPINInput();
                }
            }
        });
        openWindow(window.getView(), false);
    }

    @Override
    public void newPINDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.create_pin_title)
                .setMessage(R.string.create_pin_msg)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        PINInputWindow window = new PINInputWindow(MainActivity.this, viewHolder.main, new function() {
                            @Override
                            public void run(String... params) {
                                closeWindow();
                                presenter.onNewPIN(params[0]);
                            }
                        });
                        dialogInterface.cancel();
                        openWindow(window.getView(), true);
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

    public void onAddClick(View v){
        accountWindow.clearAddAccDialog();
        accountWindow.setOnDialogOkClick(new function() {
            @Override
            public void run(String... params) {
                if(accountWindow.checkAccDialogFilling()) {
                    closeWindow();
                    presenter.addNewAccount(accountWindow.getAccount());
                }
            }
        });
        openWindow(accountWindow.getView(), true);
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
                        presenter.removeAccount(accountToDelete);
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
                    closeWindow();
                    Account account1 = accountWindow.getAccount();
                    account.setName(account1.getName());
                    account.setLogin(account1.getLogin());
                    account.setPassword(account1.getPassword());
                    presenter.editAccount(account);
                }
            }
        });
        openWindow(accountWindow.getView(), true);
    }

    @Override
    public void onBackPressed() {
        if(keyboard.isCustomKeyboardVisible()){
            keyboard.hideCustomKeyboard();
            return;
        }
        if(windowOpened()&&closeOnBackPressed) closeWindow();
        else super.onBackPressed();
    }

}
