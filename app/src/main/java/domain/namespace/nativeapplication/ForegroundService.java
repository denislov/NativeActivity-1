package domain.namespace.nativeapplication;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import android.util.Log;

public class ForegroundService extends Service {
    private final String TAG = "ForegroundService";
    private final int NOTIF_ID = 77;

    public static final String CHANNEL_ID = "ForegroundServiceChannel";

    private boolean isServiceStarted = false;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * Override this method to start your tasks, maybe in a thread
     */
    public void startJob() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!isServiceStarted) {
            createNotificationChannel();
            startForeground(NOTIF_ID, getNotification("Native Thread running!"));
            isServiceStarted = true;
            Log.v(TAG, "Foreground Service started");
        }

        startJob();

        return START_STICKY;
    }

    private Notification getNotification(String text) {
        Intent notificationIntent = new Intent(this, CustomNativeActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Foreground Service")
                .setContentText(text)
                .setUsesChronometer(true)
                .setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentIntent(pendingIntent)
                .build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isServiceStarted = false;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(CHANNEL_ID, "Foreground Service Channel", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
}