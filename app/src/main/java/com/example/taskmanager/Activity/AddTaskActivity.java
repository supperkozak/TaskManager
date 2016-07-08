package com.example.taskmanager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.taskmanager.R;
import com.example.taskmanager.constant.Constant;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.utils.MyAlertDialog;
import com.example.taskmanager.utils.SpeechRecognitionHelper;

import java.util.ArrayList;

public class AddTaskActivity extends AppCompatActivity {
    EditText mEdTaskName;
    EditText mEdTaskComment;
    Toolbar mToolbar;
    ImageButton mEtitTaskNameVoice;
    ImageButton mEtitTaskCommentVoice;
    TextInputLayout mInputLayoutName;
    TextInputLayout mInputLayoutComment;

    Task mTask;

    public MyAlertDialog mAlertDialog = new MyAlertDialog();
    public SpeechRecognitionHelper speechHelper = new SpeechRecognitionHelper();

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

        initVoice();

        if (mTask != null){
            editData();
        } else {
            initData();
        }
        mInputLayoutName = (TextInputLayout) findViewById(R.id.input_layout_task_name);
        mInputLayoutComment = (TextInputLayout) findViewById(R.id.input_layout_task_comment);

        mEdTaskName.addTextChangedListener(new MyTextWatcher(mEdTaskName));
        mEdTaskComment.addTextChangedListener(new MyTextWatcher(mEdTaskComment));
    }

    private void initVoice() {
        mEtitTaskNameVoice = (ImageButton)findViewById(R.id.button_voice_name);
        mEtitTaskCommentVoice = (ImageButton) findViewById(R.id.button_voice_comment);
        mEtitTaskNameVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startVoice(Constant.KEY_VOICE_NAME);
            }
        });
        mEtitTaskCommentVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startVoice(Constant.KEY_VOICE_COMMENT);
            }
        });
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
                if(!submitForm())
                    break;

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

    public void startVoice(int extra){
        speechHelper.run(this, extra);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            ArrayList <String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String sb = matches.get(0);

            if (matches.size() > 0) {
                if (requestCode == Constant.KEY_VOICE_NAME) {
                    mEdTaskName.setText(sb);
                } else if (requestCode == Constant.KEY_VOICE_COMMENT) {
                    mEdTaskComment.setText(sb);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Validating form
     */
    private boolean submitForm() {
        if (!validateName()) {
            return false;
        }

        if (!validateComment()) {
            return false;
        }
        return true;

    }

    private boolean validateName() {
        if (mEdTaskName.getText().toString().length()<5) {
            mInputLayoutName.setError(getString(R.string.err_msg_name));
            requestFocus(mEdTaskName);
            return false;
        } else {
            mInputLayoutName.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateComment() {
        if (mEdTaskComment.getText().toString().length()<10) {
            mInputLayoutComment.setError(getString(R.string.err_msg_comment));
            requestFocus(mEdTaskComment);
            return false;
        } else {
            mInputLayoutComment.setErrorEnabled(false);
        }

        return true;
    }


    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.input_name:
                    validateName();
                    break;
                case R.id.input_comment:
                    validateComment();
                    break;
            }
        }
    }
}

