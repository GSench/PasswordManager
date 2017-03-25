package ru.gsench.passwordmanager.viewholder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import ru.gsench.passwordmanager.R;

/**
 * Created by grish on 11.03.2017.
 */

public class KeyInputViewHolder {

    public ViewGroup main;
    public EditText keyEdit;
    public TextView message;
    public Button enter;

    public KeyInputViewHolder(Context context, ViewGroup parent){
        main = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.input_key_layout, parent, false);
        keyEdit = (EditText) main.findViewById(R.id.edit_key);
        message = (TextView) main.findViewById(R.id.key_input_msg);
        enter = (Button) main.findViewById(R.id.enter);
    }
}
