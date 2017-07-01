package tk.urbantaxi.dtxi.classes;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.List;

import tk.urbantaxi.dtxi.Call;

/**
 * Created by steph on 7/1/2017.
 */

public class MessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.e("Google", "From: " + remoteMessage.getData());

        Boolean onForeground = isAppOnForeground(getApplicationContext());
        Boolean activityOnForeground = isForeground("tk.urbantaxi.dtxi.Call");
        Log.e("Test", activityOnForeground + "");

        Intent dialogIntent = new Intent(this, Call.class);
        dialogIntent.putExtra("id", remoteMessage.getData().get("id"));
        dialogIntent.putExtra("sessionId", remoteMessage.getData().get("session_id"));
        dialogIntent.putExtra("latitude", remoteMessage.getData().get("latitude"));
        dialogIntent.putExtra("longitude", remoteMessage.getData().get("longitude"));
        dialogIntent.putExtra("address", remoteMessage.getData().get("address"));
        dialogIntent.putExtra("user_id", remoteMessage.getData().get("user_id"));
        dialogIntent.putExtra("name", remoteMessage.getData().get("name"));
        dialogIntent.putExtra("destination", remoteMessage.getData().get("destination"));
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(dialogIntent);
    }

    private boolean isAppOnForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        final String packageName = context.getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    public boolean isForeground(String myPackage) {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfo = manager.getRunningTasks(1);
        ComponentName componentInfo = runningTaskInfo.get(0).topActivity;
        return componentInfo.getClassName().equals(myPackage);
    }
}
