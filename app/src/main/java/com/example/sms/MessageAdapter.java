package com.example.sms;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MessageAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> messages;
    private ArrayList<String> senders;

    public MessageAdapter(Context context, ArrayList<String> senders, ArrayList<String> messages) {
        this.context = context;
        this.messages = messages;
        this.senders = senders;
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_message, parent, false);
        }

        TextView tvSender = convertView.findViewById(R.id.tvSender);
        TextView tvMessage = convertView.findViewById(R.id.tvMessage);
        TextView tvTimestamp = convertView.findViewById(R.id.tvTimestamp);

        // Set sender and message text
        tvSender.setText("From: " + senders.get(position));
        tvMessage.setText(messages.get(position));
        // For simplicity, we'll assume the timestamp is static
        tvTimestamp.setText("12:45 PM");

        return convertView;
    }
}
