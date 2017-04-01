package ru.gsench.passwordmanager.presentation.presenter;

import ru.gsench.passwordmanager.domain.interactor.PINInputUseCase;
import ru.gsench.passwordmanager.domain.utils.function;
import ru.gsench.passwordmanager.presentation.view.PINInputView;

/**
 * Created by Григорий Сенченок on 29.03.2017.
 */

public class PINInputPresenter {

    private PINInputUseCase interactor;

    public void setView(PINInputView view) {
        this.view = view;
    }

    private PINInputView view;

    public PINInputPresenter(PINInputUseCase interactor){
        this.interactor=interactor;
    }

    public void start(){
        view.init();
        long block = interactor.isPINBlocked();
        if(block>0) blockPINFor(block);
    }

    public void onPINInput(String pin){
        view.resetPINInput();
        long block = interactor.isPINBlocked();
        if (block > 0){
            blockPINFor(block);
            return;
        }
        interactor.onPINInput(pin);
    }

    public void onResetBtn(){
        view.showResetPINConfirmDialog();
    }

    public void onResetConfirmBtn(){
        interactor.onResetPIN();
    }

    private void blockPINFor(final long time){
        interactor.countDownTime(time, 1000,
                new function() {
                    @Override
                    public void run(String... params) {
                        view.setRetryLockMsg((int) (Long.valueOf(params[0])/1000));
                    }
                },
                new function() {
                    @Override
                    public void run(String... params) {
                        view.removeMsg();
                        view.unlockPIN();
                    }
                });
        view.lockPIN();
        view.setRetryLockMsg((int) (time/1000));
    }

}
