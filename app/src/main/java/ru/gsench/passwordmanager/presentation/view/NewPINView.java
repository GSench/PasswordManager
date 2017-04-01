package ru.gsench.passwordmanager.presentation.view;

/**
 * Created by Григорий Сенченок on 29.03.2017.
 */

public interface NewPINView {

    public void init();
    public void closeView();
    public void hideView();
    public void showView();
    public void openAskPINDialog();
    public void resetPINInput();
    public void createPINMsg();
    public void confirmPINMsg();
    public void PINsNotEqualMsg();

}
