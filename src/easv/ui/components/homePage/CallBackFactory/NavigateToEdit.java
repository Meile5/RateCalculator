package easv.ui.components.homePage.CallBackFactory;

import easv.ui.components.common.PageManager;
import easv.ui.pages.editPage.EditController;
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
        EditController editController = new EditController();
        root= editController.getEditPage();
    }
}
