package ru.gsench.passwordmanager.presentation.view.aview;

import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.andrognito.pinlockview.PinLockListener;

import ru.gsench.passwordmanager.domain.interactor.MainInteractor;
import ru.gsench.passwordmanager.R;
import ru.gsench.passwordmanager.domain.interactor.PINInputUseCase;
import ru.gsench.passwordmanager.presentation.presenter.PINInputPresenter;
import ru.gsench.passwordmanager.presentation.utils.AView;
import ru.gsench.passwordmanager.presentation.utils.AViewContainer;
import ru.gsench.passwordmanager.presentation.view.PINInputView;
import ru.gsench.passwordmanager.presentation.viewholder.PINInputViewHolder;

/**
 * Created by Григорий Сенченок on 12.03.2017.
 */

public class PINInputAView extends AView implements PINInputView{

    private PINInputViewHolder viewHolder;
    private PINInputPresenter presenter;

    public PINInputAView(AViewContainer container, PINInputUseCase interactor){
        super(container);
        viewHolder = new PINInputViewHolder(context, parent);
        presenter = new PINInputPresenter(interactor, this);
    }

    @Override
    public ViewGroup getView(){
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
        viewHolder.resetPIN.setText(context.getString(R.string.reset_pin));
        viewHolder.resetPIN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onResetBtn();
            }
        });
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
    public void lockPIN() {
        viewHolder.block.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
    }

    @Override
    public void unlockPIN() {
        viewHolder.block.setOnTouchListener(null);
    }

    @Override
    public void setRetryLockMsg(int secLeft) {
        viewHolder.pinMsg.setVisibility(View.VISIBLE);
        viewHolder.pinMsg.setText(context.getString(R.string.pin_retry, secLeft));
    }

    @Override
    public void removeMsg() {
        viewHolder.pinMsg.setText(null);
        viewHolder.pinMsg.setVisibility(View.GONE);
    }

    @Override
    public void showResetPINConfirmDialog() {
        new AlertDialog.Builder(context)
                .setTitle(R.string.reset_pin)
                .setMessage(R.string.reset_pin_msg)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        presenter.onResetConfirmBtn();
                    }
                })
                .setNeutralButton(R.string.cancel, null)
                .create()
                .show();
    }

}
