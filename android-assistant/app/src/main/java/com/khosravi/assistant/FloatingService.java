package com.khosravi.assistant;

import android.app.*;
import android.content.*;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.*;
import android.provider.Settings;
import android.view.*;
import android.widget.*;

public class FloatingService extends Service {
    private WindowManager wm;
    private View bubble;
    private static final String CHANNEL = "assistant_overlay";

    @Override public void onCreate() {
        super.onCreate();
        createChannel();
        if (Build.VERSION.SDK_INT >= 29) {
            startForeground(7, notification());
        }
        showBubble();
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel c = new NotificationChannel(CHANNEL, "Khosravi Assistant", NotificationManager.IMPORTANCE_LOW);
            ((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).createNotificationChannel(c);
        }
    }

    private Notification notification() {
        Intent i = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this,0,i,PendingIntent.FLAG_IMMUTABLE|PendingIntent.FLAG_UPDATE_CURRENT);
        return new Notification.Builder(this, CHANNEL).setContentTitle("Khosravi Assistant")
            .setContentText("Floating assistant is active").setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pi).setOngoing(true).build();
    }

    private void showBubble() {
        if (!Settings.canDrawOverlays(this)) { stopSelf(); return; }
        wm = (WindowManager)getSystemService(WINDOW_SERVICE);
        TextView b = new TextView(this);
        b.setText("AI");
        b.setTextColor(Color.WHITE);
        b.setTextSize(16);
        b.setGravity(Gravity.CENTER);
        b.setBackgroundColor(Color.rgb(103,80,164));
        b.setOnClickListener(v -> {
            Intent i = new Intent(this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        });
        bubble = b;
        WindowManager.LayoutParams p = new WindowManager.LayoutParams(
            140,140, Build.VERSION.SDK_INT >= 26 ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT);
        p.gravity = Gravity.TOP|Gravity.END; p.x=30; p.y=220;
        wm.addView(bubble,p);
    }

    @Override public int onStartCommand(Intent i,int flags,int id){ return START_STICKY; }
    @Override public void onDestroy(){ if(wm!=null && bubble!=null) wm.removeView(bubble); super.onDestroy(); }
    @Override public android.os.IBinder onBind(Intent i){ return null; }
}
