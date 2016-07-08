package com.example.taskmanager.model;

import android.os.Parcel;
import android.os.Parcelable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Task extends RealmObject implements Parcelable {
    
    @PrimaryKey
    private String mId;
    private String mTaskName;
    private String mTaskComment;
    private String mTimeTaskStart;
    private String mTimeTaskFinish;
    private String mTimeForToDo;
    private long mTimeTaskNotifikation;

    public Task() {
        mId = Long.toString(System.currentTimeMillis());
        mTimeTaskStart = "";
        mTimeTaskFinish = "";
        mTaskName = "";
        mTaskComment = "";
    }

    public Task(String taskName, String taskComment) {
        mId = Long.toString(System.currentTimeMillis());
        mTaskName = taskName;
        mTaskComment = taskComment;
        mTimeTaskStart = "";
        mTimeTaskFinish = "";
    }

    protected Task(Parcel in) {
        mId = in.readString();
        mTaskName = in.readString();
        mTaskComment = in.readString();
        mTimeTaskStart = in.readString();
        mTimeTaskFinish = in.readString();
        mTimeForToDo = in.readString();
        mTimeTaskNotifikation = in.readLong();

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
        dest.writeString(mId);
        dest.writeString(mTaskName);
        dest.writeString(mTaskComment);
        dest.writeString(mTimeTaskStart);
        dest.writeString(mTimeTaskFinish);
        dest.writeString(mTimeForToDo);
        dest.writeLong(mTimeTaskNotifikation);

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

    public long getTimeTaskNotifikation() {
        return mTimeTaskNotifikation;
    }

    public void setTimeTaskNotifikation(long timeTaskNotifikation) {
        mTimeTaskNotifikation = timeTaskNotifikation;
    }

    public String getID() {
        return mId;
    }

    public void setID(String ID) {
        mId = ID;
    }

}
