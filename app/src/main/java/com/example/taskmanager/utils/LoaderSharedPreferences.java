package com.example.taskmanager.utils;

import android.content.Context;

import com.example.taskmanager.interfases.LoadCompleter;
import com.example.taskmanager.model.Task;

import java.util.ArrayList;

public class LoaderSharedPreferences implements Runnable {
    Thread thread;
    LoadCompleter loadCompleter;
    SharedPreference sharedPreference;
    ArrayList<Task> mListTask;
    Context context;

    public LoaderSharedPreferences(Context context, LoadCompleter loadCompleter) throws InterruptedException {
        sharedPreference = new SharedPreference();
        this.loadCompleter = loadCompleter;
        this.context = context;
        thread = new Thread(this, "Load SharedPreferences");
        thread.setPriority(10);
        thread.start();
    }


    public void run() {
        mListTask = sharedPreference.getTasksFromSharedPreferencesGSON(context);
        loadCompleter.loadCallback(mListTask);

    }
}

