package com.wix.reactnativenotifications.gcm;

import java.util.Map;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.wix.reactnativenotifications.core.notification.IPushNotification;
import com.wix.reactnativenotifications.core.notification.PushNotification;

import static com.wix.reactnativenotifications.Defs.LOGTAG;

public class GcmMessageHandlerService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        try {
            final Bundle bundle = new Bundle();

            for (Map.Entry<String, String> entry : remoteMessage.getData().entrySet()) {
                bundle.putString(entry.getKey(), entry.getValue());
            }

            final IPushNotification notification = PushNotification.get(getApplicationContext(), bundle);
            notification.onReceived();
        } catch (IPushNotification.InvalidNotificationException e) {
            // A GCM message, yes - but not the kind we know how to work with.
            Log.v(LOGTAG, "GCM message handling aborted", e);
        }
    }
}
