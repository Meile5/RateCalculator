package easv.ui.pages.geographyManagementPage.countryComponents;

import easv.Utility.WindowsManagement;
import easv.be.Country;
import easv.exception.ErrorCode;
import easv.ui.components.confirmationView.ConfirmationWindowController;
import easv.ui.components.confirmationView.OperationHandler;
import easv.ui.pages.modelFactory.IModel;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DeleteCountryController implements Initializable, OperationHandler {

    @FXML
    private VBox deleteComponent;
    @FXML
    private VBox deleteContainer;
    private StackPane firstLayout;
    private IModel model;
    private Country country;
    private VBox employeesContainer;
    private HBox employeeComponent;
    private ConfirmationWindowController confirmationWindowController;
    private Service<Void> deleteEmployee;

    public DeleteCountryController(StackPane firstLayout , IModel model, Country country) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("DeleteCountryComponent.fxml"));
        loader.setController(this);
        this.firstLayout=firstLayout;
        this.model= model;
        this.country = country;
        try {
            deleteComponent = loader.load();
           // this.deleteContainer = deleteContainer;

        } catch (IOException e) {
            //ExceptionHandler.errorAlertMessage(ErrorCode.LOADING_FXML_FAILED.getValue());
        }

    }


    public VBox getRoot() {
        return deleteComponent;
    }

    private void deleteOperation() {
        firstLayout.getChildren().clear();
        ConfirmationWindowController confirmationWindowController = new ConfirmationWindowController(firstLayout, this);
        firstLayout.getChildren().add(confirmationWindowController.getRoot());
        firstLayout.setDisable(false);
        firstLayout.setVisible(true);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        deleteComponent.addEventHandler(MouseEvent.MOUSE_CLICKED, this::addEventHandler);
    }
    private void addEventHandler(MouseEvent event) {
        deleteOperation();
    }

    @Override
    public void performOperation() {
        initializeDelete();


    }
    private void initializeDelete() {

        deleteEmployee = new Service<Void>() {

            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        Thread.sleep(2000);
                        //model.deleteEmployee(country);
                        return null;
                    }
                };
            }
        };


        deleteEmployee.setOnSucceeded(event -> WindowsManagement.closeStackPane(firstLayout));

        deleteEmployee.setOnFailed(event -> {
            confirmationWindowController.setErrorMessage(ErrorCode.DELETING_EMPLOYEES_FAILED.getValue());
        });

        deleteEmployee.restart();
    }

}

