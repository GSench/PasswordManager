package ru.gsench.passwordmanager.presentation.view;

import ru.gsench.passwordmanager.domain.account_system.Account;
import ru.gsench.passwordmanager.domain.interactor.AccountListUseCase;
import ru.gsench.passwordmanager.domain.interactor.EditAccountUseCase;
import ru.gsench.passwordmanager.domain.interactor.KeyInputUseCase;
import ru.gsench.passwordmanager.domain.interactor.NewKeyUseCase;
import ru.gsench.passwordmanager.domain.interactor.NewPINUseCase;
import ru.gsench.passwordmanager.domain.interactor.PINInputUseCase;
import ru.gsench.passwordmanager.domain.interactor.SelectBaseUseCase;

/**
 * Created by grish on 26.02.2017.
 */

public interface CoordinatorView {

    public void init();
    public void exit();
    public void openAccountList(AccountListUseCase interactor);
    public void keyInputView(KeyInputUseCase interactor);
    public void selectBaseView(SelectBaseUseCase interactor);
    public void newKeyView(NewKeyUseCase interactor);
    public void openPINInputView(PINInputUseCase interactor);
    public void editAccountView(EditAccountUseCase interactor, Account account);
    public void newPINView(NewPINUseCase interactor);
    public void closeCurrentView();
}
