package easv.ui.pages.distribution;

import easv.ui.pages.IModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

public class DistributionController {
    @FXML
    private Parent distributionPage;
    private IModel model;

    public DistributionController(IModel model) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Distribution.fxml"));
        loader.setController(this);
        this.model= model;
        try {
            distributionPage =loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Parent getDistributionPage() {
        return distributionPage;
    }



}
