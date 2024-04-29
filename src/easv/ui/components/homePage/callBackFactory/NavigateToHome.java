package easv.ui.components.homePage.callBackFactory;
import easv.ui.components.common.PageManager;
import easv.ui.components.homePage.map.WorldMap;
import javafx.scene.Parent;

public class NavigateToHome implements CallBack{

        private PageManager pageManager;
        private Parent root;

    public NavigateToHome(PageManager pageManager) {
            this.pageManager = pageManager;
        }

        @Override
        public void call() {
            initializeRoot();
            pageManager.changePage(root);
        }

        private void initializeRoot(){
            WorldMap  worldMap= new WorldMap();
            root= worldMap.getRoot();
        }

}
