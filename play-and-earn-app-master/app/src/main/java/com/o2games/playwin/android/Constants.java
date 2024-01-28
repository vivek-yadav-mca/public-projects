package dummydata.android;

public class Constants {

    public static final String APP_PLAYSTORE_LINK = "dummydata";
    public static final String GOOGLE_FORCE_UPDATE = "google_force_update";
    public static final String CHECK_DOWNLOAD_FROM_PLAYSTORE = "isDownloadFromPlayStore";
    public static final String CHECK_FRAUD_USER = "user_blocking_enabled";

    public static final String MAX_IN_GAME_COINS = "max_in_game_coins";
    public static final String MIN_IN_GAME_COINS = "min_in_game_coins";
    public static final String MAX_GAME_QUIZ_COINS = "max_game_quiz_coins";
    public static final String MIN_GAME_QUIZ_COINS = "min_game_quiz_coins";

    /***** FB Remote Config Key *****/
    public static final String IS_APP_UNDER_MAINTENANCE = "is_app_under_maintenance";
    public static final String UNDER_MAINTENANCE_CLOSING_TIME = "under_maintenance_closing_time";
    public static final String UNDER_MAINTENANCE_TITLE = "under_maintenance_title";
    public static final String UNDER_MAINTENANCE_MSG = "under_maintenance_message";

    public static final String SHOW_HOME_IMP_NOTICE_DIALOG = "show_home_imp_notice_dialog";
    public static final String GET_HOME_IMP_NOTICE_TITLE = "home_imp_notice_dialog_title";
    public static final String GET_HOME_IMP_NOTICE_TITLE_ENG = "home_imp_notice_dialog_msg_eng";
    public static final String GET_HOME_IMP_NOTICE_TITLE_HI = "home_imp_notice_dialog_msg_hindi";

    public static final String AUTO_CLEAN_LEADERBOARD = "autoCleanLeaderboardData";
    public static final String CLEAN_LEADERBOARD_INTERVAL = "clean_leaderboard_interval";
    public static final String LEADERBOARD_DELETION_FUTURE_TIME = "leaderboard_deletion_future_time";
    public static final String IS_TAPJOY_ENABLED = "isTapjoy_enabled";
    public static final String IS_TAPJOY_OFFERWALL = "isTapjoy_offerwall";

    public static final String IS_APPLOVIN_ENABLED = "isAppLovin_enabled";
    public static final String IS_APPLOVIN_BANNER = "isAppLovin_banner";
    public static final String IS_APPLOVIN_INTERSTITIAL = "isAppLovin_interstitial";
    public static final String IS_APPLOVIN_REWARDED = "isAppLovin_rewarded";
    public static final String IS_APPLOVIN_MEDIUM_NATIVE = "isAppLovin_native_medium";

    public static final String IS_APPODEAL_ENABLED = "isAppodeal_enabled";
    public static final String IS_APPODEAL_BANNER = "isAppodeal_banner";
    public static final String IS_APPODEAL_INTERSTITIAL = "isAppodeal_interstitial";
    public static final String IS_APPODEAL_REWARDED = "isAppodeal_rewarded";
    public static final String IS_APPODEAL_NATIVE = "isAppodeal_native";

    /***** FB Remote Config Key *****/

    public static final String FOR_USER_WITHDRAW = "for_user_withdraw";
    public static final int DAILY_REWARD_DEFAULT_REQUEST_CODE = 0;
    public static final int FREE_CHANCE_NOR_SPIN_REQUEST_CODE = 1;
    public static final int FREE_CHANCE_GOL_SPIN_REQUEST_CODE = 2;
    public static final int FREE_CHANCE_NOR_SCRATCH_REQUEST_CODE = 3;
    public static final int FREE_CHANCE_GOL_SCRATCH_REQUEST_CODE = 4;
    public static final int FREE_CHANCE_NOR_FLIP_REQUEST_CODE = 5;
    public static final int FREE_CHANCE_GOL_FLIP_REQUEST_CODE = 6;

    public static final String DEFAULT_CHANCE_LEFT = "0";
    public static final String DEFAULT_COINS = "0";
    public static final String DEFAULT_CASH = "0";

    public static final String TEST_DB_CHILD = "test_child"; //leaderboard_data

    public static final String USER_TABLE = "user_data";
    public static final String BLOCKED_USER_TABLE = "blocked_user_table";
    public static final String USER_COIN_BALANCE = "user_coin_balance"; //** For blocking purpose only **/
    public static final String LEADERBOARD_TABLE = "leaderboard_data";
    public static final String LEADERBOARD_CHILD_USERCOIN_NAME = "userTotalCOIN"; // Child Name
    public static final String LEADERBOARD_ORDERING_PARENT = "userTotalCOIN"; // for leaderboard ordering

    public static final String IS_DAILY_WINNER_LEADERBOARD = "is_daily_winner";
    public static final String IS_WEEKLY_WINNER_LEADERBOARD = "is_weekly_winner";
    public static final String WINNER_LIST = "winner_list";
    public static final String PAST_WINNER_ORDERING_PARENT = "rank"; // for leaderboard ordering
    public static final String NEXT_WINNER_ANNOUNCEMENT_TIME = "next_winner_announcement_time";
    public static final String WINNER_LIST_DAILY_LEADERBOARD = "daily_leaderboard_winner_list"; // winner of daily leaderboard
    public static final String YESTERDAY_WINNER_LIST = "yesterday_winner_list"; // yesterday winner
    public static final String WINNER_LIST_WEEKLY_LEADERBOARD = "weekly_leaderboard_winner_list"; // winner of weekly leaderboard
    public static final String PAST_WEEK_WINNER_LIST = "past_week_winner_list"; // weekly winner

    public static final String IS_PAYPAL_PAYMENT_ENABLED = "isPaypalPayment_enabled";
    public static final String IS_AMAZON_PAYMENT_ENABLED = "isAmazonPayment_enabled";
    public static final String USD_1_PAYMENT_ENABLED = "usd_1_payment_option";

    public static final String USER_WALLET = "user_wallet";
    public static final String USER_WALLET_CHILD_AMT_NAME = "walletAmount"; // Child Name
    public static final String CASH_WITHDRAWN = "cash_withdrawn";
    public static final String CASH_WITHDRAWN_USER_SPECIFIC = "cash_withdrawn_user_specific";
    public static final String DELETED_ACCOUNT = "deleted_account";

    public static final String COMMON_DATA_FOR_ALL = "common_data";
    public static final String WALLET_BALANCE = "wallet_balance"; // changed to leaderboard_table
    public static final String WITHDRAW_DETAILS = "withdraw_request"; // changed to cash_withdrawn

    /*********
     *
     * ********/

    public static final String USER_SPECIFIC = "_user_specific_data";
    public static final String SP_NORMAL_SPIN_AUTO_INTERSTITIAL = "normal_spin_game_played";
    public static final String SP_NORMAL_SPIN_EARN_BTN_TIME = "normal_spin_earn_btn_time";
    public static final String SP_NORMAL_SCRATCH_AUTO_INTERSTITIAL = "normal_scratch_game_played";
    public static final String SP_NORMAL_SCRATCH_EARN_BTN_TIME = "normal_scratch_earn_btn_time";
    public static final String SP_NORMAL_FLIP_AUTO_INTERSTITIAL = "normal_flip_game_played";
    public static final String SP_NORMAL_FLIP_EARN_BTN_TIME = "normal_flip_earn_btn_time";

    public static final String SHARED_PREF_COMMON = "o2_common_spref";
    public static final String SP_USER_SELECTED_LANGUAGE = "user_selected_language";
    public static final String SP_SHOW_LANGUAGE_ACTIVITY = "user_show_language_activity";

    public static final String SP_10_BUTTON_TIME = "reward_10_button_time";
    public static final String SP_25_BUTTON_TIME = "reward_25_button_time";
    public static final String SP_50_BUTTON_TIME = "reward_50_button_time";
    public static final String SP_75_BUTTON_TIME = "reward_75_button_time";
    public static final String SP_100_BUTTON_TIME = "reward_100_button_time";

    public static final String SP_USER_ID = "spref_user_id";
    public static final String SP_USER_NAME = "spref_user_name";
    public static final String SP_USER_EMAIL = "spref_user_email";
    public static final String SP_USER_GOOGLE_ID = "spref_user_google_id";
    public static final String SP_USER_PHOTO_URL = "spref_user_photo_url";
    public static final String SP_USER_AUTH_UID = "spref_user_auth_uid";
    public static final String SP_USER_ADVER_ID = "spref_user_adver_id";

    public static final String SP_CONTACT_US_FUTURE_TIME = "contact_us_future_time";
    public static final String SP_RATING_BSDIALOG_FUTURE_TIME = "rating_bsdialog_future_time";
    public static final String SP_REFRESH_LEADERBOARD_TIME = "refresh_leaderboard_time";

}
