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

    public void setView(CoordinatorView view) {
        this.view = view;
    }

    private CoordinatorView view;

    public CoordinatorPresenter(SystemInterface systemInterface){
        interactor=new MainInteractor(this, systemInterface);
    }

    public void start(){
        view.init();
        interactor.selectBase();
    }

    public void selectBaseView() {
        view.selectBaseView(new SelectBasePresenter(interactor));
    }

    public void closeCurrentView() {
        view.closeCurrentView();
    }

    public void keyInputView() {
        view.keyInputView(new KeyInputPresenter(interactor));
    }

    public void newKeyView() {
        view.newKeyView(new NewKeyPresenter(interactor));
    }

    public void newPINView(){
        view.newPINView(new NewPINPresenter(interactor));
    }

    public void openPINView() {
        view.openPINInputView(new PINInputPresenter(interactor));
    }

    public void openAccountList(){
        view.openAccountList(new AccountListPresenter(interactor));
    }

    public void editAccountView(Account account){
        view.editAccountView(new EditAccountPresenter(interactor, account));
    }

    public void onBackPressed() {
        if(view.isViewOpened()) view.closeCurrentView();
        else view.exit();
    }
}
