package com.Ganeshkumarkvt.kvthub;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.InputStream;
import java.net.URL;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public static int NOTIFICATION_ID = 1;
    Bitmap bitmap;
    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.d("NEW_TOKEN", s);
        sendRegistrationToServer(s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        String Title;
        Title = Objects.requireNonNull(remoteMessage.getNotification()).getTitle();
        String msg = remoteMessage.getNotification().getBody();
        Uri photourl = remoteMessage.getNotification().getImageUrl();

        Bitmap notiImg = getImagefromUrl(photourl);


        generateNotification(msg, Title, notiImg);
    }

    private Bitmap getImagefromUrl(Uri photourl) {
        try {
            URL url = new URL(photourl.toString());
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
        }catch (Exception e){
            e.printStackTrace();
        }
        return bitmap;
    }

    private void generateNotification(String body, String title, Bitmap bitmap1) {

        Intent intent = new Intent(this, Social.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent  = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri sounduri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = null;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            builder = new NotificationCompat.Builder(this, "KvtHubMsg")
                        .setSmallIcon(R.drawable.ic_stat_name)
                        .setColor(ContextCompat.getColor(this,R.color.colorPrimary))
                        .setContentTitle(title)
                        .setContentText(body)
                        .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap1))
                        .setAutoCancel(true)
                        .setSound(sounduri)
                        .setContentIntent(pendingIntent)
                        .setPriority(NotificationManager.IMPORTANCE_MAX);
        }else {
            builder = new NotificationCompat.Builder(this, "KvtHubMsg")
                    .setSmallIcon(R.drawable.ic_lolipop)
                    .setSound(sounduri)
                    .setColor(ContextCompat.getColor(this,R.color.colorPrimary))
                    .setContentTitle(title)
                    .setContentText(body)
                    .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap1))
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);
        }


        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (NOTIFICATION_ID > 1073741824){
            NOTIFICATION_ID = 0;
        }
        notificationManager.notify(NOTIFICATION_ID++, builder.build());
    }

    private void sendRegistrationToServer(String token){
        FirebaseDatabase.getInstance().getReference().child("FCM_Device_Tokens").push().setValue(token);
    }

}
