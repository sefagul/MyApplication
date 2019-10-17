package com.example.myapplication;

import android.os.AsyncTask;
import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class insertMessageToDB extends AsyncTask<Request, Void, Void> {
    @Override
    protected Void doInBackground(Request... requests) {

        OkHttpClient client = new OkHttpClient();
        try {
            client.newCall(requests[0]).execute();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
