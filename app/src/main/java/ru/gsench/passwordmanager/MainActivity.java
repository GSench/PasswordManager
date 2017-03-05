package ru.gsench.passwordmanager;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.inputmethodservice.KeyboardView;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;

import java.io.File;

import account_system.Account;
import account_system.AccountSystem;
import utils.CustomKeyboard;
import utils.MyTextWatcher;
import utils.RandomPassword;

public class MainActivity extends AppCompatActivity implements MainView {

    private final String defaultBaseFileName = "base.xml";
    private final String defaultBaseFilePath = new File(Environment.getExternalStorageDirectory(), "Password Manager/"+defaultBaseFileName).getAbsolutePath();

    //TODO App Preferences
    public static final String APP_PREFERENCES = "AppPreferences";
    public static final String FULLY_RANDOM = "fully_random";
    public static final String WITHOUT_SYM = "without_sym";

    MainPresenter presenter;
    AccountListAdapter accountListAdapter;
    AndroidInterface androidInterface;
    MainViewHolder viewHolder;
    AddAccountViewHolder aViewHolder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewHolder = new MainViewHolder(this);
        setupAddAccDialog();
        androidInterface = new AndroidInterface(this);
        checkAndRequestPermissions();
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

    private void setupAddAccDialog(){
        aViewHolder = new AddAccountViewHolder(this, viewHolder.main);
        CustomKeyboard customKeyboard = new CustomKeyboard(this, aViewHolder.keyboard);
        customKeyboard.enableHapticFeedback(true);
        customKeyboard.registerEditText(aViewHolder.editName);
        customKeyboard.registerEditText(aViewHolder.editLogin);
        customKeyboard.registerEditText(aViewHolder.editPassword);
        aViewHolder.randomPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aViewHolder.editPassword.setText(RandomPassword.getRandomPassword(fullyRandomPref(), withoutSymPref()));
            }
        });
        aViewHolder.randomPINCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aViewHolder.editPassword.setText(RandomPassword.getRandomPINCode());
            }
        });
        aViewHolder.randomLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aViewHolder.editLogin.setText(RandomPassword.getRandomLogin());
            }
        });
        aViewHolder.editName.addTextChangedListener(new MyTextWatcher(){
            @Override
            public void afterTextChanged(Editable editable) {
                aViewHolder.nameInput.setErrorEnabled(false);
                aViewHolder.nameInput.setError(null);
            }
        });
        aViewHolder.editLogin.addTextChangedListener(new MyTextWatcher(){
            @Override
            public void afterTextChanged(Editable editable) {
                aViewHolder.loginInput.setErrorEnabled(false);
                aViewHolder.loginInput.setError(null);
            }
        });
        aViewHolder.editPassword.addTextChangedListener(new MyTextWatcher(){
            @Override
            public void afterTextChanged(Editable editable) {
                aViewHolder.passwordInput.setErrorEnabled(false);
                aViewHolder.passwordInput.setError(null);
            }
        });
        aViewHolder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeDialog();
            }
        });
    }

    private interface AddAccDialogListener{
        void onOkClick();
    }

    private void setOnDialogOkClick(final AddAccDialogListener listener){
        aViewHolder.ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onOkClick();
            }
        });
    }

    //TODO to separate class
    private void checkAndRequestPermissions(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M&&!writeExternalPermGranted(this)){
            new AlertDialog.Builder(this)
                    .setMessage("Get write permission")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                        }
                    })
                    .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                            finish();
                        }
                    })
                    .setCancelable(false)
                    .create()
                    .show();
        } else {
            presenter = new MainPresenter(this, new AndroidInterface(this));
            presenter.onStart();
        }
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

    //TODO Add QR-code scanner
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
                } else Toast.makeText(MainActivity.this, "Incorrect key phrase", Toast.LENGTH_SHORT).show();
            }
        });
        inputKeyLayout.findViewById(R.id.scan_qr_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        CustomKeyboard customKeyboard = new CustomKeyboard(this, (KeyboardView) inputKeyLayout.findViewById(R.id.keyboard_view));
        customKeyboard.enableHapticFeedback(true);
        customKeyboard.registerEditText(editText);
        showDialog(inputKeyLayout);
    }

    @Override
    public void noBaseDialog() {
        LinearLayout dialogLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.choose_base_dialog, viewHolder.main, false);
        dialogLayout.findViewById(R.id.choose_existing_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestFile();
            }
        });
        dialogLayout.findViewById(R.id.choose_new_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestDirectory();
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

    private void requestFile(){
        DialogProperties properties = new DialogProperties();
        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.selection_type = DialogConfigs.FILE_SELECT;
        properties.root = new File(DialogConfigs.DEFAULT_DIR);
        properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
        properties.offset = new File(DialogConfigs.DEFAULT_DIR);
        properties.extensions = null;
        FilePickerDialog dialog = new FilePickerDialog(MainActivity.this,properties);
        dialog.setTitle(getString(R.string.select_file));
        dialog.setDialogSelectionListener(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(String[] files) {
                if(files==null||files.length==0) return;
                closeDialog();
                presenter.onBaseSelected(files[0]);
            }
        });
        dialog.show();
    }

    private void requestDirectory(){
        DialogProperties properties = new DialogProperties();
        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.selection_type = DialogConfigs.DIR_SELECT;
        properties.root = new File(DialogConfigs.DEFAULT_DIR);
        properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
        properties.offset = new File(DialogConfigs.DEFAULT_DIR);
        properties.extensions = null;
        FilePickerDialog dialog = new FilePickerDialog(MainActivity.this,properties);
        dialog.setTitle(getString(R.string.select_path));
        dialog.setDialogSelectionListener(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(String[] files) {
                if(files==null||files.length==0) return;
                closeDialog();
                presenter.onNewBaseSelected(new File(new File(files[0]), defaultBaseFileName).getAbsolutePath());
            }
        });
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    //TODO new key dialog
    //TODO QR-code generator
    @Override
    public void noKeyDialog() {
        openPasswordDialog();
    }

    private boolean fullyRandomPref(){
        return getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE).getBoolean(FULLY_RANDOM, false);
    }

    private boolean withoutSymPref(){
        return getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE).getBoolean(WITHOUT_SYM, false);
    }

    private void clearAddAccDialog(){
        aViewHolder.nameInput.setErrorEnabled(false);
        aViewHolder.nameInput.setError(null);
        aViewHolder.loginInput.setErrorEnabled(false);
        aViewHolder.loginInput.setError(null);
        aViewHolder.passwordInput.setErrorEnabled(false);
        aViewHolder.passwordInput.setError(null);
        aViewHolder.editName.setText("");
        aViewHolder.editLogin.setText("");
        aViewHolder.editPassword.setText("");
    }

    public void onAddClick(View v){
        clearAddAccDialog();
        setOnDialogOkClick(new AddAccDialogListener() {
            @Override
            public void onOkClick() {
                addNewAccountFromDialog();
            }
        });
        showDialog(aViewHolder.main);
    }

    private boolean checkAccDialogFilling(){
        if(aViewHolder.editName.getText().toString().equals("")){
            aViewHolder.nameInput.setErrorEnabled(true);
            aViewHolder.nameInput.setError(getString(R.string.input_name_error));
            return false;
        }
        if(aViewHolder.editLogin.getText().toString().equals("")){
            aViewHolder.loginInput.setErrorEnabled(true);
            aViewHolder.loginInput.setError(getString(R.string.input_login_error));
            return false;
        }
        if(aViewHolder.editPassword.getText().toString().equals("")){
            aViewHolder.passwordInput.setErrorEnabled(true);
            aViewHolder.passwordInput.setError(getString(R.string.input_password_error));
            return false;
        }
        return true;
    }
    
    private void addNewAccountFromDialog(){
        if(checkAccDialogFilling()) {
            closeDialog();
            presenter.addNewAccount(new Account(
                    presenter.getAccountCount(),
                    aViewHolder.editName.getText().toString(),
                    aViewHolder.editLogin.getText().toString(),
                    aViewHolder.editPassword.getText().toString()));
        }
    }

    private void editAccountFromDialog(Account account){
        if(checkAccDialogFilling()) {
            closeDialog();
            account.setName(aViewHolder.editName.getText().toString());
            account.setLogin(aViewHolder.editLogin.getText().toString());
            account.setPassword(aViewHolder.editPassword.getText().toString());
            presenter.editAccount(account);
        }
    }

    //TODO to separate class
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    presenter = new MainPresenter(this, new AndroidInterface(this));
                    presenter.onStart();
                } else {
                    Toast.makeText(MainActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
                    checkAndRequestPermissions();
                }
            }
        }
    }

    //TODO to separate class
    public static boolean writeExternalPermGranted(Context context) {
        return (context.checkCallingOrSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
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
        clearAddAccDialog();
        aViewHolder.editName.setText(account.getName());
        aViewHolder.editLogin.setText(account.getLogin());
        aViewHolder.editPassword.setText(account.getPassword());
        setOnDialogOkClick(new AddAccDialogListener() {
            @Override
            public void onOkClick() {
                editAccountFromDialog(account);
            }
        });
        showDialog(aViewHolder.main);
    }

    //TODO Edit for password input
    @Override
    public void onBackPressed() {
        if(dialogOpened()) closeDialog();
        else super.onBackPressed();
    }

}
