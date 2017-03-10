package ru.gsench.passwordmanager.windows;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;

import java.io.File;

import ru.gsench.passwordmanager.R;
import utils.function;

/**
 * Created by grish on 11.03.2017.
 */

public class SelectBaseWindow {

    private Context context;
    private ViewGroup main;

    public SelectBaseWindow(Context context, ViewGroup parent, final function onBaseSelected, final function onNewBaseSelected, final function onDefBaseSelected){
        this.context=context;
        main = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.choose_base_dialog, parent, false);
        main.findViewById(R.id.choose_existing_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestFile(DialogConfigs.FILE_SELECT, SelectBaseWindow.this.context.getString(R.string.select_file), onBaseSelected);
            }
        });
        main.findViewById(R.id.choose_new_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestFile(DialogConfigs.DIR_SELECT, SelectBaseWindow.this.context.getString(R.string.select_path), onNewBaseSelected);
            }
        });
        main.findViewById(R.id.use_default_btn).setOnClickListener(new View.OnClickListener() {
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

    public ViewGroup getView(){
        return main;
    }
}
