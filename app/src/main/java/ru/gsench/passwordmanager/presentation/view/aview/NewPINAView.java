package ru.gsench.passwordmanager.presentation.view.aview;

import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;

import com.andrognito.pinlockview.PinLockListener;

import ru.gsench.passwordmanager.R;
import ru.gsench.passwordmanager.presentation.presenter.NewPINPresenter;
import ru.gsench.passwordmanager.presentation.utils.AView;
import ru.gsench.passwordmanager.presentation.utils.AViewContainer;
import ru.gsench.passwordmanager.presentation.view.NewPINView;
import ru.gsench.passwordmanager.presentation.viewholder.PINInputViewHolder;

/**
 * Created by Григорий Сенченок on 29.03.2017.
 */

public class NewPINAView extends AView implements NewPINView {

    public PINInputViewHolder viewHolder;
    private NewPINPresenter presenter;

    public NewPINAView(AViewContainer container, NewPINPresenter presenter) {
        super(container);
        viewHolder = new PINInputViewHolder(context, parent);
        this.presenter=presenter;
        presenter.setView(this);
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
    public void hideView() {
        viewHolder.main.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showView() {
        viewHolder.main.setVisibility(View.VISIBLE);
    }

    @Override
    public void openAskPINDialog() {
        new AlertDialog.Builder(context)
                .setTitle(R.string.create_pin_title)
                .setMessage(R.string.create_pin_msg)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                        presenter.onConfirmPINCreation();
                    }
                })
                .setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                        presenter.onCancelPINCreation();
                    }
                })
                .setCancelable(false)
                .create()
                .show();
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
