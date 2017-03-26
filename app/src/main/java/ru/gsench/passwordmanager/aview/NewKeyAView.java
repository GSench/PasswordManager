package ru.gsench.passwordmanager.aview;

import android.view.View;
import android.view.ViewGroup;

import interactor.MainInteractor;
import ru.gsench.passwordmanager.R;
import ru.gsench.passwordmanager.presenter.NewKeyPresenter;
import ru.gsench.passwordmanager.utils.AView;
import ru.gsench.passwordmanager.utils.BaseActivity;
import ru.gsench.passwordmanager.view.NewKeyView;
import ru.gsench.passwordmanager.viewholder.KeyInputViewHolder;

/**
 * Created by grish on 26.03.2017.
 */

public class NewKeyAView extends AView implements NewKeyView {

    public KeyInputViewHolder viewHolder;
    private NewKeyPresenter presenter;

    public NewKeyAView(BaseActivity context, ViewGroup parent, MainInteractor interactor){
        super(context, parent);
        viewHolder = new KeyInputViewHolder(context, parent);
        presenter = new NewKeyPresenter(interactor, this);
    }

    @Override
    protected ViewGroup getView() {
        return viewHolder.main;
    }

    @Override
    protected void start() {
        presenter.onStart();
    }

    @Override
    public void init() {
        viewHolder.enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onEnter();
            }
        });
        viewHolder.message.setText(context.getString(R.string.input_new_key));
    }

    @Override
    public void closeView() {
        closeSelf();
    }

    @Override
    public String getKeyInput() {
        return viewHolder.keyEdit.getText().toString();
    }

    @Override
    public void setConfirmKeyMsg() {
        viewHolder.message.setText(context.getString(R.string.reenter_key));
    }

    @Override
    public void setKeysNotEqualMsg() {
        viewHolder.message.setText(context.getString(R.string.keys_not_equal)+"\n\n"+context.getString(R.string.input_new_key));
    }

    @Override
    public void clearKeyInput() {
        viewHolder.keyEdit.getText().clear();
    }
}
