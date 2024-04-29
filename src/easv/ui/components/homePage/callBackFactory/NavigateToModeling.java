package easv.ui.components.homePage.callBackFactory;

import easv.ui.components.common.PageManager;
import easv.ui.pages.modelingPage.ModelingController;
import javafx.scene.Parent;

public class NavigateToModeling  implements CallBack{
    private PageManager pageManager;
    private Parent root;
    private boolean isOpened;



    public NavigateToModeling(PageManager pageManager) {
        this.pageManager= pageManager;

    }

    @Override
    public void call() {
        if(isOpened){
            return;
        }
        initializePage();
        pageManager.changePage(root);
        isOpened=true;
    }


    private void initializePage(){
         ModelingController  modelingController= new ModelingController();
        root=modelingController.getModelingPage();
    }

    public void setOpened(boolean opened) {
        isOpened = opened;
    }

}
