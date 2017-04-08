package ru.gsench.passwordmanager.presentation.utils;

import android.app.Activity;
import android.content.Context;
import android.inputmethodservice.KeyboardView;
import android.widget.EditText;

/**
 * Created by grish on 08.04.2017.
 */

public class KeyboardPref extends CustomKeyboard {

    private static String KEYBOARD_PREF = "keyboard_prefs";
    private static String USE_CUSTOM = "use_custom";

    public KeyboardPref(Activity host, KeyboardView keyboardView, boolean keyPreview) {
        super(host, keyboardView, keyPreview);
    }

    @Override
    public void registerEditText(EditText edittext, boolean enableClipboard) {
        if(useCustomKeyboard(mHostActivity)) super.registerEditText(edittext, enableClipboard);
    }

    public static void saveKeyboardPref(boolean useCustom, Context context){
        context.getSharedPreferences(KEYBOARD_PREF, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(USE_CUSTOM, useCustom)
                .commit();
    }

    public static boolean useCustomKeyboard(Context context){
        return context.getSharedPreferences(KEYBOARD_PREF, Context.MODE_PRIVATE).getBoolean(USE_CUSTOM, true);
    }
}
