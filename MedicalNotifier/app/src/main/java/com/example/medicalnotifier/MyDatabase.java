package com.example.medicalnotifier;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;
public class MyDatabase extends SQLiteOpenHelper{
    public static String DATABASE_NAME="medicine.db";
    public MyDatabase(@Nullable Context context,@Nullable String name,@Nullable SQLiteDatabase.CursorFactory factory,int version)
    {
        super(context,name,factory,version);
    }
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE MEDICINE_NAMES(NAME TEXT,MDATE DATE,MTIME TEXT)");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){
    }
}