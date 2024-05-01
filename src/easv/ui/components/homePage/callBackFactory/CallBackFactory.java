package easv.ui.components.homePage.callBackFactory;
import easv.be.Navigation;
import easv.ui.components.common.PageManager;
import easv.ui.components.homePage.openPageObserver.Observable;
import easv.ui.components.homePage.openPageObserver.Observer;
import easv.ui.pages.IModel;

import java.util.HashMap;

public class CallBackFactory {


    private static final HashMap<Navigation, CallBack> callBacks = new HashMap<>();
    private static PageManager pageManager;
    private static IModel model;
    private final static Observable observer = new Observer();



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
                NavigateToDistribution navigateToDistribution = new NavigateToDistribution(pageManager);
                callBack = navigateToDistribution;
                callBacks.put(pageTo, callBack);
                observer.addSubject(navigateToDistribution);
            }
            case CREATE -> {
                NavigateToCreate navigateToCreate = new NavigateToCreate(pageManager);
                callBack = navigateToCreate;
                callBacks.put(pageTo, callBack);
                observer.addSubject(navigateToCreate);
            }
            case EMPLOYEES -> {
                NavigateToEmployees navigateToEmployees = new NavigateToEmployees(pageManager);
                callBack = navigateToEmployees;
                callBacks.put(pageTo, callBack);
                observer.addSubject(navigateToEmployees);
            }
            case MODELING -> {
                NavigateToModeling navigateToModeling = new NavigateToModeling(pageManager);
                callBack = navigateToModeling;
                callBacks.put(pageTo, callBack);
                observer.addSubject(navigateToModeling);
            }
            case PROFILE -> {
                NavigateToProfile navigateToProfile = new NavigateToProfile(pageManager);
                callBack = navigateToProfile;
                callBacks.put(pageTo, callBack);
                observer.addSubject(navigateToProfile);
            }
            default -> {
                System.out.println(model.getCountries());
                NavigateToHome navigateToHome = new NavigateToHome(pageManager,model);
                callBack = navigateToHome;
                callBacks.put(pageTo, callBack);
                observer.addSubject(navigateToHome);
            }
        }
        return callBack;
    }

    public static Observable getObserver() {
        return observer;
    }
    public static void setPageHolder(PageManager pageHolder) {
        pageManager = pageHolder;
    }
    public static void setModel(IModel modelParam) {
        model=modelParam;
    }


}
