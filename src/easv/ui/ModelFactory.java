package easv.ui;
import easv.exception.RateException;
import easv.ui.pages.IModel;
import easv.ui.pages.Model;
import easv.ui.pages.NewModel;
import java.util.HashMap;


public class ModelFactory {
    final private static HashMap<ModelType, IModel> models = new HashMap<>();

    public enum ModelType {
        NORMAL_MODEL,
        MODELOTHER
    }


    public static IModel createModel(ModelType modelType) throws RateException {
        if (models.containsKey(modelType)) {
            return models.get(modelType);
        }
        IModel model = null;
        switch (modelType) {
            case NORMAL_MODEL -> {
                model = new NewModel();
                models.put(modelType, model);
            }
            case MODELOTHER -> {
                model = new Model();
                models.put(modelType, model);
            }
        }
        return model;
    }
}
