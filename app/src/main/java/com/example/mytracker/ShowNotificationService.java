package com.example.mytracker;

import static android.app.PendingIntent.getActivity;
import static androidx.core.content.ContextCompat.getSystemService;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class ShowNotificationService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "SERVICE RUNNING", Toast.LENGTH_SHORT).show();
        NotificationManager notificationManager = null;
        String details=intent.getStringExtra("details");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "myNotificationChanel";
            String description = "my notification channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("my_chanel", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this.
            notificationManager =(NotificationManager) getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "my_chanel")
                .setSmallIcon(R.drawable.notification)
                .setContentTitle("geofence broken")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("This is to alert you that your vehicle is out of geofence\n more details"+details))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        notificationManager.notify(1,builder.build());
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();

    }

}
