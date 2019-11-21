package com.annevonwolffen.androidschool.simpleaudiorecorder.view;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.annevonwolffen.androidschool.simpleaudiorecorder.R;

import java.io.IOException;

import static com.annevonwolffen.androidschool.simpleaudiorecorder.view.MainActivity.EXTRA_FILENAME;

public class RecordAudioService extends Service {

    private static OnRecordListener onRecordListener;
    private static final String CHANNEL_ID = "Channel_1";
    private static final String TAG = "RecordAudioService";
    private static final int NOTIFICATION_ID = 1;

    private static final String ACTION_PAUSE = "ActionPauseRecord";
    private static final String ACTION_STOP = "ActionStopRecord";

    private MediaRecorder mRecorder = null;
    private boolean mIsRecording;

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand() called with: intent = [" + intent + "], flags = [" + flags + "], startId = [" + startId + "]");

        if (intent != null) {
            if (!TextUtils.isEmpty(intent.getAction())) {
                switch (intent.getAction()) {
                    case ACTION_PAUSE:
                        Log.d(TAG, "onStartCommand: ACTION_PAUSE");
                        pauseRecord();
                        break;
                    case ACTION_STOP:
                        Log.d(TAG, "onStartCommand: ACTION_STOP");
                        stopRecord();
//                        Intent returnToActivityIntent = new Intent(this, MainActivity.class);
//                        startActivity(returnToActivityIntent);
                        //stopForeground(true); // todo: updateNotification (make new layout for this issue and call another create notification method)
                        stopSelf();
                        break;
                }
            } else {
                // start record
                // etwas mit media recorder
                Log.d(TAG, "onStartCommand: before_start record");
                startRecord(intent.getStringExtra(EXTRA_FILENAME));
                startForeground(startId, createNotification(mIsRecording));
            }
        }


//        // start record
//        // etwas mit media recorder
//        startRecord(intent.getStringExtra(EXTRA_FILENAME));
//        startForeground(startId, createNotification(intent.getStringExtra(EXTRA_FILENAME)));

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

    private Notification createNotification(boolean isRecording) {
        Log.d(TAG, "createNotification: ");
        // todo: создать pending intent'ы
        // 1) pending intent for return to activity
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        // 2) pending intent for pause/continue record
        Intent pauseIntent = new Intent(this, RecordAudioService.class);
        pauseIntent.setAction(ACTION_PAUSE);
        PendingIntent pausePendingIntent = PendingIntent.getService(this, 0, pauseIntent, 0);
        // 3) pending intent for stop and save record
        Intent stopIntent = new Intent(this, RecordAudioService.class);
        stopIntent.setAction(ACTION_STOP);
        PendingIntent stopPendingIntent = PendingIntent.getService(this, 0, stopIntent, 0);

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification);
        if (isRecording)
            remoteViews.setImageViewResource(R.id.btn_pause_record, R.drawable.ic_pause_black_24dp);
        else remoteViews.setImageViewResource(R.id.btn_pause_record, R.drawable.ic_play_arrow_black_24dp);
        remoteViews.setOnClickPendingIntent(R.id.btn_pause_record, pausePendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.btn_stop_record, stopPendingIntent);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_mic_black_24dp)
                .setContentTitle("SimpleAudioRecorder")
                .setContentText("recording...")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .addAction(R.drawable.ic_stop_black_24dp, "Stop record", stopPendingIntent)
                .setContentIntent(pendingIntent)
                //.setCustomContentView(remoteViews)
                .build();
    }

    private void updateNotification(boolean isRecording) {
        Notification notification = createNotification(isRecording);

        Log.d(TAG, "updateNotification: ");
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(NOTIFICATION_ID, notification);
    }

    /**
     * start media recorder
     */
    private void startRecord(String filename) {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4
        );
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mRecorder.setOutputFile(filename);
        try {
            mRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mRecorder.start();   // Recording is now started
        Log.d(TAG, "startRecord: ");
        mIsRecording = true;
        updateNotification(mIsRecording);
        //onRecordListener.onStartRecord();
    }

    private void pauseRecord() {
        if (mRecorder != null) {
            if (mIsRecording){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { // todo: implement for api < 24
                    mRecorder.pause();
                }
                mIsRecording = false;
                updateNotification(mIsRecording);
            }
            else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { // todo: implement for api < 24
                    mRecorder.resume();
                }
                mIsRecording = true;
                updateNotification(mIsRecording);
            }
        }
    }

    private void stopRecord() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        //onRecordListener.onFinishRecord();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "onDestroy() called");

        if (mRecorder != null) {
            stopRecord();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

interface OnRecordListener {
    void onStartRecord();
    void onFinishRecord();
}
