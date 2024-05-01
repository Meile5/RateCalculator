package easv.ui.components.homePage.callBackFactory;

import easv.ui.components.common.PageManager;
import easv.ui.components.homePage.openPageObserver.Subject;
import easv.ui.pages.IModel;
import easv.ui.pages.modelingPage.ModelingController;
import javafx.scene.Parent;

public class NavigateToModeling  implements CallBack, Subject {
    private PageManager pageManager;
    private Parent root;
    private boolean isOpened;
    private IModel model;



    public NavigateToModeling(PageManager pageManager,IModel model) {
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
         ModelingController  modelingController= new ModelingController(model);
        root=modelingController.getModelingPage();
    }


    @Override
    public void modifyDisplay(boolean val) {
        isOpened=val;
    }
}
