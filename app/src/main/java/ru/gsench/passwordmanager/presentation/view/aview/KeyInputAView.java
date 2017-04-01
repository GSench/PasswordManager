package ru.gsench.passwordmanager.presentation.view.aview;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import ru.gsench.passwordmanager.R;
import ru.gsench.passwordmanager.domain.interactor.KeyInputUseCase;
import ru.gsench.passwordmanager.presentation.presenter.KeyInputPresenter;
import ru.gsench.passwordmanager.domain.interactor.MainInteractor;
import ru.gsench.passwordmanager.presentation.utils.AView;
import ru.gsench.passwordmanager.presentation.utils.AViewContainer;
import ru.gsench.passwordmanager.presentation.view.KeyInputView;
import ru.gsench.passwordmanager.presentation.viewholder.KeyInputViewHolder;

/**
 * Created by grish on 11.03.2017.
 */

public class KeyInputAView extends AView implements KeyInputView {

    public KeyInputViewHolder viewHolder;
    private KeyInputPresenter presenter;

    public KeyInputAView(AViewContainer container, KeyInputUseCase interactor){
        super(container);
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
    public void showUnableToParseBaseToast() {
        Toast.makeText(context, R.string.unable_parse_base, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showUnexpectedExceptionToast() {
        Toast.makeText(context, R.string.unexpected_error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public ViewGroup getView(){
        return viewHolder.main;
    }

}
