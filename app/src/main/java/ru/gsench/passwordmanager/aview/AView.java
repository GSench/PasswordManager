package ru.gsench.passwordmanager.aview;

import android.content.Context;
import android.view.ViewGroup;

import utils.function;

/**
 * Created by grish on 25.03.2017.
 */

public abstract class AView {

    protected Context context;
    protected ViewGroup parent;
    protected function closeSelf = new function() {
        @Override
        public void run(String... params) {

        }
    };
    protected function onResult = new function() {
        @Override
        public void run(String... params) {

        }
    };

    public boolean isCloseOnBackPressed() {
        return closeOnBackPressed;
    }

    private boolean closeOnBackPressed = true;

    public AView(Context context, ViewGroup parent){
        this.context=context;
        this.parent=parent;
    }

    public AView closeSelf(function closeSelf){
        this.closeSelf=closeSelf;
        return this;
    }

    public AView onResult(function onResult){
        this.onResult=onResult;
        return this;
    }

    public AView closeOnBackPressed(boolean closeOnBackPressed){
        this.closeOnBackPressed=closeOnBackPressed;
        return this;
    }

    public abstract ViewGroup getView();

}