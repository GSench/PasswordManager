package ru.gsench.passwordmanager.presentation.view.aview;

import android.text.Editable;
import android.view.View;
import android.view.ViewGroup;

import ru.gsench.passwordmanager.R;
import ru.gsench.passwordmanager.presentation.presenter.EditAccountPresenter;
import ru.gsench.passwordmanager.presentation.utils.AView;
import ru.gsench.passwordmanager.presentation.utils.AViewContainer;
import ru.gsench.passwordmanager.presentation.utils.MyTextWatcher;
import ru.gsench.passwordmanager.presentation.view.EditAccountView;
import ru.gsench.passwordmanager.presentation.viewholder.EditAccountViewHolder;

/**
 * Created by Григорий Сенченок on 10.03.2017.
 */

public class EditAccountAView extends AView implements EditAccountView {

    //TODO Rich random settings

    public EditAccountViewHolder aViewHolder;
    private EditAccountPresenter presenter;

    public EditAccountAView(AViewContainer container, EditAccountPresenter presenter){
        super(container);
        aViewHolder = new EditAccountViewHolder(context, parent);
        this.presenter=presenter;
        presenter.setView(this);
    }

    @Override
    public void init() {
        aViewHolder.randomPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onRandomPWBtn();
            }
        });
        aViewHolder.randomPINCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onRandomPINBtn();
            }
        });
        aViewHolder.randomLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onRandomLoginBtn();
            }
        });
        aViewHolder.editName.addTextChangedListener(new MyTextWatcher(){
            @Override
            public void afterTextChanged(Editable editable) {
                presenter.onNameInput();
            }
        });
        aViewHolder.editLogin.addTextChangedListener(new MyTextWatcher(){
            @Override
            public void afterTextChanged(Editable editable) {
                presenter.onLoginInput();
            }
        });
        aViewHolder.editPassword.addTextChangedListener(new MyTextWatcher(){
            @Override
            public void afterTextChanged(Editable editable) {
                presenter.onPWInput();
            }
        });
        aViewHolder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onCancelBtn();
            }
        });
        aViewHolder.ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onOKBtn();
            }
        });
    }

    @Override
    public void closeView() {
        closeSelf();
    }

    @Override
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

    @Override
    public String getName() {
        return aViewHolder.editName.getText().toString();
    }

    @Override
    public String getLogin() {
        return aViewHolder.editLogin.getText().toString();
    }

    @Override
    public String getPassword() {
        return aViewHolder.editPassword.getText().toString();
    }

    @Override
    public void setName(String name) {
        aViewHolder.editName.setText(name);
    }

    @Override
    public void setLogin(String login) {
        aViewHolder.editLogin.setText(login);
    }

    @Override
    public void setPassword(String pw) {
        aViewHolder.editPassword.setText(pw);
    }

    @Override
    public ViewGroup getView(){
        return aViewHolder.main;
    }

    @Override
    protected void start() {
        presenter.start();
    }

    @Override
    public void setNameError() {
        aViewHolder.nameInput.setErrorEnabled(true);
        aViewHolder.nameInput.setError(context.getString(R.string.input_name_error));
    }

    @Override
    public void setLoginError() {
        aViewHolder.loginInput.setErrorEnabled(true);
        aViewHolder.loginInput.setError(context.getString(R.string.input_login_error));
    }

    @Override
    public void setPasswordError() {
        aViewHolder.passwordInput.setErrorEnabled(true);
        aViewHolder.passwordInput.setError(context.getString(R.string.input_password_error));
    }

    @Override
    public void removeNameError() {
        aViewHolder.nameInput.setErrorEnabled(false);
        aViewHolder.nameInput.setError(null);
    }

    @Override
    public void removeLoginError() {
        aViewHolder.loginInput.setErrorEnabled(false);
        aViewHolder.loginInput.setError(null);
    }

    @Override
    public void removePasswordError() {
        aViewHolder.passwordInput.setErrorEnabled(false);
        aViewHolder.passwordInput.setError(null);
    }

}
