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
import android.widget.Toast;


import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;

import com.example.taskmanager.R;
import com.example.taskmanager.activity.MainActivity;
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
        SwipeLayout swipeLayout;
        public TextView mTextViewNameTask;
        public TextView mTextViewCommentTask;
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
            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            mButtonDelete = (Button) itemView.findViewById(R.id.button_delete_task);
            mButtonEdit = (ImageButton) itemView.findViewById(R.id.button_edit_task);
            mButtonReset = (ImageButton) itemView.findViewById(R.id.button_reset_task);
            mButtonStart = (Button) itemView.findViewById(R.id.button_start_task);
            mButtonFinish = (Button) itemView.findViewById(R.id.button_finish_task);
            mButtonFinish.setVisibility(View.INVISIBLE);

            mTextViewNameTask = (TextView) itemView.findViewById(R.id.tv_name_task);
            mTextViewCommentTask = (TextView) itemView.findViewById(R.id.tv_comment_task);
            mTextViewTaskStart = (TextView) itemView.findViewById(R.id.tvTimeTaskStart);
            mTextViewTaskFinish = (TextView) itemView.findViewById(R.id.tvTimeTaskFinish);
            mView =  itemView.findViewById(R.id.item_container);
        }
    }

    private Context mContext;
    private List<Task> mListTask;
    SetPositionLisener setPositionLisener;
    SharedPreference sharedPreference = new SharedPreference();
    AlarmManager mAlarmManager;


    //protected SwipeItemRecyclerMangerImpl mItemManger = new SwipeItemRecyclerMangerImpl(this);

    public RecyclerViewAdapter(Context context, List<Task> dataset, SetPositionLisener setPositionLisener) {
        this.mContext = context;
        this.setPositionLisener = setPositionLisener;
        mListTask = dataset;
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item_start, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder viewHolder, final int position) {
        viewHolder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
        viewHolder.swipeLayout.addDrag(SwipeLayout.DragEdge.Left, viewHolder.swipeLayout.findViewById(R.id.swipe_edit));
        viewHolder.swipeLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onOpen(SwipeLayout layout) {
                YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.trash));
                YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.button_reset_task));
                YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.button_edit_task));
            }
        });
        final SimpleDateFormat sdf = new SimpleDateFormat("MM:dd:yyyy:HH:mm");
        final String currentDateAndTime = sdf.format(new Date());

        viewHolder.mButtonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mListTask.get(position).getTimeTaskStart() == null || mListTask.get(position).getTimeTaskStart().equalsIgnoreCase("")) {
                    viewHolder.mButtonStart.setVisibility(View.INVISIBLE);
                    viewHolder.mButtonFinish.setVisibility(View.VISIBLE);
                    viewHolder.mTextViewTaskStart.setText(currentDateAndTime.toString());
                    mListTask.get(position).setTimeTaskStart(viewHolder.mTextViewTaskStart.getText().toString());
                    mListTask.get(position).setTimeTaskNotifikation(new Date().getTime() + sharedPreference.getTimeAutoStopFromPreferences(mContext, Constant.AUTO_STOP));
                    startNotify(mListTask.get(position));
                    Snackbar.make(view, mContext.getResources().getString(R.string.snack_start) + " " + mListTask.get(position).getTimeTaskStart(), Snackbar.LENGTH_LONG)
                            .show();


                }
                sharedPreference.saveTasksToSharedPreferencesGSON(mContext, mListTask);
                setPositionLisener.notifyAdapter();
            }
        });

        viewHolder.mButtonFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mListTask.get(position).getTimeTaskFinish() == null || mListTask.get(position).getTimeTaskFinish().equalsIgnoreCase("")) {
                    viewHolder.mTextViewTaskFinish.setText(currentDateAndTime.toString());
                    mListTask.get(position).setTimeTaskFinish(viewHolder.mTextViewTaskFinish.getText().toString());

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

                    sharedPreference.saveTasksToSharedPreferencesGSON(mContext, mListTask);
                    setPositionLisener.notifyAdapter();
                }
            }
        });
        viewHolder.swipeLayout.getSurfaceView().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //setPositionLisener.setPosition(position);
                return true;
            }
        });

        viewHolder.swipeLayout.setOnDoubleClickListener(new SwipeLayout.DoubleClickListener() {
            @Override
            public void onDoubleClick(SwipeLayout layout, boolean surface) {
                Toast.makeText(mContext, "DoubleClick", Toast.LENGTH_SHORT).show();
            }
        });
        viewHolder.mButtonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemManger.removeShownLayouts(viewHolder.swipeLayout);
                mListTask.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, mListTask.size());
                mItemManger.closeAllItems();
                Snackbar.make(view, mContext.getResources().getString(R.string.task_comment)+" " + viewHolder.mTextViewNameTask.getText().toString() + " "
                        +mContext.getResources().getString(R.string.task_deleted), Snackbar.LENGTH_LONG).show();

            }
        });
        viewHolder.mButtonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPositionLisener.setPosition(position);
            }
        });
        viewHolder.mButtonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mListTask.get(position).getTimeTaskFinish()!= null && !(mListTask.get(position).getTimeTaskFinish().equalsIgnoreCase(""))) {
                    mListTask.get(position).setTimeTaskFinish("");
                    Snackbar.make(view, mContext.getResources().getString(R.string.snack_reset) , Snackbar.LENGTH_LONG).show();
                } else if (mListTask.get(position).getTimeTaskStart() != null && !(mListTask.get(position).getTimeTaskStart().equalsIgnoreCase(""))) {
                    mListTask.get(position).setTimeTaskStart("");
                    Snackbar.make(view, mContext.getResources().getString(R.string.snack_reset) , Snackbar.LENGTH_LONG).show();

                } else {
                    Snackbar.make(view, R.string.snack_not_start, Snackbar.LENGTH_LONG).show();
                }
                sharedPreference.saveTasksToSharedPreferencesGSON(mContext, mListTask);
                setPositionLisener.notifyAdapter();
            }
        });
        viewHolder.mTextViewNameTask.setText(mListTask.get(position).getTaskName());
        viewHolder.mTextViewCommentTask.setText(mListTask.get(position).getTaskComment());
        viewHolder.mTextViewTaskStart.setText(mListTask.get(position).getTimeTaskStart());
        viewHolder.mTextViewTaskFinish.setText(mListTask.get(position).getTimeTaskFinish());
        viewHolder.mView.setBackgroundColor(sharedPreference.getColorFromPreferences(mContext, Constant.COLOR_NOT_START, R.color.notStart));
        viewHolder.mButtonStart.setVisibility(View.VISIBLE);
        viewHolder.mButtonFinish.setVisibility(View.INVISIBLE);

        if (mListTask.get(position).getTimeTaskStart() != null && !mListTask.get(position).getTimeTaskStart().isEmpty()) {
            viewHolder.mView.setBackgroundColor(sharedPreference.getColorFromPreferences(mContext, Constant.COLOR_ON_START, R.color.onStart));
            viewHolder.mButtonStart.setVisibility(View.INVISIBLE);
            viewHolder.mButtonFinish.setVisibility(View.VISIBLE);
        }
        if (mListTask.get(position).getTimeTaskFinish() != null &&!mListTask.get(position).getTimeTaskFinish().isEmpty()) {
            viewHolder.mView.setBackgroundColor(sharedPreference.getColorFromPreferences(mContext, Constant.COLOR_ON_FINISH, R.color.onFinish));
            viewHolder.mButtonFinish.setVisibility(View.INVISIBLE);
        }
    }

    private void startNotify(Task task) {
        mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(mContext, TaskNotification.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);
// На случай, если мы ранее запускали активити, а потом поменяли время,
// откажемся от уведомления
        mAlarmManager.cancel(pendingIntent);
// Устанавливаем разовое напоминание
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
}
