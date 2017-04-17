package ru.gsench.passwordmanager.domain.interactor;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.xml.parsers.ParserConfigurationException;

import ru.gsench.passwordmanager.domain.SystemInterface;
import ru.gsench.passwordmanager.domain.account_system.AccountSystem;
import ru.gsench.passwordmanager.domain.usecase.KeyInputUseCase;
import ru.gsench.passwordmanager.domain.usecase.NewKeyUseCase;
import ru.gsench.passwordmanager.domain.usecase.NewPINUseCase;
import ru.gsench.passwordmanager.domain.usecase.PINInputUseCase;
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
        } catch (IOException | AccountBaseInteractor.EmptyBaseException e) {
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
        final boolean[] ret = new boolean[]{false};
        function error = new function() {
            @Override
            public void run(String... params) {
                presenter.onSaveBaseError();
                ret[0]=true;
            }
        };
        AccountBaseInteractor.saveAccountBase(system, accountSystem, error, error);
        if(ret[0]) return;
        String pin = system.getSavedString(PIN, null);
        if(pin!=null) PINInteractor.onNewPIN(system, pin, newKey);
        presenter.closeView();
    }

    @Override
    public void onNewPIN(String pin) {
        PINInteractor.onNewPIN(system, pin, accountSystem.getKey());
    }

    @Override
    public void onKeyInput(final String key, final KeyInputPresenter presenter) {
        system.doOnBackground(new function() {
            @Override
            public void run(String... params) {
                try {
                    accountSystem.initSystem(key);
                } catch (SAXException | IOException e) {
                    system.doOnForeground(new function() {
                        @Override
                        public void run(String... params) {
                            presenter.unableToParseBase();
                            resetBase();
                        }
                    });
                    return;
                } catch (GeneralSecurityException e) {
                    system.doOnForeground(new function() {
                        @Override
                        public void run(String... params) {
                            presenter.onIncorrectKeyInput();
                        }
                    });
                    return;
                } catch (ParserConfigurationException e) {
                    system.doOnForeground(new function() {
                        @Override
                        public void run(String... params) {
                            presenter.unexpectedException();
                        }
                    });
                    return;
                }
                system.doOnForeground(new function() {
                    @Override
                    public void run(String... params) {
                        presenter.onCorrectKeyInput();
                        SettingsInteractor.this.presenter.closeView();
                    }
                });
            }
        });
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

    @Override
    public long isPINBlocked(){
        return PINInteractor.isPINBlocked(system);
    }

    @Override
    public void countDownTime(long time, long interval, function onTick, function onFinish) {
        system.countDown(time, interval, onTick, onFinish);
    }

}
