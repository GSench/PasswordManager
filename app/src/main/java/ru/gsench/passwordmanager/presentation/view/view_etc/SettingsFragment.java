package ru.gsench.passwordmanager.presentation.view.view_etc;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import ru.gsench.passwordmanager.R;
import ru.gsench.passwordmanager.domain.utils.function;

/**
 * Created by grish on 08.04.2017.
 */

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }

    public void init(final function onKeyboardPref, final function onBasePref, final function onKeyPref, final function onPINPref){

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

}
