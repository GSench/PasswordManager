package ru.gsench.passwordmanager.presentation.view;

/**
 * Created by Григорий Сенченок on 29.03.2017.
 */

public interface EditAccountView {

    public void init();
    public void closeView();
    public void clearAddAccDialog();
    public String getName();
    public String getLogin();
    public String getPassword();
    public void setName(String name);
    public void setLogin(String login);
    public void setPassword(String pw);
    public void setNameError();
    public void setLoginError();
    public void setPasswordError();
    public void removeNameError();
    public void removeLoginError();
    public void removePasswordError();

}
