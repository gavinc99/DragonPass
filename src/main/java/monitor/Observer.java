package monitor;

import model.Logger;

public abstract class Observer {

    protected Subject subject;
    public abstract void update(Logger log);
}
