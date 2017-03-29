package ru.gsench.passwordmanager.aview;

import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;

import com.andrognito.pinlockview.PinLockListener;

import interactor.MainInteractor;
import ru.gsench.passwordmanager.R;
import ru.gsench.passwordmanager.presenter.NewPINPresenter;
import ru.gsench.passwordmanager.utils.AView;
import ru.gsench.passwordmanager.utils.BaseActivity;
import ru.gsench.passwordmanager.view.NewPINView;
import ru.gsench.passwordmanager.viewholder.PINInputViewHolder;

/**
 * Created by Григорий Сенченок on 29.03.2017.
 */

public class NewPINAView extends AView implements NewPINView {

    public PINInputViewHolder viewHolder;
    private NewPINPresenter presenter;

    public NewPINAView(BaseActivity context, ViewGroup parent, MainInteractor interactor) {
        super(context, parent);
        viewHolder = new PINInputViewHolder(context, parent);
        presenter= new NewPINPresenter(interactor, this);
    }

    @Override
    protected ViewGroup getView() {
        return viewHolder.main;
    }

    @Override
    protected void start() {
        presenter.start();
    }

    @Override
    public void init() {
        viewHolder.pinView.attachIndicatorDots(viewHolder.dots);
        viewHolder.pinView.setPinLength(4);
        viewHolder.pinView.setTextColor(ContextCompat.getColor(context, R.color.pin_color));
        viewHolder.pinView.setPinLockListener(new PinLockListener() {
            @Override
            public void onComplete(String pin) {
                presenter.onPINInput(pin);
            }

            @Override
            public void onEmpty() {

            }

            @Override
            public void onPinChange(int pinLength, String intermediatePin) {

            }
        });
        viewHolder.resetPIN.setText(context.getString(R.string.cancel));
        viewHolder.resetPIN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onResetBtn();
            }
        });
        viewHolder.pinMsg.setVisibility(View.VISIBLE);
    }

    @Override
    public void closeView() {
        closeSelf();
    }

    @Override
    public void resetPINInput() {
        viewHolder.pinView.resetPinLockView();
    }

    @Override
    public void createPINMsg() {
        viewHolder.pinMsg.setText(context.getString(R.string.create_pin));
    }

    @Override
    public void confirmPINMsg() {
        viewHolder.pinMsg.setText(context.getString(R.string.reenter_pin));
    }

    @Override
    public void PINsNotEqualMsg() {
        viewHolder.pinMsg.setText(context.getString(R.string.pins_not_equal)+" "+context.getString(R.string.create_pin));
    }
}
