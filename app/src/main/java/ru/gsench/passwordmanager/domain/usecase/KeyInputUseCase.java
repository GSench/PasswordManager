package ru.gsench.passwordmanager.domain.usecase;

import ru.gsench.passwordmanager.presentation.presenter.KeyInputPresenter;

/**
 * Created by grish on 01.04.2017.
 */

public interface KeyInputUseCase {

    public void onKeyInput(String key, KeyInputPresenter presenter);

}
