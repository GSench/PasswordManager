package ru.gsench.passwordmanager.domain.usecase;

import ru.gsench.passwordmanager.domain.utils.function;

/**
 * Created by grish on 01.04.2017.
 */

public interface PINInputUseCase {

    public void onResetPIN();
    public void onPINInput(String pin);
    public long isPINBlocked();
    public void countDownTime(long time, long interval, function onTick, function onFinish);

}
