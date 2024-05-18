package easv.ui.pages.geographyManagementPage.regionComponents;

import easv.Utility.WindowsManagement;
import easv.be.Region;
import easv.ui.pages.geographyManagementPage.geographyMainPage.GeographyManagementController;
import easv.ui.pages.modelFactory.IModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class RegionComponent implements Initializable {

    @FXML
    private HBox regionInfoComponent;
    @FXML
    private Label nameLB, countryLB, teamLB;
    @FXML
    private VBox editButton, deleteContainer;

    private StackPane pane;
    private StackPane secondPane;
    private Region region;
    private IModel model;
    private DeleteRegionController deleteRegionController;
    private GeographyManagementController controller;

    public RegionComponent(IModel model, StackPane pane, Region region, DeleteRegionController deleteRegionController, GeographyManagementController controller) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("RegionComponent.fxml"));
        loader.setController(this);
        this.model = model;
        this.region = region;
        this.deleteRegionController = deleteRegionController;
        this.pane = pane;
        this.controller = controller;
        try {
            regionInfoComponent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setLabels();
        setEditButton();
        displayDelete();
    }

    private void displayDelete() {

    }

    private void setLabels() {
        if(region != null) {
            nameLB.setText(region.getRegionName());
            countryLB.setText("" + region.getCountries().size());
            int numberOfTeams = region.getCountries().stream().mapToInt(country -> country.getTeams().size()).sum();
            teamLB.setText(numberOfTeams + "");
        }
    }

    private void setEditButton() {
        this.editButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            ManageRegionController manageRegionController = new ManageRegionController(model, pane, secondPane, region, controller);
            this.pane.getChildren().add(manageRegionController.getRoot());
            WindowsManagement.showStackPane(pane);

//            EditController editController = new EditController(model, firstLayout, employee, this);
//            this.firstLayout.getChildren().add(editController.getRoot());
//            employeeController.setSelectedComponentStyleToSelected(this);
//            employeeController.setEmployeesVboxContainerStyleToEdit();
//            WindowsManagement.showStackPane(firstLayout);
        });
    }

    public HBox getRoot() {
        return regionInfoComponent;
    }
}
