package easv.ui.pages.employeesPage.employeeMainPage;

import easv.Utility.DisplayEmployees;
import easv.be.Country;
import easv.be.Employee;
import easv.be.Team;
import easv.exception.ErrorCode;
import easv.exception.ExceptionHandler;
import easv.exception.RateException;
import easv.ui.pages.employeesPage.deleteEmployee.DeleteEmployeeController;
import easv.ui.pages.modelFactory.ModelFactory;
import easv.ui.pages.employeesPage.employeeInfo.EmployeeInfoController;
import easv.ui.pages.modelFactory.IModel;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class EmployeeMainPageController implements Initializable, DisplayEmployees {
    @FXML
    private VBox employeesContainer;
    @FXML
    private Parent employeePage;

    private IModel model;

    @FXML
    private MFXProgressSpinner progressBar;
    @FXML
    private MFXComboBox countriesFilterCB, teamsFilterCB;

    public VBox getEmployeesContainer() {
        return employeesContainer;
    }

    private StackPane firstLayout;
    @FXML
    private PopupControl popupWindow;
    @FXML
    private TextField searchField, dayRateField, hourlyRateField;
    @FXML
    private ListView<Employee> searchResponseHolder;
    @FXML
    private Button goBackButton;
    private Service<Void> loadEmployeesFromDB;
    @FXML
    private HBox countryRevert;
    @FXML
    private HBox teamRevert;
    @FXML
    private SVGPath svgPath;
    @FXML
    private SVGPath countriesSvgPath;
    @FXML
    private SVGPath teamsSvgPath;
    private boolean filterActive = false;
    @FXML
    private SVGPath teamRevertSvg;
    @FXML
    private SVGPath countryRevertSvg;


    private EmployeeInfoController selectedToEdit;


    public EmployeeMainPageController(StackPane firstLayout) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("EmployeesMainPage.fxml"));
        loader.setController(this);
        this.firstLayout = firstLayout;
        try {
            employeePage = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
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
            loadSearchSVG();
            initializeEmployeeLoadingService();
            createPopUpWindow();
            searchFieldListener();
            addSelectionListener();
            populateFilterComboBox();
            filterByCountryListener();
            filterByTeamListener();
            addFocusListener(countriesFilterCB, countryRevert);
            addFocusListener(teamsFilterCB, teamRevert);
            revertCountryFilter(countryRevertSvg);
            revertTeamFilter(teamRevertSvg);
         } catch (RateException e) {
            ExceptionHandler.errorAlertMessage(ErrorCode.LOADING_FXML_FAILED.getValue());
        }
    }

    public void displayEmployees() {
        employeesContainer.getChildren().clear();
        model.getUsersToDisplay()
                .forEach(e -> {
                    DeleteEmployeeController deleteEmployeeController = new DeleteEmployeeController(firstLayout, model, e);
                    EmployeeInfoController employeeInfoController = new EmployeeInfoController(e, deleteEmployeeController, model, firstLayout, this);
                    employeesContainer.getChildren().add(employeeInfoController.getRoot());
                    setTotalRates();
                });
    }


    private void initializeEmployeeLoadingService() {
        progressBar.setVisible(true);
        loadEmployeesFromDB = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        model.returnEmployees();
                        return null;
                    }

                    ;
                };
            }
        };

        loadEmployeesFromDB.setOnSucceeded((event) -> {
            // Update the UI with loaded employees
            displayEmployees();
            // Hide the progress bar
            progressBar.setVisible(false);
        });
        loadEmployeesFromDB.setOnFailed((event) -> {

            ExceptionHandler.errorAlertMessage(ErrorCode.LOADING_EMPLOYEES_FAILED.getValue());
        });
        loadEmployeesFromDB.restart();
    }

    // Method to load the first SVG path
    private void loadSearchSVG() {
        String defaultSVGPath = "M269.46,1163.45 C263.17,1163.45 258.071,1158.44 258.071,1152.25 C258.071,1146.06 263.17,1141.04 269.46,1141.04 C275.75,1141.04 280.85,1146.06 280.85,1152.25 C280.85,1158.44 275.75,1163.45 269.46,1163.45 L269.46,1163.45 Z M287.688,1169.25 L279.429,1161.12 C281.591,1158.77 282.92,1155.67 282.92,1152.25 C282.92,1144.93 276.894,1139 269.46,1139 C262.026,1139 256,1144.93 256,1152.25 C256,1159.56 262.026,1165.49 269.46,1165.49 C272.672,1165.49 275.618,1164.38 277.932,1162.53 L286.224,1170.69 C286.629,1171.09 287.284,1171.09 287.688,1170.69 C288.093,1170.3 288.093,1169.65 287.688,1169.25 L287.688,1169.25 Z";
        svgPath.getStyleClass().clear();
        svgPath.getStyleClass().add("svg-search");
        svgPath.setContent(defaultSVGPath);
    }

    // Method to load the second SVG path
    private void loadRevertSVG() {
        String alternativeSVGPath = "M 15 3 L 10 7 L 15 11 L 15 8 C 18.877838 8 22 11.12216 22 15 C 22 18.87784 18.877838 22 15 22 C 11.122162 22 8 18.87784 8 15 C 8 13.485854 8.4798822 12.090114 9.2910156 10.947266 L 7.8730469 9.5292969 C 6.7042423 11.047902 6 12.942076 6 15 C 6 19.95872 10.041282 24 15 24 C 19.958718 24 24 19.95872 24 15 C 24 10.04128 19.958718 6 15 6 L 15 3 z ";
        svgPath.getStyleClass().clear();
        svgPath.getStyleClass().add("svg-revert");
        svgPath.setContent(alternativeSVGPath);
    }

    private void loadRevertSVGCountries() {
        String alternativeSVGPath = "M 15 3 L 10 7 L 15 11 L 15 8 C 18.877838 8 22 11.12216 22 15 C 22 18.87784 18.877838 22 15 22 C 11.122162 22 8 18.87784 8 15 C 8 13.485854 8.4798822 12.090114 9.2910156 10.947266 L 7.8730469 9.5292969 C 6.7042423 11.047902 6 12.942076 6 15 C 6 19.95872 10.041282 24 15 24 C 19.958718 24 24 19.95872 24 15 C 24 10.04128 19.958718 6 15 6 L 15 3 z ";
        countriesSvgPath.getStyleClass().clear();
        countriesSvgPath.getStyleClass().add("svg-revert");
        countriesSvgPath.setContent(alternativeSVGPath);
    }

    private void loadRevertSVGTeams() {
        String alternativeSVGPath = "M 15 3 L 10 7 L 15 11 L 15 8 C 18.877838 8 22 11.12216 22 15 C 22 18.87784 18.877838 22 15 22 C 11.122162 22 8 18.87784 8 15 C 8 13.485854 8.4798822 12.090114 9.2910156 10.947266 L 7.8730469 9.5292969 C 6.7042423 11.047902 6 12.942076 6 15 C 6 19.95872 10.041282 24 15 24 C 19.958718 24 24 19.95872 24 15 C 24 10.04128 19.958718 6 15 6 L 15 3 z ";
        teamsSvgPath.getStyleClass().clear();
        teamsSvgPath.getStyleClass().add("svg-revert");
        teamsSvgPath.setContent(alternativeSVGPath);
    }

    @FXML
    private void goBack() throws RateException {
        if (filterActive) {
            model.teamFilterActiveRevert();

        } else {
            model.performEmployeeSearchUndoOperation();
        }
        searchField.clear();
        Platform.runLater(this::loadSearchSVG);
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
        popupWindow.getScene().getStylesheets().add("/easv/ui/styling/EmployeePage.css");
        ((Parent) popupWindow.getScene().getRoot()).getStyleClass().add("popupView");
        searchResponseHolder.getStylesheets().add("/easv/ui/styling/EmployeePage.css");
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
                loadRevertSVG();
                searchField.clear();
                popupWindow.hide();
            }

        });
        searchField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                popupWindow.hide();
            }
        });
    }

    private void populateFilterComboBox() {
        ObservableList<Country> countries = model.getCountiesValues();
        ObservableList<Team> teams = FXCollections.observableArrayList(model.getTeams().values());

        countriesFilterCB.setItems(countries);
        teamsFilterCB.setItems(teams);
    }

    private void filterByCountryListener() {
        countriesFilterCB.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                try {
                    model.filterByCountry((Country) newValue);
                    filterActive = true;
                    setTotalRates();
                    showRevertButtonByFilterActive(countryRevertSvg);
                } catch (RateException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void filterByTeamListener() {
        teamsFilterCB.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                try {
                    model.filterByTeam((Team) newValue);
                    filterActive = true;
                    setTotalRates();
                    showRevertButtonByFilterActive(teamRevertSvg);
                } catch (RateException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void filterByTeamAndCountryListener() {

    }

    public void setTotalRates() {
        dayRateField.setText(model.calculateGroupDayRate().toString());
        hourlyRateField.setText(model.calculateGroupHourlyRate().toString());
    }

    private void addSelectionListener() throws RateException {
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
                loadRevertSVG();
                popupWindow.hide();
            }
        });
    }


    public void setSelectedComponentStyleToSelected(EmployeeInfoController selectedToEdit) {
        if (this.selectedToEdit != null) {
            this.selectedToEdit.getRoot().getStyleClass().remove("employeeComponentClicked");
        }
        this.selectedToEdit = selectedToEdit;
        this.selectedToEdit.getRoot().getStyleClass().add("employeeComponentClicked");
    }


    @FXML
    private void goBackFromCountries() throws RateException {
        model.performEmployeeSearchUndoOperation();
        searchField.clear();
 //       countriesSvgPath.setContent("");
    }

    @FXML
    private void goBackFromTeams() throws RateException {
        model.performEmployeeSearchUndoOperation();
        searchField.clear();
   //     teamsSvgPath.setContent("");
    }


    private void addFocusListener(MFXTextField filterInput, HBox sibling) {
        filterInput.focusWithinProperty().addListener((obs, wasFocused, isNowFocused) -> {
            System.out.println("Focus changed for filterInput: " + isNowFocused);
            if (isNowFocused) {
                sibling.getStyleClass().add("countryFilterFocused");
            } else {
                sibling.getStyleClass().remove("countryFilterFocused");
            }
        });
    }


    private void showRevertButtonByFilterActive(SVGPath revertSvg) {
        System.out.println("called");
        revertSvg.setVisible(true);
    }

    private void hideRevertButton(SVGPath svgPath) {
        PauseTransition pauseTransition = new PauseTransition(Duration.millis(500));
        pauseTransition.setOnFinished((event) -> {
            svgPath.setVisible(false);
        });
        pauseTransition.playFromStart();
    }

    private void revertCountryFilter(SVGPath svgPath) throws RateException{
        svgPath.addEventHandler(MouseEvent.MOUSE_CLICKED,event->{
            try {
                goBackFromCountries();
                hideRevertButton(svgPath);
                System.out.println("clicked on country");
            } catch (RateException e) {
                throw new RuntimeException();
            }
        });
    }

    private void revertTeamFilter(SVGPath svgPath) throws RateException{
        svgPath.addEventHandler(MouseEvent.MOUSE_CLICKED,event->{
            try {
                goBackFromTeams();
                hideRevertButton(svgPath);
            } catch (RateException e) {
                throw new RuntimeException();
            }
        });
    }

}




