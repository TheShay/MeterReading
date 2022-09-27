package com.wasa.meterreading;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyExceptionHandler implements Thread.UncaughtExceptionHandler {
    private final Activity activity;

    public MyExceptionHandler(Activity a) {
        activity = a;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        try {
            Intent intent = new Intent(activity, MainActivity.class);
            intent.putExtra("crash", true);
            /*PendingIntent pendingIntent = PendingIntent.getActivity(activity.getBaseContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager mgr = (AlarmManager) activity.getBaseContext().getSystemService(Context.ALARM_SERVICE);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, pendingIntent);
            ex.printStackTrace();
            activity.finish();
            System.exit(2);*/
        } catch (Exception e) {
            String exception = "[Exception in MyExceptionHandler:uncaughtException] \n[" + e.getLocalizedMessage() + "]";
            Log.d("IvoItCurves", exception);
        }
    }
}
