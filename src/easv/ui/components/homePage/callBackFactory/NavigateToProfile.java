package easv.ui.components.homePage.callBackFactory;

import easv.ui.components.common.PageManager;
import easv.ui.components.homePage.openPageObserver.Subject;
import easv.ui.pages.IModel;
import easv.ui.pages.profilePage.ProfilePageController;
import javafx.scene.Parent;

public class NavigateToProfile implements CallBack, Subject {
    private PageManager pageManager;
    private Parent root;
    private boolean isOpened;
    private IModel model;



    public NavigateToProfile(PageManager pageManager,IModel model) {
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
        ProfilePageController  profilePageController= new ProfilePageController(model);
        root=profilePageController.getProfilePage();
    }

    public void setOpened(boolean opened) {
        isOpened = opened;
    }

    @Override
    public void modifyDisplay(boolean val) {
        isOpened=val;
    }
}
