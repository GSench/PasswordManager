package ru.gsench.passwordmanager.presenter;

import android.os.CountDownTimer;
import android.view.View;

import java.util.concurrent.TimeUnit;

import interactor.MainInteractor;
import ru.gsench.passwordmanager.R;
import ru.gsench.passwordmanager.view.PINInputView;
import utils.function;

/**
 * Created by Григорий Сенченок on 29.03.2017.
 */

public class PINInputPresenter {

    private MainInteractor interactor;
    private PINInputView view;

    public PINInputPresenter(MainInteractor interactor, PINInputView view){
        this.interactor=interactor;
        this.view=view;
    }

    public void start(){
        view.init();
        long block = interactor.isPINBlocked();
        if(block>0) blockPINFor(block);
    }

    public void onPINInput(String pin){
        view.resetPINInput();
        try {
            interactor.onPINInput(pin);
        } catch (MainInteractor.BlockPINException e) {
        }
        long block = interactor.isPINBlocked();
        if (block > 0) blockPINFor(block);
    }

    public void onResetBtn(){
        view.showResetPINConfirmDialog();
    }

    public void onResetConfirmBtn(){
        interactor.onResetPINBtn();
    }

    private void blockPINFor(final long time){
        interactor.system.countDown(time, 1000,
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
