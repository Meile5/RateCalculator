package easv.ui.components.homePage.callBackFactory;
import easv.ui.components.common.PageManager;
import easv.ui.components.homePage.openPageObserver.Subject;
import easv.ui.pages.createPage.CreateController;
import javafx.scene.Parent;

public class NavigateToCreate implements CallBack, Subject {
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
      pageManager.changePage(root,this);
      isOpened=true;
  }


  private void initializePage(){
      CreateController createController = new CreateController();
      root=createController.getCreatePage();
  }


    @Override
    public void modifyDisplay(boolean val) {
        isOpened=val;
    }
}
