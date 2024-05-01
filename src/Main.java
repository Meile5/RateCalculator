
import easv.dal.teamDao.TeamDao;
import easv.exception.ErrorCode;
import easv.exception.ExceptionHandler;
import easv.exception.RateException;
import easv.ui.ModelFactory;
import easv.ui.pages.IModel;
import easv.ui.pages.homePage.HomePageController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main extends Application {
    public static void main(String[] args) {
        Application.launch();

    }


    /**the model initialization will be moved to the class that will  manage all the initialization of the view based on the role
     * the model will be initialized in that class and we will use dependency injection, to inject  the model in our components*/
    @Override
    public void start(Stage primaryStage) throws Exception {

        IModel model = null;
        TeamDao teamDao = new TeamDao();
        try{
            model =    ModelFactory.createModel(ModelFactory.ModelType.NORMAL_MODEL);
        }catch (RateException e){
            ExceptionHandler.errorAlertMessage(ErrorCode.LOADING_FXML_FAILED.getValue());
            primaryStage.close();
        }

        HomePageController homePageController = new HomePageController(model);
        Scene scene = new Scene(homePageController.getRoot());
        primaryStage.setTitle("Overhead manager");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}