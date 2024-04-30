package easv.ui.components.homePage.callBackFactory;
import easv.ui.components.common.PageManager;
import easv.ui.components.homePage.map.WorldMap;
import easv.ui.components.homePage.openPageObserver.Subject;
import javafx.scene.Parent;

public class NavigateToHome implements CallBack, Subject {

        private PageManager pageManager;
        private Parent root;
        private boolean isOpened;

    public NavigateToHome(PageManager pageManager) {
            this.pageManager = pageManager;
        }

        @Override
        public void call() {
        if (isOpened){
            return;
        }
            initializeRoot();
            pageManager.changePage(root,this);
            isOpened= true;
        }

        private void initializeRoot(){
            WorldMap  worldMap= new WorldMap();
            root= worldMap.getRoot();
        }

    @Override
    public void modifyDisplay(boolean val) {
        isOpened=val;

    }
}
