package com.pinkal.todolist.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.pinkal.todolist.R;
import com.pinkal.todolist.database.DatabaseHelper;
import com.pinkal.todolist.model.WidgetModel;
import com.pinkal.todolist.utils.Constant;
import com.pinkal.todolist.utils.Utils;

import java.util.ArrayList;

/**
 * Created by Pinkal Daliya on 19-Oct-16.
 */

public class WidgetListAdapter implements RemoteViewsService.RemoteViewsFactory {

    private ArrayList<WidgetModel> taskList;
    private Context context = null;
    private int appWidgetId;
    private Utils utils = new Utils();
    private DatabaseHelper mDatabaseHelper;

    public WidgetListAdapter(Context appContext, Intent intent) {

        this.context = appContext;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        Log.e("app wid id : ", String.valueOf(appWidgetId));
        mDatabaseHelper = new DatabaseHelper(context);
        updateWidgetListView();

    }

    private void updateWidgetListView() {
        taskList = new ArrayList<WidgetModel>();
        taskList.clear();

        mDatabaseHelper.openWritableDB();
        Cursor c1 = mDatabaseHelper.listAllTask();
        if (c1 != null && c1.getCount() != 0) {
            if (c1.moveToFirst()) {
                do {
                    WidgetModel widgetModel = new WidgetModel();
                    String f = c1.getString(c1.getColumnIndex(Constant.TASK_FLAG));
                    Log.e("Flag : ", f);
                    if (f.equals("0")) {

                        widgetModel.id = c1.getString(c1.getColumnIndex(Constant.TASK_ID));
                        widgetModel.title = c1.getString(c1.getColumnIndex(Constant.TASK_TITLE));
                        widgetModel.task = c1.getString(c1.getColumnIndex(Constant.TASK_TASK));
                        widgetModel.time = c1.getString(c1.getColumnIndex(Constant.TASK_TIME));

                        taskList.add(widgetModel);
                    }
                } while (c1.moveToNext());
            }
        }
        c1.close();
        mDatabaseHelper.closeDB();

    }

    @Override
    public void onCreate() {
        updateWidgetListView();
    }

    @Override
    public void onDataSetChanged() {
        updateWidgetListView();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return taskList.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        updateWidgetListView();
        RemoteViews row = new RemoteViews(context.getPackageName(), R.layout.widget_listview_task_row);

        WidgetModel widgetModel = taskList.get(position);

        String id = widgetModel.id;
        String title = widgetModel.title;
        String task = widgetModel.task;
        String taskTime = widgetModel.time;

        row.setTextViewText(R.id.txtWidgetTaskTitle, title);
        row.setTextViewText(R.id.txtWidgetTask, task);

        Log.e("id -*-*> ", id);
        Log.e("task -*-*> ", task);
        Log.e("title -*-*> ", title);

        String time = "";
        String timeFormat;
        if (!taskTime.equals(null)) {
            if (!taskTime.equals("")) {
                timeFormat = utils.getFormatTime(taskTime);
                time = timeFormat;
            }
        }
        row.setTextViewText(R.id.txtWidgetTaskTime, time);

        Intent i = new Intent();
        Bundle extras = new Bundle();

        extras.putString(Constant.ROW_ID, id);
        extras.putBoolean(Constant.WIDGET, true);
        i.putExtras(extras);
//        i.putExtras(extras);

        row.setOnClickFillInIntent(R.id.widgetRow, i);

        return row;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        if (taskList.size() == 0) {
            return 1;
        } else {
            return taskList.size();
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
