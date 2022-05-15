package monitor;

import model.Logger;

import java.util.ArrayList;
import java.util.List;

public class Subject {
    private List<Observer> observers = new ArrayList<Observer>();
    private Logger log;


    public void setLog(Logger log) {
        this.log = log;
        notifyAllObservers(log);
    }

    public void attach(Observer observer){
        observers.add(observer);
    }

    public void notifyAllObservers(Logger log){
        for (Observer observer : observers) {
            observer.update(log);
        }
    }

}

