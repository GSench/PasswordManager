package ru.gsench.passwordmanager.utils;

import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by grish on 25.03.2017.
 */

public abstract class BaseActivity extends AppCompatActivity {

    private ViewGroup aViewContentView;
    private AView currentView;

    public void setContentView(int res, int aViewContent){
        setContentView(res);
        aViewContentView = (ViewGroup) findViewById(aViewContent);
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
        aViewContentView.addView(view.getView(), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    protected void closeView(){
        aViewContentView.removeAllViews();
    }

    private boolean viewOpened(){
        return aViewContentView.getChildCount()!=0;
    }

    @Override
    public void onBackPressed() {
        if(viewOpened()&&currentView.isCloseOnBackPressed()) closeView();
        else super.onBackPressed();
    }

}
