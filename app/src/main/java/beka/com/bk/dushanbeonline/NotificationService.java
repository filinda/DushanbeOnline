package beka.com.bk.dushanbeonline;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;

import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;

import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class NotificationService extends FirebaseMessagingService {

    String CHANNEL_ID = "vatanOnline";
    String CHANNEL_NAME = "vatanOnline";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        System.out.println("abab message");
        sendNotification(remoteMessage.getNotification().getBody());

    }
    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, NotificationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder;


        // Create an Intent for the activity you want to start

// Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = null;
        PendingIntent resultPendingIntent;
        Intent resultIntent = new Intent(this, NotificationActivity.class);
        stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);



        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("My channel description");
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(false);
            notificationManager.createNotificationChannel(channel);
            notificationBuilder = new NotificationCompat.Builder(this).setChannelId(CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_blue_notification)
                    .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_blue_notification))
                    .setContentTitle(this.getString(R.string.app_name))
                    .setContentText(messageBody)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri);
        }else {
            notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_blue_notification)
                    .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_blue_notification))
                    .setContentTitle(this.getString(R.string.app_name))
                    .setContentText(messageBody)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri);
        }
        notificationBuilder.setContentIntent(resultPendingIntent);

        notificationManager.notify(0, notificationBuilder.build());
    }

}
