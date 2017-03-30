package ru.gsench.passwordmanager.presentation.view;

/**
 * Created by grish on 26.03.2017.
 */

public interface KeyInputView {

    public void init();
    public void closeView();
    public String getKeyInput();
    public void showIncorrectKeyToast();

}
