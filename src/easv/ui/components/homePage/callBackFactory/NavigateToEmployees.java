package easv.ui.components.homePage.callBackFactory;

import easv.ui.components.common.PageManager;
import easv.ui.components.homePage.openPageObserver.Subject;
import easv.ui.employeesPage.employeeMainPage.EmployeeMainPageController;
import easv.ui.pages.displayPage.EmployeeController;
import javafx.scene.Parent;

public class NavigateToEmployees implements CallBack, Subject {
    private PageManager pageManager;
    private Parent root;
    private boolean isOpened;

    public NavigateToEmployees(PageManager pageManager) {
        this.pageManager = pageManager;
    }

    @Override
    public void call() {
        if(isOpened){
            return;
        }
        initializeRoot();
        pageManager.changePage(root,this);
        isOpened= true;
    }

    private void initializeRoot(){
        EmployeeMainPageController employeeMainPageController = new EmployeeMainPageController();
        root= employeeMainPageController.getRoot();
    }

    @Override
    public void modifyDisplay(boolean val) {
        isOpened=val;

    }
}
