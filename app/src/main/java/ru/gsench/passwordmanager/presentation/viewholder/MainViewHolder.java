package ru.gsench.passwordmanager.presentation.viewholder;

import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import ru.gsench.passwordmanager.R;

/**
 * Created by grish on 01.03.2017.
 */

public class MainViewHolder {

    public ViewGroup main;
    public RelativeLayout dialogContent;
    public ViewGroup accountsContent;

    public MainViewHolder(AppCompatActivity act){
        main = (ViewGroup) act.findViewById(R.id.activity_main);
        dialogContent = (RelativeLayout) act.findViewById(R.id.dialog_content);
        accountsContent = (RelativeLayout) act.findViewById(R.id.account_list_content);
    }
}
