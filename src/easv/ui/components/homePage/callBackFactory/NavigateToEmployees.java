package easv.ui.components.homePage.callBackFactory;

import easv.ui.components.common.PageManager;
import easv.ui.pages.displayPage.EmployeeController;
import javafx.scene.Parent;

public class NavigateToEmployees implements CallBack {
    private PageManager pageManager;
    private Parent root;

    public NavigateToEmployees(PageManager pageManager) {
        this.pageManager = pageManager;
    }

    @Override
    public void call() {
        initializeRoot();
        pageManager.changePage(root);
        System.out.println("Navigating to the Employees Page");
    }

    private void initializeRoot(){
        EmployeeController employeeController = new EmployeeController();
        root= employeeController.getEmployeePage();
    }
}
