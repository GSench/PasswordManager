package ru.gsench.passwordmanager.utils;

import android.view.ViewGroup;

/**
 * Created by grish on 25.03.2017.
 */

public abstract class AView {

    protected BaseActivity context;
    protected ViewGroup parent;

    public boolean isCloseOnBackPressed() {
        return closeOnBackPressed;
    }

    private boolean closeOnBackPressed = true;

    public AView(BaseActivity context, ViewGroup parent){
        this.context=context;
        this.parent=parent;
    }

    public void open(){
        context.openView(this);
    }

    public void closeSelf(){
        context.closeView();
    }

    public AView closeOnBackPressed(boolean closeOnBackPressed){
        this.closeOnBackPressed=closeOnBackPressed;
        return this;
    }

    protected abstract ViewGroup getView();

    protected abstract void start();

}