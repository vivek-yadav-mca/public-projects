package dummydata.android;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class App extends Application {

    public static final String CHANNEL_DAILY_REWARD = "channel_daily_reward";
    public static final String CHANNEL_FREE_CHANCE = "channel_free_chance";
    public static final String CHANNEL_MISCELLANEOUS = "channel_miscellaneous";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channelDailyReward = new NotificationChannel(
                    CHANNEL_DAILY_REWARD,
                    "Daily Reward",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channelDailyReward.setDescription("This will remind you about your daily reward availability.");


            NotificationChannel channelFreeChance = new NotificationChannel(
                    CHANNEL_FREE_CHANCE,
                    "Reminder",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channelFreeChance.setDescription("This will remind you about your free game chance availability status.");


            NotificationChannel channelMiscellaneous = new NotificationChannel(
                    CHANNEL_MISCELLANEOUS,
                    "Miscellaneous",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channelMiscellaneous.setDescription("Miscellaneous notifications.");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channelDailyReward);
            manager.createNotificationChannel(channelFreeChance);
            manager.createNotificationChannel(channelMiscellaneous);
        }
    }
}
