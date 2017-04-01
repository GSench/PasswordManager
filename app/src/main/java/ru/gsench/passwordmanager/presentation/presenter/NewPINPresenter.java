package ru.gsench.passwordmanager.presentation.presenter;

import ru.gsench.passwordmanager.domain.interactor.NewPINUseCase;
import ru.gsench.passwordmanager.presentation.view.NewPINView;

/**
 * Created by Григорий Сенченок on 29.03.2017.
 */

public class NewPINPresenter {

    private NewPINUseCase interactor;
    private NewPINView view;

    private String prevPIN = null;

    public NewPINPresenter(NewPINUseCase interactor, NewPINView view){
        this.interactor=interactor;
        this.view=view;
    }

    public void start(){
        view.hideView();
        view.openAskPINDialog();
    }

    public void onConfirmPINCreation(){
        view.showView();
        view.init();
        view.createPINMsg();
    }

    public void onCancelPINCreation(){
        view.closeView();
    }

    public void onPINInput(String pin) {
        view.resetPINInput();
        if(prevPIN!=null){
            if(prevPIN.equals(pin)){
                view.closeView();
                interactor.onNewPIN(pin);
            } else {
                view.PINsNotEqualMsg();
                prevPIN = null;
            }
        } else {
            view.confirmPINMsg();
            prevPIN = pin;
        }
    }

    public void onResetBtn() {
        view.closeView();
    }
}
