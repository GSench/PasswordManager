package ru.gsench.passwordmanager.presenter;

import interactor.MainInteractor;
import ru.gsench.passwordmanager.R;
import ru.gsench.passwordmanager.view.NewPINView;

/**
 * Created by Григорий Сенченок on 29.03.2017.
 */

public class NewPINPresenter {

    private MainInteractor interactor;
    private NewPINView view;

    private String prevPIN = null;

    public NewPINPresenter(MainInteractor interactor, NewPINView view){
        this.interactor=interactor;
        this.view=view;
    }

    public void start(){
        view.init();
        view.createPINMsg();
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
