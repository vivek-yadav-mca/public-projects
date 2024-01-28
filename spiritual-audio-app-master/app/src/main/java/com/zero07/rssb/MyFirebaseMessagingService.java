package dummydata;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import dummydata.activity.MainActivity;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public static int NOTIFICATION_ID = 1;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        generateNotification(remoteMessage.getNotification().getTitle(),
                             remoteMessage.getNotification().getBody());
    }

    private void generateNotification(String title, String body) {

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                                                                PendingIntent.FLAG_ONE_SHOT);

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "Spiritual Quotes")
                          .setSmallIcon(R.drawable.app_icon_transp_100)
                          .setContentTitle(title)
                          .setContentText(body)
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

    public void onSendError (String msgId, Exception exception) {
        Log.e("Error", "notification error: " + exception.getMessage() + exception.getStackTrace());
    }

    public void onMessageSent (String msgId) {
        Log.i("Info", "message sent"  + msgId);
    }


}