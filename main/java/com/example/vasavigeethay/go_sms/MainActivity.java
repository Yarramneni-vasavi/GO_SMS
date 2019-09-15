package com.example.vasavigeethay.go_sms;

import android.app.ActionBar;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.*;

public class MainActivity extends AppCompatActivity {

    int year_x,month_x,day_x,hour_x,min_x;
    final static int DIALOG_ID_DATE = 0;
    final static int DIALOG_ID_TIME = 1;
    private final int PICK_CONTACT = 1;
    int d = 0, t = 0;
    Button set_time, set_Date;
    Button bt;
    EditText num,msg;
    ImageButton search,ib;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        set_Date = (Button)findViewById(R.id.button3);
        set_Date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d = 1;
                t = 0;
                showDialog(DIALOG_ID_DATE);
            }
        });

        set_time = (Button)findViewById(R.id.button2);
        set_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                t = 1; d = 0;
                showDialog(DIALOG_ID_TIME);
            }
        });

        bt = (Button)findViewById(R.id.button);
        search = (ImageButton)findViewById(R.id.imageButton);
        num = (EditText)findViewById(R.id.editText);
        msg = (EditText)findViewById(R.id.editText2);
        ib = (ImageButton)findViewById(R.id.imageButton2);

        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callContacts(v);
            }
        });

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String p = num.getText().toString().trim();
                String m = msg.getText().toString().trim();
                if (p.isEmpty() || p.length() == 0 || p.equals("") || p == null) {
                    Toast.makeText(getApplicationContext(), "enter recipient number", Toast.LENGTH_LONG).show();
                } else if (m.isEmpty() || m.length() == 0 || m.equals("") || m == null) {
                    Toast.makeText(getApplicationContext(), "enter a message to recipient", Toast.LENGTH_LONG).show();
                } else {
                    scheduleAlarm(v);
                    Toast.makeText(getApplicationContext(), num.getText().toString(), Toast.LENGTH_LONG).show();
                }
            }
        });

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/*".equals(type)) {
                handleSendText(intent); // Handle text being sent
            }
        }
    }

    private void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            msg.setText(sharedText);
        }
    }

    public void promptSpeechInput()
    {
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL , RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something!!");

        try{
            startActivityForResult(i, 100);
        }
        catch (ActivityNotFoundException e)
        {
            Toast.makeText(getApplicationContext(),"Sorry! your device doesn't support speech.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected Dialog onCreateDialog(int id)
    {
        if (id == DIALOG_ID_DATE && d == 1 && t == 0)
        {
                return new DatePickerDialog(this, dpickerListener, year_x, month_x, day_x);
        }
        if (id == DIALOG_ID_TIME && t == 1 && d == 0)
        {
                return new TimePickerDialog(this, tpickerListener , hour_x, min_x, false);
        }
        return null;
    }

    private TimePickerDialog.OnTimeSetListener tpickerListener = new TimePickerDialog.OnTimeSetListener(){

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            hour_x = hourOfDay;
            min_x = minute;
            Toast.makeText(MainActivity.this, hour_x+":"+min_x , Toast.LENGTH_SHORT).show();
        }
    };


    private DatePickerDialog.OnDateSetListener dpickerListener = new DatePickerDialog.OnDateSetListener(){
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            year_x = year;
            month_x = monthOfYear;
            day_x = dayOfMonth;
            Toast.makeText(MainActivity.this, year_x + "/" + (month_x) + "/" + day_x , Toast.LENGTH_SHORT).show();
        }
    };

    protected void callContacts(View v)
    {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(intent, PICK_CONTACT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode)
        {
            case 100:   if(resultCode == RESULT_OK && data != null)
            {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                msg.setText(result.get(0));
            }
                break;

        }
        if(requestCode == PICK_CONTACT)
        {
            if(resultCode == AppCompatActivity.RESULT_OK)
            {
                Uri ContactData = data.getData();
                Cursor c = getContentResolver().query(ContactData,null,null,null,null);

                if (c.moveToFirst())
                {
                    String nu = c.getString(c.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    num.setText(nu);
                }
            }
        }
    }

    protected void scheduleAlarm(View v)
    {
        Calendar thatDay = Calendar.getInstance();
        thatDay.set(Calendar.DAY_OF_MONTH, day_x);
        thatDay.set(Calendar.MONTH, month_x);
        thatDay.set(Calendar.YEAR, year_x);
        thatDay.set(Calendar.HOUR_OF_DAY, hour_x);
        thatDay.set(Calendar.MINUTE, min_x);

        Long time = thatDay.getTimeInMillis();
        Intent intentAlarm = new Intent(this, AlarmReceiver.class);
        intentAlarm.putExtra("num", num.getText().toString());
        intentAlarm.putExtra("msg", msg.getText().toString());
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, time, PendingIntent.getBroadcast(this, 1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
        Toast.makeText(this, "Alarm Scheduled !", Toast.LENGTH_LONG).show();
    }

}
