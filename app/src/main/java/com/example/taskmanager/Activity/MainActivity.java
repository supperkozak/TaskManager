package com.example.taskmanager.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import android.support.design.widget.Snackbar;

import com.example.taskmanager.Adapter.ListViewAdapterTask;
import com.example.taskmanager.Constant.Constant;
import com.example.taskmanager.R;
import com.example.taskmanager.Model.Task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button mButtonAdd;
    ListView mlvListTask;
    TextView mTextViewTimeTaskStart;
    TextView mTextViewTimeTaskFinish;

    ListViewAdapterTask mAdapter;
    ArrayList <Task> mListTask;

    int mListEditPosition = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null){
            mListTask = savedInstanceState.getParcelableArrayList(Constant.KEY_SAVE_STATE);
        } else {
            mListTask = new ArrayList<>();
        }
        initView();
        initData();
    }

    private void initData() {
        mAdapter = new ListViewAdapterTask(mListTask, this);
        mlvListTask.setAdapter(mAdapter);
    }


    private void initView() {
        mButtonAdd = (Button) findViewById(R.id.add_button);
        mlvListTask = (ListView) findViewById(R.id.listView);
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
                    Snackbar.make(v, getResources().getString(R.string.snack_start)+" "+mListTask.get(position).getTimeTaskStart(), Snackbar.LENGTH_LONG)
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
                        mListTask.get(position).setTimeForToDo(timeForToDo);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }


                    Snackbar.make(v, getResources().getString(R.string.snack_finish) + " "+mListTask.get(position).getTimeTaskStart() +
                            " "+mListTask.get(position).getTimeTaskFinish()+ " "+
                            mListTask.get(position).getTimeForToDo()/1000/60/60+":"+mListTask.get(position).getTimeForToDo()/1000/60, Snackbar.LENGTH_LONG).show();
                }else{
                    Snackbar.make(v, R.string.snack_resume, Snackbar.LENGTH_LONG)
                            .show();
                }

                mAdapter.notifyDataSetChanged();

            }

        });
        mlvListTask.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {

                Task mTask = (Task) mListTask.get(position);
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
        outState.putParcelableArrayList(Constant.KEY_SAVE_STATE, mListTask);
    }
}
