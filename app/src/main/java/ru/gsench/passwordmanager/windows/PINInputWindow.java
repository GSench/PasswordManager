package ru.gsench.passwordmanager.windows;

import android.content.Context;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.andrognito.pinlockview.PinLockListener;

import ru.gsench.passwordmanager.R;
import utils.function;

/**
 * Created by Григорий Сенченок on 12.03.2017.
 */

public class PINInputWindow {

    private Context context;
    private PINInputViewHolder viewHolder;
    private CountDownTimer timer;

    public PINInputWindow(Context context, ViewGroup parent, final function onPinInput, final function onResetPin, String resetPinBtn){
        this.context=context;
        viewHolder = new PINInputViewHolder(context, parent);
        viewHolder.pinView.attachIndicatorDots(viewHolder.dots);
        viewHolder.pinView.setPinLength(4);
        viewHolder.pinView.setTextColor(ContextCompat.getColor(context, R.color.pin_color));
        viewHolder.pinView.setPinLockListener(new PinLockListener() {
            @Override
            public void onComplete(String pin) {
                viewHolder.pinView.resetPinLockView();
                onPinInput.run(pin);
            }

            @Override
            public void onEmpty() {

            }

            @Override
            public void onPinChange(int pinLength, String intermediatePin) {

            }
        });
        if(resetPinBtn!=null&&!resetPinBtn.equals("")){
            viewHolder.resetPIN.setVisibility(View.VISIBLE);
            viewHolder.resetPIN.setText(resetPinBtn);
            viewHolder.resetPIN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onResetPin.run();
                }
            });
        }
    }

    public ViewGroup getView(){
        return viewHolder.main;
    }

    public void blockPINFor(long time){
        if(timer!=null) return;
        timer = new CountDownTimer(time, 1000) {
            @Override
            public void onTick(long l) {
                viewHolder.pinMsg.setText(context.getString(R.string.pin_retry, l/1000));
            }

            @Override
            public void onFinish() {
                viewHolder.block.setOnTouchListener(null);
                viewHolder.pinMsg.setText(null);
                viewHolder.pinMsg.setVisibility(View.GONE);
                timer=null;
            }
        };
        viewHolder.block.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        viewHolder.pinMsg.setVisibility(View.VISIBLE);
        viewHolder.pinMsg.setText(context.getString(R.string.pin_retry, time/1000));
        timer.start();
    }

    public void setMessage(String msg){
        viewHolder.pinMsg.setVisibility(View.VISIBLE);
        viewHolder.pinMsg.setText(msg);
    }
}
