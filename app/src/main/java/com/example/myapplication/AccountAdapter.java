package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class AccountAdapter extends ArrayAdapter<Account> {

    private Context mContext;
    private List<Account> rows = new ArrayList<>();
    private int count;

    AccountAdapter(@NonNull Context context, @SuppressLint("SupportAnnotationUsage") @LayoutRes List<Account> list) {
        super(context, R.layout.list_item, list);
        mContext = context;
        rows = list;
        count = rows.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ViewHolder holder;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item,parent,false);

            holder = new ViewHolder();
            holder.personNameLabel = convertView.findViewById(R.id.textView_name);
            holder.url = convertView.findViewById(R.id.textView_url);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.personNameLabel.setText(this.rows.get(position).getAccountName());
        holder.url.setText(this.rows.get(position).getUrl());

        return convertView;
    }

    @Override
    public Account getItem(int position){

        return this.rows.get(position);
    }

    @Override
    public int getCount() {

        return this.rows.size();
    }

    public void setCount(int count) {

        this.count = count;
    }

        class ViewHolder {
        TextView personNameLabel;
        TextView url;
    }
}
