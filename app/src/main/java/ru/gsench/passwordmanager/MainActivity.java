package ru.gsench.passwordmanager;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import account_system.Account;
import account_system.AccountSystem;
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
    AlertDialog addNewAccDialog;


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

    private void setupAddAccDialog(){
        aViewHolder = new AddAccountViewHolder(this, viewHolder.main);
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
        addNewAccDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.add_new_account)
                .setView(aViewHolder.main)
                .setPositiveButton(R.string.ok, null)
                .setNeutralButton(R.string.cancel, null)
                .create();
        addNewAccDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                addNewAccDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onDialogOkButtonClick();
                    }
                });
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
            accountListAdapter=new AccountListAdapter(this, R.layout.account, accounts);
            viewHolder.accountList.setAdapter(accountListAdapter);
        } else {
            accountListAdapter.notifyDataSetChanged();
        }
    }

    //TODO Password dialog
    //TODO with custom keyboard
    //TODO with QR-code scanner
    @Override
    public void openPasswordDialog() {
        final EditText editText = new EditText(this);
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle("Input Key Phrase")
                .setView(editText)
                .setPositiveButton("Enter", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(presenter.isKeyPhraseCorrect(editText.getText().toString())){
                            dialogInterface.cancel();
                            presenter.onCorrectKeyPhraseInput();
                        } else Toast.makeText(MainActivity.this, "Incorrect key phrase", Toast.LENGTH_SHORT).show();
                    }
                })
                .create()
                .show();
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

    public void onAddClick(View v){
        aViewHolder.nameInput.setErrorEnabled(false);
        aViewHolder.nameInput.setError(null);
        aViewHolder.loginInput.setErrorEnabled(false);
        aViewHolder.loginInput.setError(null);
        aViewHolder.passwordInput.setErrorEnabled(false);
        aViewHolder.passwordInput.setError(null);
        aViewHolder.editName.setText("");
        aViewHolder.editLogin.setText("");
        aViewHolder.editPassword.setText("");
        addNewAccDialog.show();
    }
    
    private void onDialogOkButtonClick(){
        boolean ok = true;
        if(aViewHolder.editName.getText().toString().equals("")){
            aViewHolder.nameInput.setErrorEnabled(true);
            aViewHolder.nameInput.setError(getString(R.string.input_name_error));
            ok=false;
        }
        if(aViewHolder.editLogin.getText().toString().equals("")){
            aViewHolder.loginInput.setErrorEnabled(true);
            aViewHolder.loginInput.setError(getString(R.string.input_login_error));
            ok=false;
        }
        if(aViewHolder.editPassword.getText().toString().equals("")){
            aViewHolder.passwordInput.setErrorEnabled(true);
            aViewHolder.passwordInput.setError(getString(R.string.input_password_error));
            ok=false;
        }
        if(ok) {
            aViewHolder.nameInput.setErrorEnabled(false);
            aViewHolder.nameInput.setError(null);
            aViewHolder.loginInput.setErrorEnabled(false);
            aViewHolder.loginInput.setError(null);
            aViewHolder.passwordInput.setErrorEnabled(false);
            aViewHolder.passwordInput.setError(null);
            addNewAccDialog.cancel();
            presenter.addNewAccount(new Account(
                    presenter.getAccountCount(),
                    aViewHolder.editName.getText().toString(),
                    aViewHolder.editLogin.getText().toString(),
                    aViewHolder.editPassword.getText().toString()));
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

    //TODO editing accounts
    private class AccountListAdapter extends ArrayAdapter<Account> {

        private AccountSystem accounts;
        private Context context;

        public AccountListAdapter(Context context, int resource, AccountSystem accounts) {
            super(context, resource, accounts.getAccounts());
            this.accounts=accounts;
            this.context=context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final Account current = accounts.getAccount(position);
            if(convertView==null){
                convertView= LayoutInflater.from(context).inflate(R.layout.account, parent, false);
            }
            TextView name = (TextView) convertView.findViewById(R.id.name);
            TextView login = (TextView) convertView.findViewById(R.id.login);
            TextView password = (TextView) convertView.findViewById(R.id.password);
            name.setText(current.getName());
            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(isStringUrl(current.getName()))
                    try {
                        openURL(toURL(current.getName()));
                    }
                    catch (Throwable e){
                    }
                }
            });
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    androidInterface.copyText(current.getLogin());
                    Toast.makeText(MainActivity.this, getString(R.string.login_copied, current.getLogin()), Toast.LENGTH_SHORT).show();
                }
            });
            password.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    androidInterface.copyText(current.getPassword());
                    Toast.makeText(MainActivity.this, R.string.password_copied, Toast.LENGTH_SHORT).show();
                }
            });
            login.setText(current.getLogin());
            password.setText(current.getPassword());
            ((Button)(convertView.findViewById(R.id.delete_btn))).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    confirmDeleteDialog(current);
                }
            });
            return convertView;
        }

    }

    //TODO to separate class
    private void openURL(String url){
        Intent intent= new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    //TODO to separate class
    public boolean isStringUrl(String string){
        Pattern p = Pattern.compile("^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$");
        Matcher m = p.matcher(string);
        return m.find();
    }

    //TODO to separate class
    public String toURL(String str){
        return str.startsWith("http") ? str : "http://"+str;
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

}
