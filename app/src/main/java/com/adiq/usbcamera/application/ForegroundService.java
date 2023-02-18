package com.adiq.usbcamera.application;

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

import com.adiq.usbcamera.R;
import com.adiq.usbcamera.view.USBCameraActivity;

public class ForegroundService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent streamIntent = new Intent(this, USBCameraActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, streamIntent, 0);
        createNotificationChannel();


        Notification notification = new NotificationCompat.Builder(this, "Stream")
                .setContentText("Streaming service is running")
                .setContentTitle("AD-IQ")
                .setSmallIcon(R.mipmap.btn_shutter_default)
                .setContentIntent(pendingIntent).build();

        startForeground(1, notification);

        return START_STICKY;
    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("Stream",
                    "Streaming service", NotificationManager.IMPORTANCE_HIGH);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(notificationChannel);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        stopForeground(false);
        super.onDestroy();
    }
}
