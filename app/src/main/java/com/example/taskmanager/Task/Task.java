package com.example.taskmanager.Task;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Тарас on 28.05.2016.
 */
public class Task implements Parcelable {
    private String mTaskName;
    private  String mTaskComment;

    public Task() {
    }

    public Task(String mTaskName, String mTaskComment) {
        this.mTaskName = mTaskName;
        this.mTaskComment = mTaskComment;
    }


    protected Task(Parcel in) {
        mTaskName = in.readString();
        mTaskComment = in.readString();
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

    }

    @Override
    public String toString() {
        return "Task{" +
                "mTaskName='" + mTaskName + '\'' +
                ", mTaskComment='" + mTaskComment + '\'' +
                '}';
    }

    public String getmTaskName() {
        return mTaskName;
    }

    public void setmTaskName(String mTaskName) {
        this.mTaskName = mTaskName;
    }

    public String getmTaskComment() {
        return mTaskComment;
    }

    public void setmTaskComment(String mTaskComment) {
        this.mTaskComment = mTaskComment;
    }
}
