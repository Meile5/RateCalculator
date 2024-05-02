package easv.ui.components.homePage.callBackFactory;
import easv.ui.components.common.PageManager;
import easv.ui.components.homePage.openPageObserver.Subject;
import easv.ui.employeesPage.employeeMainPage.EmployeeMainPageController;
import easv.ui.pages.modelFactory.IModel;
import javafx.scene.Parent;

public class NavigateToEmployees implements CallBack, Subject {
    private PageManager pageManager;
    private Parent root;
    private boolean isOpened;
    private IModel model;

    public NavigateToEmployees(PageManager pageManager, IModel model) {
        this.pageManager = pageManager;
        this.model = model;
    }

    @Override
    public void call() {
        if (isOpened) {
            return;
        }
        initializeRoot();

        pageManager.changePage(root,this);
        isOpened= true;
    }

    private void initializeRoot(){
        EmployeeMainPageController employeeMainPageController = new EmployeeMainPageController();
        root= employeeMainPageController.getRoot();
        pageManager.changePage(root, this);
        isOpened = true;
    }


    @Override
    public void modifyDisplay(boolean val) {
        isOpened = val;

    }
}
