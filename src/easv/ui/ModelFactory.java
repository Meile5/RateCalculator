package easv.ui;

import easv.be.Employee;
import easv.ui.pages.IModel;
import easv.ui.pages.Model;
import easv.ui.pages.NewModel;
import javafx.collections.ObservableMap;


public class ModelFactory {
    final private static IModel[] models = new IModel[1];
    public enum modelType{
        NormalModel,
        ModelOther
    };


    public static IModel createModel(modelType Model){


        if(models[0] == null){
            IModel model = null;
            switch (Model){
                case NormalModel -> {  model = new NewModel();}
                case ModelOther -> { model = new Model();}


            }

             models[0] = model;
            return model;
        }
        else {
            return models[0];
        }

    }
}
