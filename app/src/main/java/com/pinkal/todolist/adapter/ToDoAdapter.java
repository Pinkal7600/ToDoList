package com.pinkal.todolist.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.pinkal.todolist.R;
import com.pinkal.todolist.activity.AddUpdateActivity;
import com.pinkal.todolist.utils.Constant;
import com.pinkal.todolist.utils.Utils;
import com.pinkal.todolist.database.DatabaseHelper;
import com.pinkal.todolist.model.TaskListItems;

import java.util.ArrayList;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by Pinkal Daliya on 06-Oct-16.
 */

public class ToDoAdapter extends BaseAdapter implements StickyListHeadersAdapter {
    private boolean isLongPressEnable = false;
    private Context context;
    private ArrayList<TaskListItems> taskList;
    private static LayoutInflater inflater = null;
    private DatabaseHelper mDatabaseHelper;
    CheckboxListner checkboxListner;
    int count = 0;
    Utils utils = new Utils();
    private SparseBooleanArray mSelectedItemsIds;

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;

        LayoutInflater inflater = LayoutInflater.from(context);

        if (convertView == null) {

            convertView = inflater.inflate(R.layout.listview_task_header, parent, false);
            holder = new HeaderViewHolder(convertView);
            convertView.setTag(holder);

        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }

        if (getHeaderId(position) != 0) {
            String headerText = utils.getDaysDate(taskList.get(position).getDate());
            holder.txtHeader.setText(headerText);
        } else {
            holder.txtHeader.setText("No dates");
        }

        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        String dat = "0";
        if (taskList.get(position).getDate().length() != 0) {
            dat = utils.getFormatDateHeaderId(taskList.get(position).getDate());
        }
        return Long.parseLong(dat);
    }

    private class HeaderViewHolder {
        private TextView txtHeader;

        public HeaderViewHolder(View v) {
            txtHeader = (TextView) v.findViewById(R.id.txtHeader);
        }
    }


    public interface CheckboxListner {
        void checkboxCount(boolean visible, int count);
    }

    public ToDoAdapter(Context context, ArrayList<TaskListItems> list, CheckboxListner listner) {

        this.context = context;
        taskList = list;
        mDatabaseHelper = new DatabaseHelper(context);
        checkboxListner = listner;
    }

    @Override
    public int getCount() {
        return taskList.size();
    }

    @Override
    public Object getItem(int position) {
        return taskList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
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
    public int getItemViewType(int position) {
        return position;
    }

    public static class ViewHolder {

        public CheckBox checkboxTask;
        public TextView txtSectionHeader, txtTask, txtTaskTime, txtTaskTitle, txtTaskCat, txtNoTask;

        public ViewHolder(View view) {
//            checkboxTask = (CheckBox) view.findViewById(R.id.checkboxTask);
            txtTaskTitle = (TextView) view.findViewById(R.id.txtTaskTitle);
            txtTask = (TextView) view.findViewById(R.id.txtTask);
            txtTaskTime = (TextView) view.findViewById(R.id.txtTaskTime);
            txtTaskCat = (TextView) view.findViewById(R.id.txtTaskCat);
//            txtDateTime = (TextView) view.findViewById(R.id.txtDateTime);
            txtSectionHeader = (TextView) view.findViewById(R.id.txtHeader);
            txtNoTask = (TextView) view.findViewById(R.id.txtNoTask);
        }

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View view = convertView;
        final ViewHolder holder;
        final TaskListItems taskListItems = taskList.get(position);

        if (convertView == null) {

            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.listview_task_row, null);

            holder = new ViewHolder(view);

//            holder.checkboxTask.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//
//
//                    taskListItems.setSelected(isChecked);
//
//                    if (isChecked) {
//                        count = count + 1;
//                    } else {
//                        count = count - 1;
//                    }
//                    if (count == 0)
//                        checkboxListner.checkboxCount(true, count);
//                    else {
//                        checkboxListner.checkboxCount(false, count);
//                    }
//                }
//            });

            view.setTag(holder);
            view.setTag(R.id.txtTaskTitle, holder.txtTaskTitle);
            view.setTag(R.id.txtTask, holder.txtTask);
//            view.setTag(R.id.txtDateTime, holder.txtDateTime);
            view.setTag(R.id.txtHeader, holder.txtSectionHeader);
            view.setTag(R.id.txtTaskTime, holder.txtTaskTime);
            view.setTag(R.id.txtTaskCat, holder.txtTaskCat);
//            view.setTag(R.id.checkboxTask, holder.checkboxTask);

        } else {
            holder = (ViewHolder) view.getTag();
        }


        //task title
        holder.txtTaskTitle.setText(taskListItems.getTaskTitle());
        //task
        holder.txtTask.setText(taskListItems.getTask());

        String time = "";
        String timeFormat;
        if (!taskListItems.getTime().equals(null)) {
            if (!taskListItems.getTime().equals("")) {
                timeFormat = utils.getFormatTime(taskListItems.getTime());
                time = timeFormat;
            }
        }
        holder.txtTaskTime.setText(time);

        final String cat = mDatabaseHelper.getCategoryName(taskListItems.getCategoryId());
        holder.txtTaskCat.setText(cat);


        //date
//        String date = "";
//        if (taskListItems.getDate().length() != 0) {
//            date = utils.getFormatDate(taskListItems.getDate());
//        }
//
//        //time
//        String time;
//        String timeFormat;
//        if (taskListItems.getTime().equals("") || taskListItems.getTime().equals(null)) {
//            time = "";
//        } else {
//            timeFormat = utils.getFormatTime(taskListItems.getTime());
//            time = ", " + timeFormat;
//        }

        //date & time
//        holder.txtDateTime.setText(date + time);


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (isLongPressEnable) {
//                    v.setBackgroundColor(R.color.colorPrimaryDark);
                    if (taskListItems.isSelected()) {
                        v.setSelected(false);
                        taskListItems.setSelected(false);
//                        holder.checkboxTask.setChecked(false);
                        count = count - 1;
//                        v.setBackgroundColor(R.color.white);
                    } else {
                        v.setSelected(true);
                        taskListItems.setSelected(true);
//                        holder.checkboxTask.setChecked(true);
                        count = count + 1;
                    }

                    if (count == 0) {
                        isLongPressEnable = false;
                        checkboxListner.checkboxCount(true, count);
                    } else {
                        checkboxListner.checkboxCount(false, count);
                    }

                } else {
                    String rowId = taskListItems.getId();

                    Intent intent = new Intent(context, AddUpdateActivity.class);
                    intent.putExtra(Constant.ROW_ID, rowId);
                    context.startActivity(intent);
                }
            }
        });

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                isLongPressEnable = true;
                taskListItems.setSelected(true);
                v.setSelected(true);
//                holder.checkboxTask.setChecked(true);
                count = count + 1;
                if (count == 0) {
                    isLongPressEnable = false;
                    checkboxListner.checkboxCount(true, count);
                } else {
                    checkboxListner.checkboxCount(false, count);
                }
//                v.setBackgroundColor(R.color.colorPrimaryDark);

                return true;
            }
        });

        return view;
    }

    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }

    public void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);
        notifyDataSetChanged();
    }

    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }

    //    @Override
    public void remove(String string) {
        taskList.remove(string);
        notifyDataSetChanged();
    }
}
