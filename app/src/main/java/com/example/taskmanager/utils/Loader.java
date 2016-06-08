package com.example.taskmanager.utils;

import android.content.Context;

import com.example.taskmanager.activity.MainActivity;
import com.example.taskmanager.interfases.LoadCompleter;
import com.example.taskmanager.model.Task;

import java.util.ArrayList;

/**
 * Created by Тарас on 07.06.2016.
 */
public class Loader implements Runnable {
    Thread thread;
    LoadCompleter loadCompleter;
    SharedPreference sharedPreference;
    ArrayList<Task> mListTask;
    Context context;
    // Конструктор
    public Loader(Context context, LoadCompleter loadCompleter) {
        sharedPreference = new SharedPreference();
        this.loadCompleter = loadCompleter;
        this.context = context;
        thread = new Thread(this, "Load SharedPreferences");
        thread.start();
    }

    // Обязательный метод для интерфейса Runnable
    public void run() {
        mListTask = sharedPreference.getTasksFromSharedPreferences(context);
        loadCompleter.loadCallback(mListTask);

    }
}

