package easv.ui.components.homePage.callBackFactory;

import easv.ui.components.common.PageManager;
import easv.ui.components.homePage.openPageObserver.Subject;
import easv.ui.pages.IModel;
import easv.ui.pages.displayPage.EmployeeController;
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
        pageManager.changePage(root, this);
        isOpened = true;
    }

    private void initializeRoot() {
        EmployeeController employeeController = new EmployeeController(model);
        root = employeeController.getEmployeePage();
    }

    @Override
    public void modifyDisplay(boolean val) {
        isOpened = val;

    }
}
