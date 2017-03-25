package ru.gsench.passwordmanager.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Toast;

import java.io.File;

import account_system.Account;
import account_system.AccountSystem;
import ru.gsench.passwordmanager.AndroidInterface;
import ru.gsench.passwordmanager.R;
import ru.gsench.passwordmanager.aview.EditAccountWindow;
import ru.gsench.passwordmanager.aview.KeyInputWindow;
import ru.gsench.passwordmanager.aview.PINInputWindow;
import ru.gsench.passwordmanager.aview.SelectBaseWindow;
import ru.gsench.passwordmanager.presenter.MainPresenter;
import ru.gsench.passwordmanager.utils.CustomKeyboard;
import ru.gsench.passwordmanager.view.MainView;
import ru.gsench.passwordmanager.view_etc.AccountListAdapter;
import ru.gsench.passwordmanager.view_etc.PermissionManager;
import ru.gsench.passwordmanager.viewholder.MainViewHolder;
import utils.function;

public class MainActivity extends BaseActivity implements MainView {

    private final String defaultBaseFileName = "base.xml";
    private final String defaultBaseFilePath = new File(Environment.getExternalStorageDirectory(), "Password Manager/"+defaultBaseFileName).getAbsolutePath();

    /**TODO App Preferences
     * - keyboard option
     * - base reselection
     * - pin setting
     * - key setting
     * */
    //TODO Passwords' categories
    //TODO Search

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
        setContentView(R.layout.activity_main, R.id.dialog_content);
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

    private void setupKeyboard(){
        keyboard = new CustomKeyboard(this, (KeyboardView) findViewById(R.id.keyboard_view), true);
        keyboard.enableHapticFeedback(true);
    }

    private void setupAccountWindow(){
        accountWindow = (EditAccountWindow) new EditAccountWindow(this, viewHolder.main).closeSelf(new function() {
            @Override
            public void run(String... p) {
                closeView();
            }
        });
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
        KeyInputWindow window = (KeyInputWindow) new KeyInputWindow(this, viewHolder.main).closeOnBackPressed(false).onResult(new function() {
            @Override
            public void run(String... params) {
                presenter.onKeyInput(params[0]);
            }
        });
        window.setMessage(getString(R.string.input_key));
        keyboard.registerEditText(window.viewHolder.keyEdit, true);
        openView(window);
    }

    @Override
    public void onCorrectKeyInput(){
        closeView();
    }

    @Override
    public void onIncorrectKeyInput() {
        Toast.makeText(this, R.string.incorrect_key, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void unableToParseBase() {
        Toast.makeText(this, R.string.unable_parse_base, Toast.LENGTH_SHORT).show();
        presenter.resetBase();
    }

    @Override
    public void unexpectedException() {
        Toast.makeText(this, R.string.unexpected_error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void unableToEditBaseFile() {
        Toast.makeText(this, R.string.unable_to_edit_file, Toast.LENGTH_SHORT).show();
        presenter.resetBase();
    }

    @Override
    public void unableToReadBaseFile() {
        Toast.makeText(this, R.string.unable_to_read_file, Toast.LENGTH_SHORT).show();
        presenter.resetBase();
    }

    @Override
    public void selectBaseWindow() {
        SelectBaseWindow window = (SelectBaseWindow) new SelectBaseWindow(this, viewHolder.main,
                new function() {
                    @Override
                    public void run(String... params) {
                        closeView();
                        presenter.onExistingBaseSelected(params[0]);
                    }
                },
                new function() {
                    @Override
                    public void run(String... params) {
                        closeView();
                        presenter.onNewBaseSelected(new File(new File(params[0]), defaultBaseFileName).getAbsolutePath());
                    }
                },
                new function() {
                    @Override
                    public void run(String... params) {
                        closeView();
                        presenter.onNewBaseSelected(defaultBaseFilePath);
                    }
                }).closeOnBackPressed(false);
        openView(window);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void newKeyWindow() {
        final String[] key1 = {null};
        final KeyInputWindow[] window = {null};
        window[0] = (KeyInputWindow) new KeyInputWindow(this, viewHolder.main).closeOnBackPressed(false).onResult(new function() {
            @Override
            public void run(String... params) {
                if(key1[0]!=null){
                    if(key1[0].equals(params[0])){
                        presenter.afterNewKeyInput(params[0]);
                    } else {
                        window[0].clearKeyEdit();
                        window[0].setMessage(getString(R.string.keys_not_equal)+"\n\n"+getString(R.string.input_new_key));
                        key1[0] = null;
                    }
                } else {
                    window[0].clearKeyEdit();
                    window[0].setMessage(getString(R.string.reenter_key));
                    key1[0] = params[0];
                }
            }
        });
        window[0].setMessage(getString(R.string.input_new_key));
        keyboard.registerEditText(window[0].viewHolder.keyEdit, true);
        openView(window[0]);
    }

    @Override
    public void openPINWindow() {
        final PINInputWindow PINWindow[] = new PINInputWindow[]{null};
        PINWindow[0] = (PINInputWindow) new PINInputWindow(this, viewHolder.main, new function() {
            @Override
            public void run(String... params) {
                try {
                    presenter.onPINInput(params[0]);
                } catch (MainPresenter.BlockPINException e) {
                }
                long block = presenter.isPINBlocked();
                if (block > 0) PINWindow[0].blockPINFor(block);
            }
        }, new function() {
            @Override
            public void run(String... params) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(R.string.reset_pin)
                        .setMessage(R.string.reset_pin_msg)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                closeView();
                                presenter.onResetPINBtn();
                            }
                        })
                        .setNeutralButton(R.string.cancel, null)
                        .create()
                        .show();
            }
        }, getString(R.string.reset_pin)).closeOnBackPressed(false);
        long block = presenter.isPINBlocked();
        if(block>0) PINWindow[0].blockPINFor(block);
        openView(PINWindow[0]);
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
        final String[] pin1 = {null};
        final PINInputWindow[] window = {null};
        window[0] = (PINInputWindow) new PINInputWindow(MainActivity.this, viewHolder.main, new function() {
            @Override
            public void run(String... params) {
                if(pin1[0]!=null){
                    if(pin1[0].equals(params[0])){
                        closeView();
                        presenter.onNewPIN(params[0]);
                    } else {
                        window[0].setMessage(getString(R.string.pins_not_equal));
                        pin1[0] = null;
                    }
                } else {
                    window[0].setMessage(getString(R.string.reenter_pin));
                    pin1[0] = params[0];
                }
            }
        }, new function() {
            @Override
            public void run(String... params) {
                closeView();
            }
        }, getString(R.string.cancel)).closeOnBackPressed(true);
        openView(window[0]);
    }

    public void onAddClick(View v){
        accountWindow.clearAddAccDialog();
        accountWindow.setOnDialogOkClick(new function() {
            @Override
            public void run(String... params) {
                if(accountWindow.checkAccDialogFilling()) {
                    closeView();
                    presenter.addNewAccount(accountWindow.getAccount());
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
                    closeView();
                    Account account1 = accountWindow.getAccount();
                    account.setName(account1.getName());
                    account.setLogin(account1.getLogin());
                    account.setPassword(account1.getPassword());
                    presenter.editAccount(account);
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
