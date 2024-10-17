package com.example.sms;

import static android.telephony.SmsMessage.createFromPdu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class Smsreciever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        SmsMessage[] messages = null;
        String sender = "";
        String messageBody = "";

        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            if (pdus != null) {
                messages = new SmsMessage[pdus.length];
                for (int i = 0; i < pdus.length; i++) {
                    messages[i] = createFromPdu((byte[]) pdus[i], bundle.getString("format"));
                    sender = messages[i].getOriginatingAddress();
                    messageBody += messages[i].getMessageBody();
                }

                // Display or handle the SMS received
                Toast.makeText(context, "Received SMS from: " + sender + "\nMessage: " + messageBody, Toast.LENGTH_LONG).show();

                // Send received SMS to the main activity
                Intent smsIntent = new Intent("SMS_RECEIVED_ACTION");
                smsIntent.putExtra("sms_sender", sender);
                smsIntent.putExtra("sms_body", messageBody);
                context.sendBroadcast(smsIntent);
            }
        }
    }
}