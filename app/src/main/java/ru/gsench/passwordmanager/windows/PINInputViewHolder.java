package ru.gsench.passwordmanager.windows;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockView;

import ru.gsench.passwordmanager.R;

/**
 * Created by Григорий Сенченок on 15.03.2017.
 */

public class PINInputViewHolder {

    public IndicatorDots dots;
    public PinLockView pinView;
    public TextView timer;
    public ViewGroup main;
    public View block;

    public PINInputViewHolder(Context context, ViewGroup parent){
        main = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.input_pin, parent, false);
        pinView = (PinLockView) main.findViewById(R.id.pin_lock_view);
        dots = (IndicatorDots) main.findViewById(R.id.pin_dots);
        timer = (TextView) main.findViewById(R.id.pin_timer);
        block = main.findViewById(R.id.block);
    }

}
