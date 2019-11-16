package com.annevonwolffen.androidschool.simpleaudiorecorder.view;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.annevonwolffen.androidschool.simpleaudiorecorder.R;

import static com.annevonwolffen.androidschool.simpleaudiorecorder.view.MainActivity.EXTRA_FILENAME;

public class RecordAudioService extends Service {


    private static final String CHANNEL_ID = "Channel_1";
    private static final String TAG = "RecordAudioService";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");
        // start record
        // etwas mit media recorder
        startForeground(startId, createNotification(intent.getStringExtra(EXTRA_FILENAME)));

        return START_NOT_STICKY;


    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,
                    getString(R.string.notification_channel_name), NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription(getString(R.string.notification_channel_description));
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private Notification createNotification(String stringExtra) {
        // todo: создать pending intent'ы
        // 1) pending intent for return to activity
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        // 2) pending intent for pause/continue record

        // 3) pending intent for stop and save record

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification);
        //remoteViews.setOnClickPendingIntent(R.id.btn_pause_record, rootPendingIntent);
        //remoteViews.setOnClickPendingIntent(R.id.btn_stop_record, rootPendingIntent);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentIntent(pendingIntent)
                .setCustomContentView(remoteViews)
                .build();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
