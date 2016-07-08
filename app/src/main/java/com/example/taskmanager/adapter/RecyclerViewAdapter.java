package com.example.taskmanager.adapter;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;

import com.example.taskmanager.R;
import com.example.taskmanager.constant.Constant;
import com.example.taskmanager.interfases.SetPositionLisener;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.utils.RealmController;
import com.example.taskmanager.utils.SharedPreference;
import com.example.taskmanager.utils.TaskNotification;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.OrderedRealmCollection;
import io.realm.RealmResults;

public class RecyclerViewAdapter extends RecyclerSwipeAdapter<RecyclerViewAdapter.SimpleViewHolder>{

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        SwipeLayout mSwipeLayout;
        public TextView mTextViewTaskName;
        public TextView mTextViewTaskComment;
        public TextView mTextViewTaskStart;
        public TextView mTextViewTaskFinish;
        public View mView;

        Button mButtonDelete;
        ImageButton mButtonEdit;
        ImageButton mButtonReset;
        Button mButtonStart;
        Button mButtonFinish;

        Task mTask;

        public SimpleViewHolder(View itemView) {
            super(itemView);
            mSwipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            mButtonDelete = (Button) itemView.findViewById(R.id.button_delete_task);
            mButtonEdit = (ImageButton) itemView.findViewById(R.id.button_edit_task);
            mButtonReset = (ImageButton) itemView.findViewById(R.id.button_reset_task);
            mButtonStart = (Button) itemView.findViewById(R.id.button_start_task);
            mButtonFinish = (Button) itemView.findViewById(R.id.button_finish_task);
            mButtonFinish.setVisibility(View.INVISIBLE);

            mTextViewTaskName = (TextView) itemView.findViewById(R.id.tv_name_task);
            mTextViewTaskComment = (TextView) itemView.findViewById(R.id.tv_comment_task);
            mTextViewTaskStart = (TextView) itemView.findViewById(R.id.tvTimeTaskStart);
            mTextViewTaskFinish = (TextView) itemView.findViewById(R.id.tvTimeTaskFinish);
            mView =  itemView.findViewById(R.id.item_container);
        }
    }

    private Context mContext;
    private SetPositionLisener mPositionLisener;
    RealmController mRealmController = new RealmController();
    OrderedRealmCollection<Task> mRealmAdapter;
    private SharedPreference mSharedPreference = new SharedPreference();
    AlarmManager mAlarmManager;

    public RecyclerViewAdapter(Context context, RealmResults <Task> realmAdapter, SetPositionLisener positionLisener) {
        this.mContext = context;
        this.mPositionLisener = positionLisener;
        this.mRealmAdapter = realmAdapter;
        //mRealmAdapter.addChangeListener(this);
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item_start, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder viewHolder, final int position) {
        viewHolder.mTask = mRealmAdapter.get(position);

        viewHolder.mSwipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
        viewHolder.mSwipeLayout.addDrag(SwipeLayout.DragEdge.Left, viewHolder.mSwipeLayout.findViewById(R.id.swipe_edit));
        viewHolder.mSwipeLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onOpen(SwipeLayout layout) {
                YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.trash));
                YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.button_reset_task));
                YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.button_edit_task));
            }
        });

        final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        final String currentDateAndTime = sdf.format(new Date());

        viewHolder.mButtonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (viewHolder.mTask.getTimeTaskStart().isEmpty()) {
                    viewHolder.mButtonStart.setVisibility(View.INVISIBLE);
                    viewHolder.mButtonFinish.setVisibility(View.VISIBLE);
                    viewHolder.mTextViewTaskStart.setText(currentDateAndTime.toString());
                    mRealmController.beginTransaction();
                    viewHolder.mTask.setTimeTaskStart(viewHolder.mTextViewTaskStart.getText().toString());
                    viewHolder.mTask.setTimeTaskNotifikation(new Date().getTime() + mSharedPreference.getTimeAutoStopFromPreferences(mContext, Constant.AUTO_STOP));
                    mRealmController.commitTransaction();

                    Snackbar.make(view, mContext.getResources().getString(R.string.snack_start) + " " + viewHolder.mTask.getTimeTaskStart(), Snackbar.LENGTH_LONG)
                            .show();
                    notifyItemChanged(position);
                    mItemManger.updateConvertView(view,position);
                }
            }
        });

        viewHolder.mButtonFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (viewHolder.mTask.getTimeTaskFinish().isEmpty()) {
                    viewHolder.mTextViewTaskFinish.setText(currentDateAndTime.toString());
                    mRealmController.beginTransaction();
                    viewHolder.mTask.setTimeTaskFinish(viewHolder.mTextViewTaskFinish.getText().toString());
                    viewHolder.mTask.setTimeTaskNotifikation(0);


                    String dateStringStart = viewHolder.mTask.getTimeTaskStart();
                    String dateStringFinish = viewHolder.mTask.getTimeTaskFinish();

                    try {
                        long timeStartTask = sdf.parse(dateStringStart).getTime();
                        long timeFinishTask = sdf.parse(dateStringFinish).getTime();
                        long timeForToDo = timeFinishTask - timeStartTask;
                        viewHolder.mTask.setTimeForToDo(Long.toString(timeForToDo));

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    mRealmController.commitTransaction();

                    Snackbar.make(view, mContext.getResources().getString(R.string.snack_finish) + " " + viewHolder.mTask.getTimeTaskStart() +
                            " " + viewHolder.mTask.getTimeTaskFinish() + " " +
                            Long.valueOf(viewHolder.mTask.getTimeForToDo()) / 1000 / 60 / 60 + ":" +
                            Long.valueOf(viewHolder.mTask.getTimeForToDo()) / 1000 / 60 % 60, Snackbar.LENGTH_LONG).show();

                    viewHolder.mButtonFinish.setVisibility(View.INVISIBLE);
                    mRealmController.copyToRealmOrUpdate(viewHolder.mTask);
                    mPositionLisener.notifyAdapter();
                    mItemManger.updateConvertView(view,position);
                }
            }
        });

        viewHolder.mButtonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemManger.removeShownLayouts(viewHolder.mSwipeLayout);
                mRealmController.delete(viewHolder.mTask);
                notifyItemRemoved(position);
                mItemManger.closeAllItems();
                Snackbar.make(view, mContext.getResources().getString(R.string.task_comment)+" " + viewHolder.mTextViewTaskName.getText().toString() + " "
                        +mContext.getResources().getString(R.string.task_deleted), Snackbar.LENGTH_LONG).show();

            }
        });
        viewHolder.mButtonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPositionLisener.setPosition(position);
            }
        });

        viewHolder.mButtonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if ( !(viewHolder.mTask.getTimeTaskFinish().isEmpty())) {
                    mRealmController.beginTransaction();
                    viewHolder.mTask.setTimeTaskFinish("");
                    viewHolder.mTask.setTimeTaskNotifikation(new Date().getTime() + mSharedPreference.getTimeAutoStopFromPreferences(mContext, Constant.AUTO_STOP));
                    mRealmController.commitTransaction();
                    startNotification(viewHolder.mTask);
                    mItemManger.updateConvertView(view,position);
                    Snackbar.make(view, mContext.getResources().getString(R.string.snack_reset) , Snackbar.LENGTH_LONG).show();
                } else if ( !(viewHolder.mTask.getTimeTaskStart().isEmpty())) {
                    mRealmController.beginTransaction();
                    viewHolder.mTask.setTimeTaskStart("");
                    viewHolder.mTask.setTimeTaskNotifikation(0);
                    mRealmController.commitTransaction();
                    Snackbar.make(view, mContext.getResources().getString(R.string.snack_reset) , Snackbar.LENGTH_LONG).show();

                } else {
                    Snackbar.make(view, R.string.snack_not_start, Snackbar.LENGTH_LONG).show();
                }
                mPositionLisener.notifyAdapter();
                mItemManger.updateConvertView(view,position);
            }
        });

        if(!(viewHolder.mTask.getTimeTaskNotifikation() == 0)) {

            if (viewHolder.mTask.getTimeTaskNotifikation() < new Date().getTime()) {
                mRealmController.beginTransaction();
                viewHolder.mTask.setTimeTaskFinish(sdf.format(viewHolder.mTask.getTimeTaskNotifikation()));
                mRealmController.commitTransaction();
            }
        }

        viewHolder.mTextViewTaskName.setText(viewHolder.mTask.getTaskName());
        viewHolder.mTextViewTaskComment.setText(viewHolder.mTask.getTaskComment());
        viewHolder.mTextViewTaskStart.setText(viewHolder.mTask.getTimeTaskStart());
        viewHolder.mTextViewTaskFinish.setText(viewHolder.mTask.getTimeTaskFinish());
        viewHolder.mView.setBackgroundColor(mSharedPreference.getColorFromPreferences(mContext, Constant.COLOR_NOT_START, R.color.notStart));
        viewHolder.mButtonStart.setVisibility(View.VISIBLE);
        viewHolder.mButtonFinish.setVisibility(View.INVISIBLE);

        if (!viewHolder.mTask.getTimeTaskStart().isEmpty()) {
            viewHolder.mView.setBackgroundColor(mSharedPreference.getColorFromPreferences(mContext, Constant.COLOR_ON_START, R.color.onStart));
            viewHolder.mButtonStart.setVisibility(View.INVISIBLE);
            viewHolder.mButtonFinish.setVisibility(View.VISIBLE);
        }
        if (!viewHolder.mTask.getTimeTaskFinish().isEmpty()) {
            viewHolder.mView.setBackgroundColor(mSharedPreference.getColorFromPreferences(mContext, Constant.COLOR_ON_FINISH, R.color.onFinish));
            viewHolder.mButtonFinish.setVisibility(View.INVISIBLE);
        }
        mItemManger.bindView(viewHolder.itemView, position);
    }

    public void startNotification(Task task) {
        Long alertTime = task.getTimeTaskNotifikation();
        Intent intent = new Intent(mContext, TaskNotification.class);
        intent.putExtra(Constant.TASK_KEY, task);
        mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mAlarmManager.set(AlarmManager.RTC_WAKEUP, alertTime ,pendingIntent);
        //mAlarmManager.cancel(pendingIntent);
        mAlarmManager.set(AlarmManager.RTC_WAKEUP, task.getTimeTaskNotifikation(), pendingIntent);
        }

    @Override
    public int getItemCount() {
        //noinspection ConstantConditions
        return isDataValid() ? mRealmAdapter.size() : 0;
    }
    private boolean isDataValid() {
        return mRealmAdapter != null && mRealmAdapter.isValid();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    public void setRealm(RealmResults realmController) {
        mRealmAdapter = realmController;
    }

    public void updateResults(RealmResults<Task> results) {
        mRealmAdapter = results;
        notifyDataSetChanged();
    }
}
