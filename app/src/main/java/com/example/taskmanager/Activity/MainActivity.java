package com.example.taskmanager.activity;

import android.content.Intent;
import android.support.annotation.MainThread;
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
import com.example.taskmanager.utils.Loader;
import com.example.taskmanager.utils.SharedPreference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, LoadCompleter {
    Button mButtonAdd;
    ListView mlvListTask;
    TextView mTextViewTimeTaskStart;
    TextView mTextViewTimeTaskFinish;

    ListViewAdapterTask mAdapter;
    ArrayList <Task> mListTask;
    SharedPreference sharedPreference;

    int mListEditPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null){
            mListTask = savedInstanceState.getParcelableArrayList(Constant.KEY_SAVE_STATE);
        } else {
            //mListTask = new ArrayList<>();

            sharedPreference = new SharedPreference();

            mListTask = sharedPreference.getJson(this);

            Loader loader = new Loader(getApplicationContext(), this);
        }

        initView();
        initData();
    }

    private void initData() {
        mAdapter = new ListViewAdapterTask(mListTask, this);
        mlvListTask.setAdapter(mAdapter);

    }

    private void initView() {
        mlvListTask = (ListView) findViewById(R.id.listView);
        mButtonAdd = (Button) findViewById(R.id.add_button);
        mTextViewTimeTaskStart = (TextView) findViewById(R.id.tvTimeTaskStart);
        mTextViewTimeTaskFinish = (TextView) findViewById(R.id.tvTimeTaskFinish);
        mButtonAdd.setOnClickListener(this);

        mlvListTask.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                TextView tvmTimeTaskStart = (TextView) v.findViewById(R.id.tvTimeTaskStart);
                TextView tvmTimeTaskFinish = (TextView) v.findViewById(R.id.tvTimeTaskFinish);

                SimpleDateFormat sdf = new SimpleDateFormat("MM:dd:yyyy:HH:mm");
                String currentDateAndTime = sdf.format(new Date());

                if(mListTask.get(position).getTimeTaskStart() == null){
                    tvmTimeTaskStart.setText(currentDateAndTime.toString());
                    mListTask.get(position).setTimeTaskStart(tvmTimeTaskStart.getText().toString());
                    Snackbar.make(v, getResources().getString(R.string.snack_start) + " " + mListTask.get(position).getTimeTaskStart(), Snackbar.LENGTH_LONG)
                            .show();
                }else if(mListTask.get(position).getTimeTaskFinish() == null) {
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

                    Snackbar.make(v, R.string.snack_resume, Snackbar.LENGTH_LONG).setAction(R.string.snack_resume_yes, null)
                            .show();
                }

                mAdapter.notifyDataSetChanged();
                sharedPreference.saveTasksToSharedPreferences(MainActivity.this, mListTask);
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
            switch (requestCode) {
                case Constant.KEY_ADD_TASK:
                    Task mTask = data.getParcelableExtra(Task.class.getCanonicalName());
                    if (mListEditPosition == -1){
                        mListTask.add(mTask);

                    } else {
                        mListTask.set(mListEditPosition, mTask);
                        mListEditPosition = -1;
                    }

                    sharedPreference.saveTasksToSharedPreferences(this, mListTask);
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
            sharedPreference.saveTasksToSharedPreferences(this, mListTask);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_tasks:
                Task task = new Task();

                mListTask.add(task);
                mAdapter.notifyDataSetChanged();

                int height = calculateHeight(mlvListTask);
                int screen = mlvListTask.getHeight();
                mAdapter.notifyDataSetChanged();

                for (int i = 0; i < 3 * (screen / height) - 1; i++) {

                    task.setTaskName("" + i);
                    mListTask.add(task);
                    mAdapter.notifyDataSetChanged();
                }


                DisplayMetrics dm = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(dm);
                mListTask.add(task);
                mAdapter.notifyDataSetChanged();
                int heightDisplay = dm.heightPixels;

                int high = mlvListTask.getFirstVisiblePosition();
                int down = mlvListTask.getLastVisiblePosition();


                Snackbar.make(mlvListTask, heightDisplay + " " + high + " " + down, Snackbar.LENGTH_LONG).show();

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
    protected void onStop() {
        super.onStop();
        if (mListTask != null) {

            sharedPreference.saveTasksToSharedPreferences(MainActivity.this, mListTask);
        }
    }

    @Override
    public void loadCallback(ArrayList<Task> listTask) {
        if (listTask == null) {
            mListTask = new ArrayList<Task>();
        } else {
            mListTask = listTask;
        }
        //initData();
    }

    private int calculateHeight(ListView list) {

        int height = 0;

        for (int i = 0; i < list.getCount(); i++) {
            View childView = list.getAdapter().getView(i, null, list);
            childView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            height += childView.getMeasuredHeight();
        }

        height += list.getDividerHeight() * list.getCount();

        return height;
    }
}



