package ru.gsench.passwordmanager.presentation.presenter;

import ru.gsench.passwordmanager.domain.SystemInterface;
import ru.gsench.passwordmanager.domain.interactor.SettingsInteractor;
import ru.gsench.passwordmanager.presentation.view.SettingsView;

/**
 * Created by Григорий Сенченок on 07.04.2017.
 */

public class SettingsPresenter {

    private SettingsInteractor interactor;

    private boolean closeOnBackPressed = true;

    public void setView(SettingsView view) {
        this.view = view;
    }

    private SettingsView view;

    public SettingsPresenter(SystemInterface systemInterface){
        interactor=new SettingsInteractor(this, systemInterface);
    }

    public void start(){
        view.init();
        interactor.selectBase();
    }

    public void keyInputView() {
        view.keyInputView(new KeyInputPresenter(interactor));
        closeOnBackPressed=false;
    }

    public void openPINView() {
        view.openPINInputView(new PINInputPresenter(interactor));
        closeOnBackPressed=false;
    }

    public void onSaveBaseError(){
        view.onSaveBaseErrorMsg();
    }

    public void onBasePrefBtn(){
        view.changeBaseConfirmDialog();
    }

    public void onPINPrefBtn(){
        view.newPINView(new NewPINPresenter(interactor));
        closeOnBackPressed = true;
    }

    public void onKeyPrefBtn(){
        view.changeKeyConfirmDialog();
    }

    public void closeView(){
        view.closeCurrentView();
    }

    public void onBackPressed(){
        if(view.isViewOpened()&&closeOnBackPressed) view.closeCurrentView();
        else view.exit();
    }

    public void exit(){
        view.exit();
    }

    public void onChangeBaseConfirm() {
        interactor.resetBase();
        view.exit();
    }

    public void onChangeKeyConfirm() {
        view.newKeyView(new NewKeyPresenter(interactor));
        closeOnBackPressed = true;
    }
}
