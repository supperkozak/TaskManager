package com.example.taskmanager.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.taskmanager.R;
import com.example.taskmanager.Task.Task;

import java.util.List;

/**
 * Created by Тарас on 28.05.2016.
 */
public class ListViewAdapterTask extends BaseAdapter {
    List<Task> mListTask;
    Context context;
    LayoutInflater mlInflater;

    public ListViewAdapterTask(List<Task> mListTask, Context context) {
        this.mListTask = mListTask;
        this.context = context;

        mlInflater = (LayoutInflater) this.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

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
        tvNameTask.setText(mListTask.get(position).getmTaskName());
        tvCommentTask.setText(mListTask.get(position).getmTaskComment());

        return view;
    }
}

