package com.pinkal.todolist.activity;

import android.app.NotificationManager;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.pinkal.todolist.R;
import com.pinkal.todolist.adapter.ToDoAdapter;
import com.pinkal.todolist.utils.Constant;
import com.pinkal.todolist.database.DatabaseHelper;
import com.pinkal.todolist.model.TaskListItems;
import com.pinkal.todolist.widget.CounterWidgetProvider;
import com.pinkal.todolist.widget.TaskWidgetProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class ToDoActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ToDoAdapter.CheckboxListner {

    private Toolbar toolbar;
    private TextView toolbar_title;
    private TextView txtNoTask;

    private StickyListHeadersListView taskListView;
    private ArrayList<TaskListItems> taskList;
    private DatabaseHelper mDatabaseHelper;
    private ToDoAdapter adapter;

    MenuItem menuItemSetting, menuItemFinish, menuItemDelete;
    Menu mMenu;

    private Bundle extras;
    private String rowID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatabaseHelper = new DatabaseHelper(this);

        extras = getIntent().getExtras();
        if (extras != null) {
            rowID = extras.getString(Constant.ROW_ID);
            mDatabaseHelper.finishTask(rowID);
            if (extras.getBoolean(Constant.NOTIFICATION)) {
                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.cancel(Integer.parseInt(rowID));
                Toast.makeText(this, "Task finish", Toast.LENGTH_SHORT).show();
                widgetUpdate();
                updateAllWidgets();
                finish();
            }
        }
        setContentView(R.layout.drawer_main);

        setToolbar();
        setFloatingBtn();
        setDrawer();
        initialize();

        if (mDatabaseHelper.getTaskRowCount() == 0) {
            txtNoTask.setVisibility(View.VISIBLE);
        } else {
            showList();
        }
//        if (taskList.size() == 0) {
//            txtNoTask.setVisibility(View.VISIBLE);
//            Log.e("size 0 ***** ", " " + taskList.size());
//        }else {
//            txtNoTask.setVisibility(View.GONE);
//            showList();
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (mDatabaseHelper.getTaskRowCount() == 0) {
//            txtNoTask.setVisibility(View.VISIBLE);
//        } else {
//            txtNoTask.setVisibility(View.GONE);
//            showList();
//        }
        showList();
        Log.e("size ***** ", " " + taskList.size());

        if (taskList.size() == 0) {
            txtNoTask.setVisibility(View.VISIBLE);
            Log.e("size 0 ***** ", " " + taskList.size());
        } else {
            txtNoTask.setVisibility(View.GONE);
        }

        if (mMenu != null) {
            checkboxCount(true, 0);
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
                    if (f.equals("0")) {

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

        adapter = new ToDoAdapter(this, taskList, this);

        Collections.sort(taskList, new Comparator<TaskListItems>() {
            @Override
            public int compare(TaskListItems lhs, TaskListItems rhs) {
                return lhs.getDate().compareTo(rhs.getDate());
            }
        });

        taskListView.setAdapter(adapter);
//        taskListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//
////                view.setSelected(true);
////                view.setBackgroundColor(R.color.colorPrimaryDark);
////                Toast.makeText(ToDoActivity.this, "Long" + position + " , " + id, Toast.LENGTH_SHORT).show();
//
//                return false;
//            }
//        });
    }

    private void initialize() {
        taskListView = (StickyListHeadersListView) findViewById(R.id.taskListView);
        txtNoTask = (TextView) findViewById(R.id.txtNoTask);
    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_drawerMain);
        toolbar_title = (TextView) findViewById(R.id.toolbar_title_drawerMain);
        toolbar_title.setText(R.string.toDoList);
        toolbar.setTitle("");

        setSupportActionBar(toolbar);
    }

    private void setFloatingBtn() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabAdd);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(ToDoActivity.this, AddUpdateActivity.class));
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });
    }

    private void setDrawer() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        widgetUpdate();
        updateAllWidgets();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mMenu = menu;
        menuItemSetting = menu.findItem(R.id.menuMainSetting);
        menuItemFinish = menu.findItem(R.id.menuMainFinish);
        menuItemDelete = menu.findItem(R.id.menuMainDelete);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.menuMainSetting) {

            return true;

        } else if (id == R.id.menuMainDelete) {
            optionDelete();
            return true;

        } else if (id == R.id.menuMainFinish) {
            //finish code
            optionFinish();
//            Toast.makeText(this, "Finish", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menuToDoList:
                Toast.makeText(this, "To Do List", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menuManageCategory:
                startActivity(new Intent(this, ManageCategoryActivity.class));
                break;
            case R.id.menuHistory:
                startActivity(new Intent(this, HistoryActivity.class));
                break;
            case R.id.menuRateUs:
                Toast.makeText(this, "Rate Us", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menuShareApp:
                Toast.makeText(this, "Share App", Toast.LENGTH_SHORT).show();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void checkboxCount(boolean visible, int count) {
        if (menuItemSetting != null) {
            menuItemSetting.setVisible(visible);
        }
        if (visible == true) {
            toolbar_title.setText(R.string.toDoList);
            menuItemDelete.setVisible(false);
            menuItemFinish.setVisible(false);
        } else {
            toolbar_title.setText(count + " Selected");
            menuItemDelete.setVisible(true);
            menuItemFinish.setVisible(true);
        }
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

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.dialogTitleDelete);
        alert.setMessage(R.string.dialogMsgDelete);
        final String finalIds = ids;
        final int finalCountDelete = countDelete;
        alert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDatabaseHelper.deleteMultipleTask(finalIds);
                adapter.notifyDataSetChanged();
                Toast.makeText(ToDoActivity.this, finalCountDelete + " Task deleted", Toast.LENGTH_SHORT).show();
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

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.dialogTitleFinish);
        alert.setMessage(R.string.dialogMsgFinish);
        final String finalIds = ids;
        final int finalCountDelete = countDelete;
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                mDatabaseHelper.finishTask(finalIds);
                adapter.notifyDataSetChanged();
                Toast.makeText(ToDoActivity.this, finalCountDelete + " Task Finish", Toast.LENGTH_SHORT).show();
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

    private void widgetUpdate() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int appWidgetIds[] = appWidgetManager.getAppWidgetIds(new ComponentName(this, TaskWidgetProvider.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widgetListview);

        AppWidgetManager appWidgetManager1 = AppWidgetManager.getInstance(this);
        int appWidgetIdsCounter[] = appWidgetManager.getAppWidgetIds(new ComponentName(this, CounterWidgetProvider.class));
        RemoteViews widget = new RemoteViews(this.getPackageName(), R.layout.widget_counter_layout);
        appWidgetManager1.updateAppWidget(appWidgetIdsCounter, widget);
//        appWidgetManager1.notifyAppWidgetViewDataChanged(appWidgetIdsCounter, R.id.txtWidgetCounter);
    }

    private void updateAllWidgets() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, CounterWidgetProvider.class));
        if (appWidgetIds.length > 0) {
            new CounterWidgetProvider().onUpdate(this, appWidgetManager, appWidgetIds);
        }
    }
}
