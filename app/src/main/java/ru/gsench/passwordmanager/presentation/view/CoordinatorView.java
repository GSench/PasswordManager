package ru.gsench.passwordmanager.presentation.view;

import ru.gsench.passwordmanager.presentation.presenter.AccountListPresenter;
import ru.gsench.passwordmanager.presentation.presenter.EditAccountPresenter;
import ru.gsench.passwordmanager.presentation.presenter.KeyInputPresenter;
import ru.gsench.passwordmanager.presentation.presenter.NewKeyPresenter;
import ru.gsench.passwordmanager.presentation.presenter.NewPINPresenter;
import ru.gsench.passwordmanager.presentation.presenter.PINInputPresenter;
import ru.gsench.passwordmanager.presentation.presenter.SelectBasePresenter;

/**
 * Created by grish on 26.02.2017.
 */

public interface CoordinatorView {

    public void init();
    public void exit();
    public void openAccountList(AccountListPresenter presenter);
    public void keyInputView(KeyInputPresenter presenter);
    public void selectBaseView(SelectBasePresenter presenter);
    public void newKeyView(NewKeyPresenter presenter);
    public void openPINInputView(PINInputPresenter presenter);
    public void editAccountView(EditAccountPresenter presenter);
    public void newPINView(NewPINPresenter presenter);
    public boolean isViewOpened();
    public void closeCurrentView();
}
