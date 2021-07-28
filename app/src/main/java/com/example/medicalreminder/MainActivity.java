package com.example.medicalreminder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatActivity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.w3c.dom.Text;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
        EditText txtMedicineName,txtDate,txtTime;
        Button btnSave,btnShow;
        TextView lblData;
        MyDatabase myDatabase;
        @Override
        protected void onCreate(Bundle savedInstanceState){
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_main);
                txtMedicineName=(EditText)findViewById(R.id.txt_medicine_name);
                txtDate=(EditText)findViewById(R.id.txt_date);
                txtTime=(EditText)findViewById(R.id.txt_time);
                btnSave=(Button)findViewById(R.id.btn_save);
                btnSave.setOnClickListener(this);
                btnShow=(Button)findViewById(R.id.btn_show);
                btnShow.setOnClickListener(this);
                lblData=(TextView)findViewById(R.id.lbl_data);
                myDatabase=new MyDatabase(getBaseContext(), MyDatabase.DATABASE_NAME,null,1);
        }
        public void onClick(View v)
        {
                if(v.equals(btnSave))
                {
                        String medicineName=txtMedicineName.getText().toString();
                        String date=txtDate.getText().toString();
                        String time=txtTime.getText().toString();
                        SQLiteDatabase database=myDatabase.getWritableDatabase();
                        ContentValues cv=new ContentValues();
                        cv.put("NAME",medicineName);
                        cv.put("MDATE",date);
                        cv.put("MTIME",time);

                        database.insert("MEDICINE_NAMES",null,cv);
                        Toast.makeText(getBaseContext(),"DataSaved",Toast.LENGTH_LONG).show();
                }
                else if(v.equals(btnShow))
                {
                        SQLiteDatabase database=myDatabase.getReadableDatabase();
                        Cursor cursor=database.query("MEDICINE_NAMES",
                        new String[]{"NAME","MDATE","MTIME"},null,null,null,null,null);
                        lblData.setText("NAME\tDATE\tTIME\n");
                        while(cursor.moveToNext())
                        {
                                lblData.append(cursor.getString(0)+"\t");
                                lblData.append(cursor.getString(1)+"\t");
                                lblData.append(cursor.getString(2)+"\n");
                        }
                }
        }
}