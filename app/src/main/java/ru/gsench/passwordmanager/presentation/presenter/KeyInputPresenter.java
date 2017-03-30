package ru.gsench.passwordmanager.presentation.presenter;

import ru.gsench.passwordmanager.domain.interactor.MainInteractor;
import ru.gsench.passwordmanager.presentation.view.KeyInputView;
import ru.gsench.passwordmanager.domain.utils.function;

/**
 * Created by grish on 26.03.2017.
 */

public class KeyInputPresenter {

    private MainInteractor interactor;
    private KeyInputView view;

    public KeyInputPresenter(MainInteractor interactor, KeyInputView view){
        this.interactor=interactor;
        this.view=view;
    }

    public void start(){
        view.init();
    }

    public void onEnter(){
        interactor.onKeyInput(view.getKeyInput(),
                new function() {
                    @Override
                    public void run(String... params) {
                        view.closeView();
                    }
                }, //Correct
                new function() {
                    @Override
                    public void run(String... params) {
                        view.showIncorrectKeyToast();
                    }
                });  //Incorrect
    }

}