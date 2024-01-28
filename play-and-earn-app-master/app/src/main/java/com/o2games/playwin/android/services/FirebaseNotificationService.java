package dummydata.android.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import dummydata.android.App;
import dummydata.android.R;
import dummydata.android.activity.MainActivity;

public class FirebaseNotificationService extends FirebaseMessagingService {

    public static int NOTIFICATION_ID = 1;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        generateNotification(
                remoteMessage.getNotification().getTitle(),
                remoteMessage.getNotification().getBody(),
                remoteMessage.getNotification().getChannelId() );
    }

    private void generateNotification(String fbNotiTitle, String fbNotiBody, String fbChannelId) {

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, App.CHANNEL_MISCELLANEOUS)
                .setSmallIcon(R.drawable.ic_app_notif_icon_cartoon_g1_svg)
                .setContentTitle(fbNotiTitle)
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setContentText(fbNotiBody)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (NOTIFICATION_ID > 1073741824) {
            NOTIFICATION_ID = 0;
        } else {
            notificationManager.notify(NOTIFICATION_ID ++, notificationBuilder.build());
        }
    }

}
