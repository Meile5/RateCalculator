package easv.ui.components.homePage.CallBackFactory;
import easv.be.Navigation;
import easv.ui.components.common.PageManager;
import easv.ui.pages.createPage.CreateController;
import javafx.scene.Parent;

public class NavigateToCreate implements CallBack {
   private PageManager pageManager;
   private Parent root;

  public NavigateToCreate(PageManager pageManager) {
  this.pageManager= pageManager;

  }

  @Override
  public void call() {
      initializePage();
    pageManager.changePage(root);
    System.out.println("Opening the create Page");
  }


  private void initializePage(){
      CreateController createController = new CreateController();
      root=createController.getCreatePage();
  }
}
