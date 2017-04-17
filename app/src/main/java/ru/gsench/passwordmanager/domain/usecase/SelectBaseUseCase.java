package ru.gsench.passwordmanager.domain.usecase;

import ru.gsench.passwordmanager.presentation.presenter.SelectBasePresenter;

/**
 * Created by grish on 01.04.2017.
 */

public interface SelectBaseUseCase {

    public void onNewBaseSelected(String path, SelectBasePresenter presenter);
    public void onExistingBaseSelected(String path);

}
