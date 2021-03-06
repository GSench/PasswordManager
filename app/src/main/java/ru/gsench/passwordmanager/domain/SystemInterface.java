package ru.gsench.passwordmanager.domain;

import java.io.IOException;

import ru.gsench.passwordmanager.domain.utils.function;

/**
 * Created by grish on 26.02.2017.
 */

public interface SystemInterface {

    public byte[] readFileFromPath(String path) throws IOException;
    public void writeFileToPath(byte[] file, String path) throws IOException;
    public String getSavedString(String title, String def);
    public void saveString(String title, String string);
    public int getSavedInt(String title, int def);
    public void saveInt(String title, int i);
    public long getSavedLong(String title, long def);
    public void saveLong(String title, long i);
    public boolean getSavedBoolean(String title, boolean b);
    public void saveBoolean(String title, boolean b);
    public void createFileIfNotExist(String path) throws IOException;
    public void deleteFile(String path);
    public void removeSaved(String str);
    public void doOnBackground(function background);
    public void doOnForeground(function function);
    public void countDown(long time, long interval, function onTick, function onFinish);
}
