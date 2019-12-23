package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;

public class MainActivity extends AppCompatActivity{

    Button loginButton;
    Context context;
    TextView info;
    AccountAdapter mAdapter;
    Button deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        info = findViewById(R.id.textView2);
        loginButton = findViewById(R.id.button);
        deleteButton = findViewById(R.id.button5);
        List<Account> accountList = new ArrayList<>();
        ListView list = findViewById(R.id.listView);

        AccountManager accountManager = AccountManager.get(context);
        android.accounts.Account[] accounts = accountManager.getAccountsByType("Regular");

        if(SaveSharedPreference.getLoggedStatus(getApplicationContext())) {

            Intent intent = new Intent(getApplicationContext(), AfterLogin.class);
            startActivity(intent);
        }else{

            if(accounts.length >0){

                for(int j = 0; j <accounts.length ; j++){

                    String url = accountManager.getUserData(accounts[j], "url");
                    int userId = Integer.parseInt(accountManager.getUserData(accounts[j], "userId"));
                    String token = accountManager.getUserData(accounts[j], "token");
                    String validationToken = accountManager.getUserData(accounts[j], "validationToken");
                    String name = accounts[j].name;
                    Account account = new Account();
                    account.setUserId(userId);
                    account.setUrl(url);
                    account.setAccountName(name);
                    account.setToken(token);
                    account.setValidationToken(validationToken);
                    accountList.add(account);
                }
            }

            if(accounts.length == 0){

                info.setText("Hiç hesap bulunamadı, lütfen hesap ekleyiniz.");
            }else{
                mAdapter = new AccountAdapter(this,accountList);
                list.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
                list.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {

                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        Account selected = (Account)parent.getAdapter().getItem(position);
                        Toast.makeText(getApplicationContext(), "Seçilen Hesap: " + selected.getAccountName(), Toast.LENGTH_SHORT).show();
                        SaveSharedPreference.setLoggedIn(getApplicationContext(), true);
                        SaveSharedPreference.setUserId(getApplicationContext(), selected.getUserId());
                        SaveSharedPreference.setUrl(getApplicationContext(), selected.getUrl());
                        SaveSharedPreference.setToken(getApplicationContext(), selected.getToken());
                        SaveSharedPreference.setValidationToken(getApplicationContext(), selected.getValidationToken());
                        Intent login = new Intent(MainActivity.this, AfterLogin.class);
                        login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK |FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(login);
                    }
                });
                info.setText("Hesaplardan birini seçiniz.");
            }
        }

        deleteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AccountManager accMgr = AccountManager.get(getApplicationContext());
                android.accounts.Account[] deleteAccounts = accMgr.getAccountsByType("Regular");
                for (android.accounts.Account ac : deleteAccounts) {
                    accMgr.removeAccount(ac, null, null);
                }
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), AccountActivity.class);
                startActivity(intent);
            }
        });
    }
}
