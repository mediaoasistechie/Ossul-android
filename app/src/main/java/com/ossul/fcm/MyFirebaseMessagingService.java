package com.ossul.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ossul.R;
import com.ossul.activities.MainActivity;
import com.ossul.appconstant.AppConstants;
import com.ossul.utility.AppUtilsMethod;
import com.ossul.utility.Validator;

import org.json.JSONObject;

import java.util.Date;
import java.util.Map;

/**
 * Created by ${Rajan} on 11-Feb-17.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = FirebaseMessagingService.class.getSimpleName();
    JSONObject res = null;

    @Override
    public void onMessageReceived(RemoteMessage message) {
        super.onMessageReceived(message);
        Log.d(TAG, "onMessageReceived");
        if (message.getData() != null && message.getData().size() > 0) {
            Map data = message.getData();
            Log.d(TAG, "notification: " + data.toString());
            Bundle extras = new Bundle();
            for (Map.Entry<String, String> entry : message.getData().entrySet()) {
                extras.putString(entry.getKey(), entry.getValue());
            }
            String itemType = "", msg = "", image = "", itemId = "", title = "";
            int notificationId = extras.getInt("notificationId", 0);
            if (extras.containsKey("itemId"))
                itemId = extras.getString("itemId");
            if (extras.containsKey("itemType"))
                itemType = extras.getString("itemType");
            if (extras.containsKey("body"))
                msg = extras.getString("body");
            if (extras.containsKey("title"))
                title = extras.getString("title");
            if (extras.containsKey("image"))
                image = extras.getString("image");
            sendNotification(title, image, msg, itemId, itemType, notificationId);
        }
    }

    private void sendNotification(String title, String image, String message, String itemId, String itemType, int notificationId) {
        long Id = new Date().getTime();
        Intent intent = new Intent(this, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(AppConstants.NOTIFICATION_ID, notificationId);
        bundle.putString(AppConstants.ITEM_ID, itemId);
        bundle.putString(AppConstants.ITEM_TYPE, itemType);
        bundle.putInt(AppConstants.ID, (int) Id);
        intent.putExtras(bundle);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, (int) Id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = null;
        //big image style
        NotificationCompat.Style style;
        try {
            if (!Validator.isEmptyString(image) && image.startsWith("http")) {
                Bitmap bpMap = AppUtilsMethod.getNotificationBitmap(image);
                if (bpMap != null) {
                    style = new NotificationCompat.BigPictureStyle().setSummaryText(message).bigPicture(bpMap);
                } else {
                    style = new NotificationCompat.BigTextStyle().bigText(message);
                }
            } else {
                style = new NotificationCompat.BigTextStyle().bigText(message);
            }
        } catch (Exception e) {
            style = new NotificationCompat.BigTextStyle().bigText(message);
        }

        if (title != null && !title.equals("")) {
            title = getResources().getString(R.string.app_name);
        }
        notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
//                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_ossul_logo))
//                        .setStyle(style)
                .setSound(defaultSoundUri)
                .setStyle(style)
                .setContentIntent(pendingIntent);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setSmallIcon(R.drawable.ic_logo);
            notificationBuilder.setColor(getResources().getColor(R.color.colorAccent));
        } else {
            notificationBuilder.setSmallIcon(R.drawable.ic_logo);
        }

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationId != 0)
            notificationManager.notify(notificationId, notificationBuilder.build());
        else
            notificationManager.notify((int) Id, notificationBuilder.build());
    }
}
