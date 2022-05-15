package monitor;

import model.Logger;
import dragonpass.project.DBMySQLite;

public class SQLiteObserver extends Observer {

    public SQLiteObserver(Subject subject) {
        this.subject = subject;
        this.subject.attach(this);
    }

    @Override
    public void update(Logger log) {
        System.out.println("SQLite "+log.toString());
        DBMySQLite.getInstance();
        DBMySQLite.insert(log);

    }
}
