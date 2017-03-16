package ru.gsench.passwordmanager;

import java.io.IOException;

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
    public void createFileIfNotExist(String path) throws IOException;
    public void deleteFile(String path);
    public void removeSaved(String str);
}
