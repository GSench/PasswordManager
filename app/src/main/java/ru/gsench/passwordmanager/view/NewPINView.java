package ru.gsench.passwordmanager.view;

/**
 * Created by Григорий Сенченок on 29.03.2017.
 */

public interface NewPINView {

    public void init();
    public void closeView();
    public void resetPINInput();
    public void createPINMsg();
    public void confirmPINMsg();
    public void PINsNotEqualMsg();

}
