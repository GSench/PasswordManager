package ru.gsench.passwordmanager.presentation.view;

/**
 * Created by grish on 26.03.2017.
 */

public interface SelectBaseView {

    public void init();
    public void openRequestFileDialog();
    public void openRequestDirDialog();
    public void closeView();
    public void showUnableToEditBaseFileToast();

}
