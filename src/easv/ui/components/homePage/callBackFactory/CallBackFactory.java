package easv.ui.components.homePage.callBackFactory;
import easv.be.Navigation;
import easv.ui.components.common.PageManager;
import java.util.HashMap;


public class CallBackFactory {
    private static final HashMap<Navigation, CallBack> callBacks = new HashMap<>();
    private static PageManager pageManager;

    public static void setPageHolder(PageManager pageHolder) {
        pageManager = pageHolder;
    }

    public static CallBack createCallBack(Navigation pageTo) {
        return () -> getCallBack(pageTo).call();
    }

    private static CallBack getCallBack(Navigation pageTo) {
        if (callBacks.containsKey(pageTo)) {
            return callBacks.get(pageTo);
        }
        CallBack callBack = null;
        switch (pageTo) {
            case DISTRIBUTION -> {
                callBack = new NavigateToDistribution(pageManager);
                callBacks.put(pageTo, callBack);
            }
            case CREATE -> {
                System.out.println(pageManager);
                callBack = new NavigateToCreate(pageManager);
                callBacks.put(pageTo, callBack);
            }
            case EMPLOYEES -> {
                callBack = new NavigateToEmployees(pageManager);
                callBacks.put(pageTo, callBack);
            }
            case MODELING -> {
                callBack= new NavigateToModeling(pageManager);
                callBacks.put(pageTo,callBack);
            }
            case PROFILE -> {
                callBack = new NavigateToProfile(pageManager);
                callBacks.put(pageTo, callBack);
            }
            default -> {
                callBack = new NavigateToCreate(pageManager);
                callBacks.put(pageTo, callBack);
            }
        }
        return callBack;
    }




}
