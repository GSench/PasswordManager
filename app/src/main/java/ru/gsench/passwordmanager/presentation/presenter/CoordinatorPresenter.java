package ru.gsench.passwordmanager.presentation.presenter;

import ru.gsench.passwordmanager.domain.SystemInterface;
import ru.gsench.passwordmanager.domain.account_system.Account;
import ru.gsench.passwordmanager.domain.interactor.MainInteractor;
import ru.gsench.passwordmanager.presentation.view.CoordinatorView;

/**
 * Created by grish on 30.03.2017.
 */

public class CoordinatorPresenter {

    private MainInteractor interactor;
    private CoordinatorView view;

    public CoordinatorPresenter(CoordinatorView view, SystemInterface systemInterface){
        interactor=new MainInteractor(this, systemInterface);
        this.view=view;
    }

    public void start(){
        view.init();
        interactor.selectBase();
    }

    public void selectBaseView() {
        view.selectBaseView(interactor);
    }

    public void closeCurrentView() {
        view.closeCurrentView();
    }

    public void keyInputView() {
        view.keyInputView(interactor);
    }

    public void newKeyView() {
        view.newKeyView(interactor);
    }

    public void newPINView(){
        view.newPINView(interactor);
    }

    public void openPINView() {
        view.openPINInputView(interactor);
    }

    public void openAccountList(){
        view.openAccountList(interactor);
    }

    public void editAccountView(Account account){
        view.editAccountView(interactor, account);
    }

    public void onBackPressed() {
        view.exit();
    }
}
