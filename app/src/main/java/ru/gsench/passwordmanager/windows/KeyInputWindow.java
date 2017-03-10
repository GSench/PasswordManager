package ru.gsench.passwordmanager.windows;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import utils.function;

/**
 * Created by grish on 11.03.2017.
 */

public class KeyInputWindow {

    private Context context;
    public KeyInputViewHolder viewHolder;

    public KeyInputWindow(Context context, ViewGroup parent, final function onEnter){
        this.context=context;
        viewHolder = new KeyInputViewHolder(context, parent);
        viewHolder.enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onEnter.run(viewHolder.keyEdit.getText().toString());
            }
        });
    }

    public void setMessage(String msg){
        viewHolder.message.setText(msg);
    }

    public ViewGroup getView(){
        return viewHolder.main;
    }
}
