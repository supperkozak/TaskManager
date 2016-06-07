package com.example.taskmanager.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Тарас on 28.05.2016.
 */
public class Task implements Parcelable {
    private String mTaskName;
    private String mTaskComment;
    private String mTimeTaskStart;
    private String mTimeTaskFinish;
    private String mTimeForToDo;



    public Task() {
    }

    public Task(String taskName, String taskComment) {
        mTaskName = taskName;
        mTaskComment = taskComment;
    }


    protected Task(Parcel in) {
        mTaskName = in.readString();
        mTaskComment = in.readString();
        mTimeTaskStart = in.readString();
        mTimeTaskFinish = in.readString();


    }

    public static final Creator<Task> CREATOR = new Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTaskName);
        dest.writeString(mTaskComment);
        dest.writeString(mTimeTaskStart);
        dest.writeString(mTimeTaskFinish);


    }

    @Override
    public String toString() {
        return "Task{" +
                "mTaskName='" + mTaskName + '\'' +
                ", mTaskComment='" + mTaskComment + '\'' +
                ", mTimeTaskStart='" + mTimeTaskStart + '\'' +
                ", mTimeTaskFinish='" + mTimeTaskFinish + '\'' +
                '}';
    }

    public String getTaskName() {
        return mTaskName;
    }

    public void setTaskName(String taskName) {
        mTaskName = taskName;
    }

    public String getTaskComment() {
        return mTaskComment;
    }

    public void setTaskComment(String taskComment) {
        mTaskComment = taskComment;
    }

    public String getTimeTaskStart() {
        return mTimeTaskStart;
    }

    public void setTimeTaskStart(String timeTaskStart) {
        mTimeTaskStart = timeTaskStart;
    }

    public String getTimeTaskFinish() {
        return mTimeTaskFinish;
    }

    public void setTimeTaskFinish(String timeTaskFinish) {
        mTimeTaskFinish = timeTaskFinish;
    }

    public String getTimeForToDo() {
        return mTimeForToDo;
    }

    public void setTimeForToDo(String timeForToDo) {
        mTimeForToDo = timeForToDo;
    }



}