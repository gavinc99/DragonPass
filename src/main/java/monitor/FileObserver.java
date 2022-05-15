package monitor;

import model.Logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


public class FileObserver extends Observer {
    Subject monitor = new Subject();
    Observer observe = new SQLiteObserver(monitor);

    public FileObserver(Subject subject) {
        this.subject = subject;
        this.subject.attach(this);
    }

    @Override
    public void update(Logger log) {

        System.out.println("File " + log.toString());
        try (FileWriter fw = new FileWriter("log.txt", true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(log.toString());
        } catch (IOException e) {
            monitor.setLog(new Logger("warn", e.getCause().getMessage()));
        }
    }
}
