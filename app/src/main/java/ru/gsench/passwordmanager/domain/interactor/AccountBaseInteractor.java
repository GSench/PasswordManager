package ru.gsench.passwordmanager.domain.interactor;

import java.io.IOException;
import java.security.GeneralSecurityException;

import ru.gsench.passwordmanager.domain.SystemInterface;
import ru.gsench.passwordmanager.domain.account_system.AccountSystem;
import ru.gsench.passwordmanager.domain.utils.function;

/**
 * Created by Григорий Сенченок on 07.04.2017.
 */

public class AccountBaseInteractor {

    static final String ACCOUNT_BASE = "base_path";

    private final static Object synchronizationStub = new Object();
    static void saveAccountBase(final SystemInterface system, final AccountSystem accountSystem, final function securityException, final function ioException){
        system.doOnBackground(new function() {
            @Override
            public void run(String... params) {
                synchronized (synchronizationStub){
                    try {
                        system.writeFileToPath(accountSystem.encryptSystem(), getAccountBasePath(system));
                    } catch (GeneralSecurityException e) {
                        system.doOnForeground(securityException);
                    } catch (IOException e) {
                        system.doOnForeground(ioException);
                    }
                }
            }
        });
    }

    static AccountSystem getAccountSystem(SystemInterface system) throws IOException, EmptyBaseException {
        byte[] base = system.readFileFromPath(getAccountBasePath(system));
        if(base.length==0) throw new EmptyBaseException();
        return new AccountSystem(base);
    }

    public static void resetBase(SystemInterface system) {
        system.removeSaved(ACCOUNT_BASE);
    }

    public static void selectBase(String path, SystemInterface system){
        system.saveString(ACCOUNT_BASE, path);
    }

    public static String getAccountBasePath(SystemInterface system){
        return system.getSavedString(ACCOUNT_BASE, null);
    }

    public static class EmptyBaseException extends Exception{}

}
