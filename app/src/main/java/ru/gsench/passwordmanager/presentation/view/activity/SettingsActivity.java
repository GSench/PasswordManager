package ru.gsench.passwordmanager.presentation.view.activity;

import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import ru.gsench.passwordmanager.R;
import ru.gsench.passwordmanager.domain.utils.function;
import ru.gsench.passwordmanager.presentation.AndroidInterface;
import ru.gsench.passwordmanager.presentation.presenter.KeyInputPresenter;
import ru.gsench.passwordmanager.presentation.presenter.NewKeyPresenter;
import ru.gsench.passwordmanager.presentation.presenter.NewPINPresenter;
import ru.gsench.passwordmanager.presentation.presenter.PINInputPresenter;
import ru.gsench.passwordmanager.presentation.presenter.SettingsPresenter;
import ru.gsench.passwordmanager.presentation.utils.AViewContainer;
import ru.gsench.passwordmanager.presentation.utils.KeyboardPref;
import ru.gsench.passwordmanager.presentation.view.SettingsView;
import ru.gsench.passwordmanager.presentation.view.aview.KeyInputAView;
import ru.gsench.passwordmanager.presentation.view.aview.NewKeyAView;
import ru.gsench.passwordmanager.presentation.view.aview.NewPINAView;
import ru.gsench.passwordmanager.presentation.view.aview.PINInputAView;
import ru.gsench.passwordmanager.presentation.view.view_etc.SettingsFragment;
import ru.gsench.passwordmanager.presentation.viewholder.SettingsViewHolder;

/**
 * Created by grish on 08.04.2017.
 */

public class SettingsActivity extends AppCompatActivity implements SettingsView {

    public static final int SETTINGS = 1;

    private SettingsViewHolder viewHolder;
    private AViewContainer container;
    private SettingsFragment settingsFragment;
    private SettingsPresenter presenter;
    private KeyboardPref keyboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);
        viewHolder = new SettingsViewHolder(this);
        container = new AViewContainer(viewHolder.dialogContent);
        presenter = new SettingsPresenter(new AndroidInterface(this));
        presenter.setView(this);
        presenter.start();
    }

    @Override
    public void init() {
        settingsFragment = new SettingsFragment();
        settingsFragment.init(
                new function() {
                    @Override
                    public void run(String... params) {
                        KeyboardPref.saveKeyboardPref(Boolean.parseBoolean(params[0]), SettingsActivity.this);
                    }
                },
                new function() {
                    @Override
                    public void run(String... params) {
                        presenter.onBasePrefBtn();
                    }
                },
                new function() {
                    @Override
                    public void run(String... params) {
                        presenter.onKeyPrefBtn();
                    }
                },
                new function() {
                    @Override
                    public void run(String... params) {
                        presenter.onPINPrefBtn();
                    }
                }
        );
        getFragmentManager().beginTransaction().replace(R.id.settings_list, settingsFragment).commit();
        keyboard = new KeyboardPref(this, (KeyboardView) findViewById(R.id.keyboard_view), true);
        keyboard.enableHapticFeedback(true);
    }

    @Override
    public void exit() {
        finish();
    }

    @Override
    public void newKeyView(NewKeyPresenter presenter) {
        NewKeyAView aView = new NewKeyAView(container, presenter);
        keyboard.registerEditText(aView.viewHolder.keyEdit, true);
        aView.open();
    }

    @Override
    public void newPINView(NewPINPresenter presenter) {
        new NewPINAView(container, presenter).open();
    }

    @Override
    public void openPINInputView(PINInputPresenter presenter) {
        new PINInputAView(container, presenter).open();
    }

    @Override
    public void keyInputView(KeyInputPresenter presenter) {
        KeyInputAView aView = new KeyInputAView(container, presenter);
        keyboard.registerEditText(aView.viewHolder.keyEdit, true);
        aView.open();
    }

    @Override
    public boolean isViewOpened() {
        return container.viewOpened();
    }

    @Override
    public void closeCurrentView() {
        container.closeView();
    }

    @Override
    public void onSaveBaseErrorMsg() {
        Toast.makeText(this, R.string.unable_to_edit_file, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        if(keyboard.onBackHandle()) return;
        presenter.onBackPressed();
    }

}
