package model;
import java.time.LocalTime;


public class Logger {
    private final LocalTime occurTime;
    private final String level;
    private final String message;

    public Logger(String level, String message) {
        this.level = level;
        this.message = message;
        this.occurTime = LocalTime.now();
    }

    public LocalTime getOccurenceTime() {
        return occurTime;
    }

    public String getLevel() {
        return level;
    }

    public String getMessage() {
        return message;
    }
    public String toString(){
        return this.level +" : "+ this.message+ " : "+this.occurTime;
    }
}
