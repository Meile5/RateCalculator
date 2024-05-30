package easv.ui.components.homePage.openPageObserver;

import java.util.ArrayList;
import java.util.List;

public class Observer implements Observable {
    private List<Subject> subjects;

    public Observer() {
    subjects= new ArrayList<>();
    }

    public void addSubject(Subject subject){
        subjects.add(subject);
    }

    public void removeSubject(Subject subject){
        subjects.remove(subject);
    }

    public void modifyDisplay(Subject subject){
        for(Subject item : subjects ){
            item.modifyDisplay(item.equals(subject));
        }
    }


}
