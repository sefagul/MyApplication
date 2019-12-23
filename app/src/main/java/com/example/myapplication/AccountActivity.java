package com.example.myapplication;

import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AccountActivity extends AppCompatActivity {

    EditText userName;
    EditText password;
    EditText url;
    EditText accountName;
    Button addAccount;
    Button cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.addaccount);
        userName = findViewById(R.id.editText3);
        password = findViewById(R.id.editText4);
        url = findViewById(R.id.editText5);
        accountName = findViewById(R.id.editText6);
        addAccount = findViewById(R.id.button2);
        cancel = findViewById(R.id.button3);

        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        addAccount.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String user,pass,accountText,urlText;
                user = userName.getText().toString();
                pass = password.getText().toString();
                accountText = accountName.getText().toString();
                urlText = url.getText().toString();
                JSONObject userData = new JSONObject();
                if(accountText.length() != 0 && urlText.length() != 0){

                    try {
                        userData.put("username", user);
                        userData.put("password", pass);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");
                    RequestBody body = RequestBody.create(MEDIA_TYPE, userData.toString());
                    Request request = new Request.Builder().url("http://192.168.10.157:4000/authenticate").post(body).build();

                    OkHttpClient client = new OkHttpClient();
                    try {
                        Response response = client.newCall(request).execute();
                        JSONObject object = new JSONObject(response.body().string());
                        Boolean isLogin = Boolean.valueOf(object.getString("login"));
                        int userId = Integer.parseInt(object.getString("userId"));
                        String token = object.getString("token");
                        String validationToken = object.getString("validationToken");

                        if(isLogin){

                            Account account = new Account();
                            account.setUserName(user);
                            account.setPassword(pass);
                            account.setAccountName(accountText);
                            account.setUrl(urlText);
                            account.setUserId(userId);
                            account.setToken(token);

                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            createAccount(account.getAccountName(),account.getPassword(),token, account.getUrl(),account.getUserId(), validationToken);
                            startActivity(intent);
                        }else{
                            Toast.makeText(getApplicationContext(), "Kullanıcı adı ya da şifre yanlış!", Toast.LENGTH_LONG).show();
                        }
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Kullanıcı adı ya da şifre yanlış!", Toast.LENGTH_LONG).show();
                    }

                }else{
                    Toast.makeText(getApplicationContext(), "Hesap ismi ve Url boş olamaz!", Toast.LENGTH_LONG).show();
                }
            }
    });
}

    public void createAccount(String email, String password, String authToken, String url, int userId, String validationToken) {
        android.accounts.Account account = new android.accounts.Account(email, "Regular");

        AccountManager acm = AccountManager.get(this);
        Bundle bundle = new Bundle();
        bundle.putString("url",url);
        bundle.putString("userId", String.valueOf(userId));
        bundle.putString("token",authToken);
        bundle.putString("validationToken", validationToken);
        acm.addAccountExplicitly(account, password, bundle);
        acm.setAuthToken(account, "full_access", authToken);
    }
}
