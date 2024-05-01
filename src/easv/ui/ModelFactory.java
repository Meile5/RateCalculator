package easv.ui;

import easv.be.Employee;
import easv.ui.pages.IModel;
import easv.ui.pages.Model;
import easv.ui.pages.NewModel;
import javafx.collections.ObservableMap;


public class ModelFactory {
    final private static IModel[] models = new IModel[1];



    public static IModel createModel(){


        if(models[0] == null){
            IModel model = new NewModel();
             models[0] = model;
            return model;
        }
        else {
            return models[0];
        }

    }
}
