package easv;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FakeMain extends Application {
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("./ui/employeesPage/employeeMainPage/EmployeesMainPage.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("main");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}