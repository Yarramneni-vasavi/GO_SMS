package com.example.vasavigeethay.go_sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.gsm.SmsManager;
import android.widget.Toast;

/**
 * Created by Vasavi Geetha .Y on 21-May-16.
 */
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent)
    {
            String num = intent.getStringExtra("num");
            String msg = intent.getStringExtra("msg");
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(num,null,msg,null,null);
            Toast.makeText(context,"Alarm triggered",Toast.LENGTH_LONG).show();
    }
}
