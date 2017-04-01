package ru.gsench.passwordmanager.presentation.view.aview;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;

import java.io.File;

import ru.gsench.passwordmanager.R;
import ru.gsench.passwordmanager.domain.interactor.MainInteractor;
import ru.gsench.passwordmanager.domain.interactor.SelectBaseUseCase;
import ru.gsench.passwordmanager.presentation.presenter.SelectBasePresenter;
import ru.gsench.passwordmanager.presentation.utils.AView;
import ru.gsench.passwordmanager.presentation.utils.AViewContainer;
import ru.gsench.passwordmanager.presentation.view.SelectBaseView;
import ru.gsench.passwordmanager.presentation.viewholder.SelectBaseViewHolder;
import ru.gsench.passwordmanager.domain.utils.function;

/**
 * Created by grish on 11.03.2017.
 */

public class SelectBaseAView extends AView implements SelectBaseView {

    private SelectBaseViewHolder viewHolder;
    private SelectBasePresenter presenter;

    public SelectBaseAView(AViewContainer container, SelectBaseUseCase interactor){
        super(container);
        viewHolder = new SelectBaseViewHolder(context, parent);
        presenter = new SelectBasePresenter(interactor, this);
    }

    @Override
    public void start() {
        presenter.start();
    }

    @Override
    public void init() {
        viewHolder.chooseBase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onExistBaseBtn();
            }
        });
        viewHolder.chooseNewBase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onNewBaseBtn();
            }
        });
        viewHolder.useDefBase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onDefBaseBtn();
            }
        });
    }

    @Override
    public void openRequestFileDialog() {
        requestFile(DialogConfigs.FILE_SELECT, SelectBaseAView.this.context.getString(R.string.select_file), new function() {
            @Override
            public void run(String... params) {
                presenter.onFileSelected(params[0]);
            }
        });
    }

    @Override
    public void openRequestDirDialog() {
        requestFile(DialogConfigs.DIR_SELECT, SelectBaseAView.this.context.getString(R.string.select_path), new function() {
            @Override
            public void run(String... params) {
                presenter.onDirSelected(params[0]);
            }
        });
    }

    @Override
    public void closeView() {
        closeSelf();
    }

    @Override
    public void showUnableToEditBaseFileToast() {
        Toast.makeText(context, R.string.unable_to_read_file, Toast.LENGTH_SHORT).show();
    }

    private void requestFile(int selection_type, String title, final function doAfter){
        DialogProperties properties = new DialogProperties();
        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.selection_type = selection_type;
        properties.root = new File(DialogConfigs.DEFAULT_DIR);
        properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
        properties.offset = new File(DialogConfigs.DEFAULT_DIR);
        properties.extensions = null;
        FilePickerDialog dialog = new FilePickerDialog(context, properties);
        dialog.setTitle(title);
        dialog.setDialogSelectionListener(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(String[] files) {
                if(files==null||files.length==0) return;
                doAfter.run(files[0]);
            }
        });
        dialog.show();
    }

    @Override
    public ViewGroup getView(){
        return viewHolder.main;
    }

}
