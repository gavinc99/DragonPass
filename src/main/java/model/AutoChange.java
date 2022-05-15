package model;

public class AutoChange {

    public static AutoChange instance;
    public static String auto;

    public AutoChange(String auto){
        AutoChange.auto = auto;
    }

    public static AutoChange getInstance(String auto){

        instance = new AutoChange(auto);

        return instance;
    }

    public String getAuto(){
        return auto;
    }

    public static void cleanAutoChange(){
        auto = "off";
    }
}

