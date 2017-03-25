package ru.gsench.passwordmanager.aview;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import ru.gsench.passwordmanager.viewholder.KeyInputViewHolder;

/**
 * Created by grish on 11.03.2017.
 */

public class KeyInputWindow extends AView{

    public KeyInputViewHolder viewHolder;

    public KeyInputWindow(Context context, ViewGroup parent){
        super(context, parent);
        viewHolder = new KeyInputViewHolder(context, parent);
        viewHolder.enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onResult.run(viewHolder.keyEdit.getText().toString());
            }
        });
    }

    public void setMessage(String msg){
        viewHolder.message.setText(msg);
    }

    public void clearKeyEdit(){
        viewHolder.keyEdit.setText(null);
    }

    @Override
    public ViewGroup getView(){
        return viewHolder.main;
    }
}
