package ru.gsench.passwordmanager.presentation.presenter;

import ru.gsench.passwordmanager.domain.interactor.KeyInputUseCase;
import ru.gsench.passwordmanager.presentation.view.KeyInputView;

/**
 * Created by grish on 26.03.2017.
 */

public class KeyInputPresenter {

    private KeyInputUseCase interactor;
    private KeyInputView view;

    public KeyInputPresenter(KeyInputUseCase interactor, KeyInputView view){
        this.interactor=interactor;
        this.view=view;
    }

    public void start(){
        view.init();
    }

    public void onEnter(){
        interactor.onKeyInput(view.getKeyInput(), this);
    }

    public void onIncorrectKeyInput(){
        view.showIncorrectKeyToast();
    }

    public void unableToParseBase(){
        view.showUnableToParseBaseToast();
    }

    public void unexpectedException(){
        view.showUnexpectedExceptionToast();
    }

    public void onCorrectKeyInput(){
    }

}
