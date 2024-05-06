package easv.ui.pages.employeesPage.employeeMainPage;
import easv.Utility.DisplayEmployees;
import easv.be.Employee;
import easv.exception.ErrorCode;
import easv.exception.ExceptionHandler;
import easv.exception.RateException;
import easv.ui.pages.employeesPage.deleteEmployee.DeleteEmployeeController;
import easv.ui.pages.modelFactory.ModelFactory;
import easv.ui.pages.employeesPage.employeeInfo.EmployeeInfoController;
import easv.ui.pages.modelFactory.IModel;
import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;

public class EmployeeMainPageController implements Initializable , DisplayEmployees {
    @FXML
    private VBox employeesContainer;
    @FXML
    private Parent employeePage;

    private IModel model;

    @FXML
    private MFXProgressSpinner progressBar;

    public VBox getEmployeesContainer() {
        return employeesContainer;
    }

    private StackPane firstLayout;
    @FXML
    private PopupControl popupWindow;
    @FXML
    private TextField searchField;
    @FXML
    private ListView<Employee> searchResponseHolder;
    @FXML
    private Button goBackButton;
    private Service<Void> loadEmployeesFromDB;
    private final Image revertImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/easv/resources/images/revert.png")));
    private final Image searchImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/easv/resources/images/search (1).png")));



    private EmployeeInfoController selectedToEdit;


    public EmployeeMainPageController(StackPane firstLayout) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("EmployeesMainPage.fxml"));
        loader.setController(this);
        this.firstLayout = firstLayout;
        try {
            employeePage = loader.load();
        } catch (IOException e) {

            ExceptionHandler.errorAlertMessage(ErrorCode.LOADING_FXML_FAILED.getValue());
        }

    }

    public Parent getRoot() {
        return employeePage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {

            model = ModelFactory.createModel(ModelFactory.ModelType.NORMAL_MODEL);
            progressBar.setVisible(true);
            showSearchImage();
            initializeEmployeeLoadingService();

            createPopUpWindow();
            searchFieldListener();
            addSelectionListener();


        } catch (RateException e) {
            ExceptionHandler.errorAlertMessage(ErrorCode.LOADING_FXML_FAILED.getValue());
        }
    }
    public void displayEmployees() {
        employeesContainer.getChildren().clear();
        model.getUsersToDisplay()
                .forEach(e -> {
                    DeleteEmployeeController deleteEmployeeController = new DeleteEmployeeController(firstLayout, model, e);
                    EmployeeInfoController employeeInfoController = new EmployeeInfoController(e, deleteEmployeeController, model, firstLayout,this);
                    employeesContainer.getChildren().add(employeeInfoController.getRoot());
                });
    }



    private void initializeEmployeeLoadingService(){
        progressBar.setVisible(true);
        loadEmployeesFromDB= new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        model.returnEmployees();
                        return null;
                    };
                };
            }
        };

        loadEmployeesFromDB.setOnSucceeded((event) -> {
            // Update the UI with loaded employees
            displayEmployees();

            // Hide the progress bar
            progressBar.setVisible(false);
        });
        loadEmployeesFromDB.setOnFailed((event)->{
            loadEmployeesFromDB.getException().printStackTrace();
            ExceptionHandler.errorAlertMessage(ErrorCode.LOADING_EMPLOYEES_FAILED.getValue());
        });
        loadEmployeesFromDB.restart();
    }

    private void showRevertImage(){
        goBackButton.setGraphic(new ImageView(revertImage));
    }
    private void showSearchImage(){
        goBackButton.setGraphic(new ImageView(searchImage));
    }

    @FXML
    private void goBack() throws RateException {
        model.performEmployeeSearchUndoOperation();
        System.out.println("nnn");
        Platform.runLater(this::showSearchImage);

    }


    private void createPopUpWindow() {
        popupWindow = new PopupControl();
        searchResponseHolder = new ListView<>();
        popupWindow.getScene().setRoot(searchResponseHolder);
    }

    private void configurePopUpWindow() {
        Bounds boundsInScreen = searchField.localToScreen(searchField.getBoundsInLocal());
        searchResponseHolder.setPrefWidth(searchField.getWidth());
        searchResponseHolder.setMaxWidth(searchField.getWidth());
        searchResponseHolder.setMaxHeight(250);
        popupWindow.setPrefWidth(searchField.getWidth());
        popupWindow.setMaxWidth(searchField.getWidth());
        popupWindow.setMaxHeight(250);
        popupWindow.show(searchField, boundsInScreen.getMinX(), boundsInScreen.getMaxY());
    }

    private void searchFieldListener() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.isEmpty()) {
                    searchResponseHolder.setItems(model.getSearchResult(newValue));
                    if (!searchResponseHolder.getItems().isEmpty()) {
                        configurePopUpWindow();
                    } else {

                        popupWindow.hide();

                    }
                } else {
                    showRevertImage();
                    popupWindow.hide();
                }

        });
        searchField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                popupWindow.hide();
            }
        });
    }

    private void addSelectionListener() throws RateException  {
        searchResponseHolder.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                try {
                    model.performSelectUserSearchOperation(newValue);
                } catch (RateException e) {
                    ExceptionHandler.errorAlertMessage(ErrorCode.INVALID_INPUT.getValue());
                }

                Platform.runLater(() -> {
                    if (!searchResponseHolder.getItems().isEmpty()) {
                        searchResponseHolder.getSelectionModel().clearSelection();
                    }
                });
                showRevertImage();
                popupWindow.hide();
            }
        });
    }


public void setSelectedComponentStyleToSelected(EmployeeInfoController selectedToEdit){
        if(this.selectedToEdit!=null){
            this.selectedToEdit.getRoot().getStyleClass().remove("employeeComponentClicked");
        }
        this.selectedToEdit=selectedToEdit;
        this.selectedToEdit.getRoot().getStyleClass().add("employeeComponentClicked");
}


}




