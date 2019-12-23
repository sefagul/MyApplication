package com.example.myapplication;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.Telephony;
import android.telephony.TelephonyManager;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NotificationService extends Service {

    Context context;
    NotificationManager manager;
    int userId;

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    public void onCreate() {

        context = getApplicationContext();
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            SmsListener smsReceiver = new SmsListener();
            IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            intentFilter.addAction(Telephony.Sms.Intents.DATA_SMS_RECEIVED_ACTION);
            this.registerReceiver(smsReceiver, intentFilter);
        }
        if(SaveSharedPreference.getLoggedStatus(context)) {
            userId = SaveSharedPreference.getUserId(context);
            new Timer().scheduleAtFixedRate(new TimerTask() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void run() {
                    getMessages();
                }
            }, 0, 10000);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public JSONObject getClientInformation(){
        TelephonyManager m_telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String IMEI, IMSI, OS, DEVICE, MODEL, PRODUCT, PNUMBER, OPERATOR;
        int USERID;

        if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            return new JSONObject();
        }

        IMEI = m_telephonyManager.getDeviceId();
        IMSI = m_telephonyManager.getSubscriberId();
        OS = android.os.Build.VERSION.RELEASE;
        DEVICE = android.os.Build.DEVICE;
        MODEL = android.os.Build.MODEL;
        PRODUCT = android.os.Build.PRODUCT;
        PNUMBER = m_telephonyManager.getLine1Number();
        OPERATOR = m_telephonyManager.getSimOperator();
        USERID = userId;

        JSONObject clientInformation = new JSONObject();
        try {
            clientInformation.put("IMEI",IMEI);
            clientInformation.put("IMSI",IMSI);
            clientInformation.put("OS",OS);
            clientInformation.put("DEVICE",DEVICE);
            clientInformation.put("MODEL",MODEL);
            clientInformation.put("PRODUCT",PRODUCT);
            clientInformation.put("PNUMBER",PNUMBER);
            clientInformation.put("OPERATOR",OPERATOR);
            clientInformation.put("INCOMING",false);
            clientInformation.put("USERID",USERID);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return clientInformation;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void getMessages() {

        if(SaveSharedPreference.getLoggedStatus(context)) {

            JSONObject clientInformation = getClientInformation();
            MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(MEDIA_TYPE, clientInformation.toString());

            OkHttpClient client = new OkHttpClient();
            String url = SaveSharedPreference.getUrl(context);
            String token = SaveSharedPreference.getToken(context);
            token = "Bearer " + token;

            try {
                Request request = new Request.Builder().url(url).post(body).header("Authorization", token).build();
                Response response = client.newCall(request).execute();
                JSONArray array = new JSONArray(response.body().string());

                for (int i = 0; i < array.length(); i++) {

                    JSONObject object = array.getJSONObject(i);
                    Message message = new Message();
                    String tempMessage = object.getString("message");
                    message.setText(tempMessage);

                    if (!message.getText().equals("noMessage") && message.getText() != null) {

                        tempMessage = tempMessage + " Sent by MY_APP";
                        message.setText(tempMessage);
                        message.setPhoneNumber(object.getString("phoneNumber"));
                        message.setType(object.getString("type"));

                        if (message.getType().equals("sms")) {
                            sendSms(message);
                        }
                        if (message.getType().equals("whatsapp")) {
                            sendWhatsappMessage(message);
                        }
                    }
                }
            } catch (IOException | JSONException | IllegalArgumentException  e) {
                e.printStackTrace();
            }
        }
    }

    public void sendWhatsappMessage(Message message){

        PackageManager packageManager = context.getPackageManager();
        Intent i = new Intent(Intent.ACTION_VIEW);

        try {
            String url = "https://api.whatsapp.com/send?phone="+ message.getPhoneNumber() +"&text=" + URLEncoder.encode(message.getText(), "UTF-8");
            i.setPackage("com.whatsapp");
            i.setData(Uri.parse(url));

            if (i.resolveActivity(packageManager) != null) {
                context.startActivity(i);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        showNotification(message);
    }

    public void showNotification(Message message){

        Intent intent=new Intent(context,MainActivity.class);
        //PendingIntent pending=PendingIntent.getActivity(context, 0, intent, 0);

        createNotificationChannel();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "test")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Mesaj Gönderildi")
                .setContentText(message.getText())
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message.getText()))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        Random randomNumber = new Random();
        int randomId = randomNumber.nextInt();
        notificationManager.notify(randomId, builder.build());

    }

    public void sendSms(Message message){

            android.telephony.SmsManager sms=android.telephony.SmsManager.getDefault();
        sms.sendTextMessage(message.getPhoneNumber(), null, message.getText(), null, null);
            showNotification(message);
    }

    @Override
    public void onDestroy() {

        manager.cancel(11);

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, Restarter.class);
        this.sendBroadcast(broadcastIntent);
    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "test";
            String description = "test";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("test", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}
