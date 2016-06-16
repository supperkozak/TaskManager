package com.example.taskmanager.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.ActionMode;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.taskmanager.R;
import com.example.taskmanager.adapter.ListViewAdapterTask;
import com.example.taskmanager.constant.Constant;
import com.example.taskmanager.interfases.LoadCompleter;
import com.example.taskmanager.interfases.YesNoListener;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.utils.LoaderSharedPreferences;
import com.example.taskmanager.utils.MyAlertDialog;
import com.example.taskmanager.utils.SharedPreference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements LoadCompleter, YesNoListener {
    Button mButtonAdd;
    ListView mlvListTask;
    Toolbar mToolbar;

    ListViewAdapterTask mAdapter;
    ArrayList<Task> mListTask;

    private SharedPreference sharedPreference;
    ActionMode actionMode;
    MyAlertDialog alertDialod;

    int mListEditPosition = -1;

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
        onClickListView();
    }

    private void initData() {
        mlvListTask = (ListView) findViewById(R.id.listView);
        mAdapter = new ListViewAdapterTask(mListTask, this);
        mlvListTask.setAdapter(mAdapter);
    }

    private void onClickListView (){
            mlvListTask.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                TextView tvmTimeTaskStart = (TextView) v.findViewById(R.id.tvTimeTaskStart);
                TextView tvmTimeTaskFinish = (TextView) v.findViewById(R.id.tvTimeTaskFinish);

                SimpleDateFormat sdf = new SimpleDateFormat("MM:dd:yyyy:HH:mm");
                String currentDateAndTime = sdf.format(new Date());

                if(mListTask.get(position).getTimeTaskStart() == null || mListTask.get(position).getTimeTaskStart().equalsIgnoreCase("")){
                    tvmTimeTaskStart.setText(currentDateAndTime.toString());
                    mListTask.get(position).setTimeTaskStart(tvmTimeTaskStart.getText().toString());
                    Snackbar.make(v, getResources().getString(R.string.snack_start) + " " + mListTask.get(position).getTimeTaskStart(), Snackbar.LENGTH_LONG)
                            .show();
                }else if(mListTask.get(position).getTimeTaskFinish() == null||mListTask.get(position).getTimeTaskFinish().equalsIgnoreCase("")) {
                    tvmTimeTaskFinish.setText(currentDateAndTime.toString());
                    mListTask.get(position).setTimeTaskFinish(tvmTimeTaskFinish.getText().toString());

                    String dateStringStart = mListTask.get(position).getTimeTaskStart();
                    String dateStringFinish = mListTask.get(position).getTimeTaskFinish();

                    try {
                        long timeStartTask = sdf.parse(dateStringStart).getTime();
                        long timeFinishTask = sdf.parse(dateStringFinish).getTime();
                        long timeForToDo = timeFinishTask - timeStartTask;
                        mListTask.get(position).setTimeForToDo(Long.toString(timeForToDo));

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    Snackbar.make(v, getResources().getString(R.string.snack_finish) + " " + mListTask.get(position).getTimeTaskStart() +
                            " " + mListTask.get(position).getTimeTaskFinish() + " " +
                            Long.valueOf(mListTask.get(position).getTimeForToDo())/1000/60/60 + ":" +
                            Long.valueOf(mListTask.get(position).getTimeForToDo())/1000/60%60, Snackbar.LENGTH_LONG).show();

                }else{

                    Snackbar.make(v, R.string.snack_resume, Snackbar.LENGTH_LONG)
                            .show();
                }
                sharedPreference.saveTasksToSharedPreferences(MainActivity.this, mListTask);
                mAdapter.notifyDataSetChanged();
            }
        });
        mlvListTask.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {

                Task mTask = mListTask.get(position);
                mListEditPosition = position;
                Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
                intent.putExtra(Task.class.getCanonicalName(), mTask);
                startActivityForResult(intent, Constant.KEY_ADD_TASK);
                return false;
            }
        });
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
                    sharedPreference.saveTasksToSharedPreferences(MainActivity.this, mListTask);
                    mAdapter.notifyDataSetChanged();
                    break;
            }
        } else {
            Snackbar.make(mlvListTask, "Wrong result", Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mListTask != null){
            outState.putParcelableArrayList(Constant.KEY_SAVE_STATE, mListTask);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        sharedPreference.saveTasksToSharedPreferences(MainActivity.this, mListTask);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
                Snackbar.make(mlvListTask,getResources().getString(R.string.add_tasks_snack_1) + " " + itemsCount + " " +
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
                break;

            case R.id.add_task:
                startActivityForResult(new Intent(MainActivity.this, AddTaskActivity.class), Constant.KEY_ADD_TASK);
                break;

            case R.id.action_settings:
                startActivity(new Intent(getApplicationContext(), PreferencesActivity.class));
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
        ListViewAdapterTask listAdapter = (ListViewAdapterTask) mlvListTask.getAdapter();

        if (!listAdapter.isEmpty()) {
            View viewItem = listAdapter.getView(0, null, mlvListTask);
            viewItem.measure(0, 0);
            double itemHighDp = viewItem.getMeasuredHeight();
            double listViewHighPx = mlvListTask.getHeight();
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            double dpiScreen = dm.densityDpi;
            double itemHighPx = itemHighDp * (dpiScreen / 160.0);

            return listViewHighPx /(itemHighPx + mlvListTask.getDividerHeight());

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
    public void onNoAlertDialog() {

    }

    private static long back_pressed;

    @Override
    public void onBackPressed() {

        if (back_pressed + 2000 > System.currentTimeMillis())
            super.onBackPressed();
        else
            Snackbar.make(mlvListTask,R.string.snackbar_exit, Snackbar.LENGTH_LONG).show();

        back_pressed = System.currentTimeMillis();
    }
}
