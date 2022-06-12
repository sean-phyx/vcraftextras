package ga.phyx.vcraftextras.util;

public class MilConvert {

    public static String convertDifference(long timeToConvert) {
        long seconds = (System.currentTimeMillis() - timeToConvert)/1000;
        long minutes = seconds/60;
        long hours = minutes/60;
        long days = hours/24;
        return days + " days " + hours % 24 + " hours " + minutes % 60 + " minutes " + seconds % 60 + " seconds";
    }
    public static String convertDifference(String timeToConvert) {
        long seconds = (System.currentTimeMillis() - Long.valueOf(timeToConvert))/1000;
        long minutes = seconds/60;
        long hours = minutes/60;
        long days = hours/24;
        return days + " days " + hours % 24 + " hours " + minutes % 60 + " minutes " + seconds % 60 + " seconds";
    }
}
