package com.example.sms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    EditText editTextPhone,editTextMessage;
    private ListView listViewMessages;
    private EditText editTextForwardPhone;
    Button btnSent,btnViewReplies;
    private ArrayList<String> receivedMessages;
    private ArrayAdapter<String> adapter;
    private String selectedMessage = null;
    private String lastSender = "";
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);
        listViewMessages = findViewById(R.id.listViewMessages);
        editTextForwardPhone = findViewById(R.id.editTextForwardPhone);
        editTextPhone=findViewById(R.id.editTextPhone);
        editTextMessage=findViewById(R.id.editTextMessage);
        btnSent=findViewById(R.id.btnSent);
        Button btnCopy = findViewById(R.id.btnCopy);
        Button btnForward = findViewById(R.id.btnForward);
        TextView repliesTextView;
        BroadcastReceiver smsReceiver;
        repliesTextView = findViewById(R.id.repliesTextView);

        receivedMessages = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, receivedMessages);
        listViewMessages.setAdapter(adapter);
        listViewMessages.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        loadMessages();
        // Handle message selection from ListView
        listViewMessages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedMessage = receivedMessages.get(position); // Store selected message
            }
        });
        // Register to receive SMS broadcast
        smsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String sender = intent.getStringExtra("sms_sender");
                String message = intent.getStringExtra("sms_body");
                receivedMessages.add("From: " + sender + "\n" + message);
                adapter.notifyDataSetChanged();
                dbHelper.insertMessage(sender, message);
                // Update the TextView with received SMS
//                repliesTextView.append("From: " + sender + "\n" + message + "\n\n");
            }
        };
        registerReceiver(smsReceiver, new IntentFilter("SMS_RECEIVED_ACTION"));
        btnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedMessage != null) {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("SMS", selectedMessage);
                    clipboard.setPrimaryClip(clip);

                    Toast.makeText(MainActivity.this, "Message copied to clipboard", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Please select a message", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedMessage != null) {
                    String forwardPhoneNumber = editTextForwardPhone.getText().toString().trim();
                    if (!forwardPhoneNumber.isEmpty()) {
                        // Send the selected message using SmsManager
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(forwardPhoneNumber, null, selectedMessage, null, null);

                        Toast.makeText(MainActivity.this, "Message forwarded", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Please enter a phone number", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Please select a message", Toast.LENGTH_SHORT).show();
                }
            }
        });


        btnSent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check condition for permission
                if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS)== PackageManager.PERMISSION_GRANTED){
                    //when permission granted
                    //create a method
                    sendSMS();
                }else{
                    //when permission is denied
                    //request for permission
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS},100);
                }
            }
        });

//        btnViewReplies.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, Smsreciever.class);
//                startActivity(intent);
//            }
//        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==100 && grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            //permission is granted
            //call method
            sendSMS();
        }else{
            //when permission is deneid
            //display toast message
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendSMS(){
        //get value from edittext
        String phones = editTextPhone.getText().toString();
        String messages = editTextMessage.getText().toString();

        // Check if input fields are not empty
        if (!phones.isEmpty() && !messages.isEmpty()) {
            // Split phone numbers and messages by commas
            String[] phoneArray = phones.split(",");
            String[] messageArray = messages.split(",");

            // Check if both arrays have the same length
            if (phoneArray.length == messageArray.length) {
                SmsManager smsManager = SmsManager.getDefault();

                // Loop through phone numbers and messages
                for (int i = 0; i < phoneArray.length; i++) {
                    String phone = phoneArray[i].trim();
                    String message = messageArray[i].trim();
                    // Send SMS for each pair
                    smsManager.sendTextMessage(phone, null, message, null, null);
                    dbHelper.insertMessage("Me", message);
                }

                // Display success toast
                Toast.makeText(this, "SMS sent to multiple recipients", Toast.LENGTH_SHORT).show();
            } else {
                // Error: mismatch in number of phones and messages
                Toast.makeText(this, "Mismatch between phone numbers and messages", Toast.LENGTH_SHORT).show();
            }
        } else {
            // If input is empty, show error message
            Toast.makeText(this, "Please enter valid phone numbers and messages", Toast.LENGTH_SHORT).show();
        }

    }
    private void loadMessages() {
        // Load messages from the database
        receivedMessages.clear();
        receivedMessages.addAll(dbHelper.getAllMessages());
        adapter.notifyDataSetChanged();
    }
}