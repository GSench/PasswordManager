package ru.gsench.passwordmanager.presentation.view.view_etc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.adapters.BaseSwipeAdapter;

import java.util.ArrayList;

import ru.gsench.passwordmanager.R;
import ru.gsench.passwordmanager.domain.account_system.Account;
import ru.gsench.passwordmanager.presentation.utils.IntentUtils;


/**
 * Created by grish on 04.03.2017.
 */

public class AccountListAdapter extends BaseSwipeAdapter {

    private Context context;

    private ArrayList<Account> accounts;
    private AccountListInterface listInterface;

    public AccountListAdapter(Context context, ArrayList<Account> accounts, AccountListInterface accountListInterface){
        this.context=context;
        this.accounts=accounts;
        listInterface=accountListInterface;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    @Override
    public View generateView(int position, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.account, parent, false);
    }

    @Override
    public void fillValues(int position, View convertView) {
        final Account current = getItem(position);
        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView login = (TextView) convertView.findViewById(R.id.login);
        TextView password = (TextView) convertView.findViewById(R.id.password);
        name.setText(current.getName());
        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(IntentUtils.isStringUrl(current.getName()))
                    try {
                        IntentUtils.openURL(IntentUtils.toURL(current.getName()), context);
                    }
                    catch (Throwable e){
                    }
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentUtils.copyText(current.getLogin(), context);
                Toast.makeText(context, context.getString(R.string.login_copied, current.getLogin()), Toast.LENGTH_SHORT).show();
            }
        });
        password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentUtils.copyText(current.getPassword(), context);
                Toast.makeText(context, R.string.password_copied, Toast.LENGTH_SHORT).show();
            }
        });
        login.setText(current.getLogin());
        password.setText(current.getPassword());
        convertView.findViewById(R.id.delete_acc_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listInterface.onAccountDelete(current);
            }
        });
        convertView.findViewById(R.id.edit_acc_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listInterface.onAccountEdit(current);
            }
        });
    }

    public void notifyDataSetChanged(ArrayList<Account> accounts){
        this.accounts=accounts;
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return accounts.size();
    }

    @Override
    public Account getItem(int i) {
        return accounts.get(i);
    }

    @Override
    public long getItemId(int i) {
        return getItem(i).getId();
    }

    public interface AccountListInterface{
        public void onAccountDelete(Account account);
        public void onAccountEdit(Account account);
    }
}
