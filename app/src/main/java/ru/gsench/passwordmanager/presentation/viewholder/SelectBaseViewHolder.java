package ru.gsench.passwordmanager.presentation.viewholder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import ru.gsench.passwordmanager.R;

/**
 * Created by grish on 25.03.2017.
 */

public class SelectBaseViewHolder {

    public ViewGroup main;
    public Button chooseBase;
    public Button chooseNewBase;
    public Button useDefBase;

    public SelectBaseViewHolder(Context context, ViewGroup parent){
        main = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.choose_base_dialog, parent, false);
        chooseBase = (Button) main.findViewById(R.id.choose_existing_btn);
        chooseNewBase = (Button) main.findViewById(R.id.choose_new_btn);
        useDefBase = (Button) main.findViewById(R.id.use_default_btn);
    }
}
