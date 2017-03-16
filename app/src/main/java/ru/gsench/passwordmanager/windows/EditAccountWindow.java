package ru.gsench.passwordmanager.windows;

import android.content.Context;
import android.text.Editable;
import android.view.View;
import android.view.ViewGroup;

import account_system.Account;
import ru.gsench.passwordmanager.R;
import utils.MyTextWatcher;
import utils.RandomPassword;
import utils.function;

import static ru.gsench.passwordmanager.MainActivity.APP_PREFERENCES;

/**
 * Created by Григорий Сенченок on 10.03.2017.
 */

public class EditAccountWindow {

    private static final String FULLY_RANDOM = "fully_random";
    private static final String WITHOUT_SYM = "without_sym";

    private Context context;
    public EditAccountViewHolder aViewHolder;
    private int currentID = -1;

    public EditAccountWindow(Context context, ViewGroup parent, final function onClose){
        this.context=context;
        aViewHolder = new EditAccountViewHolder(context, parent);
        updateRandomBtnsWithPref();
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
                onClose.run();
            }
        });
    }

    public void updateRandomBtnsWithPref(){
        aViewHolder.randomPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aViewHolder.editPassword.setText(RandomPassword.getRandomPassword(fullyRandomPref(), withoutSymPref()));
            }
        });
    }

    private boolean fullyRandomPref(){
        return context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE).getBoolean(FULLY_RANDOM, false);
    }

    private boolean withoutSymPref(){
        return context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE).getBoolean(WITHOUT_SYM, false);
    }

    public void setOnDialogOkClick(final function action){
        aViewHolder.ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                action.run();
            }
        });
    }

    public void clearAddAccDialog(){
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

    public boolean checkAccDialogFilling(){
        if(aViewHolder.editName.getText().toString().equals("")){
            aViewHolder.nameInput.setErrorEnabled(true);
            aViewHolder.nameInput.setError(context.getString(R.string.input_name_error));
            return false;
        }
        if(aViewHolder.editLogin.getText().toString().equals("")){
            aViewHolder.loginInput.setErrorEnabled(true);
            aViewHolder.loginInput.setError(context.getString(R.string.input_login_error));
            return false;
        }
        if(aViewHolder.editPassword.getText().toString().equals("")){
            aViewHolder.passwordInput.setErrorEnabled(true);
            aViewHolder.passwordInput.setError(context.getString(R.string.input_password_error));
            return false;
        }
        return true;
    }

    public ViewGroup getView(){
        return aViewHolder.main;
    }

    public Account getAccount(){
        return new Account(
                currentID,
                aViewHolder.editName.getText().toString(),
                aViewHolder.editLogin.getText().toString(),
                aViewHolder.editPassword.getText().toString()
        );
    }

    public void setAccount(Account account){
        currentID=account.getId();
        aViewHolder.editName.setText(account.getName());
        aViewHolder.editLogin.setText(account.getLogin());
        aViewHolder.editPassword.setText(account.getPassword());
    }

}
