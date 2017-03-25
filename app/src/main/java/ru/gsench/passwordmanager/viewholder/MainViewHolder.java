package ru.gsench.passwordmanager.viewholder;

import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.github.clans.fab.FloatingActionButton;

import ru.gsench.passwordmanager.R;

/**
 * Created by grish on 01.03.2017.
 */

public class MainViewHolder {

    public ViewGroup main;
    public RelativeLayout dialogContent;
    public ListView accountList;
    public FloatingActionButton fab;

    public MainViewHolder(AppCompatActivity act){
        main = (ViewGroup) act.findViewById(R.id.activity_main);
        dialogContent = (RelativeLayout) act.findViewById(R.id.dialog_content);
        accountList = (ListView) act.findViewById(R.id.account_list);
        fab = (FloatingActionButton) act.findViewById(R.id.fab);
    }
}
