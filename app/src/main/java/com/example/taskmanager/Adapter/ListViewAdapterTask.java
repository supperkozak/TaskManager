package com.example.taskmanager.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.taskmanager.R;
import com.example.taskmanager.Model.Task;


import java.util.List;

/**
 * Created by Тарас on 28.05.2016.
 */
public class ListViewAdapterTask extends BaseAdapter  {
    List<Task> mListTask;
    Context mContext;
    LayoutInflater mlInflater;

    public ListViewAdapterTask(List<Task> listTask, Context context) {
        mListTask = listTask;
        mContext = context;

        mlInflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }



    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mListTask.size();
    }

    @Override
    public Object getItem(int position) {

        return mListTask.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = mlInflater.inflate(R.layout.task_item, parent, false);
        }

        TextView tvNameTask = (TextView) view.findViewById(R.id.tvNameTask);
        TextView tvCommentTask= (TextView) view.findViewById(R.id.tvCommentTask);
        TextView tvTimeTaskStart = (TextView) view.findViewById(R.id.tvTimeTaskStart);
        TextView tvTimeTaskFinish = (TextView) view.findViewById(R.id.tvTimeTaskFinish);


        tvNameTask.setText(mListTask.get(position).getTaskName());
        tvCommentTask.setText(mListTask.get(position).getTaskComment());
        tvTimeTaskStart.setText(mListTask.get(position).getTimeTaskStart());
        tvTimeTaskFinish.setText(mListTask.get(position).getTimeTaskFinish());


        return view;
    }

}

