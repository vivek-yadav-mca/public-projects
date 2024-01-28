package dummydata.userModels;

public class UserContext {
    private static UserContext instance;
    private static User loggedInUser = null;
    private static Boolean openStoryInsideApp = false;

    private static Boolean isAdmobEnabled = false;
    private static Boolean isAdmobInterstitialEnabled = false;

    private static Boolean isTapdaqEnabled = false;
    private static Boolean isTapdaqInterstitialEnabled = false;

    private static Boolean isApplovinEnabled = false;
    private static Boolean isApplovinBanner = false;
    private static Boolean isApplovinInterstitial = false;

    private static Boolean isUnityEnabled = false;

    private static Boolean isIronsourceEnabled = false;
    private static Boolean isIronsource_banner = false;
    private static Boolean isIronsource_interstitial = false;

    private UserContext(User loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    public static UserContext getInstance(User loggedInUser) {
        if (instance == null) {
            synchronized (UserContext.class) {
                instance = new UserContext(loggedInUser);
            }
        }
        return instance;
    }

    public static User getLoggedInUser() {
        return loggedInUser;
    }

    public static void setLoggedInUser(User user) {
        loggedInUser = user;
    }

    public static Boolean getOpenStoryInsideApp() {
        return openStoryInsideApp;
    }

    public static void setOpenStoryInsideApp(Boolean openStoryInsideApp) {
        UserContext.openStoryInsideApp = openStoryInsideApp;
    }

    public static Boolean getIsAdmobEnabled() {
        return isAdmobEnabled;
    }

    public static void setIsAdmobEnabled(Boolean isAdmobEnabled) {
        UserContext.isAdmobEnabled = isAdmobEnabled;
    }

    public static Boolean getIsAdmobInterstitialEnabled() {
        return isAdmobInterstitialEnabled;
    }

    public static void setIsAdmobInterstitialEnabled(Boolean isAdmobInterstitialEnabled) {
        UserContext.isAdmobInterstitialEnabled = isAdmobInterstitialEnabled;
    }



    public static Boolean getIsTapdaqEnabled() {
        return isTapdaqEnabled;
    }

    public static void setIsTapdaqEnabled(Boolean isTapdaqEnabled) {
        UserContext.isTapdaqEnabled = isTapdaqEnabled;
    }

    public static Boolean getIsTapdaqInterstitialEnabled() {
        return isTapdaqInterstitialEnabled;
    }

    public static void setIsTapdaqInterstitialEnabled(Boolean isTapdaqInterstitialEnabled) {
        UserContext.isTapdaqInterstitialEnabled = isTapdaqInterstitialEnabled;
    }


    public static Boolean getIsApplovinEnabled() {
        return isApplovinEnabled;
    }

    public static void setIsApplovinEnabled(Boolean isApplovinEnabled) {
        UserContext.isApplovinEnabled = isApplovinEnabled;
    }

    public static Boolean getIsApplovinBanner() {
        return isApplovinBanner;
    }

    public static void setIsApplovinBanner(Boolean isApplovinBanner) {
        UserContext.isApplovinBanner = isApplovinBanner;
    }

    public static Boolean getIsApplovinInterstitial() {
        return isApplovinInterstitial;
    }

    public static void setIsApplovinInterstitial(Boolean isApplovinInterstitial) {
        UserContext.isApplovinInterstitial = isApplovinInterstitial;
    }

    public static Boolean getIsUnityEnabled() {
        return isUnityEnabled;
    }

    public static void setIsUnityEnabled(Boolean isUnityEnabled) {
        UserContext.isUnityEnabled = isUnityEnabled;
    }



    public static Boolean getIsIronsourceEnabled() {
        return isIronsourceEnabled;
    }

    public static void setIsIronsourceEnabled(Boolean isIronsourceEnabled) {
        UserContext.isIronsourceEnabled = isIronsourceEnabled;
    }

    public static Boolean getIsIronsource_banner() {
        return isIronsource_banner;
    }

    public static void setIsIronsource_banner(Boolean isIronsource_banner) {
        UserContext.isIronsource_banner = isIronsource_banner;
    }

    public static Boolean getIsIronsource_interstitial() {
        return isIronsource_interstitial;
    }

    public static void setIsIronsource_interstitial(Boolean isIronsource_interstitial) {
        UserContext.isIronsource_interstitial = isIronsource_interstitial;
    }
}
