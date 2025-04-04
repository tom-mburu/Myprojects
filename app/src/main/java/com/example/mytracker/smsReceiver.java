package com.example.mytracker;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsMessage;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import java.util.Date;
import java.util.Locale;

public class smsReceiver extends BroadcastReceiver {
    static public registeredvehicles mycontext;
    static ProgressDialog dialog;
    static String phoneNumber=null;
    static public void  update(ProgressDialog d){
        dialog=d;

    }
    static public void getContext(registeredvehicles context){
        mycontext=context;


    };
    static public void getPhoneNumber(String PhoneNumber){
        phoneNumber=PhoneNumber;
    }
   // public static final String SMS_BUNDLE = "pdus";

    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

    @Override
    public void onReceive(Context context, Intent intent) {
        dialog.hide();

        //Toast.makeText(context.getApplicationContext(),"RECEIVED SMS",Toast.LENGTH_LONG).show();
        if (intent.getAction().equals(SMS_RECEIVED)) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                // get sms objects
                Object[] pdus = (Object[]) bundle.get("pdus");
                assert pdus != null;
                if (pdus.length == 0) {
                    return;
                }
                // large message might be broken into many
                SmsMessage[] messages = new SmsMessage[pdus.length];
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < pdus.length; i++) {
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    sb.append(messages[i].getMessageBody());
                }
                String sender = messages[0].getOriginatingAddress();
                String message = sb.toString();
                //if(sender.contains(phoneNumber)){
                //Toast.makeText(context.getApplicationContext(), message+"\n sender :"+sender, Toast.LENGTH_SHORT).show();
                //open maps and display current location on maps
                if(message.contains("geofenceBroken")){
                    //vehicle is out of the geofence
                    //push notification
                    Intent startServiceIntent=new Intent(context, ShowNotificationService.class);
                    intent.putExtra("details",message);
                    context
                    .startService(startServiceIntent);
                   mycontext.pushNotification(message);
                }
                Toast.makeText(context.getApplicationContext(),"sender : "+sender,Toast.LENGTH_LONG).show();
               if (sender.contains(phoneNumber.replaceFirst("0","+254"))) {
                    if(message.contains("latitude") && message.contains("longitude")) {
                        //display on maps the received locations .
                        String[] coordinates = message.trim().split("\n");
                        String latitude = coordinates[0].split(":")[1];
                        //latitude :-1.277952
                        //longitude : 36.8214016
                        String longitude = coordinates[1].split(":")[1];
                        mycontext.showGoogleMaps(latitude, longitude);
                       // Toast.makeText(context.getApplicationContext(), latitude + "\n" + longitude, Toast.LENGTH_LONG).show();

                   }
                    if(message.contains("lat")&&message.contains("lng")){
                        String [] parts=message.split("\n");
                        String lat=parts[0].split(":")[1];
                        String lng=parts[1].split(":")[1];
                        String date=new Date().toString();
                        mycontext.saveCurrentLocation(lat,lng,date);
                    }



                }
            }


            }
                // prevent any other broadcast receivers from receiving broadcast
                // abortBroadcast();
            //}
        }
    }


