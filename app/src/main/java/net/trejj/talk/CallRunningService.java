/*
 * Created by Devlomi on 2021
 */

package net.trejj.talk;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import net.trejj.talk.activities.CallScreenActivity;
import net.trejj.talk.activities.main.MainActivity;

public class CallRunningService extends Service {
    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        String input = intent.getStringExtra("inputExtra");
        createNotificationChannel();
        SharedPreferences preferences = this.getSharedPreferences("net.trejj.talk", Context.MODE_PRIVATE);
        String number = preferences.getString("number","");
        String callername = preferences.getString("callername","");
        Intent notificationIntent = new Intent(this, CallScreenActivity.class)
                .putExtra("callername",callername).putExtra("number",number)
                .putExtra("isCallInProgress",true);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                |Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Trejj Talk")
                .setContentText("Tap to return")
                .setSmallIcon(R.drawable.ic_call)
                .setContentIntent(pendingIntent)
                .build();
        Log.i("starrrr","start");
        startForeground(1, notification);
        //do heavy work on a background thread
        //stopSelf();
        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("starrrr","destroy");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private void createNotificationChannel() {

        Log.i("starrrr","noti");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

}