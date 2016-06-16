package com.example.taskmanager.activity;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceFragment;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.afollestad.materialdialogs.color.CircleView;
import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.afollestad.materialdialogs.internal.ThemeSingleton;
import com.afollestad.materialdialogs.util.DialogUtils;
import com.example.taskmanager.R;

public class PreferencesActivity extends AppCompatActivity implements ColorChooserDialog.ColorCallback{

    ActionBar mToolbar;

    private int notStart;
    private int onStart;
    private int onFinish;

    private int accentPreselect;
    private Handler mHandler;


        public static class SettingsFragment extends PreferenceFragment {
            @Override
            public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                addPreferencesFromResource(R.xml.preferences);
            }
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.preference_activity_custom);
          //  mToolbar = (Toolbar) findViewById(R.id.toolbar_settings);
            //mToolbar.setTitle(R.string.title_settings);
            mToolbar = getSupportActionBar();
            mToolbar.setDisplayHomeAsUpEnabled(true);
            mToolbar.setTitle(R.string.title_settings);
            mToolbar.setDisplayShowTitleEnabled(true);
            mToolbar.setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_action_back));


            if (getFragmentManager().findFragmentById(R.id.content_frame) == null) {
                getFragmentManager().beginTransaction().replace(R.id.content_frame, new SettingsFragment()).commit();
            }
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            if (item.getItemId() == android.R.id.home) {
                onBackPressed();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }


  /*  Toolbar mToolbar;

    Button mButton1;
    Button mButton2;
    Button mButton3;
    Button mButton4;

    private int notStart;
    private int onStart;
    private int onFinish;

    private int accentPreselect;
    private Handler mHandler;

    private int chooserDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);

        initData();

        mHandler = new Handler();
        notStart = getResources().getColor(R.color.notStart);
        onStart = getResources().getColor(R.color.onStart);
        onFinish = getResources().getColor(R.color.onFinish);
        accentPreselect = DialogUtils.resolveColor(this, R.attr.colorAccent);

        mButton1 = (Button)findViewById(R.id.settings_button_1);
        mButton1.setBackgroundColor(notStart);
        mButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showColorChooser(notStart);
            }
        });

        mButton2 = (Button)findViewById(R.id.settings_button_2);
        mButton2.setBackgroundColor(onStart);
        mButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showColorChooser(onStart);
            }
        });

        mButton3 = (Button)findViewById(R.id.settings_button_3);
        mButton3.setBackgroundColor(onFinish);
        mButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showColorChooser(onFinish);
            }
        });

        mButton4 = (Button)findViewById(R.id.settings_button_4);
        mButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void initData() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar_settings);
        mToolbar.setTitle(R.string.title_settings);
       // setSupportActionBar(mToolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_back));
    }

    public void showColorChooser(int color) {
     *//*   new ColorChooserDialog.Builder(PreferencesActivity.this, R.string.color_palette)
                .titleSub(R.string.colors)
                .accentMode(true)
                .preselect(color)
                .show();*//*
    }*/

    @Override
    public void onColorSelection(@NonNull ColorChooserDialog dialog, @ColorInt int color) {
        if (dialog.isAccentMode()) {
            accentPreselect = color;
            ThemeSingleton.get().positiveColor = DialogUtils.getActionTextStateList(this, color);
            ThemeSingleton.get().neutralColor = DialogUtils.getActionTextStateList(this, color);
            ThemeSingleton.get().negativeColor = DialogUtils.getActionTextStateList(this, color);
            ThemeSingleton.get().widgetColor = color;
        } else {
            notStart = color;
           // if (getSupportActionBar() != null)
             //   getSupportActionBar().setBackgroundDrawable(new ColorDrawable(color));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(CircleView.shiftColorDown(color));
                getWindow().setNavigationBarColor(color);
            }
        }
    }
}

