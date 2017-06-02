package com.pinkal.todolist.fragment;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.pinkal.todolist.R;
import com.pinkal.todolist.activity.HistoryActivity;
import com.pinkal.todolist.activity.MainActivity;
import com.pinkal.todolist.adapter.ToDoAdapter;
import com.pinkal.todolist.database.DatabaseHelper;
import com.pinkal.todolist.model.TaskListItems;
import com.pinkal.todolist.utils.Constant;
import com.pinkal.todolist.widget.CounterWidgetProvider;
import com.pinkal.todolist.widget.TaskWidgetProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * Created by Pinkal Daliya on 26-Oct-16.
 */
public class HistoryFragment extends Fragment implements ToDoAdapter.CheckboxListner {

    private View view;
    private TextView txtNoHistory;

    private StickyListHeadersListView taskListView;
    private ArrayList<TaskListItems> taskList;
    private DatabaseHelper mDatabaseHelper;
    private ToDoAdapter adapter;

    MenuItem menuItemSetting, menuItemFinish, menuItemDelete;
    Menu mMenu;
    private MainActivity mMainActivity;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        view = inflater.inflate(R.layout.fragment_history, container, false);
        mMainActivity = (MainActivity) getActivity();

        mDatabaseHelper = new DatabaseHelper(getContext());
        initialize();

        if (mDatabaseHelper.getTaskRowCount() == 0) {
            txtNoHistory.setVisibility(View.VISIBLE);
        } else {
            showList();
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        showList();
        if (mMenu != null) {
            checkboxCount(true, 0);
        }
        if (taskList.size() == 0) {
            txtNoHistory.setVisibility(View.VISIBLE);
            Log.e("history size 0 ***** ", " " + taskList.size());
        } else {
            txtNoHistory.setVisibility(View.GONE);
        }
        widgetUpdate();
        updateAllWidgets();
    }

    private void showList() {
        taskList = new ArrayList<TaskListItems>();
        taskList.clear();

        mDatabaseHelper.openWritableDB();
        Cursor c1 = mDatabaseHelper.listAllTask();
        if (c1 != null && c1.getCount() != 0) {
            if (c1.moveToFirst()) {
                do {
                    TaskListItems taskListItems = new TaskListItems();
                    String f = c1.getString(c1.getColumnIndex(Constant.TASK_FLAG));
                    Log.e("Flag : ", f);
                    if (f.equals("1")) {

                        taskListItems.setId(c1.getString(c1.getColumnIndex(Constant.TASK_ID)));
                        taskListItems.setTaskTitle(c1.getString(c1.getColumnIndex(Constant.TASK_TITLE)));
                        taskListItems.setTask(c1.getString(c1.getColumnIndex(Constant.TASK_TASK)));
                        taskListItems.setDate(c1.getString(c1.getColumnIndex(Constant.TASK_DATE)));
                        taskListItems.setTime(c1.getString(c1.getColumnIndex(Constant.TASK_TIME)));
                        taskListItems.setFlag(c1.getString(c1.getColumnIndex(Constant.TASK_FLAG)));
                        taskListItems.setCategoryId(c1.getString(c1.getColumnIndex(Constant.TASK_CATEGORY_ID)));
                        taskListItems.setSelected(false);


                        taskList.add(taskListItems);
                    } else {
//                        Toast.makeText(this, "done", Toast.LENGTH_SHORT).show();
                    }

                } while (c1.moveToNext());
            }
        }
        c1.close();
        mDatabaseHelper.closeDB();

        adapter = new ToDoAdapter(getContext(), taskList, this);

        Collections.sort(taskList, new Comparator<TaskListItems>() {
            @Override
            public int compare(TaskListItems lhs, TaskListItems rhs) {
                return lhs.getDate().compareTo(rhs.getDate());
            }
        });

        taskListView.setAdapter(adapter);

    }

    private void initialize() {
        taskListView = (StickyListHeadersListView) view.findViewById(R.id.historyTaskListView);
        txtNoHistory = (TextView) view.findViewById(R.id.txtNoHistory);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        mMenu = menu;
        menuItemSetting = menu.findItem(R.id.menuMainSetting);
        menuItemSetting.setVisible(false);
        menuItemFinish = menu.findItem(R.id.menuMainUnFinish);
        menuItemDelete = menu.findItem(R.id.menuMainDelete);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.menuMainSetting) {

            return true;

        } else if (id == R.id.menuMainDelete) {
            optionDelete();
            return true;

        } else if (id == R.id.menuMainUnFinish) {
            //finish code
            optionFinish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void optionDelete() {

        int countDelete = 0;

        String ids = "";
        for (int i = 0; i < taskList.size(); i++) {
            if (taskList.get(i).isSelected()) {
                ids = ids + "," + taskList.get(i).getId();
                countDelete = countDelete + 1;
            }
        }
        ids = ids.replaceFirst(",", "");

        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle(R.string.dialogTitleDelete);
        alert.setMessage(R.string.dialogMsgDelete);
        final String finalIds = ids;
        final int finalCountDelete = countDelete;
        alert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDatabaseHelper.deleteMultipleTask(finalIds);
                adapter.notifyDataSetChanged();
                Toast.makeText(getContext(), finalCountDelete + " Task deleted", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                onResume();
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.show();

    }

    private void optionFinish() {

        int countDelete = 0;

        String ids = "";
        for (int i = 0; i < taskList.size(); i++) {
            if (taskList.get(i).isSelected()) {
                ids = ids + "," + taskList.get(i).getId();
                countDelete = countDelete + 1;
            }
        }
        ids = ids.replaceFirst(",", "");

        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle(R.string.dialogTitleUnFinish);
        alert.setMessage(R.string.dialogMsgUnFinish);
        final String finalIds = ids;
        final int finalCountDelete = countDelete;
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                mDatabaseHelper.unFinishTask(finalIds);
                adapter.notifyDataSetChanged();
                Toast.makeText(getContext(), finalCountDelete + " Task mark as unfinish", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                onResume();
            }
        });
        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.show();

    }

    @Override
    public void checkboxCount(boolean visible, int count) {
//        if (menuItemSetting != null) {
//            menuItemSetting.setVisible(visible);
//        }
        if (visible == true) {
            mMainActivity.setToolbarTitle("History");
            menuItemDelete.setVisible(false);
            menuItemFinish.setVisible(false);
        } else {
            mMainActivity.setToolbarTitle(count + " Selected");
            menuItemDelete.setVisible(true);
            menuItemFinish.setVisible(true);
        }
    }

    private void widgetUpdate() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getContext());
        int appWidgetIds[] = appWidgetManager.getAppWidgetIds(new ComponentName(getActivity(), TaskWidgetProvider.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widgetListview);

        AppWidgetManager appWidgetManager1 = AppWidgetManager.getInstance(getContext());
        int appWidgetIdsCounter[] = appWidgetManager.getAppWidgetIds(new ComponentName(getActivity(), CounterWidgetProvider.class));
        RemoteViews widget = new RemoteViews(getActivity().getPackageName(), R.layout.widget_counter_layout);
        appWidgetManager1.updateAppWidget(appWidgetIdsCounter, widget);
//        appWidgetManager1.notifyAppWidgetViewDataChanged(appWidgetIdsCounter, R.id.txtWidgetCounter);
    }

    private void updateAllWidgets() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getContext().getApplicationContext());
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(getActivity(), CounterWidgetProvider.class));
        if (appWidgetIds.length > 0) {
            new CounterWidgetProvider().onUpdate(getContext(), appWidgetManager, appWidgetIds);
        }
    }
}
