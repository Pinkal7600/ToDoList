package com.pinkal.todolist.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.pinkal.todolist.utils.Constant;
import com.pinkal.todolist.widget.CounterWidgetProvider;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Pinkal Daliya on 06-Oct-16.
 */

public class DatabaseHelper extends SQLiteOpenHelper {


    private Context context;
    private SQLiteDatabase db;

    public DatabaseHelper(Context context) {
        super(context, Constant.DATABASE_NAME, null, Constant.DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String CREATE_TABLE = " CREATE TABLE IF NOT EXISTS ";
        final String PS = " ( ";
        final String PE = " ) ";
        final String PRIMARY_KEY = " INTEGER PRIMARY KEY AUTOINCREMENT, ";
        final String TEXT = " TEXT, ";
        final String END_TEXT = " TEXT ";
        final String INTEGER = " INTEGER,";
        final String FOREIGN_KEY = " FOREIGN KEY ";
        final String REFERENCES = " REFERENCES ";

        String createTableCategory = CREATE_TABLE + Constant.TABLE_CATEGORY + PS
                + Constant.CATEGORY_ID + PRIMARY_KEY
                + Constant.CATEGORY_NAME + END_TEXT
                + PE;
        db.execSQL(createTableCategory);

        ContentValues cv = new ContentValues();
        cv.put(Constant.CATEGORY_NAME, "Personal");
        db.insert(Constant.TABLE_CATEGORY, null, cv);

        ContentValues cv1 = new ContentValues();
        cv1.put(Constant.CATEGORY_NAME, "Business");
        db.insert(Constant.TABLE_CATEGORY, null, cv1);

        ContentValues cv2 = new ContentValues();
        cv2.put(Constant.CATEGORY_NAME, "Insurance");
        db.insert(Constant.TABLE_CATEGORY, null, cv2);

        ContentValues cv3 = new ContentValues();
        cv3.put(Constant.CATEGORY_NAME, "Shopping");
        db.insert(Constant.TABLE_CATEGORY, null, cv3);

        ContentValues cv4 = new ContentValues();
        cv4.put(Constant.CATEGORY_NAME, "Banking");
        db.insert(Constant.TABLE_CATEGORY, null, cv4);

        String createTableTask = CREATE_TABLE + Constant.TABLE_TASK + PS
                + Constant.TASK_ID + PRIMARY_KEY
                + Constant.TASK_TITLE + TEXT
                + Constant.TASK_TASK + TEXT
                + Constant.TASK_DATE + TEXT
                + Constant.TASK_TIME + TEXT
                + Constant.TASK_FLAG + TEXT
                + Constant.TASK_CATEGORY_ID + INTEGER
                + FOREIGN_KEY + PS + Constant.TASK_CATEGORY_ID + PE
                + REFERENCES + Constant.TABLE_CATEGORY + PS + Constant.CATEGORY_ID + PE
                + PE;
        db.execSQL(createTableTask);

//        this.insertIntoCategory("Personal");
//        this.insertIntoCategory("Business");
//        this.insertIntoCategory("Insurance");
//        this.insertIntoCategory("Shopping");
//        this.insertIntoCategory("Banking");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Constant.TABLE_CATEGORY);
        db.execSQL("DROP TABLE IF EXISTS " + Constant.TABLE_TASK);
        onCreate(db);
    }

    public void openWritableDB() {
        db = this.getWritableDatabase();
    }

    public void closeDB() {
        db.close();
    }


    //****************************************** Category Table **********************************************************

    //insert in category table
    public void insertIntoCategory(String name) {

        ContentValues mContentValues = new ContentValues();

        mContentValues.put(Constant.CATEGORY_NAME, name);

        openWritableDB();
        db.insert(Constant.TABLE_CATEGORY, null, mContentValues);
        closeDB();
    }

    //update in category table
    public void updateIntoCategory(String id, String name) {
        ContentValues mContentValues = new ContentValues();
        mContentValues.put(Constant.CATEGORY_NAME, name);

        openWritableDB();
        db.update(Constant.TABLE_CATEGORY, mContentValues, Constant.CATEGORY_ID + "=" + id, null);
        closeDB();
    }

    //delete in category table
    public void deleteIntoCategory(String id) {
        openWritableDB();
        db.delete(Constant.TABLE_CATEGORY, Constant.CATEGORY_ID + "=" + id, null);
        closeDB();
    }

    //list of all category
    public Cursor listAllCategory() {
        return db.query(Constant.TABLE_CATEGORY,
                new String[]{Constant.CATEGORY_ID, Constant.CATEGORY_NAME},
                null, null, null, null, null);
    }

    //get category name from id
    public String getCategoryName(String id) {

        String catName = null;

        openWritableDB();

        Cursor cursor = db.rawQuery(
                "SELECT " + Constant.CATEGORY_NAME
                        + " FROM " + Constant.TABLE_CATEGORY
                        + " WHERE " + Constant.CATEGORY_ID + "=?",
                new String[]{id + ""});

        if (cursor.getCount() > 0) {

            cursor.moveToFirst();
            catName = cursor.getString(cursor.getColumnIndex(Constant.CATEGORY_NAME));
        }
        closeDB();
        cursor.close();

        return catName;
    }

    //get category id from name
    public String getCategoryId(String name) {

        String catId = null;

        openWritableDB();

        Cursor cursor = db.rawQuery(
                "SELECT " + Constant.CATEGORY_ID
                        + " FROM " + Constant.TABLE_CATEGORY
                        + " WHERE " + Constant.CATEGORY_NAME + "=?",
                new String[]{name + ""});

        if (cursor.getCount() > 0) {

            cursor.moveToFirst();
            catId = cursor.getString(cursor.getColumnIndex(Constant.CATEGORY_ID));
        }
        closeDB();
        cursor.close();

        return catId;
    }

    //count of category table (total number of rows)
    public int getCategoryRowCount() {

        String countQuery = "SELECT  * FROM " + Constant.TABLE_CATEGORY;
        openWritableDB();
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        closeDB();
        return cnt;

    }

    //****************************************** Task Table **********************************************************

    //insert in task table
    public long insertIntoTask(String title, String task, String date, String time, String cat_id) {

        ContentValues mContentValues = new ContentValues();

        mContentValues.put(Constant.TASK_TITLE, title);
        mContentValues.put(Constant.TASK_TASK, task);
        mContentValues.put(Constant.TASK_DATE, date);
        mContentValues.put(Constant.TASK_TIME, time);
        mContentValues.put(Constant.TASK_FLAG, 0);
        mContentValues.put(Constant.TASK_CATEGORY_ID, cat_id);

        openWritableDB();
        long id = db.insert(Constant.TABLE_TASK, null, mContentValues);
        closeDB();
        return id;
    }

    //update in task table
    public void updateIntoTask(String id, String title, String task, String date, String time, String cat_id) {

        ContentValues mContentValues = new ContentValues();

        mContentValues.put(Constant.TASK_TITLE, title);
        mContentValues.put(Constant.TASK_TASK, task);
        mContentValues.put(Constant.TASK_DATE, date);
        mContentValues.put(Constant.TASK_TIME, time);
        mContentValues.put(Constant.TASK_FLAG, 0);
        mContentValues.put(Constant.TASK_CATEGORY_ID, cat_id);

        openWritableDB();
        db.update(Constant.TABLE_TASK, mContentValues, Constant.TASK_ID + "=" + id, null);
        closeDB();
    }

    //delete in task table
    public void deleteIntoTask(String id) {
        openWritableDB();
        db.delete(Constant.TABLE_TASK, Constant.TASK_ID + "='" + id + "'", null);
        closeDB();
    }

    public void deleteMultipleTask(String ids) {
        openWritableDB();
        try {

            db.execSQL(String.format("DELETE FROM " + Constant.TABLE_TASK + " WHERE " + Constant.TASK_ID + " IN (%s);", ids));
        } catch (Exception e) {
            Log.e("Exception : ", e + "");
        }
        closeDB();
    }

    //list of all task
    public Cursor listAllTask() {

        return db.query(Constant.TABLE_TASK,
                new String[]{
                        Constant.TASK_ID,
                        Constant.TASK_TITLE,
                        Constant.TASK_TASK,
                        Constant.TASK_DATE,
                        Constant.TASK_TIME,
                        Constant.TASK_FLAG,
                        Constant.TASK_CATEGORY_ID},
                null, null, null, null, null);

    }

    //count of task table (total number of rows)
    public int getTaskRowCount() {

        String countQuery = "SELECT  * FROM " + Constant.TABLE_TASK;
        openWritableDB();
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        closeDB();
        return cnt;

    }

    public Cursor getTaskFromRowId(String rowId) {

//        openWritableDB();
//
//        Cursor cursor = db.rawQuery(
//                "SELECT * FROM " + Constant.TABLE_TASK
//                        + " WHERE " + Constant.TASK_ID + "=?",
//                new String[]{rowId + ""});
//
////        if (cursor.getCount() > 0) {
////
////            cursor.moveToFirst();
////            catName = cursor.getString(cursor.getColumnIndex(Constant.CATEGORY_NAME));
////        }
//        closeDB();
////        cursor.close();

        return db.rawQuery(
                "SELECT * FROM " + Constant.TABLE_TASK
                        + " WHERE " + Constant.TASK_ID + "=?",
                new String[]{rowId + ""});
    }

    public void finishTask(String ids) {

//        ContentValues mContentValues = new ContentValues();
//
//        mContentValues.put(Constant.TASK_FLAG, "1");

        openWritableDB();
//        db.update(Constant.TABLE_TASK, mContentValues, Constant.TASK_ID + "=" + ids, null);
//        Log.e("finish at id", ids);

        try {
            db.execSQL(String.format("UPDATE " + Constant.TABLE_TASK + " SET " + Constant.TASK_FLAG + " = 1 WHERE " + Constant.TASK_ID + " IN (%s);", ids));
        } catch (Exception e) {
            Log.e("Exception : ", e + "");
        }

        closeDB();
    }

    public void unFinishTask(String ids) {

//        ContentValues mContentValues = new ContentValues();
//
//        mContentValues.put(Constant.TASK_FLAG, 0);

        openWritableDB();
//        db.update(Constant.TABLE_TASK, mContentValues, Constant.TASK_ID + "=" + ids, null);
//        Log.e("finish at id", ids);

        try {
            db.execSQL(String.format("UPDATE " + Constant.TABLE_TASK + " SET " + Constant.TASK_FLAG + " = 0 WHERE " + Constant.TASK_ID + " IN (%s);", ids));
        } catch (Exception e) {
            Log.e("Exception : ", e + "");
        }

        closeDB();
    }

    public int getTodayTaskCount() {

        Calendar c = Calendar.getInstance();
        Log.e("Current time => ", "" + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String todayDate = df.format(c.getTime());

        openWritableDB();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + Constant.TABLE_TASK
                        + " WHERE " + Constant.TASK_DATE + "=?"
                        + " AND " + Constant.TASK_FLAG + " = 0 ",
                new String[]{todayDate + ""});

        int cnt = cursor.getCount();
        Log.e("cnt *-*-*-* ", "" + cnt);
        cursor.close();
        closeDB();

        return cnt;

    }

}
