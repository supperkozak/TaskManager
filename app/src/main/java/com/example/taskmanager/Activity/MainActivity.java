package com.example.taskmanager.Activity;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.taskmanager.Adapter.ListViewAdapterTask;
import com.example.taskmanager.Constant.Constant;
import com.example.taskmanager.R;
import com.example.taskmanager.Task.Task;

import java.util.ArrayList;
import java.util.List;

import static com.example.taskmanager.R.id.listView;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button mButtonAdd;
    ListView mlvListTask;
    ListViewAdapterTask adapter;
    ArrayList listTask;
    private static final String KEY_SAVE_STATE = "state";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null){
            listTask = savedInstanceState.getParcelableArrayList(KEY_SAVE_STATE);
        } else {
            listTask = new ArrayList<Task>();
        }

        initView();
        initData();

    }

    private void initData() {
        // listTask = new ArrayList<Task>();
        adapter = new ListViewAdapterTask(listTask, this);
        mlvListTask.setAdapter(adapter);
    }


    private void initView() {
        mButtonAdd = (Button) findViewById(R.id.button);
        mlvListTask = (ListView) findViewById(listView);
        mButtonAdd.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        startActivityForResult(new Intent(this, AddTaskActivity.class), Constant.REQUEST_STRING);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constant.REQUEST_STRING:
                    Task mTask = data.getParcelableExtra(Task.class.getCanonicalName());
                    listTask.add(mTask);
                    adapter.notifyDataSetChanged();
                    break;
            }

        } else {
            Toast.makeText(this, "Wrong result", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(KEY_SAVE_STATE, listTask);
    }
}
