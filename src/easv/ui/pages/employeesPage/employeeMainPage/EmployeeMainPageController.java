package easv.ui.pages.employeesPage.employeeMainPage;

import easv.Utility.DisplayEmployees;
import easv.be.Country;
import easv.be.Employee;
import easv.be.Region;
import easv.be.Team;
import easv.exception.ErrorCode;
import easv.exception.ExceptionHandler;
import easv.exception.RateException;
import easv.ui.components.searchComponent.EmployeeSearchHandler;
import easv.ui.components.searchComponent.SearchController;
import easv.ui.pages.employeesPage.deleteEmployee.DeleteEmployeeController;
import easv.ui.pages.modelFactory.ModelFactory;
import easv.ui.pages.employeesPage.employeeInfo.EmployeeInfoController;
import easv.ui.pages.modelFactory.IModel;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

//TODO  redo the calculations when the filter and the undo buttons are active,

public class EmployeeMainPageController implements Initializable, DisplayEmployees {
    @FXML
    private VBox employeesContainer;
    @FXML
    private Parent employeePage;

    private IModel model;

    @FXML
    private MFXProgressSpinner progressBar;
    @FXML
    private MFXComboBox<Country> countriesFilterCB;

    @FXML
    private MFXComboBox<Team> teamsFilterCB;

    @FXML
    private MFXComboBox<Region> regionsFilter;
    @FXML
    private MFXScrollPane employeesScrollPane;

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
    private HBox countryRevertButton;
    @FXML
    private HBox teamRevertButton;
    @FXML
    private HBox regionRevertButton;
    @FXML
    private VBox employeesVboxContainer;


    //Todo remove this if not needed and also the methods that are commented
    @FXML
    private SVGPath svgPathButton;
    private boolean filterActive = false;

    @FXML
    private boolean regionFilterActive = false;
    @FXML
    private boolean countryFilterActive = false;
    @FXML
    private SVGPath teamRevertSvg;
    @FXML
    private SVGPath countryRevertSvg;
    @FXML
    private SVGPath regionRevertSvg;
    @FXML
    private SVGPath svgPath;
    private ObservableList<Team> teams;
    private ObservableList<Country> countries;
    private EmployeeInfoController selectedToEdit;
    private Service<Boolean> calculateEditOperationPerformedEdit;
    private String dayRateValue;
    private String hourlyRateValue;
    @FXML
    private GridPane employeeSearchContainer;
    private SearchController<Employee> employeeSearch;
    private EmployeeSearchHandler employeeSearchHandler;

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
            employeeSearchHandler = new EmployeeSearchHandler(model);
            employeeSearch = new SearchController<>(employeeSearchHandler);
            employeeSearchContainer.add(employeeSearch.getSearchRoot(), 0, 0);
            //loadSearchSVG();
            initializeEmployeeLoadingService();
            // createPopUpWindow();
            //searchFieldListener();
            // addSelectionListener();
            //
            /*populate the filter combo boxes  with the associated values*/
            populateFilterComboBox();
            /*add listener for the region to change the countries filter combo box  values  */
            addRegionFilterListener();
            /* add the listener that will change the team list based on the selected country */
            addCountryFilterListener();
            /*add teams filter listener that will change the displayed employees based on the selected team*/
            addTeamFilterListener();
            /*undo the region filter */
            revertRegionFilter(regionRevertButton, regionRevertSvg);

            /*undo the country filter*/
            revertCountryFilter(countryRevertButton, countryRevertSvg);
            revertTeamFilter(teamRevertButton,teamRevertSvg);


            // filterByCountryListener();
            //filterByTeamListener();

            /*change the style for the revert button to have the same style like the filter inputs , when are on focus  */
            addFocusListener(countriesFilterCB, countryRevertButton);
            addFocusListener(teamsFilterCB, teamRevertButton);

            addFocusListener(regionsFilter, regionRevertButton);

            /*undo the filter operation*/
//
//            revertCountryFilter(countryRevertButton, countryRevertSvg);
//            revertTeamFilter(teamRevertButton, teamRevertSvg);


            //setTotalRatesDefault();
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


    //rename this classes
    private void addFocusListener(MFXTextField filterInput, HBox sibling) {
        filterInput.focusWithinProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (isNowFocused) {
                sibling.getStyleClass().add("countryFilterFocused");
            } else {
                sibling.getStyleClass().remove("countryFilterFocused");
            }
        });
    }

    /**
     * populate filter combo boxes with values
     */
    private void populateFilterComboBox() {
        countriesFilterCB.setItems(model.getOperationalCountries().sorted());
        teamsFilterCB.setItems(model.getOperationalTeams().sorted());
        regionsFilter.setItems(model.getOperationalRegions().sorted());
    }

    /**show the rates off the selected filter teams*/
    public void setTotalRates(){
        System.out.println(model.calculateGroupDayRate()+" day rate");
        dayRateField.setText(model.calculateGroupDayRate().toString());
        hourlyRateField.setText(model.calculateGroupHourRate()+"");
        System.out.println(model.calculateGroupHourRate() + "hourly rate");
    }


    /**
     * add  region selection listener  that will filter the countries by regions and teams by countries in the region
     */
    private void addRegionFilterListener() {
        this.regionsFilter.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                regionFilterActive = true;
                ObservableList<Country> regionCountries = FXCollections.observableArrayList(newValue.getCountries());
                if (this.countriesFilterCB.getSelectionModel().getSelectedItem() != null) {
                    this.countriesFilterCB.clearSelection();
                }
                this.countriesFilterCB.setItems(regionCountries);
                //save the selected countries in the model , in order to be displayed , change the name toFilterByRegion
                countriesFilterCB.selectItem(regionCountries.getFirst());
                countriesFilterCB.positionCaret(0);
                model.filterByRegion(newValue, newValue.getCountries());
                ObservableList<Team> regionCountriesTeams = FXCollections.observableArrayList();
                for (Country country : regionCountries) {
                    regionCountriesTeams.addAll(country.getTeams());
                }
                this.teamsFilterCB.setItems(regionCountriesTeams);
                this.teamsFilterCB.selectItem(regionCountriesTeams.getFirst());
                showRevertButtonByFilterActive(regionRevertButton, regionRevertSvg);
                setTotalRates();
            }
        });
    }


    //TODO this method  trows an error that i can not solve, but is not affeccting the application , it is related to the javafx


    /**
     * add country selection listener
     */
    private void addCountryFilterListener() {
        this.countriesFilterCB.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                countryFilterActive = true;
                ObservableList<Team> countryTeams = FXCollections.observableArrayList(newValue.getTeams());
                this.teamsFilterCB.clearSelection();
              try {
                  if (!countryTeams.isEmpty()) {
                      this.teamsFilterCB.setItems(countryTeams);
                  }
                  this.teamsFilterCB.selectItem(countryTeams.getFirst());
              }catch (IndexOutOfBoundsException e){
                  System.out.println(e.getMessage());
              }
                model.filterByCountryTeams(newValue);
                showRevertButtonByFilterActive(countryRevertButton, countryRevertSvg);
                setTotalRates();
            }
        });
    }

    /**
     * add teams filter selection listener
     */

    private void addTeamFilterListener() {
        this.teamsFilterCB.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                model.filterEmployeesByTeam(newValue);
                showRevertButtonByFilterActive(teamRevertButton, teamRevertSvg);
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


    /**
     * show revert button if the filter is applied
     */
    private void showRevertButtonByFilterActive(HBox button, SVGPath revertSvg) {
        revertSvg.setVisible(true);
        button.setDisable(false);
    }


    private void hideRevertButton(SVGPath svgPath, HBox button) {
        PauseTransition pauseTransition = new PauseTransition(Duration.millis(100));
        pauseTransition.setOnFinished((event) -> {
            svgPath.setVisible(false);
            button.setDisable(true);
        });
        pauseTransition.playFromStart();
    }


    /**
     * change the style off the employees container, so the employee component can not be seen from the dit page
     */
    public void setEmployeesVboxContainerStyleToEdit() {
        this.employeesVboxContainer.getStyleClass().add("employeesVboxContainerOnEdit");
        this.employeesScrollPane.getStyleClass().add("employeesVboxContainerOnEdit");
    }

    /**
     * change back the style of the employee component when close, the edit window
     */
    public void setEmployeesVboxContainerStyleToDefault() {
        this.employeesVboxContainer.getStyleClass().remove("employeesVboxContainerOnEdit");
        this.employeesScrollPane.getStyleClass().remove("employeesVboxContainerOnEdit");
    }





    private void revertRegionFilter(HBox button, SVGPath revertIcon) {
        button.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            undoRegionFilter();
            hideRevertButton(revertIcon, button);
            hideRevertButton(this.teamRevertSvg, teamRevertButton);
            hideRevertButton(this.countryRevertSvg, countryRevertButton);
        });
    }

    /**
     * undo the country filter
     */
    private void revertCountryFilter(HBox button, SVGPath countryRevertSvg) {
        button.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            undoCountryFilter();
            hideRevertButton(countryRevertSvg, button);
            hideRevertButton(this.teamRevertSvg, teamRevertButton);
        });
    }


    /***/

    private void revertTeamFilter(HBox button, SVGPath revertIcon) {
        button.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            undoTeamFilter();
            hideRevertButton(revertIcon, button);
        });
    }



    /**undo the countries filters , is the region filter is active , than show the region employees , else show all employees*/
    @FXML
    private void undoCountryFilter() {
        if (regionFilterActive && countriesFilterCB.getSelectionModel().getSelectedItem() != null) {
            model.returnEmployeesByRegion();
            teamsFilterCB.clearSelection();
         //   model.performRegionTeamsOverheadCalculations();
            // searchField.clear();
            //setTotalRates();
        } else {
            model.performEmployeeSearchUndoOperation();
            teamsFilterCB.clearSelection();
            teamsFilterCB.setItems(FXCollections.observableArrayList(model.getOperationalTeams()));
            countriesFilterCB.clearSelection();
            countryFilterActive = false;
            setTotalRatesDefault();
        }
    }
    @FXML
    private void undoRegionFilter() {
        regionsFilter.clearSelection();
        model.performEmployeeSearchUndoOperation();
        countriesFilterCB.clearSelection();
        ObservableList<Country> countriesInSystem = FXCollections.observableArrayList();
        countriesInSystem.setAll(model.getOperationalCountries());
        countriesFilterCB.setItems(countriesInSystem);
        teamsFilterCB.clearSelection();
        ObservableList<Team> teamsInSystem = FXCollections.observableArrayList();
        teamsInSystem.setAll(model.getOperationalTeams());
        teamsFilterCB.setItems(teamsInSystem);
        setTotalRatesDefault();
        regionFilterActive = false;
    }


    public void setTotalRatesDefault() {
        dayRateField.setText("");
        hourlyRateField.setText("");
    }

    private void undoTeamFilter() {
        if (countryFilterActive && teamsFilterCB.getSelectionModel().getSelectedItem() != null) {
            model.returnEmployeesByCountry();
            teamsFilterCB.clearSelection();
            // searchField.clear();
            //setTotalRates();
        } else if (regionFilterActive && teamsFilterCB.getSelectionModel().getSelectedItem() != null) {
            model.returnEmployeesByRegion();
            teamsFilterCB.clearSelection();
            // searchField.clear();
            //setTotalRates();
        } else {
            model.performEmployeeSearchUndoOperation();
            teamsFilterCB.clearSelection();
            teamsFilterCB.setItems(FXCollections.observableArrayList(model.getOperationalTeams()));
//            countriesFilterCB.clearSelection();
//            countryFilterActive = false;
            setTotalRatesDefault();
        }
    }


}








   /* private void goBack() throws RateException {
        if (filterActive && countriesFilterCB.getSelectionModel().getSelectedItem() != null &&
                teamsFilterCB.getSelectionModel().getSelectedItem() != null) {
            model.teamFilterActiveRevert();
            setTotalRates();
        } else if (filterActive && countriesFilterCB.getSelectionModel().getSelectedItem() != null) {
            model.returnEmployeesByCountry();
            setTotalRates();
        } else {
            model.performEmployeeSearchUndoOperation();
            setTotalRatesDefault();
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
                setTotalRates();
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



    private void filterByCountryListener() {
        countriesFilterCB.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                try {
                    Country selectedCountry = (Country) newValue;
                    ObservableList <Team> teamsForCountry =  FXCollections.observableArrayList();
                    if(!model.getTeamsForCountry(selectedCountry).isEmpty()){
                        teamsForCountry.setAll(model.getTeamsForCountry(selectedCountry));
                    }
                    teamsFilterCB.getSelectionModel().clearSelection();
                    teamsFilterCB.setItems(teamsForCountry.sorted());
                    model.filterByCountry(selectedCountry);
                    filterActive = true;
                    setTotalRates();
                    showRevertButtonByFilterActive(countryRevertButton,countryRevertSvg);
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
                    Team selectedTeam = (Team) newValue;
                    Country selectedCountry = (Country) countriesFilterCB.getSelectionModel().getSelectedItem();
                    if(selectedCountry==null){
                        teamsFilterCB.setItems(teams.sorted());
                        model.filterByTeam(selectedTeam);
                    } else {
                        model.filterByCountryAndTeam(selectedCountry, selectedTeam);
                    }
                    filterActive = true;
                    setTotalRates();
                    showRevertButtonByFilterActive(teamRevertButton,teamRevertSvg);
                } catch (RateException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }





    private void addSelectionListener() throws RateException  {
        searchResponseHolder.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                try {
                    model.performSelectUserSearchOperation(newValue);
                    setTotalRates();
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
    private void goBackFromTeams() throws RateException {
        System.out.println("Boolean" + filterActive);
        if(filterActive && countriesFilterCB.getSelectionModel().getSelectedItem()!=null){
            model.returnEmployeesByCountry();
            teamsFilterCB.clearSelection();
            searchField.clear();
            setTotalRates();
        } else {
            model.performEmployeeSearchUndoOperation();
            teamsFilterCB.clearSelection();
            filterActive = false;
            setTotalRatesDefault();
        }
    }

    private void showRevertButtonByFilterActive(HBox button,SVGPath revertSvg) {
        revertSvg.setVisible(true);
        button.setDisable(false);
    }

    private void hideRevertButton(SVGPath svgPath,HBox button) {
        PauseTransition pauseTransition = new PauseTransition(Duration.millis(500));
        pauseTransition.setOnFinished((event) -> {
            svgPath.setVisible(false);
            button.setDisable(true);
        });
        pauseTransition.playFromStart();
    }



    private void revertTeamFilter(HBox button,SVGPath revertIcon) throws RateException{
        button.addEventHandler(MouseEvent.MOUSE_CLICKED,event->{
            try {
                goBackFromTeams();
                hideRevertButton(revertIcon,button);
            } catch (RateException e) {
                ExceptionHandler.errorAlertMessage(ErrorCode.UNDO_FAILED.getValue());
            }
        });
    }

    public void callService(){
        startPerformRedoCalculations();
    }

    private void startPerformRedoCalculations() {
        this.calculateEditOperationPerformedEdit = new Service<Boolean>() {
            @Override
            protected Task<Boolean> createTask() {
                return new Task<Boolean>() {
                    @Override
                    protected Boolean call() throws Exception {
                        try {
                            dayRateValue=model.calculateGroupDayRate().toString();
                            hourlyRateValue=model.calculateGroupHourlyRate().toString();
                            return true;
                        } catch (NumberFormatException e) {
                            ExceptionHandler.errorAlertMessage("error Message");
                            return false;
                        }
                    }
                };
            }
        };
        calculateEditOperationPerformedEdit.setOnSucceeded((e)->{
            if(calculateEditOperationPerformedEdit.getValue()){
                  dayRateField.setText(dayRateValue);
                  hourlyRateField.setText(hourlyRateValue);
              //  ExceptionHandler.errorAlertMessage("operation succesfull");
            }else{
               // ExceptionHandler.errorAlertMessage("operation fails");
            }

        });
        calculateEditOperationPerformedEdit.setOnFailed((e)->{
           calculateEditOperationPerformedEdit.getException().printStackTrace();
         //   ExceptionHandler.errorAlertMessage("thread failed to perform operations");
        });
        this.calculateEditOperationPerformedEdit.restart();
    }



*/





