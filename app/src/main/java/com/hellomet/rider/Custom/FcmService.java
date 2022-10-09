package com.hellomet.rider.Custom;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.hellomet.rider.MyFirebaseMessagingService;
import com.hellomet.rider.R;
import com.hellomet.rider.View.MainActivity;

import static com.hellomet.rider.Custom.App.CHANNEL_ID;

public class FcmService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent ii = new Intent(getApplicationContext(), MainActivity.class);
        ii.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, ii, 0);

        NotificationCompat.Builder mBuilder =
//                new NotificationCompat.Builder(getApplicationContext(), );
                new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();

        bigText.bigText("message");
        bigText.setBigContentTitle("title");
        bigText.setSummaryText("Text in detail");

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
        mBuilder.setContentTitle("title");
        mBuilder.setContentText("message");
        mBuilder.setAutoCancel(false);
        mBuilder.setPriority(Notification.PRIORITY_HIGH);
        mBuilder.setStyle(bigText);
       Notification notification =  mBuilder.build();
       startForeground(1, notification);
       return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
