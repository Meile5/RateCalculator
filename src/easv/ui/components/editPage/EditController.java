package easv.ui.components.editPage;

import easv.exception.ErrorCode;
import easv.exception.ExceptionHandler;
import easv.ui.pages.modelFactory.IModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;

import java.beans.EventHandler;
import java.io.IOException;

public class EditController {
    @FXML
    private HBox editRoot;


    private IModel model;



    public EditController(IModel model) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Edit.fxml"));
        loader.setController(this);
        this.model=model;
        try{
            editRoot= loader.load();
        }catch (IOException e){
            ExceptionHandler.errorAlertMessage(ErrorCode.INVALID_INPUT.getValue());
        }
    }

    public HBox getRoot() {
        return editRoot;
    }
}

