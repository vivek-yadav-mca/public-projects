package dummydata.android;

public class CheckEmptyUtils {

    public static boolean isBlank(String str) {
        return (str == null || str.trim().length() == 0);
    }

    public static boolean isNotBlank(String str) {
        return (str != null && str.trim().length() > 0);
    }

}
