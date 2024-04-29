package easv.ui.components.homePage.callBackFactory;

import easv.ui.components.common.PageManager;
import easv.ui.pages.modelingPage.ModelingController;
import javafx.scene.Parent;

public class NavigateToEdit implements CallBack {
    private PageManager pageManager;
    private Parent root ;

    public NavigateToEdit(PageManager pageManager) {
        this.pageManager = pageManager;
    }


    @Override
    public void call() {
        initializeEditController();
        pageManager.changePage(root);
    }

    private void initializeEditController(){
        ModelingController editController = new ModelingController();
        root= editController.getModelingPage();
    }
}
