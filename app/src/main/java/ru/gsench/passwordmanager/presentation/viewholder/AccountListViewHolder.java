package ru.gsench.passwordmanager.presentation.viewholder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ListView;

import com.github.clans.fab.FloatingActionButton;

import ru.gsench.passwordmanager.R;

/**
 * Created by grish on 01.04.2017.
 */

public class AccountListViewHolder {

    public ViewGroup main;
    public ListView accountList;
    public FloatingActionButton fab;

    public AccountListViewHolder(Context context, ViewGroup parent){
        main = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.account_list, parent, false);
        accountList = (ListView) main.findViewById(R.id.account_list);
        fab = (FloatingActionButton) main.findViewById(R.id.fab);
    }
}
