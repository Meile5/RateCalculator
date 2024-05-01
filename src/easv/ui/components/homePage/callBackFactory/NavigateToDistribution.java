package easv.ui.components.homePage.callBackFactory;
import easv.ui.components.common.PageManager;
import easv.ui.components.homePage.openPageObserver.Subject;
import easv.ui.pages.IModel;
import easv.ui.pages.Model;
import easv.ui.pages.distribution.DistributionController;
import javafx.scene.Parent;

public class NavigateToDistribution implements CallBack, Subject {
    private PageManager pageManager;
    private Parent root;
    private boolean isOpened;
    private IModel model;


    public NavigateToDistribution(PageManager pageManager, IModel model) {
        this.pageManager= pageManager;
        this.model= model;

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
        DistributionController distributionController = new DistributionController(model);
        root=distributionController.getDistributionPage();
    }

    public void setOpened(boolean opened) {
        isOpened = opened;
    }

    @Override
    public void modifyDisplay(boolean val) {
        isOpened=val;
    }
}
