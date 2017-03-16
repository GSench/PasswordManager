package ru.gsench.passwordmanager;

import android.content.DialogInterface;
import android.content.Intent;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
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
import utils.CustomKeyboard;
import utils.function;

public class MainActivity extends AppCompatActivity implements MainView {

    private final String defaultBaseFileName = "base.xml";
    private final String defaultBaseFilePath = new File(Environment.getExternalStorageDirectory(), "Password Manager/"+defaultBaseFileName).getAbsolutePath();

    /**TODO App Preferences
     * - keyboard option
     * - base reselection
     * - pin setting
     * - key setting
     * */

    public static final String APP_PREFERENCES = "AppPreferences";

    MainPresenter presenter;
    AccountListAdapter accountListAdapter;
    MainViewHolder viewHolder;
    EditAccountWindow accountWindow;
    PINInputWindow PINWindow;
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
        viewGroup.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
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
        accountWindow = new EditAccountWindow(this, viewHolder.main, new function() {
            @Override
            public void run(String... p) {
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
                        presenter.onExistingBaseSelected(params[0]);
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
        final String[] key1 = {null};
        final KeyInputWindow[] window = {null};
        window[0] = new KeyInputWindow(this, viewHolder.main, new function() {
            @Override
            public void run(String... params) {
                if(key1[0]!=null){
                    if(key1[0].equals(params[0])){
                        closeWindow();
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
        keyboard.registerEditText(window[0].viewHolder.keyEdit);
        openWindow(window[0].getView(), false);
    }

    @Override
    public void openPINWindow() {
        PINWindow = new PINInputWindow(this, viewHolder.main, new function() {
            @Override
            public void run(String... params) {
                try {
                    if (presenter.isPINCorrect(params[0])) {
                        closeWindow();
                        presenter.afterCorrectPINInput();
                    }
                } catch (MainPresenter.BlockPINException e) {
                }
                long block = presenter.isPINBlocked();
                if (block > 0) PINWindow.blockPINFor(block);
            }
        }, new function() {
            @Override
            public void run(String... params) {
                closeWindow();
                presenter.onResetPIN();
            }
        }, getString(R.string.reset_pin));
        long block = presenter.isPINBlocked();
        if(block>0) PINWindow.blockPINFor(block);
        openWindow(PINWindow.getView(), false);
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
        window[0] = new PINInputWindow(MainActivity.this, viewHolder.main, new function() {
            @Override
            public void run(String... params) {
                if(pin1[0]!=null){
                    if(pin1[0].equals(params[0])){
                        closeWindow();
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
                closeWindow();
            }
        }, getString(R.string.cancel));
        openWindow(window[0].getView(), true);
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
