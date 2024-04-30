package easv.ui.components.homePage.callBackFactory;

import easv.ui.components.common.PageManager;
import easv.ui.components.homePage.openPageObserver.Subject;
import easv.ui.pages.profilePage.ProfilePageController;
import javafx.scene.Parent;

public class NavigateToProfile implements CallBack, Subject {
    private PageManager pageManager;
    private Parent root;
    private boolean isOpened;



    public NavigateToProfile(PageManager pageManager) {
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
        ProfilePageController  profilePageController= new ProfilePageController();
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
