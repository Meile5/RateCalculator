package easv.ui.components.homePage.CallBackFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CallBackFactory {
    private static final HashMap<PageTo, CallBack> callBacks = new HashMap<>();

    public static CallBack createCallBack(PageTo pageTo) {
      return    getCallBack(pageTo);
    }

    private static CallBack getCallBack(PageTo pageTo) {
        if (callBacks.containsKey(pageTo)) {
            return callBacks.get(pageTo);
        }
        CallBack callBack = null;
        switch (pageTo) {
            case DISTRIBUTION -> {
            }
            case CREATE -> {
                callBack = new NavigateToCreate();
                callBacks.put(pageTo, callBack);
            }
            case EMPLOYEES -> {
                callBack = new NavigateToEmployees();
                callBacks.put(pageTo, callBack);
            }
            case MODELING -> {}
            case PROFILE -> {}
        }
        return callBack;
    }


    public enum PageTo {
        DISTRIBUTION,
        CREATE,
        EMPLOYEES,
        MODELING,
        PROFILE
    }

}
