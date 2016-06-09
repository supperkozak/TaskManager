package com.example.taskmanager.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.support.design.widget.Snackbar;

import com.example.taskmanager.adapter.ListViewAdapterTask;
import com.example.taskmanager.constant.Constant;
import com.example.taskmanager.R;
import com.example.taskmanager.interfases.LoadCompleter;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.utils.LoaderSharedPreferences;
import com.example.taskmanager.utils.SharedPreference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, LoadCompleter {
    Button mButtonAdd;
    ListView mlvListTask;

    ListViewAdapterTask mAdapter;
    ArrayList <Task> mListTask;

    private SharedPreference sharedPreference;

    int mListEditPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreference = new SharedPreference();
        mListTask = new ArrayList<>();
        initData();

        LoaderSharedPreferences loader = null;
        if (savedInstanceState != null) {
            mListTask = savedInstanceState.getParcelableArrayList(Constant.KEY_SAVE_STATE);
        } else {
            // mListTask = sharedPreference.getTasksFromSharedPreferences(MainActivity.this);


            try {
                loader = new LoaderSharedPreferences(getApplicationContext(), this);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        onClickListView();
    }

    private void initData() {
        mButtonAdd = (Button) findViewById(R.id.add_button);
        mlvListTask = (ListView) findViewById(R.id.listView);
        mAdapter = new ListViewAdapterTask(mListTask, this);
        mlvListTask.setAdapter(mAdapter);
    }

    private void onClickListView (){
        mButtonAdd.setOnClickListener(this);
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
                            Long.valueOf(mListTask.get(position).getTimeForToDo())/1000/60, Snackbar.LENGTH_LONG).show();

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
    public void onClick(View v) {
        startActivityForResult(new Intent(this, AddTaskActivity.class), Constant.KEY_ADD_TASK);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_tasks:
                double items;
                int itemsCount;
                if (mListTask.isEmpty()){
                    addTask(0);
                    items = calculateListViewElements() * 3;
                    itemsCount = (int) Math.ceil(items);
                    for (int i = 0; i < itemsCount; i++) {
                        addTask(i + 1);
                    }
                } else {
                    items = calculateListViewElements() * 3;
                    itemsCount = (int) Math.ceil(items);
                    for (int i = 0; i < itemsCount; i++) {
                        addTask(i);
                    }
                }

                Snackbar.make(mlvListTask,getResources().getString(R.string.add_tasks_snack_1) + " " + itemsCount + " " +
                        getResources().getString(R.string.add_tasks_snack_2), Snackbar.LENGTH_LONG).show();
                break;

            case R.id.remove_tasks:

                sharedPreference.clearSharedPreference(this);
                mListTask = new ArrayList<>();
                initData();
                mAdapter.notifyDataSetChanged();
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
}



