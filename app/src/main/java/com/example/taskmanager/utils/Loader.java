package com.example.taskmanager.utils;

import android.content.Context;

import com.example.taskmanager.interfases.LoadCompleter;
import com.example.taskmanager.model.Task;

import java.util.ArrayList;

/**
 * Created by Тарас on 07.06.2016.
 */
public class Loader implements Runnable {
    Thread thread;
    LoadCompleter loadComplete;
    SharedPreference sharedPreference;
    ArrayList<Task> mListTask;
    Context context;

    public Loader(Context context, LoadCompleter loadComplete) {
        sharedPreference = new SharedPreference();
        this.loadComplete = loadComplete;
        this.context = context;

        thread = new Thread( this, "Load sharedPreference");
        thread.start();
}

public void run() {
        mListTask = sharedPreference.getJson(context);
        loadComplete.loadCallback(mListTask);
        }
}

