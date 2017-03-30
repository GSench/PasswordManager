package ru.gsench.passwordmanager.presentation.viewholder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
    public TextView pinMsg;
    public ViewGroup main;
    public View block;
    public Button resetPIN;

    public PINInputViewHolder(Context context, ViewGroup parent){
        main = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.input_pin, parent, false);
        pinView = (PinLockView) main.findViewById(R.id.pin_lock_view);
        dots = (IndicatorDots) main.findViewById(R.id.pin_dots);
        pinMsg = (TextView) main.findViewById(R.id.pin_msg);
        block = main.findViewById(R.id.block);
        resetPIN = (Button) main.findViewById(R.id.reset_pin_btn);
    }

}
