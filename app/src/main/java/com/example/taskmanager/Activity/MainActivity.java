package com.example.taskmanager.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.ActionMode;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.example.taskmanager.R;

import com.example.taskmanager.adapter.RecyclerViewAdapter;
import com.example.taskmanager.adapter.util.DividerItemDecoration;
import com.example.taskmanager.constant.Constant;
import com.example.taskmanager.interfases.LoadCompleter;
import com.example.taskmanager.interfases.SetPositionLisener;
import com.example.taskmanager.interfases.YesNoListener;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.utils.LoaderSharedPreferences;
import com.example.taskmanager.utils.MyAlertDialog;
import com.example.taskmanager.utils.SharedPreference;
import com.example.taskmanager.utils.TaskNotification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import jp.wasabeef.recyclerview.animators.FadeInLeftAnimator;

public class MainActivity extends AppCompatActivity implements LoadCompleter, YesNoListener, SetPositionLisener {
    Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    ArrayList<Task> mListTask;

    private SharedPreference sharedPreference;
    ActionMode actionMode;
    MyAlertDialog alertDialod;

    int mListEditPosition = -1;
    private static long back_pressed;

    private Boolean getPopUmMenuVisible() {
        return mIsPopUmMenuVisible;
    }

    private void setPopUmMenuVisible(Boolean popUmMenuVisible) {
        mIsPopUmMenuVisible = popUmMenuVisible;
    }

    Boolean mIsPopUmMenuVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(getResources().getString(R.string.title_main));
        setSupportActionBar(mToolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MainActivity.this, AddTaskActivity.class), Constant.KEY_ADD_TASK);
            }
        });

        sharedPreference = new SharedPreference();
        mListTask = new ArrayList<>();

        LoaderSharedPreferences loader = null;
        if (savedInstanceState != null) {
            mListTask = savedInstanceState.getParcelableArrayList(Constant.KEY_SAVE_STATE);
            mIsPopUmMenuVisible = savedInstanceState.getBoolean(Constant.KEY_SAVE_MENU_STATE, false);

            initData();

        } else {
            // mListTask = sharedPreference.getTasksFromSharedPreferences(MainActivity.this);

            try {
                loader = new LoaderSharedPreferences(getApplicationContext(), this);
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void initData() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.divider)));
        mRecyclerView.setItemAnimator(new FadeInLeftAnimator());

        mAdapter = new RecyclerViewAdapter(this, mListTask, this);
        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    public void setPosition(int position) {
                Task mTask = mListTask.get(position);
                mListEditPosition = position;
                Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
                intent.putExtra(Task.class.getCanonicalName(), mTask);
                startActivityForResult(intent, Constant.KEY_ADD_TASK);

            }

    @Override
    public void notifyAdapter() {
        mAdapter.notifyDataSetChanged();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        if (resultCode == RESULT_OK) {
            sharedPreference = new SharedPreference();
            switch (requestCode) {
                case Constant.KEY_ADD_TASK:
                    Task mTask = data.getParcelableExtra(Task.class.getCanonicalName());
                    if (mListEditPosition == -1){
                        mListTask.add(mTask);

                    } else {

                        mListTask.set(mListEditPosition, mTask);
                        mListEditPosition = -1;
                    }
                    sharedPreference.saveTasksToSharedPreferencesGSON(MainActivity.this, mListTask);
                    mAdapter.notifyDataSetChanged();
                    break;
                case Constant.KEY_SETTINGS:

                    mAdapter.notifyDataSetChanged();
                    break;
            }
        } else {
            Snackbar.make(mRecyclerView, "Wrong result", Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mListTask != null){
            outState.putParcelableArrayList(Constant.KEY_SAVE_STATE, mListTask);
            outState.putBoolean(Constant.KEY_SAVE_MENU_STATE, mIsPopUmMenuVisible);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        sharedPreference.saveTasksToSharedPreferencesGSON(MainActivity.this, mListTask);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if ( mIsPopUmMenuVisible) {
            MenuItem mView = (MenuItem) findViewById(R.id.sort_tasks);
            showPopupMenu((View)mView);
        }
        return true;
    }

    private void showPopupMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.inflate(R.menu.menu_main_sort);
        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.sort_task_by_name_az:
                        Collections.sort(mListTask, new Comparator<Task>() {

                            @Override
                            public int compare(Task task1, Task task2) {
                                return task1.getTaskName().compareTo(task2.getTaskName());
                            }
                        });
                        mAdapter.notifyDataSetChanged();
                        break;
                    case R.id.sort_task_by_name_za:
                        Collections.sort(mListTask, new Comparator<Task>() {

                            @Override
                            public int compare(Task task2, Task task1) {
                                return task1.getTaskName().compareTo(task2.getTaskName());
                            }
                        });
                        mAdapter.notifyDataSetChanged();
                        break;
                    case R.id.sort_task_by_date_first_yang:
                        Collections.sort(mListTask, new Comparator<Task>() {

                            @Override
                            public int compare(Task task1, Task task2) {
                                return task1.getTimeTaskStart().compareTo(task2.getTimeTaskStart());
                            }
                        });
                        mAdapter.notifyDataSetChanged();
                        break;
                    case R.id.sort_task_by_date_first_old:
                        Collections.sort(mListTask, new Comparator<Task>() {
                            @Override
                            public int compare(Task task2, Task task1) {
                                return task1.getTimeTaskStart().compareTo(task2.getTimeTaskStart());
                            }
                        });
                        mAdapter.notifyDataSetChanged();
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_tasks:
                double items;
                int itemsCount;
                if (mListTask.isEmpty()){
                    addTask(0);
                    items = calculateListViewElements() * 3;
                    itemsCount = (int) Math.ceil(items);
                    for (int i = 1; i < itemsCount; i++) {
                        addTask(i);
                    }
                } else {
                    items = calculateListViewElements() * 3;
                    itemsCount = (int) Math.ceil(items);
                    for (int i = 0; i < itemsCount; i++) {
                        addTask(i);
                    }
                }

                mAdapter.notifyDataSetChanged();
                Snackbar.make(mRecyclerView,getResources().getString(R.string.add_tasks_snack_1) + " " + itemsCount + " " +
                        getResources().getString(R.string.add_tasks_snack_2), Snackbar.LENGTH_LONG).show();
                break;

            case R.id.remove_tasks:

                alertDialod = new MyAlertDialog();
                alertDialod.show(getFragmentManager(), "MyAlertDialog");

                break;

            case R.id.main_exit:
                finish();
                break;

            case R.id.sort_tasks:
                showPopupMenu(findViewById(R.id.sort_tasks));
                setPopUmMenuVisible(true);

                break;

            case R.id.add_task:
                startActivityForResult(new Intent(MainActivity.this, AddTaskActivity.class), Constant.KEY_ADD_TASK);
                break;

            case R.id.action_settings:
                startActivityForResult(new Intent(MainActivity.this, PreferencesActivity.class), Constant.KEY_SETTINGS);
                break;

        }
        return true;
    }

    @Override
    public void loadCallback(ArrayList<Task> listTask) {
        if (listTask == null) {
            mListTask = new ArrayList<>();
        } else {
            mListTask = listTask;
        }
        initData();
    }
    public double calculateListViewElements(){
        RecyclerViewAdapter recyclerAdapter = (RecyclerViewAdapter) mRecyclerView.getAdapter();
        int dfs = 0;

        if (!recyclerAdapter.isOpen(0)) {
            View viewItem = null;

            viewItem.measure(0, 0);
            double itemHighDp = viewItem.getMeasuredHeight();
            double listViewHighPx = mRecyclerView.getHeight();
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            double dpiScreen = dm.densityDpi;
            double itemHighPx = itemHighDp * (dpiScreen / 160.0);

            return listViewHighPx /(itemHighPx + 1);

        }else {
            return 0;
        }
    }

    public void addTask(int i) {
        Task task = new Task();
        task.setTaskName("" + i);
        mListTask.add(task);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onYesAlertDialog() {
        sharedPreference.clearSharedPreference(this);
        mListTask = new ArrayList<>();
        initData();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {

        if (back_pressed + 2000 > System.currentTimeMillis())
            super.onBackPressed();
        else
            Snackbar.make(mRecyclerView,R.string.snackbar_exit, Snackbar.LENGTH_LONG).show();

        back_pressed = System.currentTimeMillis();
    }
}
