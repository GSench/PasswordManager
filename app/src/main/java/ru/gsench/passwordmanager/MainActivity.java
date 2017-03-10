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
import android.widget.EditText;
import android.widget.LinearLayout;

import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;

import java.io.File;

import account_system.Account;
import account_system.AccountSystem;
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

    private void showDialog(ViewGroup viewGroup){
        closeDialog();
        viewHolder.dialogContent.addView(viewGroup, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private void closeDialog(){
        viewHolder.dialogContent.removeAllViews();
    }

    private boolean dialogOpened(){
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
                closeDialog();
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
    public void openPasswordDialog() {
        ViewGroup inputKeyLayout = (ViewGroup) getLayoutInflater().inflate(R.layout.input_key_layout, viewHolder.dialogContent, false);
        final EditText editText = (EditText) inputKeyLayout.findViewById(R.id.edit_key);
        inputKeyLayout.findViewById(R.id.enter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(presenter.isKeyPhraseCorrect(editText.getText().toString())){
                    closeDialog();
                    presenter.onCorrectKeyPhraseInput();
                }
            }
        });
        keyboard.registerEditText(editText);
        showDialog(inputKeyLayout);
    }

    @Override
    public void noBaseDialog() {
        LinearLayout dialogLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.choose_base_dialog, viewHolder.main, false);
        dialogLayout.findViewById(R.id.choose_existing_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestFile(DialogConfigs.FILE_SELECT, getString(R.string.select_file), new function() {
                    @Override
                    public void run(String... params) {
                        closeDialog();
                        presenter.onBaseSelected(params[0]);
                    }
                });
            }
        });
        dialogLayout.findViewById(R.id.choose_new_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestFile(DialogConfigs.DIR_SELECT, getString(R.string.select_path), new function() {
                    @Override
                    public void run(String... params) {
                        closeDialog();
                        presenter.onNewBaseSelected(new File(new File(params[0]), defaultBaseFileName).getAbsolutePath());
                    }
                });
            }
        });
        dialogLayout.findViewById(R.id.use_default_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeDialog();
                presenter.onNewBaseSelected(defaultBaseFilePath);
            }
        });
        showDialog(dialogLayout);
    }

    private void requestFile(int selection_type, String title, final function doAfter){
        DialogProperties properties = new DialogProperties();
        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.selection_type = selection_type;
        properties.root = new File(DialogConfigs.DEFAULT_DIR);
        properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
        properties.offset = new File(DialogConfigs.DEFAULT_DIR);
        properties.extensions = null;
        FilePickerDialog dialog = new FilePickerDialog(MainActivity.this,properties);
        dialog.setTitle(title);
        dialog.setDialogSelectionListener(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(String[] files) {
                if(files==null||files.length==0) return;
                doAfter.run(files[0]);
            }
        });
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    //TODO new key dialog
    @Override
    public void noKeyDialog() {
        openPasswordDialog();
    }

    public void onAddClick(View v){
        accountWindow.clearAddAccDialog();
        accountWindow.setOnDialogOkClick(new function() {
            @Override
            public void run(String... params) {
                if(accountWindow.checkAccDialogFilling()) {
                    closeDialog();
                    presenter.addNewAccount(accountWindow.getAccount());
                }
            }
        });
        showDialog(accountWindow.getView());
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
                    closeDialog();
                    Account account1 = accountWindow.getAccount();
                    account.setName(account1.getName());
                    account.setLogin(account1.getLogin());
                    account.setPassword(account1.getPassword());
                    presenter.editAccount(account);
                }
            }
        });
        showDialog(accountWindow.getView());
    }

    //TODO Edit for password input
    @Override
    public void onBackPressed() {
        if(keyboard.isCustomKeyboardVisible()){
            keyboard.hideCustomKeyboard();
            return;
        }
        if(dialogOpened()) closeDialog();
        else super.onBackPressed();
    }

}
