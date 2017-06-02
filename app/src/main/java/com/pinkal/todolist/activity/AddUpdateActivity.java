package com.pinkal.todolist.activity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.pinkal.todolist.broadcastreceiver.AlarmReceiver;
import com.pinkal.todolist.R;
import com.pinkal.todolist.database.DatabaseHelper;
import com.pinkal.todolist.utils.Constant;
import com.pinkal.todolist.utils.Utils;
import com.pinkal.todolist.widget.CounterWidgetProvider;
import com.pinkal.todolist.widget.TaskWidgetProvider;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Pinkal Daliya on 06-Oct-16.
 */
public class AddUpdateActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;

    private TextView toolbar_title;
    private TextInputEditText edtEnterTaskTitle, edtEnterTask, edtSetDate, edtSetTime;
    private Spinner categorySpinner;
    private Button btnTaskAddCategory;
    private Button btnTimeCancle, btnDateCancle;
    private LinearLayout addLinearLayout2;

    private Calendar myCalendar;
    private DatePickerDialog.OnDateSetListener date;
    private TimePickerDialog.OnTimeSetListener time;

    private DatabaseHelper mDatabaseHelper;
    private String cat_name_id;

    private Bundle extras;
    private String rowID;

    private String getDate = "", getTime = "";
    private Utils utils;
    String cursorTaskTitle = "", cursorTaskTask = "", cursorTaskDate = "", cursorTaskTime = "", cursorTaskCatID = "";

    String databaseStr = "", updateStr = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_update);

        setToolbar();
        initialize();
        dateAndTime();

        extras = getIntent().getExtras();
        if (extras != null) {
            rowID = extras.getString(Constant.ROW_ID);
            if (extras.getBoolean(Constant.NOTIFICATION)) {
                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.cancel(Integer.parseInt(rowID));
            }

            mDatabaseHelper.openWritableDB();
            Cursor cursor = mDatabaseHelper.getTaskFromRowId(rowID);

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                cursorTaskTitle = cursor.getString(cursor.getColumnIndex(Constant.TASK_TITLE));
                cursorTaskTask = cursor.getString(cursor.getColumnIndex(Constant.TASK_TASK));
                cursorTaskDate = cursor.getString(cursor.getColumnIndex(Constant.TASK_DATE));
                cursorTaskTime = cursor.getString(cursor.getColumnIndex(Constant.TASK_TIME));
                cursorTaskCatID = cursor.getString(cursor.getColumnIndex(Constant.TASK_CATEGORY_ID));
            }
            cursor.close();
            mDatabaseHelper.closeDB();

            edtEnterTaskTitle.setText(cursorTaskTitle);
//            int len = extras.getString(Constant.TASK_TITLE).length();
//            edtEnterTaskTitle.setSelection(len);

            edtEnterTask.setText(cursorTaskTask);

            if (!cursorTaskDate.equals(null)) {
                if (!cursorTaskDate.equals("")) {
                    btnDateCancle.setVisibility(View.VISIBLE);

                    String date = cursorTaskDate;
                    String formatDate = getFormatDate(date);

                    edtSetDate.setText(formatDate);
                    getDate = cursorTaskDate;
                }
            }
            if (!cursorTaskTime.equals(null)) {
                if (!cursorTaskTime.equals("")) {
                    addLinearLayout2.setVisibility(View.VISIBLE);
                    btnTimeCancle.setVisibility(View.VISIBLE);

                    String time = cursorTaskTime;
                    String formatTime = getFormatTime(time);

                    edtSetTime.setText(formatTime);
                    getTime = cursorTaskTime;
                }
            }
            databaseStr = rowID + cursorTaskTitle + cursorTaskTask + cursorTaskDate + cursorTaskTime + cursorTaskCatID;
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSpinnerData();
    }

    @Override
    public void onBackPressed() {

        if (getIntent().getExtras() == null) {
            if (!edtEnterTaskTitle.getText().toString().equals("") || !edtEnterTask.getText().toString().equals("")) {
                optionBack();
            } else {
                finish();
            }
        } else {
            if (extras.getBoolean(Constant.NOTIFICATION) || extras.getBoolean(Constant.WIDGET)) {
                save();
                startActivity(new Intent(this, MainActivity.class));
            } else
                save();
        }
//        super.onBackPressed();
    }

    private void initialize() {

        mDatabaseHelper = new DatabaseHelper(this);

        edtEnterTaskTitle = (TextInputEditText) findViewById(R.id.edtEnterTaskTitle);
        edtEnterTask = (TextInputEditText) findViewById(R.id.edtEnterTask);
        edtSetDate = (TextInputEditText) findViewById(R.id.edtSetDate);
        edtSetDate.setOnClickListener(this);
        edtSetTime = (TextInputEditText) findViewById(R.id.edtSetTime);
        edtSetTime.setOnClickListener(this);
        btnTaskAddCategory = (Button) findViewById(R.id.btnTaskAddCategory);
        btnTaskAddCategory.setOnClickListener(this);

        categorySpinner = (Spinner) findViewById(R.id.categorySpinner);
        loadSpinnerData();

        btnDateCancle = (Button) findViewById(R.id.btnDateCancle);
        btnDateCancle.setOnClickListener(this);
        btnTimeCancle = (Button) findViewById(R.id.btnTimeCancle);
        btnTimeCancle.setOnClickListener(this);
        addLinearLayout2 = (LinearLayout) findViewById(R.id.addLinearLayout2);
    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_add_update);
        toolbar_title = (TextView) findViewById(R.id.toolbar_title_add_update);
        toolbar_title.setText(R.string.toolTask);
        toolbar.setTitle("");

        setSupportActionBar(toolbar);
        Button btnback = (Button) findViewById(R.id.btnBackAddUpdate);
        btnback.setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_update, menu);

        if (getIntent().getExtras() == null) {

        } else {
            MenuItem menuItem = menu.findItem(R.id.menuDelete);
            menuItem.setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menuDelete:
                optionDelete();
                return true;
            case R.id.menuSave:
                save();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //save task
    private void save() {

        String taskTitle = edtEnterTaskTitle.getText().toString().trim();
        String task = edtEnterTask.getText().toString().trim();
        String date = getDate;
        String time = getTime;
        Log.e("time -----------> ", time);
        String cat_id = mDatabaseHelper.getCategoryId(cat_name_id);


        if (getIntent().getExtras() == null) {

            if (taskTitle.equals("")) {
                Toast.makeText(this, R.string.pleaseEnterTaskTitle, Toast.LENGTH_SHORT).show();
            } else {
                if (task.equals("")) {
                    Toast.makeText(this, R.string.pleaseEnterTask, Toast.LENGTH_SHORT).show();
                } else {
                    long id = mDatabaseHelper.insertIntoTask(taskTitle, task, date, time, cat_id);


                    String strId = String.valueOf(id);
                    Log.e("row id --------- >", "" + id);
                    Log.e("row str id --------- >", "" + strId);

                    if (!date.equals(null)) {
                        if (!date.equals("")) {
                            Log.e("notify : ", "Date " + date);

                            if (!time.equals(null)) {
                                if (!time.equals("")) {
                                    Log.e("notify : ", "Time " + time);

                                    notifyOnDateTime(strId, taskTitle, task, date, time);
                                }
                            } else {
                                notifyOnDate(strId, taskTitle, task, date);
                            }
                        }
                    }
                    Toast.makeText(this, R.string.taskSaveSuccess, Toast.LENGTH_SHORT).show();
                    widgetUpdate();
                    updateAllWidgets();
                    finish();
                }
            }

        } else {

            updateStr = rowID + taskTitle + task + date + time + cat_id;

            if (updateStr.equals(databaseStr)) {
                finish();
            } else {
                mDatabaseHelper.updateIntoTask(rowID, taskTitle, task, date, time, cat_id);
                Toast.makeText(this, R.string.taskUpdateSuccess, Toast.LENGTH_SHORT).show();


                if (!date.equals(null)) {
                    if (!date.equals("")) {
                        Log.e("notify : ", "Date " + date);

                        if (!time.equals(null)) {
                            if (!time.equals("")) {
                                Log.e("notify : ", "Time " + time);

                                notifyOnDateTime(rowID, taskTitle, task, date, time);
                            }
                        } else {
                            notifyOnDate(rowID, taskTitle, task, date);
                        }
                    }
                }
                widgetUpdate();
                updateAllWidgets();
                finish();
            }
        }


    }

    private void notifyOnDate(String id, String title, String task, String inputDate) {

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        int taskRowId = Integer.parseInt(id);
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra(Constant.TASK_ID, taskRowId);
        intent.putExtra(Constant.TASK_TITLE, title);
        intent.putExtra(Constant.TASK_TASK, task);

        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(inputDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        long timeInMilis = calendar.getTimeInMillis();
        Log.e("time total ------> ", "" + timeInMilis);
        Log.e("time ------> ", "" + (timeInMilis - date.getTime()));

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, taskRowId, intent, PendingIntent.FLAG_ONE_SHOT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMilis, pendingIntent);

    }

    private void notifyOnDateTime(String id, String title, String task, String inputDate, String inputTime) {

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        int taskRowId = Integer.parseInt(id);
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra(Constant.TASK_ID, taskRowId);
        intent.putExtra(Constant.TASK_TITLE, title);
        intent.putExtra(Constant.TASK_TASK, task);

        Date date = null;
        inputDate = inputDate + " " + inputTime;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).parse(inputDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        Log.e("both second ----> ", "" + calendar.getTimeInMillis());
        calendar.set(Calendar.SECOND, 0);

        long timeInMilis = calendar.getTimeInMillis();
        Log.e("both time total ----> ", "" + timeInMilis);
        Log.e("both time ------> ", "" + (timeInMilis - date.getTime()));

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, taskRowId, intent, PendingIntent.FLAG_ONE_SHOT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMilis, pendingIntent);


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBackAddUpdate:
                if (getIntent().getExtras() == null) {
                    if (!edtEnterTaskTitle.getText().toString().equals("") || !edtEnterTask.getText().toString().equals("")) {
                        optionBack();
                    } else {
                        finish();
                    }
                } else {
                    if (extras.getBoolean(Constant.NOTIFICATION) || extras.getBoolean(Constant.WIDGET)) {
                        save();
                        startActivity(new Intent(this, MainActivity.class));
                    } else
                        save();
                }
                break;
            case R.id.edtSetDate:
                setDate();
                break;
            case R.id.edtSetTime:
                setTime();
                break;
            case R.id.btnTaskAddCategory:
                addCategory();
                break;
            case R.id.btnDateCancle:
                edtSetDate.setText("");
                btnDateCancle.setVisibility(View.GONE);
                addLinearLayout2.setVisibility(View.GONE);
                edtSetTime.setText("");
                btnTimeCancle.setVisibility(View.GONE);
                getTime = "";
                getDate = "";
                break;
            case R.id.btnTimeCancle:
                edtSetTime.setText("");
                btnTimeCancle.setVisibility(View.GONE);
                getTime = "";
                break;
        }
    }

    private void optionBackUpdate() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.dialogTitleUpdate);
        alert.setMessage(R.string.dialogMsgUpdate);

        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                save();
            }
        });

        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        alert.show();
    }

    private void optionBack() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.dialogTitleSave);
        alert.setMessage(R.string.dialogMsgSave);

        alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                save();
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        alert.show();
    }

    private void optionDelete() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.dialogTitleDelete);
        alert.setMessage(R.string.dialogMsgDelete);

        alert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDatabaseHelper.deleteIntoTask(rowID);
                Toast.makeText(AddUpdateActivity.this, R.string.taskDeleted, Toast.LENGTH_SHORT).show();
                widgetUpdate();
                updateAllWidgets();
                finish();
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alert.show();
    }

    private void loadSpinnerData() {

        List<String> labels = new ArrayList<String>();

        mDatabaseHelper.openWritableDB();
        Cursor c1 = mDatabaseHelper.listAllCategory();
        if (c1 != null && c1.getCount() != 0) {
            if (c1.moveToFirst()) {
                do {
                    labels.add(c1.getString(c1.getColumnIndex(Constant.CATEGORY_NAME)));

                } while (c1.moveToNext());
            }
        }
        c1.close();
        mDatabaseHelper.closeDB();

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_category_item, labels);
        dataAdapter.setDropDownViewResource(R.layout.spinner_category_dropdown_item);
        Collections.sort(labels);
        categorySpinner.setAdapter(dataAdapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                cat_name_id = parent.getItemAtPosition(position).toString();

//                Toast.makeText(AddUpdateActivity.this, "Cat : " + cat_name_id + ", " + id, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //for update task spinner
        if (getIntent().getExtras() != null) {

            int spinnerPosition = 0;

            String catName = mDatabaseHelper.getCategoryName(cursorTaskCatID);

            spinnerPosition = dataAdapter.getPosition(catName);
            categorySpinner.setSelection(spinnerPosition);
        }
    }

    private void addCategory() {

        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.alert_dialog_add_cat, null);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(promptsView);


        final EditText input = (EditText) promptsView.findViewById(R.id.edtAddUpdateAddCat);


        alert.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                String cat = input.getText().toString().trim();

                if (cat.equals("")) {
                    Toast.makeText(AddUpdateActivity.this, R.string.pleaseEnterCategory, Toast.LENGTH_SHORT).show();
                } else {
                    mDatabaseHelper.insertIntoCategory(cat);
                    Toast.makeText(AddUpdateActivity.this, R.string.categoryAdded, Toast.LENGTH_SHORT).show();
                    onResume();
                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        AlertDialog alertDialog = alert.create();
        alertDialog.show();
    }

    private void dateAndTime() {

        myCalendar = Calendar.getInstance();

        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabelDate();
            }

        };

        time = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                myCalendar.set(Calendar.MINUTE, minute);
                updateLabelTime();
            }
        };


    }

    private void setDate() {

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, date, myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();

    }

    private void setTime() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, time, myCalendar.get(Calendar.HOUR_OF_DAY),
                myCalendar.get(Calendar.MINUTE), false);
        timePickerDialog.show();
    }

    private void updateLabelTime() {

        String myFormat = "HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        getTime = sdf.format(myCalendar.getTime());


        String myFormat2 = "h:mm a";
        SimpleDateFormat sdf2 = new SimpleDateFormat(myFormat2, Locale.US);
        edtSetTime.setText(sdf2.format(myCalendar.getTime()));
        btnTimeCancle.setVisibility(View.VISIBLE);
    }

    private void updateLabelDate() {

        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        getDate = sdf.format(myCalendar.getTime());


        String myFormat2 = "EEE, d MMM yyyy";
        SimpleDateFormat sdf2 = new SimpleDateFormat(myFormat2, Locale.US);
        edtSetDate.setText(sdf2.format(myCalendar.getTime()));

        addLinearLayout2.setVisibility(View.VISIBLE);
        btnDateCancle.setVisibility(View.VISIBLE);
    }

    public String getFormatDate(String inputDate) {

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat outputFormat = new SimpleDateFormat("EEE, d MMM yyyy");

        Date date = null;
        try {
            date = inputFormat.parse(inputDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String outputDate = outputFormat.format(date);
        return outputDate;
    }

    public String getFormatTime(String inputTime) {

        SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat outputFormat = new SimpleDateFormat("h:mm a");

        Date date = null;
        try {
            date = inputFormat.parse(inputTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String outputTime = outputFormat.format(date);
        return outputTime;
    }

    private void widgetUpdate() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int appWidgetIds[] = appWidgetManager.getAppWidgetIds(new ComponentName(this, TaskWidgetProvider.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widgetListview);

        AppWidgetManager appWidgetManager1 = AppWidgetManager.getInstance(this);
        int appWidgetIdsCounter[] = appWidgetManager.getAppWidgetIds(new ComponentName(this, CounterWidgetProvider.class));
        RemoteViews widget = new RemoteViews(this.getPackageName(), R.layout.widget_counter_layout);
        appWidgetManager1.updateAppWidget(appWidgetIdsCounter, widget);
    }

    private void updateAllWidgets() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, CounterWidgetProvider.class));
        if (appWidgetIds.length > 0) {
            new CounterWidgetProvider().onUpdate(this, appWidgetManager, appWidgetIds);
        }
    }

}
