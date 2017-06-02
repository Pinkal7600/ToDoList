package com.pinkal.todolist.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pinkal.todolist.R;
import com.pinkal.todolist.adapter.ManageCategoryAdapter;
import com.pinkal.todolist.utils.Constant;
import com.pinkal.todolist.database.DatabaseHelper;
import com.pinkal.todolist.model.CategoryListItems;

import java.util.ArrayList;

/**
 * Created by Pinkal Daliya on 06-Oct-16.
 */
public class ManageCategoryActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private TextView toolbar_title;
    private TextInputEditText edtAddCategory;
    private Button btnAddCategory;
    private ListView categoryListView;
    private DatabaseHelper mDatabaseHelper;
    private ArrayList<CategoryListItems> categoryList;
    private Context context = this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_category);

        setToolbar();
        initialize();
        viewList();
    }

    private void viewList() {

        categoryList = new ArrayList<CategoryListItems>();
        categoryList.clear();

        mDatabaseHelper.openWritableDB();
        Cursor c1 = mDatabaseHelper.listAllCategory();
        if (c1 != null && c1.getCount() != 0) {
            if (c1.moveToFirst()) {
                do {
                    CategoryListItems contactListItems = new CategoryListItems();

                    contactListItems.setId(c1.getString(c1.getColumnIndex(Constant.CATEGORY_ID)));
                    contactListItems.setName(c1.getString(c1.getColumnIndex(Constant.CATEGORY_NAME)));

                    categoryList.add(contactListItems);

                } while (c1.moveToNext());
            }
        }
        c1.close();
        mDatabaseHelper.closeDB();

        ManageCategoryAdapter contactListAdapter = new ManageCategoryAdapter(this, categoryList) {

            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {

                View v = super.getView(position, convertView, parent);

                Button btnDelete = (Button) v.findViewById(R.id.btnCatDelete);
                btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        optionDelete(position);
                    }
                });

                Button btnEdit = (Button) v.findViewById(R.id.btnCatEdit);
                btnEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        optionEdit(position);
                    }
                });

                return v;
            }
        };
        categoryListView.setAdapter(contactListAdapter);


        categoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        viewList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewList();
    }

    private void initialize() {

        mDatabaseHelper = new DatabaseHelper(this);

        edtAddCategory = (TextInputEditText) findViewById(R.id.edtAddCategory);
        btnAddCategory = (Button) findViewById(R.id.btnAddCategory);
        btnAddCategory.setOnClickListener(this);
        categoryListView = (ListView) findViewById(R.id.categoryListView);

    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_manage_category);
        toolbar_title = (TextView) findViewById(R.id.toolbar_title_manage_category);
        toolbar_title.setText(R.string.toolbarTitleManageCategory);
        toolbar.setTitle("");

        setSupportActionBar(toolbar);
        Button btnback = (Button) findViewById(R.id.btnBackManageCategory);
        btnback.setOnClickListener(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBackManageCategory:
                finish();
                break;

            case R.id.btnAddCategory:
                addCategory();
                break;
        }
    }

    private void addCategory() {
        String catName = edtAddCategory.getText().toString().trim();

        if (catName.equals("")) {
            Toast.makeText(context, R.string.pleaseEnterCategory, Toast.LENGTH_SHORT).show();
        } else {
            mDatabaseHelper.insertIntoCategory(catName);
            Toast.makeText(this, R.string.categoryAdded, Toast.LENGTH_SHORT).show();
            edtAddCategory.setText("");
            onResume();
        }
    }

    private void optionEdit(int position) {

        final String rowid = categoryList.get(position).getId();
        String currentCatName = mDatabaseHelper.getCategoryName(rowid);

        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.alert_dialog_update_cat, null);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(promptsView);

        final EditText input = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);
        input.setText(currentCatName);
        int length = currentCatName.length();
        input.setSelection(length);

        alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                String value = input.getText().toString();
                mDatabaseHelper.updateIntoCategory(rowid, value);
                Toast.makeText(context, R.string.catUpdated, Toast.LENGTH_SHORT).show();
                onResume();
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });
        AlertDialog alertDialog = alert.create();
        alertDialog.show();
    }

    public void optionDelete(final int index) {

        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle(R.string.deleteDialgoTitleCategory);
        alert.setMessage(R.string.deleteCategoryMsg);

        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int button) {

                String rowid = categoryList.get(index).getId();
                Log.d("row id : ", "" + rowid);
                mDatabaseHelper.deleteIntoCategory(rowid);
                Toast.makeText(context, R.string.deleteCatSuccess, Toast.LENGTH_SHORT).show();
                onResume();
            }
        });

        alert.setNegativeButton("No", null).show();

    }
}
