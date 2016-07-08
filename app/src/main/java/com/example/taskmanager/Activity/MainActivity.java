package com.example.taskmanager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.example.taskmanager.R;

import com.example.taskmanager.adapter.RecyclerViewAdapter;
import com.example.taskmanager.adapter.util.DividerItemDecoration;
import com.example.taskmanager.constant.Constant;
import com.example.taskmanager.interfases.SetPositionLisener;
import com.example.taskmanager.interfases.YesNoListener;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.utils.MyAlertDialog;
import com.example.taskmanager.utils.RealmController;
import com.example.taskmanager.utils.SharedPreference;

import io.realm.RealmResults;
import jp.wasabeef.recyclerview.animators.FadeInLeftAnimator;

public class MainActivity extends AppCompatActivity implements YesNoListener, SetPositionLisener {
    Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private RealmController mRealm;

    private SharedPreference sharedPreference;
    ActionMode actionMode;
    MyAlertDialog alertDialod;

    int mListEditPosition = -1;
    private static long back_pressed;

    private Boolean getPopUmMenuVisible() {
        return mIsPopUmMenuVisible;
    }

    private void setPopUmMenuVisible(Boolean popUmMenuVisible) {
        mIsPopUmMenuVisible = popUmMenuVisible;
    }

    Boolean mIsPopUmMenuVisible = false;

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

        mRealm = new RealmController();
        sharedPreference = new SharedPreference();

        if (savedInstanceState != null) {
            mIsPopUmMenuVisible = savedInstanceState.getBoolean(Constant.KEY_SAVE_MENU_STATE, false);
        }
        initData();
    }

    private void initData() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.divider)));
        mRecyclerView.setItemAnimator(new FadeInLeftAnimator());

        updateAdapter();
    }

    public void updateAdapter() {

        mAdapter = new RecyclerViewAdapter(this, sortData(), this);
        mRecyclerView.setAdapter(mAdapter);
        notifyAdapter();
    }

    private RealmResults <Task> sortData() {
        int i = sharedPreference.getSortTypeFromPreferences(this, Constant.REALM_SORT);
        RealmResults <Task> sortData = mRealm.getTasks();
        switch (i) {
            case 0:
                sortData = mRealm.sortAscending(Constant.REALM_NAME);
                break;

            case 1:
                sortData = mRealm.sortDescending(Constant.REALM_NAME);
                break;

            case 2:
                sortData = mRealm.sortAscending(Constant.REALM_TIME_START);
                break;

            case 3:
                sortData = mRealm.sortDescending(Constant.REALM_TIME_START);
                break;
        }
        return sortData;
    }

    @Override
    public void setPosition(int position) {
                Task mTask = mRealm.getTasks().get(position);
                mListEditPosition = position;
                Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
                intent.putExtra(Task.class.getCanonicalName(), mTask);
                startActivityForResult(intent, Constant.KEY_ADD_TASK);
    }

    @Override
    public void notifyAdapter() {
        mAdapter.notifyDataSetChanged();
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
                        mRealm.copyToRealmOrUpdate(mTask);
                    } else {
                        mRealm.copyToRealmOrUpdate(mTask);
                        mListEditPosition = -1;
                    }
                    notifyAdapter();
                    break;
                case Constant.KEY_SETTINGS:
                    notifyAdapter();
                    break;
            }
        } else {
            Snackbar.make(mRecyclerView, "Wrong result", Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
            outState.putBoolean(Constant.KEY_SAVE_MENU_STATE, mIsPopUmMenuVisible);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if ( mIsPopUmMenuVisible) {
            MenuItem mView = (MenuItem) findViewById(R.id.sort_tasks);
            showPopupMenu((View)mView);
        }
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
                        sharedPreference.putSortTypeToPreferences(MainActivity.this, Constant.REALM_SORT, 0);
                        updateAdapter();
                        break;
                    case R.id.sort_task_by_name_za:
                        sharedPreference.putSortTypeToPreferences(MainActivity.this, Constant.REALM_SORT, 1);
                        updateAdapter();
                        break;
                    case R.id.sort_task_by_date_first_yang:
                        sharedPreference.putSortTypeToPreferences(MainActivity.this, Constant.REALM_SORT, 2);
                        updateAdapter();
                        break;
                    case R.id.sort_task_by_date_first_old:
                        sharedPreference.putSortTypeToPreferences(MainActivity.this, Constant.REALM_SORT, 3);
                        updateAdapter();
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

                if (mRealm.getTasks().isEmpty()){
                    addTask(0);
                    updateAdapter();
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

                notifyAdapter();
                Snackbar.make(mRecyclerView,getResources().getString(R.string.add_tasks_snack_1) + " " + itemsCount + " " +
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
                setPopUmMenuVisible(true);
                break;

            case R.id.add_task:
                startActivityForResult(new Intent(MainActivity.this, AddTaskActivity.class), Constant.KEY_ADD_TASK);
                break;

            case R.id.action_settings:
                startActivityForResult(new Intent(MainActivity.this, PreferencesActivity.class), Constant.KEY_SETTINGS);
                break;
        }
        return true;
    }

    public double calculateListViewElements() {

        if (mRecyclerView.getChildAt(0)!=null) {


            double x = mRecyclerView.getChildAt(0).getHeight();
            double y = mRecyclerView.getHeight();

            return y/x;

        }else {
            return 0;
        }
    }

    public void addTask(int i) {
        Task task = new Task();
        String[] firstWord = getResources().getStringArray(R.array.first_word);
        String[] secondWord = getResources().getStringArray(R.array.second_word);
        String[] thirdWord = getResources().getStringArray(R.array.third_word);
        final String taskName = i + ". " + firstWord[(int)(Math.random()*firstWord.length)] + " "
                + secondWord[(int)(Math.random()*secondWord.length)] + " "
                + thirdWord[(int)(Math.random()*thirdWord.length)];
        task.setTaskName(taskName);
        mRealm.copyToRealmOrUpdate(task);

    }

    @Override
    public void onYesAlertDialog() {
        mRealm.deleteAllFromRealm();
        notifyAdapter();
    }

    @Override
    public void onBackPressed() {

        if (back_pressed + 2000 > System.currentTimeMillis())
            super.onBackPressed();
        else
            Snackbar.make(mRecyclerView,R.string.snackbar_exit, Snackbar.LENGTH_LONG).show();
        back_pressed = System.currentTimeMillis();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }
}
