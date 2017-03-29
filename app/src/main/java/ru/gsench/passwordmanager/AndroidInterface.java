package ru.gsench.passwordmanager;

import android.content.Context;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import utils.function;

/**
 * Created by grish on 26.02.2017.
 */

public class AndroidInterface implements SystemInterface {

    public static final String SPREF = "preferences";

    private AppCompatActivity act;

    public AndroidInterface(AppCompatActivity act){
        this.act =act;
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
        return act.getSharedPreferences(SPREF, Context.MODE_PRIVATE).getString(title, def);
    }

    @Override
    public void saveString(String title, String string) {
        act.getSharedPreferences(SPREF, Context.MODE_PRIVATE)
                .edit()
                .putString(title, string)
                .commit();
    }

    @Override
    public int getSavedInt(String title, int def) {
        return act.getSharedPreferences(SPREF, Context.MODE_PRIVATE).getInt(title, def);
    }

    @Override
    public void saveInt(String title, int i) {
        act.getSharedPreferences(SPREF, Context.MODE_PRIVATE)
                .edit()
                .putInt(title, i)
                .commit();
    }


    @Override
    public long getSavedLong(String title, long def) {
        return act.getSharedPreferences(SPREF, Context.MODE_PRIVATE).getLong(title, def);
    }

    @Override
    public void saveLong(String title, long i) {
        act.getSharedPreferences(SPREF, Context.MODE_PRIVATE)
                .edit()
                .putLong(title, i)
                .commit();
    }

    @Override
    public boolean getSavedBoolean(String title, boolean b) {
        return act.getSharedPreferences(SPREF, Context.MODE_PRIVATE).getBoolean(title, b);
    }

    @Override
    public void saveBoolean(String title, boolean b) {
        act.getSharedPreferences(SPREF, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(title, b)
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

    @Override
    public void removeSaved(String str) {
        act.getSharedPreferences(SPREF, Context.MODE_PRIVATE)
                .edit()
                .remove(str)
                .commit();
    }

    @Override
    public void doOnBackground(final function background) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                background.run();
            }
        }).start();

    }

    @Override
    public void doOnForeground(final function function) {
        if(act!=null)
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                function.run();
            }
        });
    }

    @Override
    public void countDown(long time, long interval, final function onTick, final function onFinish) {
        new CountDownTimer(time, interval){

            @Override
            public void onTick(long l) {
                onTick.run(l+"");
            }

            @Override
            public void onFinish() {
                onFinish.run();
            }

        }.start();
    }

}
