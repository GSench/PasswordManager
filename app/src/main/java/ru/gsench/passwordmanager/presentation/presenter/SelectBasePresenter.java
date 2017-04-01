package ru.gsench.passwordmanager.presentation.presenter;

import android.os.Environment;

import java.io.File;

import ru.gsench.passwordmanager.domain.interactor.SelectBaseUseCase;
import ru.gsench.passwordmanager.presentation.view.SelectBaseView;

/**
 * Created by grish on 26.03.2017.
 */

public class SelectBasePresenter {

    private final String defaultBaseFileName = "base.xml";
    private final String defaultBaseFilePath = new File(Environment.getExternalStorageDirectory(), "Password Manager/"+defaultBaseFileName).getAbsolutePath();

    private SelectBaseUseCase interactor;
    private SelectBaseView view;

    public SelectBasePresenter(SelectBaseUseCase interactor, SelectBaseView view){
        this.interactor=interactor;
        this.view=view;
    }

    public void start(){
        view.init();
    }

    public void onExistBaseBtn(){
        view.openRequestFileDialog();
    }

    public void onNewBaseBtn(){
        view.openRequestDirDialog();
    }

    public void onDefBaseBtn(){
        view.closeView();
        interactor.onNewBaseSelected(defaultBaseFilePath, this);
    }

    public void onFileSelected(String path){
        view.closeView();
        interactor.onExistingBaseSelected(path);
    }

    public void onDirSelected(String path){
        view.closeView();
        interactor.onNewBaseSelected(new File(new File(path), defaultBaseFileName).getAbsolutePath(), this);
    }

    public void unableToEditBaseFile(){
        view.showUnableToEditBaseFileToast();
    }

}
