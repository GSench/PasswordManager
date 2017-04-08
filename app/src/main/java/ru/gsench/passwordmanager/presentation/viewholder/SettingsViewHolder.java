package ru.gsench.passwordmanager.presentation.viewholder;

import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import ru.gsench.passwordmanager.R;

/**
 * Created by grish on 08.04.2017.
 */

public class SettingsViewHolder {

    public ViewGroup main;
    public RelativeLayout dialogContent;
    public ViewGroup settingsList;

    public SettingsViewHolder(AppCompatActivity act){
        main = (ViewGroup) act.findViewById(R.id.settings_layout);
        dialogContent = (RelativeLayout) act.findViewById(R.id.aview_content);
        settingsList = (RelativeLayout) act.findViewById(R.id.settings_list);
    }

}
