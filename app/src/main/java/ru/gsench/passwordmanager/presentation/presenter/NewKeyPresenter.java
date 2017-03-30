package ru.gsench.passwordmanager.presentation.presenter;

import ru.gsench.passwordmanager.domain.interactor.MainInteractor;
import ru.gsench.passwordmanager.presentation.view.NewKeyView;

/**
 * Created by grish on 26.03.2017.
 */

public class NewKeyPresenter {

    private MainInteractor interactor;
    private NewKeyView view;

    private String prevKey;

    public NewKeyPresenter(MainInteractor interactor, NewKeyView view) {
        this.interactor=interactor;
        this.view=view;
    }

    public void onStart() {
        view.init();
    }

    public void onEnter() {
        String key = view.getKeyInput();
        if(prevKey !=null){
            if(prevKey.equals(key)){
                interactor.afterNewKeyInput(key);
            } else {
                view.clearKeyInput();
                view.setKeysNotEqualMsg();
                prevKey = null;
            }
        } else {
            view.clearKeyInput();
            view.setConfirmKeyMsg();
            prevKey = key;
        }
    }
}
