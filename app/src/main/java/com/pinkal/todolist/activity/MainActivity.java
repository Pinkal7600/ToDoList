package com.pinkal.todolist.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.pinkal.todolist.R;
import com.pinkal.todolist.fragment.HistoryFragment;
import com.pinkal.todolist.fragment.ManageCategoryFragment;
import com.pinkal.todolist.fragment.ToDoFragment;

/**
 * Created by Pinkal Daliya on 26-Oct-16.
 */

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private Toolbar toolbar;
    private TextView toolbar_title;
    private LinearLayout linearLayoutNavHeader;

    Fragment fragment = null;
    Class fragmentClass = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_main);
        setToolbar();
        setDrawer();

        fragmentClass = ToDoFragment.class;
        toolbar_title.setText(R.string.toDoList);
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.mainFrame, fragment).commit();
    }

    public void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_drawerMain);
        toolbar_title = (TextView) findViewById(R.id.toolbar_title_drawerMain);
        toolbar_title.setText(R.string.toDoList);
        toolbar.setTitle("");

        setSupportActionBar(toolbar);
    }

    public void setToolbarTitle(String title) {
        toolbar_title.setText(title);
    }

    private void setDrawer() {


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View hView = navigationView.getHeaderView(0);
        ImageView imageView = (ImageView) hView.findViewById(R.id.imageViewNavHeader);
        Glide.with(this).load(R.drawable.nav_bg).centerCrop().into(imageView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.menuMainSetting) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();


        switch (id) {
            case R.id.menuToDoList:
                fragmentClass = ToDoFragment.class;
                toolbar_title.setText(R.string.toDoList);
                break;
            case R.id.menuManageCategory:
                fragmentClass = ManageCategoryFragment.class;
                toolbar_title.setText(R.string.toolbarTitleManageCategory);
                break;
            case R.id.menuHistory:
                fragmentClass = HistoryFragment.class;
                toolbar_title.setText(R.string.toolHistory);
                break;
            case R.id.menuRateUs:
                Toast.makeText(this, "Rate Us", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menuShareApp:
                Toast.makeText(this, "Share App", Toast.LENGTH_SHORT).show();
                break;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.mainFrame, fragment).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
