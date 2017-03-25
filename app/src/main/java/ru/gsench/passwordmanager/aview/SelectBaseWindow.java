package ru.gsench.passwordmanager.aview;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;

import java.io.File;

import ru.gsench.passwordmanager.R;
import ru.gsench.passwordmanager.viewholder.SelectBaseViewHolder;
import utils.function;

/**
 * Created by grish on 11.03.2017.
 */

public class SelectBaseWindow extends AView {

    private SelectBaseViewHolder viewHolder;

    public SelectBaseWindow(Context context, ViewGroup parent, final function onBaseSelected, final function onNewBaseSelected, final function onDefBaseSelected){
        super(context, parent);
        viewHolder = new SelectBaseViewHolder(context, parent);
        viewHolder.chooseBase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestFile(DialogConfigs.FILE_SELECT, SelectBaseWindow.this.context.getString(R.string.select_file), onBaseSelected);
            }
        });
        viewHolder.chooseNewBase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestFile(DialogConfigs.DIR_SELECT, SelectBaseWindow.this.context.getString(R.string.select_path), onNewBaseSelected);
            }
        });
        viewHolder.useDefBase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDefBaseSelected.run();
            }
        });
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
