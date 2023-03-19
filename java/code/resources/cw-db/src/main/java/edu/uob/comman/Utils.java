package edu.uob.comman;

public class Utils {

    public static boolean checkInt(String string) {
        try {
            Integer.parseInt(string);
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }

    public static boolean checkFloat(String string) {
        try {
            Float.parseFloat(string);
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }
}
