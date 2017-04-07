package ru.gsench.passwordmanager.presentation.view;

import ru.gsench.passwordmanager.presentation.presenter.KeyInputPresenter;
import ru.gsench.passwordmanager.presentation.presenter.NewKeyPresenter;
import ru.gsench.passwordmanager.presentation.presenter.NewPINPresenter;
import ru.gsench.passwordmanager.presentation.presenter.PINInputPresenter;

/**
 * Created by Григорий Сенченок on 07.04.2017.
 */

public interface SettingsView {

    public void init();
    public void exit();
    public void newKeyView(NewKeyPresenter presenter);
    public void newPINView(NewPINPresenter presenter);
    public void openPINInputView(PINInputPresenter presenter);
    public void keyInputView(KeyInputPresenter presenter);
    public boolean isViewOpened();
    public void closeCurrentView();
    public void onSaveBaseErrorMsg();
}
