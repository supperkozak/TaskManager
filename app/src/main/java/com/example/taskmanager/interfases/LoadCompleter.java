package com.example.taskmanager.interfases;

import com.example.taskmanager.model.Task;

import java.util.ArrayList;

/**
 * Created by Тарас on 07.06.2016.
 */
public interface LoadCompleter {
    void loadCallback(ArrayList<Task> listTask);
}
