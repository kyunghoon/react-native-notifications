package com.wix.reactnativenotifications.gcm;

import java.util.List;
import java.util.Map;
import android.content.Context;
import android.app.ActivityManager;
import android.os.Bundle;
import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.wix.reactnativenotifications.core.notification.IPushNotification;
import com.wix.reactnativenotifications.core.notification.PushNotification;

import jdk.nashorn.internal.objects.NativeJSON;

import static com.wix.reactnativenotifications.Defs.LOGTAG;

public class GcmMessageHandlerService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        try {
            if(!isForeground(getApplicationContext())) {
                final Bundle bundle = new Bundle();
                for (Map.Entry<String, String> entry : remoteMessage.getData().entrySet()) {
                    String key = entry.getKey();
                    if (key.equals("author")) {
                        bundle.putString("title", entry.getValue());
                    } else if (key.equals("twi_body")) {
                        String[] separated = entry.getValue().split(":");
                        if (separated.length > 1) {
                            bundle.putString("body", separated[1].trim());
                        } else {
                            bundle.putString("body", entry.getValue());
                        }
                    } else {
                        bundle.putString(key, entry.getValue());
                    }
                }

                final IPushNotification notification = PushNotification.get(getApplicationContext(), bundle);
                notification.onReceived();
            }
        } catch (IPushNotification.InvalidNotificationException e) {
            // A GCM message, yes - but not the kind we know how to work with.
            Log.v(LOGTAG, "GCM message handling aborted", e);
        }
    }

    private static boolean isForeground(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> tasks = am.getRunningAppProcesses();
        final String packageName = context.getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : tasks) {
            if (ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND == appProcess.importance && packageName.equals(appProcess.processName)) {
                return true;
            }
        }
        return false;
    }
}
