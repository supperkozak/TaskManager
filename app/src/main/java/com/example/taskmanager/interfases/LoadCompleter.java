package com.example.taskmanager.interfases;

import com.example.taskmanager.model.Task;

import java.util.ArrayList;


public interface LoadCompleter {
    void loadCallback(ArrayList<Task> listTask);
}
