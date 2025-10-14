package com.aula.aion.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.aula.aion.R;
import com.aula.aion.SplashScreen;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class NotificationMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCMService";
    private static final String CHANNEL_ID = "fcm_default_channel";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d(TAG, "Mensagem recebida de: " + remoteMessage.getFrom());

        // Verifica se a mensagem contém dados
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Dados da mensagem: " + remoteMessage.getData());

            // Processar dados personalizados aqui
            handleDataMessage(remoteMessage.getData());
        }

        // Verifica se a mensagem contém notificação
        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();

            Log.d(TAG, "Título da notificação: " + title);
            Log.d(TAG, "Corpo da notificação: " + body);

            // Mostra a notificação
            sendNotification(title, body);
        }
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d(TAG, "Novo token FCM: " + token);

        // Envie o token para seu servidor
        sendTokenToServer(token);
    }

    private void handleDataMessage(java.util.Map<String, String> data) {
        // Processe os dados personalizados aqui
        String title = data.get("title");
        String message = data.get("message");

        if (title != null && message != null) {
            sendNotification(title, message);
        }
    }

    private void sendNotification(String title, String messageBody) {
        // Intent para abrir o app quando a notificação for clicada
        Intent intent = new Intent(this, SplashScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
        );

        // Som padrão da notificação
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // Construir a notificação
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.logo)
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC) // Importante para tela de bloqueio
                        .setVibrate(new long[]{1000, 1000, 1000})
                        .setLights(0xFF0000FF, 3000, 3000)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody));

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Criar canal de notificação para Android O+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Notificações FCM",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Canal para notificações do Firebase");
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        if (notificationManager != null) {
            notificationManager.notify(0, notificationBuilder.build());
        }
    }

    private void sendTokenToServer(String token) {
        // Implemente aqui o código para enviar o token para seu servidor backend
        // Exemplo: fazer uma chamada API REST
        Log.d(TAG, "Token enviado para o servidor: " + token);
    }
}
