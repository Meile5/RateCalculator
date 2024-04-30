package easv.ui.components.homePage.callBackFactory;

import easv.ui.components.common.PageManager;
import easv.ui.components.homePage.openPageObserver.Subject;
import easv.ui.pages.modelingPage.ModelingController;
import javafx.scene.Parent;

public class NavigateToModeling  implements CallBack, Subject {
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
        pageManager.changePage(root,this);
        isOpened=true;
    }


    private void initializePage(){
         ModelingController  modelingController= new ModelingController();
        root=modelingController.getModelingPage();
    }


    @Override
    public void modifyDisplay(boolean val) {
        isOpened=val;

    }
}
