package com.example.medicalnotifier;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    EditText txtMedicineName,txtDate;
    String channel_name = "Medical";
    String CHANNEL_ID="MEDX";
    Button btnSave,btnShow;
    TextView lblData;
    MyDatabase myDatabase;
    Spinner dropdown;
    int notificationId=0;
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = channel_name;
            String description = "Medical Notifier";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createNotificationChannel();
        setContentView(R.layout.activity_main);
        txtMedicineName=(EditText)findViewById(R.id.txt_medicine_name);
        txtDate=(EditText)findViewById(R.id.editTextDate);

        btnSave=(Button)findViewById(R.id.btn_save);
        btnSave.setOnClickListener(this);
        btnShow=(Button)findViewById(R.id.btn_show);
        btnShow.setOnClickListener(this);
        lblData=(TextView)findViewById(R.id.lbl_data);
        myDatabase=new MyDatabase(getBaseContext(), MyDatabase.DATABASE_NAME,null,1);
        String clearDBQuery = "DELETE FROM "+"MEDICINE_NAMES";
        SQLiteDatabase db = myDatabase.getWritableDatabase();
        db.execSQL(clearDBQuery);
         dropdown = findViewById(R.id.txt_time);
        //create a list of items for the spinner.
        String[] items = new String[]{"Morning", "Afternoon", "Evening"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        //set the spinners adapter to the previously created one.
        dropdown.setAdapter(adapter);
        }
    public void scheduleNotification(Context context, long delay, int notificationId,String title,String content) {//delay is after how much time(in millis) from current time you want to schedule the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_android_black_24dp);



        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent activity = PendingIntent.getActivity(context, notificationId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(activity);

        Notification notification = builder.build();

        Intent notificationIntent = new Intent(context, MyNotificationPublisher.class);
        notificationIntent.putExtra(MyNotificationPublisher.NOTIFICATION_ID, notificationId);
        notificationIntent.putExtra(MyNotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

        public void onClick(View v)
        {
            if(v.equals(btnSave))
            {
                String medicineName=txtMedicineName.getText().toString();
                String date=txtDate.getText().toString();
                String time=dropdown.getSelectedItem().toString();
                SQLiteDatabase database=myDatabase.getWritableDatabase();
                ContentValues cv=new ContentValues();
                cv.put("NAME",medicineName);
                cv.put("MDATE",date);
                cv.put("MTIME",time);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_android_black_24dp)
                        .setContentTitle("Time For Meds")
                        .setContentText("Take "+medicineName+" Now")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setAutoCancel(true);
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);


               // notificationManager.notify(notificationId++, builder.build());
                scheduleNotification(this,5000,notificationId++,"Time for Meds","Take "+medicineName+" Now");

                database.insert("MEDICINE_NAMES",null,cv);
                Toast.makeText(getBaseContext(),"DataSaved",Toast.LENGTH_LONG).show();
            }
            else if(v.equals(btnShow))
            {
                SQLiteDatabase database=myDatabase.getReadableDatabase();
                Cursor cursor=database.query("MEDICINE_NAMES",
                        new String[]{"NAME","MDATE","MTIME"},null,null,null,null,null);
                lblData.setText("NAME\t\t\t\t\tDATE\t\t\t\t\t\t\tTIME\n\n");
                while(cursor.moveToNext())
                {
                    lblData.append(cursor.getString(0)+"\t\t\t\t\t");
                    lblData.append(cursor.getString(1)+"\t\t\t\t\t\t");
                    lblData.append(cursor.getString(2)+"\n");
                }
                cursor.close();
            }
    }
}