package hrstal.workoutjournal.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import hrstal.workoutjournal.R;
import hrstal.workoutjournal.MainActivity;
import hrstal.workoutjournal.receivers.StartTimerReceiver;

public class NotificationHelper {

    private static final String TAG = NotificationHelper.class.getSimpleName();
    private static final String CHANNEL_ID = "TimerServiceChannel";
    private static final String CHANNEL_NAME = "Timer Channel";
    public static final int NOTIFICATION_ID = 1;

    private static NotificationHelper instance = null;
    private Context context;
    private static NotificationManager notificationManager;
    private NotificationCompat.Builder builder;


    private NotificationHelper(Context context) {
        this.context = context.getApplicationContext();
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Timer Channel",
                    NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static synchronized NotificationHelper getInstance(Context context){
        if(instance == null){
            instance = new NotificationHelper(context);
        }
        return instance;
    }

    public Notification getNotification(String title, String text){
        builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setContentIntent(
                        PendingIntent.getActivity(context, 10,
                                new Intent(context, MainActivity.class)
                                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP),
                                0)
                );

        return builder.build();
    }

    public void setNotificationText(String text){
        Notification notification = builder == null
                ? getNotification(null, text)
                : builder.setContentText(text).build();
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    public void setNotificationTitle(String title){
        Notification notification = builder == null
                ? getNotification(title, null)
                : builder.setContentTitle(title).build();
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    public void addStartButton(){
        if(builder != null) {
            Intent snoozeIntent = new Intent(context, StartTimerReceiver.class);
            snoozeIntent.setAction("hrstal.workoutjournal.START_TIMER");
            PendingIntent snoozePendingIntent =
                    PendingIntent.getBroadcast(context, 0, snoozeIntent, 0);
            Notification notification = builder.addAction(0,"Start again", snoozePendingIntent).build();
            notificationManager.notify(NOTIFICATION_ID, notification);
        }
    }

}
