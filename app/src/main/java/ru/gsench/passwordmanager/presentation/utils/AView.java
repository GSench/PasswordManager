package ru.gsench.passwordmanager.presentation.utils;

import android.content.Context;
import android.view.ViewGroup;

/**
 * Created by grish on 25.03.2017.
 */

public abstract class AView {

    protected AViewContainer container;
    protected ViewGroup parent;
    public Context context;

    public boolean isCloseOnBackPressed() {
        return closeOnBackPressed;
    }

    private boolean closeOnBackPressed = true;

    public AView(AViewContainer context){
        this.container =context;
        this.parent=context.getContainerView();
        this.context=parent.getContext();
    }

    public void open(){
        container.openView(this);
    }

    public void closeSelf(){
        container.closeView();
    }

    public AView closeOnBackPressed(boolean closeOnBackPressed){
        this.closeOnBackPressed=closeOnBackPressed;
        return this;
    }

    protected abstract ViewGroup getView();

    protected abstract void start();

}