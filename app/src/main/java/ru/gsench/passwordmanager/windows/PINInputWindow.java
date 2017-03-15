package ru.gsench.passwordmanager.windows;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;

import ru.gsench.passwordmanager.R;
import utils.function;

/**
 * Created by Григорий Сенченок on 12.03.2017.
 */

public class PINInputWindow {

    private Context context;
    public ViewGroup main;

    public PINInputWindow(Context context, ViewGroup parent, final function onPinInput){
        this.context=context;
        main = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.input_pin, parent, false);
        PinLockView pin = (PinLockView) main.findViewById(R.id.pin_lock_view);
        IndicatorDots dots = (IndicatorDots) main.findViewById(R.id.pin_dots);
        pin.attachIndicatorDots(dots);
        pin.setPinLength(4);
        pin.setTextColor(ContextCompat.getColor(context, R.color.pin_color));
        dots.setIndicatorType(IndicatorDots.IndicatorType.FILL_WITH_ANIMATION);
        pin.setPinLockListener(new PinLockListener() {
            @Override
            public void onComplete(String pin) {
                onPinInput.run(pin);
            }

            @Override
            public void onEmpty() {

            }

            @Override
            public void onPinChange(int pinLength, String intermediatePin) {

            }
        });
    }

    public ViewGroup getView(){
        return main;
    }
}
