package ru.gsench.passwordmanager;

import java.io.IOException;

/**
 * Created by grish on 26.02.2017.
 */

public interface SystemInterface {

    public byte[] readFileFromPath(String path) throws IOException;
    public void writeFileToPath(byte[] file, String path) throws IOException;
    public String getSavedString(String title);
    public void saveString(String title, String string);
    public void createFileIfNotExist(String path) throws IOException;
    public void deleteFile(String path);

}
