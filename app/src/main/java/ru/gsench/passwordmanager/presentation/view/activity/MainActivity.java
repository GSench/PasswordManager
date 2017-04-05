package ru.gsench.passwordmanager.presentation.view.activity;

import android.content.Intent;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import ru.gsench.passwordmanager.R;
import ru.gsench.passwordmanager.domain.utils.function;
import ru.gsench.passwordmanager.presentation.AndroidInterface;
import ru.gsench.passwordmanager.presentation.presenter.AccountListPresenter;
import ru.gsench.passwordmanager.presentation.presenter.CoordinatorPresenter;
import ru.gsench.passwordmanager.presentation.presenter.EditAccountPresenter;
import ru.gsench.passwordmanager.presentation.presenter.KeyInputPresenter;
import ru.gsench.passwordmanager.presentation.presenter.NewKeyPresenter;
import ru.gsench.passwordmanager.presentation.presenter.NewPINPresenter;
import ru.gsench.passwordmanager.presentation.presenter.PINInputPresenter;
import ru.gsench.passwordmanager.presentation.presenter.SelectBasePresenter;
import ru.gsench.passwordmanager.presentation.utils.AViewContainer;
import ru.gsench.passwordmanager.presentation.utils.CustomKeyboard;
import ru.gsench.passwordmanager.presentation.view.CoordinatorView;
import ru.gsench.passwordmanager.presentation.view.aview.AccountListAView;
import ru.gsench.passwordmanager.presentation.view.aview.EditAccountAView;
import ru.gsench.passwordmanager.presentation.view.aview.KeyInputAView;
import ru.gsench.passwordmanager.presentation.view.aview.NewKeyAView;
import ru.gsench.passwordmanager.presentation.view.aview.NewPINAView;
import ru.gsench.passwordmanager.presentation.view.aview.PINInputAView;
import ru.gsench.passwordmanager.presentation.view.aview.SelectBaseAView;
import ru.gsench.passwordmanager.presentation.view.view_etc.PermissionManager;
import ru.gsench.passwordmanager.presentation.viewholder.MainViewHolder;

public class MainActivity extends AppCompatActivity implements CoordinatorView {

    /**TODO App Preferences
     * - keyboard option
     * - base reselection
     * - pin setting
     * - key setting
     * */
    //TODO Passwords' categories
    //TODO Search

    public static final String APP_PREFERENCES = "AppPreferences";

    private MainViewHolder viewHolder;
    private CustomKeyboard keyboard;
    private PermissionManager permissionManager;
    private AViewContainer container;
    private AccountListAView accountListView;

    private CoordinatorPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewHolder = new MainViewHolder(this);
        container = new AViewContainer(viewHolder.dialogContent);
        permissionManager = new PermissionManager(this);
        permissionManager.requestBasePermissions(this, new function() {
            @Override
            public void run(String... params) {
                presenter = new CoordinatorPresenter(new AndroidInterface(MainActivity.this));
                presenter.setView(MainActivity.this);
                presenter.start();
            }
        });
    }

    @Override
    public void init() {
        keyboard = new CustomKeyboard(this, (KeyboardView) findViewById(R.id.keyboard_view), true);
        keyboard.enableHapticFeedback(true);
    }

    @Override
    public void exit() {
        finish();
    }

    @Override
    public void openAccountList(AccountListPresenter presenter) {
        if(accountListView==null){
            accountListView = new AccountListAView(new AViewContainer(viewHolder.accountsContent), presenter);
            accountListView.open();
        } else accountListView.updateAccounts();
    }

    @Override
    public void keyInputView(KeyInputPresenter presenter) {
        KeyInputAView aView = new KeyInputAView(container, presenter);
        keyboard.registerEditText(aView.viewHolder.keyEdit, true);
        aView.open();
    }

    /**
    @Override
    public void unableToEditBaseFile() {
        Toast.makeText(this, R.string.unable_to_edit_file, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void unableToReadBaseFile() {
        Toast.makeText(this, R.string.unable_to_read_file, Toast.LENGTH_SHORT).show();
    }*/

    @Override
    public void closeCurrentView() {
        container.closeView();
    }

    @Override
    public void selectBaseView(SelectBasePresenter presenter) {
        new SelectBaseAView(container, presenter).open();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void newKeyView(NewKeyPresenter presenter) {
        NewKeyAView aView = new NewKeyAView(container, presenter);
        keyboard.registerEditText(aView.viewHolder.keyEdit, true);
        aView.open();
    }

    @Override
    public void openPINInputView(PINInputPresenter presenter) {
        new PINInputAView(container, presenter).open();
    }

    @Override
    public void newPINView(NewPINPresenter presenter){
        new NewPINAView(container, presenter).open();
    }

    @Override
    public boolean isViewOpened() {
        return container.viewOpened();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        permissionManager.onPermissionCallback(requestCode, permissions, grantResults);
    }

    @Override
    public void editAccountView(EditAccountPresenter presenter){
        EditAccountAView view = new EditAccountAView(container, presenter);
        keyboard.registerEditText(view.aViewHolder.editLogin, true);
        keyboard.registerEditText(view.aViewHolder.editName, true);
        keyboard.registerEditText(view.aViewHolder.editPassword, true);
        view.open();
    }

    @Override
    public void onBackPressed() {
        if(keyboard.onBackHandle()) return;
        presenter.onBackPressed();
    }

}
