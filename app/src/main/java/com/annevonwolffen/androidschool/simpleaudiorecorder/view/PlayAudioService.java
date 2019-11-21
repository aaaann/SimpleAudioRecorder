package com.annevonwolffen.androidschool.simpleaudiorecorder.view;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.annevonwolffen.androidschool.simpleaudiorecorder.R;

import static com.annevonwolffen.androidschool.simpleaudiorecorder.view.MainActivity.EXTRA_FILENAME;
import static com.annevonwolffen.androidschool.simpleaudiorecorder.view.MainActivity.EXTRA_IS_PLAYING;
import static com.annevonwolffen.androidschool.simpleaudiorecorder.view.MainActivity.MSG_PAUSE_PLAY;

public class PlayAudioService extends Service {

    private static final String TAG = "PlayAudioService";

    private static final String CHANNEL_ID = "Channel_1";

    private static final int NOTIFICATION_ID = 1;

    public static final int MSG_START_PLAY = 201;
    private static final String ACTION_PAUSE = "ActionPause";

    private MediaPlayer mPlayer;
    private boolean mIsPlaying;
    private String mRecordName;

    private Messenger mMessenger = new Messenger(new InternalHandler());

    private Messenger mMainActivityMessenger;

    class InternalHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case MSG_START_PLAY:
                    mMainActivityMessenger = msg.replyTo;
                    Bundle bundle = msg.getData();
                    mRecordName = bundle.getString(EXTRA_FILENAME);

                    startPlay(); //todo: implement play using MediaPlayer
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();

        Log.d(TAG, "onCreate() called");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand() called with: intent = [" + intent + "], flags = [" + flags + "], startId = [" + startId + "]");

        if (intent != null && !TextUtils.isEmpty(intent.getAction())) {
            switch(intent.getAction()){
                case ACTION_PAUSE:
                    pausePlay();
                    break;
            }
        }
//
//        startCountdownTimer(100000, 1000);

        //startForeground(startId, createNotification("100"));

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "onDestroy() called");

        if (mPlayer != null) {
            mPlayer = null;
        }
    }

    private void updateNotification() {
        Notification notification = createNotification();

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(NOTIFICATION_ID, notification);
    }

    private Notification createNotification() {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Intent pauseServiceIntent = new Intent(this, PlayAudioService.class);
        pauseServiceIntent.setAction(ACTION_PAUSE);
        PendingIntent pausePendingIntent = PendingIntent.getService(this, 0, pauseServiceIntent, 0);

        //todo: remote views
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,
                CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("SimpleAudioRecorder")
                .setContentText("playing...")
                .setOnlyAlertOnce(true)
                .addAction(R.drawable.ic_stop_black_24dp, "Stop play", pausePendingIntent)
                .setContentIntent(pendingIntent);

        return builder.build();
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

    private void startPlay() {

        // todo: start media player
        mIsPlaying = true;
        updateNotification();
    }

    private void pausePlay() {
        if (mPlayer != null) {
            if (mIsPlaying){
                //mPlayer.pause();

                mIsPlaying = false;
                updateNotification();
                stopForeground(false);
                sendMessage();
            }
            else {
                //mPlayer.resume();

                mIsPlaying = true;
                updateNotification();
                startForeground(1, createNotification());
                sendMessage();
            }
        }
    }

    private void sendMessage() {
        Bundle bundle = new Bundle();
        bundle.putBoolean(EXTRA_IS_PLAYING, mIsPlaying);

        Message message = Message.obtain(null, MSG_PAUSE_PLAY);
        message.setData(bundle);

        try {
            mMainActivityMessenger.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind() called with: intent = [" + intent + "]");

        return mMessenger.getBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind() called with: intent = [" + intent + "]");

        return super.onUnbind(intent);
    }
}
