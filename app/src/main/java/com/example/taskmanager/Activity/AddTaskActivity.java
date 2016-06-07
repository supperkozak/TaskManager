package com.example.taskmanager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.taskmanager.R;
import com.example.taskmanager.model.Task;

public class AddTaskActivity extends AppCompatActivity implements View.OnClickListener {
    EditText mEdTaskName;
    EditText mEdTaskComment;
    Button mBtnSave;
    Button mBtnExit;

    Task mTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        mTask = getIntent().getParcelableExtra(Task.class.getCanonicalName());
        if (mTask != null){
            editData();
        } else {
            initData();
        }
    }

    private void initData() {
        mEdTaskName = (EditText) findViewById(R.id.edTaskName);
        mEdTaskComment = (EditText) findViewById(R.id.edTaskComment);
        mBtnSave = (Button) findViewById(R.id.btnSave);
        mBtnExit = (Button) findViewById(R.id.btnExit);
        mBtnSave.setOnClickListener(this);
        mBtnExit.setOnClickListener(this);
    }

    private void editData() {
        mEdTaskName = (EditText) findViewById(R.id.edTaskName);
        mEdTaskName.setText(mTask.getTaskName());
        mEdTaskComment = (EditText) findViewById(R.id.edTaskComment);
        mEdTaskComment.setText(mTask.getTaskComment());
        mBtnSave = (Button) findViewById(R.id.btnSave);
        mBtnExit = (Button) findViewById(R.id.btnExit);
        mBtnSave.setOnClickListener(this);
        mBtnExit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSave:

                if (mTask != null){
                    mTask.setTaskName(mEdTaskName.getText().toString());
                    mTask.setTaskComment(mEdTaskComment.getText().toString());

                } else {
                    mTask = new Task(mEdTaskName.getText().toString(), mEdTaskComment.getText().toString());
                }

                Intent intent = new Intent();
                intent.putExtra(Task.class.getCanonicalName(),mTask);
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.btnExit:
                finish();
                break;
        }
    }
}
