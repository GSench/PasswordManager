package ru.gsench.passwordmanager.aview;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import ru.gsench.passwordmanager.R;
import ru.gsench.passwordmanager.presenter.KeyInputPresenter;
import interactor.MainInteractor;
import ru.gsench.passwordmanager.utils.AView;
import ru.gsench.passwordmanager.utils.BaseActivity;
import ru.gsench.passwordmanager.view.KeyInputView;
import ru.gsench.passwordmanager.viewholder.KeyInputViewHolder;

/**
 * Created by grish on 11.03.2017.
 */

public class KeyInputAView extends AView implements KeyInputView {

    public KeyInputViewHolder viewHolder;
    private KeyInputPresenter presenter;

    public KeyInputAView(BaseActivity context, ViewGroup parent, MainInteractor interactor){
        super(context, parent);
        viewHolder = new KeyInputViewHolder(context, parent);
        presenter = new KeyInputPresenter(interactor, this);
    }

    @Override
    protected void start() {
        presenter.start();
    }

    @Override
    public void init() {
        viewHolder.enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onEnter();
            }
        });
        viewHolder.message.setText(context.getString(R.string.input_key));
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
    public void showIncorrectKeyToast() {
        Toast.makeText(context, R.string.incorrect_key, Toast.LENGTH_SHORT).show();
    }

    @Override
    public ViewGroup getView(){
        return viewHolder.main;
    }

}
