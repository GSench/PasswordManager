package ru.gsench.passwordmanager.presentation.presenter;

import ru.gsench.passwordmanager.domain.account_system.Account;
import ru.gsench.passwordmanager.domain.interactor.MainInteractor;
import ru.gsench.passwordmanager.presentation.utils.RandomPassword;
import ru.gsench.passwordmanager.presentation.view.EditAccountView;

/**
 * Created by Григорий Сенченок on 29.03.2017.
 */

public class EditAccountPresenter {

    private static final String FULLY_RANDOM = "fully_random";
    private static final String WITHOUT_SYM = "without_sym";

    private MainInteractor interactor;
    private EditAccountView view;

    private Account account;

    public EditAccountPresenter(MainInteractor interactor, EditAccountView view, Account account){
        this.interactor=interactor;
        this.view=view;
        this.account=account;
    }

    public void start(){
        view.init();
        if(account!=null){
            view.setName(account.getName());
            view.setLogin(account.getLogin());
            view.setPassword(account.getPassword());
        }
    }

    public void onRandomPWBtn(){
        view.setPassword(RandomPassword.getRandomPassword(fullyRandomPref(), withoutSymPref()));
    }

    public void onRandomPINBtn(){
        view.setPassword(RandomPassword.getRandomPINCode());
    }

    public void onRandomLoginBtn(){
        view.setLogin(RandomPassword.getRandomLogin());
    }

    public void onNameInput(){
        view.removeNameError();
    }

    public void onLoginInput(){
        view.removeLoginError();
    }

    public void onPWInput(){
        view.removePasswordError();
    }

    public void onCancelBtn() {
        view.closeView();
    }

    private boolean fullyRandomPref(){
        return interactor.system.getSavedBoolean(FULLY_RANDOM, false);
    }

    private boolean withoutSymPref(){
        return interactor.system.getSavedBoolean(WITHOUT_SYM, false);
    }

    public void onOKBtn() {
        boolean correct = true;
        if(view.getName().equals("")){
            view.setNameError();
            correct=false;
        }
        if(view.getLogin().equals("")){
            view.setLoginError();
            correct=false;
        }
        if(view.getPassword().equals("")){
            view.setPasswordError();
            correct=false;
        }
        if(!correct) return;
        view.closeView();
        interactor.editAccount(new Account(
                account!=null ? account.getId() : interactor.getAccountsCount(),
                view.getName(),
                view.getLogin(),
                view.getPassword()
        ));
    }
}
