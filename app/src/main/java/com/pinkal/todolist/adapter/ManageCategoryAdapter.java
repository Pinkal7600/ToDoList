package com.pinkal.todolist.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pinkal.todolist.R;
import com.pinkal.todolist.database.DatabaseHelper;
import com.pinkal.todolist.model.CategoryListItems;

import java.util.ArrayList;

/**
 * Created by Pinkal Daliya on 06-Oct-16.
 */

public class ManageCategoryAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<CategoryListItems> categoryList;
    private static LayoutInflater inflater = null;
    private DatabaseHelper mDatabaseHelper;

    public ManageCategoryAdapter(Context context, ArrayList<CategoryListItems> list) {

        this.context = context;
        categoryList = list;
        mDatabaseHelper = new DatabaseHelper(context);
    }

    @Override
    public int getCount() {
        return categoryList.size();
    }

    @Override
    public Object getItem(int position) {
        return categoryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder {

        public TextView txtName;

    }


    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        View vi = convertView;
        ViewHolder holder;
        CategoryListItems categoryListItems = categoryList.get(position);

        if (convertView == null) {

            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            vi = inflater.inflate(R.layout.listview_category_row, null);

            holder = new ViewHolder();
            holder.txtName = (TextView) vi.findViewById(R.id.txtCategoryName);

            vi.setTag(holder);

        } else {
            holder = (ViewHolder) vi.getTag();
        }

        holder.txtName.setText(categoryListItems.getName());


        return vi;
    }

}
