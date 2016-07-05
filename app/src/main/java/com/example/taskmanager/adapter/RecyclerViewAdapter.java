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
import com.example.taskmanager.utils.SharedPreference;
import com.example.taskmanager.utils.TaskNotification;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerSwipeAdapter<RecyclerViewAdapter.SimpleViewHolder> {

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
    private List<Task> mListTask;
    SetPositionLisener mPositionLisener;
    SharedPreference mSharedPreference = new SharedPreference();
    AlarmManager mAlarmManager;

    public RecyclerViewAdapter(Context context, List<Task> dataSet, SetPositionLisener mPositionLisener) {
        this.mContext = context;
        this.mPositionLisener = mPositionLisener;
        mListTask = dataSet;
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item_start, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder viewHolder, final int position) {
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
        mItemManger.bindView(viewHolder.itemView, position);

        viewHolder.mTextViewTaskName.setText(mListTask.get(position).getTaskName());

        final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        final String currentDateAndTime = sdf.format(new Date());

        viewHolder.mButtonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mListTask.get(position).getTimeTaskStart().isEmpty()) {
                    viewHolder.mButtonStart.setVisibility(View.INVISIBLE);
                    viewHolder.mButtonFinish.setVisibility(View.VISIBLE);
                    viewHolder.mTextViewTaskStart.setText(currentDateAndTime.toString());
                    mListTask.get(position).setTimeTaskStart(viewHolder.mTextViewTaskStart.getText().toString());
                    mListTask.get(position).setTimeTaskNotifikation(new Date().getTime() + mSharedPreference.getTimeAutoStopFromPreferences(mContext, Constant.AUTO_STOP));
                    startNotify(mListTask.get(position));
                    Snackbar.make(view, mContext.getResources().getString(R.string.snack_start) + " " + mListTask.get(position).getTimeTaskStart(), Snackbar.LENGTH_LONG)
                            .show();
                }
                mSharedPreference.saveTasksToSharedPreferencesGSON(mContext, mListTask);
                mPositionLisener.notifyAdapter();
            }
        });

        viewHolder.mButtonFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mListTask.get(position).getTimeTaskFinish().isEmpty()) {
                    viewHolder.mTextViewTaskFinish.setText(currentDateAndTime.toString());
                    mListTask.get(position).setTimeTaskFinish(viewHolder.mTextViewTaskFinish.getText().toString());
                    mListTask.get(position).setTimeTaskNotifikation(0);

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

                    Snackbar.make(view, mContext.getResources().getString(R.string.snack_finish) + " " + mListTask.get(position).getTimeTaskStart() +
                            " " + mListTask.get(position).getTimeTaskFinish() + " " +
                            Long.valueOf(mListTask.get(position).getTimeForToDo()) / 1000 / 60 / 60 + ":" +
                            Long.valueOf(mListTask.get(position).getTimeForToDo()) / 1000 / 60 % 60, Snackbar.LENGTH_LONG).show();

                    viewHolder.mButtonFinish.setVisibility(View.INVISIBLE);

                    mSharedPreference.saveTasksToSharedPreferencesGSON(mContext, mListTask);
                    mPositionLisener.notifyAdapter();
                }
            }
        });

        viewHolder.mButtonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemManger.removeShownLayouts(viewHolder.mSwipeLayout);
                mListTask.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, mListTask.size());
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

                if ( !(mListTask.get(position).getTimeTaskFinish().isEmpty())) {
                    mListTask.get(position).setTimeTaskFinish("");
                    mListTask.get(position).setTimeTaskNotifikation(new Date().getTime() + mSharedPreference.getTimeAutoStopFromPreferences(mContext, Constant.AUTO_STOP));
                    startNotify(mListTask.get(position));
                    Snackbar.make(view, mContext.getResources().getString(R.string.snack_reset) , Snackbar.LENGTH_LONG).show();
                } else if ( !(mListTask.get(position).getTimeTaskStart().isEmpty())) {
                    mListTask.get(position).setTimeTaskStart("");
                    mListTask.get(position).setTimeTaskNotifikation(0);
                    Snackbar.make(view, mContext.getResources().getString(R.string.snack_reset) , Snackbar.LENGTH_LONG).show();

                } else {
                    Snackbar.make(view, R.string.snack_not_start, Snackbar.LENGTH_LONG).show();
                }
                mSharedPreference.saveTasksToSharedPreferencesGSON(mContext, mListTask);
                mPositionLisener.notifyAdapter();
            }
        });

        if(!(mListTask.get(position).getTimeTaskNotifikation() == 0)) {

            if (mListTask.get(position).getTimeTaskNotifikation() < new Date().getTime()) {
                mListTask.get(position).setTimeTaskFinish(sdf.format(mListTask.get(position).getTimeTaskNotifikation()));
            }
        }

        viewHolder.mTextViewTaskName.setText(mListTask.get(position).getTaskName());
        viewHolder.mTextViewTaskComment.setText(mListTask.get(position).getTaskComment());
        viewHolder.mTextViewTaskStart.setText(mListTask.get(position).getTimeTaskStart());
        viewHolder.mTextViewTaskFinish.setText(mListTask.get(position).getTimeTaskFinish());
        viewHolder.mView.setBackgroundColor(mSharedPreference.getColorFromPreferences(mContext, Constant.COLOR_NOT_START, R.color.notStart));
        viewHolder.mButtonStart.setVisibility(View.VISIBLE);
        viewHolder.mButtonFinish.setVisibility(View.INVISIBLE);

        if (!mListTask.get(position).getTimeTaskStart().isEmpty()) {
            viewHolder.mView.setBackgroundColor(mSharedPreference.getColorFromPreferences(mContext, Constant.COLOR_ON_START, R.color.onStart));
            viewHolder.mButtonStart.setVisibility(View.INVISIBLE);
            viewHolder.mButtonFinish.setVisibility(View.VISIBLE);
        }
        if (!mListTask.get(position).getTimeTaskFinish().isEmpty()) {
            viewHolder.mView.setBackgroundColor(mSharedPreference.getColorFromPreferences(mContext, Constant.COLOR_ON_FINISH, R.color.onFinish));
            viewHolder.mButtonFinish.setVisibility(View.INVISIBLE);
        }
    }

    public void startNotify(Task task) {
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
        return mListTask.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    public void setListTask(List<Task> listTask) {
        mListTask = listTask;
    }
}
