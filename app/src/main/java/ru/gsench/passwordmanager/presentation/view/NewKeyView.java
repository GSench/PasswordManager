package ru.gsench.passwordmanager.presentation.view;

/**
 * Created by grish on 26.03.2017.
 */

public interface NewKeyView {

    public void init();
    public void closeView();
    public String getKeyInput();
    public void setConfirmKeyMsg();
    public void setKeysNotEqualMsg();
    public void clearKeyInput();

}
