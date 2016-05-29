package com.example.taskmanager.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.taskmanager.R;
import com.example.taskmanager.Task.Task;


public class AddTaskActivity extends AppCompatActivity implements View.OnClickListener {
    EditText mEdTaskName;
    EditText mEdTaskComment;
    Button mBtnSave;
    Button mBtnExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        initData();
    }

    private void initData() {
        mEdTaskName = (EditText) findViewById(R.id.edTaskName);
        mEdTaskComment = (EditText) findViewById(R.id.edTaskComment);
        mBtnSave = (Button) findViewById(R.id.btnSave);
        mBtnExit = (Button) findViewById(R.id.btnExit);
        mBtnSave.setOnClickListener(this);
        mBtnExit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSave:
                Task mTask = new Task(mEdTaskName.getText().toString(), mEdTaskComment.getText().toString());

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
