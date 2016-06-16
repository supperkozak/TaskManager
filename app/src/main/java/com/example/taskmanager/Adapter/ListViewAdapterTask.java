package com.example.taskmanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.taskmanager.R;
import com.example.taskmanager.model.Task;

import java.util.List;

public class ListViewAdapterTask extends BaseAdapter  {
    List<Task> mListTask;
    Context mContext;
    LayoutInflater mlInflater;

    public ListViewAdapterTask(List<Task> listTask, Context context) {
        mListTask = listTask;
        mContext = context;

        mlInflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    static class ViewHolder {
        TextView tvNameTask;
        TextView tvCommentTask;
        TextView tvTimeTaskStart;
        TextView tvTimeTaskFinish;

        View view;
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

        ViewHolder viewHolder;

        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.task_item, parent, false);

            viewHolder.tvNameTask = (TextView) convertView.findViewById(R.id.tvNameTask);
            viewHolder.tvCommentTask = (TextView) convertView.findViewById(R.id.tvCommentTask);
            viewHolder.tvTimeTaskStart = (TextView) convertView.findViewById(R.id.tvTimeTaskStart);
            viewHolder.tvTimeTaskFinish = (TextView) convertView.findViewById(R.id.tvTimeTaskFinish);
            viewHolder.view = convertView.findViewById(R.id.item_container);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvNameTask.setText(mListTask.get(position).getTaskName());
        viewHolder.tvCommentTask.setText(mListTask.get(position).getTaskComment());
        viewHolder.tvTimeTaskStart.setText(mListTask.get(position).getTimeTaskStart());
        viewHolder.tvTimeTaskFinish.setText(mListTask.get(position).getTimeTaskFinish());
        viewHolder.view.setBackgroundColor(mContext.getResources().getColor(R.color.notStart));

        if (mListTask.get(position).getTimeTaskStart() != null && !mListTask.get(position).getTimeTaskStart().isEmpty()) {
            viewHolder.view.setBackgroundColor(mContext.getResources().getColor(R.color.onStart));
        }
        if (mListTask.get(position).getTimeTaskFinish() != null &&!mListTask.get(position).getTimeTaskFinish().isEmpty()) {
            viewHolder.view.setBackgroundColor(mContext.getResources().getColor(R.color.onFinish));
        }
        return convertView;
    }
}

