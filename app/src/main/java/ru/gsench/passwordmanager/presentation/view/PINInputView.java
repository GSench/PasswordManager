package ru.gsench.passwordmanager.presentation.view;

/**
 * Created by Григорий Сенченок on 29.03.2017.
 */

public interface PINInputView {

    public void init();
    public void closeView();
    public void resetPINInput();
    public void lockPIN();
    public void unlockPIN();
    public void setRetryLockMsg(int secLeft);
    public void removeMsg();
    public void showResetPINConfirmDialog();

}
