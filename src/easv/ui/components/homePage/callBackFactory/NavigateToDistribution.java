package easv.ui.components.homePage.callBackFactory;

import easv.ui.components.common.PageManager;
import easv.ui.pages.createPage.CreateController;
import easv.ui.pages.distribution.DistributionController;
import javafx.scene.Parent;

public class NavigateToDistribution implements CallBack{
    private PageManager pageManager;
    private Parent root;
    private boolean isOpened;



    public NavigateToDistribution(PageManager pageManager) {
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
        DistributionController distributionController = new DistributionController();
        root=distributionController.getDistributionPage();
    }

    public void setOpened(boolean opened) {
        isOpened = opened;
    }
}
