package com.pinkal.todolist.activity;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.pinkal.todolist.R;
import com.pinkal.todolist.adapter.ToDoAdapter;
import com.pinkal.todolist.database.DatabaseHelper;
import com.pinkal.todolist.model.TaskListItems;
import com.pinkal.todolist.utils.Constant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * Created by Pinkal Daliya on 20-Oct-16.
 */

public class HistoryActivity extends AppCompatActivity implements View.OnClickListener, ToDoAdapter.CheckboxListner {

    private Toolbar toolbar;
    private TextView toolbar_title;
    private TextView txtNoHistory;

    private StickyListHeadersListView taskListView;
    private ArrayList<TaskListItems> taskList;
    private DatabaseHelper mDatabaseHelper;
    private ToDoAdapter adapter;

    MenuItem menuItemFinish, menuItemDelete;
    Menu mMenu;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        mDatabaseHelper = new DatabaseHelper(this);
        setToolbar();
        initialize();

        if (mDatabaseHelper.getTaskRowCount() == 0) {
            txtNoHistory.setVisibility(View.VISIBLE);
        } else {
            showList();
        }
    }

    @Override
    protected void onResume() {
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

        adapter = new ToDoAdapter(this, taskList, this) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
//                view.setOnClickListener(null);
                return view;
            }
        };

        Collections.sort(taskList, new Comparator<TaskListItems>() {
            @Override
            public int compare(TaskListItems lhs, TaskListItems rhs) {
                return lhs.getDate().compareTo(rhs.getDate());
            }
        });

        taskListView.setAdapter(adapter);

    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_history);
        toolbar_title = (TextView) findViewById(R.id.toolbar_title_history);
        toolbar_title.setText(R.string.toolHistory);
        toolbar.setTitle("");

        setSupportActionBar(toolbar);
        Button btnback = (Button) findViewById(R.id.btnBackHistory);
        btnback.setOnClickListener(this);
    }

    private void initialize() {
        taskListView = (StickyListHeadersListView) findViewById(R.id.historyTaskListView);
        txtNoHistory = (TextView) findViewById(R.id.txtNoHistory);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBackHistory:
                finish();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_history, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mMenu = menu;
//        menuItemFinish = menu.findItem(R.id.menuHistoryUnFinish);
//        menuItemDelete = menu.findItem(R.id.menuHistoryDelete);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

//        if (id == R.id.menuHistoryDelete) {
//            optionDelete();
//            return true;
//
//        } else if (id == R.id.menuHistoryUnFinish) {
//            //finish code
//            optionFinish();
//            return true;
//        }

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
                Toast.makeText(HistoryActivity.this, finalCountDelete + " Task deleted", Toast.LENGTH_SHORT).show();
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
        alert.setTitle(R.string.dialogTitleUnFinish);
        alert.setMessage(R.string.dialogMsgUnFinish);
        final String finalIds = ids;
        final int finalCountDelete = countDelete;
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                mDatabaseHelper.unFinishTask(finalIds);
                adapter.notifyDataSetChanged();
                Toast.makeText(HistoryActivity.this, finalCountDelete + " Task mark as unfinish", Toast.LENGTH_SHORT).show();
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

        if (visible == true) {
            toolbar_title.setText(R.string.toolHistory);
            menuItemDelete.setVisible(false);
            menuItemFinish.setVisible(false);
        } else {
            toolbar_title.setText(count + " Selected");
            menuItemDelete.setVisible(true);
            menuItemFinish.setVisible(true);
        }
    }
}
