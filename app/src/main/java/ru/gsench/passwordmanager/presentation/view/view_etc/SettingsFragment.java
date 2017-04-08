package ru.gsench.passwordmanager.presentation.view.view_etc;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import ru.gsench.passwordmanager.R;
import ru.gsench.passwordmanager.domain.utils.function;
import ru.gsench.passwordmanager.presentation.utils.KeyboardPref;

/**
 * Created by grish on 08.04.2017.
 */

public class SettingsFragment extends PreferenceFragment {

    private function onKeyboardPref, onBasePref, onKeyPref, onPINPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        CheckBoxPreference keyboardPref = (CheckBoxPreference) findPreference(getString(R.string.keyboard_pref));
        Preference basePref = findPreference(getString(R.string.new_base));
        Preference keyPref = findPreference(getString(R.string.change_key));
        Preference pinPref = findPreference(getString(R.string.change_pin));

        keyboardPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                onKeyboardPref.run(((CheckBoxPreference)preference).isChecked()+"");
                return false;
            }
        });
        keyboardPref.setChecked(KeyboardPref.useCustomKeyboard(getActivity()));
        basePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                onBasePref.run();
                return false;
            }
        });
        keyPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                onKeyPref.run();
                return false;
            }
        });
        pinPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                onPINPref.run();
                return false;
            }
        });
    }

    public void init(function onKeyboardPref, function onBasePref, function onKeyPref, function onPINPref){
        this.onKeyboardPref=onKeyboardPref;
        this.onBasePref=onBasePref;
        this.onKeyPref=onKeyPref;
        this.onPINPref=onPINPref;
    }

}
