package ru.gsench.passwordmanager;

import android.content.Context;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * Created by grish on 01.03.2017.
 */

public class AddAccountViewHolder {

    public LinearLayout main;
    public TextInputEditText editName;
    public TextInputEditText editLogin;
    public TextInputEditText editPassword;
    public Button randomPassword;
    public Button randomPINCode;
    public Button randomLogin;
    public TextInputLayout nameInput;
    public TextInputLayout loginInput;
    public TextInputLayout passwordInput;

    public AddAccountViewHolder(Context context, ViewGroup parent){
        main = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.new_account, parent, false);
        editName = (TextInputEditText) main.findViewById(R.id.edit_name);
        editLogin = (TextInputEditText) main.findViewById(R.id.edit_login);
        editPassword = (TextInputEditText) main.findViewById(R.id.edit_password);
        randomPassword = (Button) main.findViewById(R.id.generate_random);
        randomPINCode = (Button) main.findViewById(R.id.generate_random_pin);
        randomLogin = (Button) main.findViewById(R.id.random_login);
        nameInput = (TextInputLayout) main.findViewById(R.id.name_input);
        loginInput = (TextInputLayout) main.findViewById(R.id.login_input);
        passwordInput = (TextInputLayout) main.findViewById(R.id.password_input);
    }
}
