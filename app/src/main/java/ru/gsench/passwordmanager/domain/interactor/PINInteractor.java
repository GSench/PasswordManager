package ru.gsench.passwordmanager.domain.interactor;

import ru.gsench.passwordmanager.domain.SystemInterface;
import ru.gsench.passwordmanager.domain.utils.function;

/**
 * Created by Григорий Сенченок on 07.04.2017.
 */

public class PINInteractor {

    private static final String KEY = "key";
    static final String PIN = "pin";
    private static final String PIN_TRIES = "pin_tries";
    private static final String LAST_PIN_TRY = "last_pin_try";
    private static final long PIN_BLOCK = 30*1000;
    private static final int PIN_LOCK_TRIES = 3;

    public static void onNewPIN(SystemInterface system, String pin, String key){
        system.saveString(PIN, pin);
        system.saveString(KEY, key);
    }

    public static void resetPIN(SystemInterface system){
        system.removeSaved(KEY);
        system.removeSaved(PIN);
        system.removeSaved(PIN_TRIES);
        system.removeSaved(LAST_PIN_TRY);
    }

    public static void onPINInput(SystemInterface system, String pin, function pinCorrect) {
        long block = isPINBlocked(system);
        if(block>0) return;

        if(!pin.equals(system.getSavedString(PIN, null))){
            system.saveInt(PIN_TRIES, system.getSavedInt(PIN_TRIES, 0)+1);
            system.saveLong(LAST_PIN_TRY, System.currentTimeMillis());
        } else {
            system.saveInt(PIN_TRIES, 0);
            String key = system.getSavedString(KEY, null);
            pinCorrect.run(key);
        }
    }

    //TODO Make secure
    public static long isPINBlocked(SystemInterface system){
        int tries = system.getSavedInt(PIN_TRIES, 0);
        long current = System.currentTimeMillis();
        long lastTry = system.getSavedLong(LAST_PIN_TRY, current-PIN_BLOCK);
        if (tries>=PIN_LOCK_TRIES && current-lastTry <= PIN_BLOCK) return PIN_BLOCK - current + lastTry;
        else return -1;
    }

}
