package ru.gsench.passwordmanager.presentation.utils;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by grish on 25.03.2017.
 */

public class AViewContainer {

    public ViewGroup getContainerView() {
        return container;
    }

    private ViewGroup container;
    private AView currentView;

    public AViewContainer(ViewGroup container){
        this.container=container;
    }

    protected void openView(AView view){
        closeView();
        currentView = view;
        view.getView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        container.addView(view.getView(), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        view.start();
    }

    public void closeView(){
        container.removeAllViews();
    }

    public boolean viewOpened(){
        return container.getChildCount()!=0;
    }

}
