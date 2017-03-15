package ru.gsench.passwordmanager;

import android.content.Context;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by grish on 26.02.2017.
 */

public class AndroidInterface implements SystemInterface {

    public static final String SPREF = "preferences";

    Context context;

    public AndroidInterface(Context context){
        this.context=context;
    }

    @Override
    public byte[] readFileFromPath(String path) throws IOException {
        InputStream in = new FileInputStream(path);
        byte[] file = new byte[in.available()];
        IOUtils.readFully(in, file);
        return file;
    }

    @Override
    public void writeFileToPath(byte[] file, String path) throws IOException {
        OutputStream out = null;
        try {
            out = new FileOutputStream(path);
            IOUtils.write(file, out);
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

    @Override
    public String getSavedString(String title, String def) {
        return context.getSharedPreferences(SPREF, Context.MODE_PRIVATE).getString(title, def);
    }

    @Override
    public void saveString(String title, String string) {
        context.getSharedPreferences(SPREF, Context.MODE_PRIVATE)
                .edit()
                .putString(title, string)
                .commit();
    }

    @Override
    public int getSavedInt(String title, int def) {
        return context.getSharedPreferences(SPREF, Context.MODE_PRIVATE).getInt(title, def);
    }

    @Override
    public void saveInt(String title, int i) {
        context.getSharedPreferences(SPREF, Context.MODE_PRIVATE)
                .edit()
                .putInt(title, i)
                .commit();
    }


    @Override
    public long getSavedLong(String title, long def) {
        return context.getSharedPreferences(SPREF, Context.MODE_PRIVATE).getLong(title, def);
    }

    @Override
    public void saveLong(String title, long i) {
        context.getSharedPreferences(SPREF, Context.MODE_PRIVATE)
                .edit()
                .putLong(title, i)
                .commit();
    }

    @Override
    public void createFileIfNotExist(String path) throws IOException {
        File file = new File(path);
        if(!file.exists()){
            File parent = file.getParentFile();
            parent.mkdirs();
            file.createNewFile();
        }
    }

    @Override
    public void deleteFile(String path) {
        new File(path).delete();
    }

}
