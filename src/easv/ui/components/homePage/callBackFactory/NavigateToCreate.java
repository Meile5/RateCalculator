package easv.ui.components.homePage.callBackFactory;
import easv.ui.components.common.PageManager;
import easv.ui.pages.createPage.CreateController;
import javafx.scene.Parent;

public class NavigateToCreate implements CallBack {
   private PageManager pageManager;
   private Parent root;
   private boolean isOpened;



    public NavigateToCreate(PageManager pageManager) {
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
      CreateController createController = new CreateController();
      root=createController.getCreatePage();
  }

    public void setOpened(boolean opened) {
        isOpened = opened;
    }
}
