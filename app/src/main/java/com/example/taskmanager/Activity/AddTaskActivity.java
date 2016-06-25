package com.example.taskmanager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.example.taskmanager.R;
import com.example.taskmanager.model.Task;

public class AddTaskActivity extends AppCompatActivity {
    EditText mEdTaskName;
    EditText mEdTaskComment;
    Toolbar mToolbar;

    Task mTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        mTask = getIntent().getParcelableExtra(Task.class.getCanonicalName());
        mToolbar = (Toolbar) findViewById(R.id.toolbar_add_task);
        setSupportActionBar(mToolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_back));
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               onBackPressed();
            }
        });

        if (mTask != null){
            editData();
        } else {
            initData();
        }
    }

    private void initData() {
        getSupportActionBar().setTitle(R.string.title_add_new_task);
        mEdTaskName = (EditText) findViewById(R.id.edTaskName);
        mEdTaskComment = (EditText) findViewById(R.id.edTaskComment);
    }

    private void editData() {

        getSupportActionBar().setTitle(R.string.title_edit_task);
        mEdTaskName = (EditText) findViewById(R.id.edTaskName);
        mEdTaskName.setText(mTask.getTaskName());
        mEdTaskComment = (EditText) findViewById(R.id.edTaskComment);
        mEdTaskComment.setText(mTask.getTaskComment());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_task, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_tasks_item:

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
            case R.id.cancel_tasks_item:
                finish();
                break;
        }
        return true;
    }


 }
