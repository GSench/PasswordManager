package ru.gsench.passwordmanager.domain.interactor;

import java.io.IOException;

import ru.gsench.passwordmanager.domain.SystemInterface;
import ru.gsench.passwordmanager.domain.account_system.AccountSystem;
import ru.gsench.passwordmanager.domain.utils.function;
import ru.gsench.passwordmanager.presentation.presenter.KeyInputPresenter;
import ru.gsench.passwordmanager.presentation.presenter.SettingsPresenter;

import static ru.gsench.passwordmanager.domain.interactor.PINInteractor.PIN;

/**
 * Created by Григорий Сенченок on 07.04.2017.
 */

public class SettingsInteractor implements NewKeyUseCase, NewPINUseCase, KeyInputUseCase, PINInputUseCase {

    private SystemInterface system;
    private SettingsPresenter presenter;
    private AccountSystem accountSystem;

    public SettingsInteractor(SettingsPresenter presenter, SystemInterface system){
        this.system=system;
        this.presenter=presenter;
    }

    public void selectBase(){
        String basePath = AccountBaseInteractor.getAccountBasePath(system);
        if(basePath==null){
            presenter.exit();
            return;
        }
        onBaseSelected();
    }

    private void onBaseSelected(){
        try {
            accountSystem = AccountBaseInteractor.getAccountSystem(system);
        } catch (IOException e) {
            presenter.exit();
            return;
        } catch (AccountBaseInteractor.EmptyBaseException e) {
            presenter.exit();
            return;
        }
        if(system.getSavedString(PIN, null)!=null)
            presenter.openPINView();
        else
            presenter.keyInputView();
    }

    public void resetBase(){
        AccountBaseInteractor.resetBase(system);
        PINInteractor.resetPIN(system);
        presenter.exit();
    }

    @Override
    public void afterNewKeyInput(String newKey) {
        accountSystem.setKey(newKey);
        function error = new function() {
            @Override
            public void run(String... params) {
                presenter.onSaveBaseError();
            }
        };
        AccountBaseInteractor.saveAccountBase(system, accountSystem, error, error);
    }

    @Override
    public void onNewPIN(String pin) {
        PINInteractor.onNewPIN(system, pin, accountSystem.getKey());
    }

    @Override
    public void onKeyInput(String key, KeyInputPresenter presenter) {

    }

    private void inputCorrectKey(final String key, final function onException){
        system.doOnBackground(new function() {
            @Override
            public void run(String... params) {
                try {
                    accountSystem.initSystem(key);
                } catch (Exception e) {
                    system.doOnForeground(new function() {
                        @Override
                        public void run(String... params) {
                            onException.run();
                        }
                    });
                    return;
                }
                system.doOnForeground(new function() {
                    @Override
                    public void run(String... params) {
                        presenter.closeView();
                    }
                });
            }
        });
    }

    @Override
    public void onResetPIN(){
        PINInteractor.resetPIN(system);
        presenter.closeView();
        presenter.keyInputView();
    }

    @Override
    public void onPINInput(final String pin) {
        PINInteractor.onPINInput(system, pin, new function() {
            @Override
            public void run(String... params) {
                inputCorrectKey(params[0], null);
            }
        });
    }

    //TODO Make secure
    @Override
    public long isPINBlocked(){
        return PINInteractor.isPINBlocked(system);
    }

    @Override
    public void countDownTime(long time, long interval, function onTick, function onFinish) {
        system.countDown(time, interval, onTick, onFinish);
    }

}
