package com.example.taskmanager.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.preference.PreferenceManager;

import com.example.taskmanager.constant.Constant;
import com.example.taskmanager.model.Task;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SharedPreference {

    public SharedPreference() {
        super();
    }

    public void aveTasksToSharedPreferences(Context context, List<Task> tasks) {
        SharedPreferences settings;
        Editor editor;

        settings = context.getSharedPreferences(Constant.PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();
        JSONArray jsonArray = new JSONArray();

        for (int i = 0; i < tasks.size(); i++) {
            JSONObject object = new JSONObject();
            try {
                object.put("TaskName", tasks.get(i).getTaskName());
                object.put("TaskComment", tasks.get(i).getTaskComment());
                object.put("TimeTaskStart", tasks.get(i).getTimeTaskStart());
                object.put("TimeTaskFinish", tasks.get(i).getTimeTaskFinish());
                object.put("TimeForToDo", tasks.get(i).getTimeForToDo());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsonArray.put(object);
        }

        String jsonArrayString = jsonArray.toString();
        editor.putString(Constant.PREFS_KEY, jsonArrayString);
        editor.apply();
    }

    public void clearSharedPreference(Context context) {
        SharedPreferences settings;
        Editor editor;


        settings = context.getSharedPreferences(Constant.PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();

        editor.clear();
        editor.apply();
    }

    public ArrayList<Task> etTasksFromSharedPreferences(Context context) {
        SharedPreferences settings;
        String json;
        ArrayList<Task> tasks = null;
        Task task;

        settings = context.getSharedPreferences(Constant.PREFS_NAME, Context.MODE_PRIVATE);
        json = settings.getString(Constant.PREFS_KEY, null);

        try {
            if (json != null) {
                JSONArray jsonArray = new JSONArray(json);

                if (jsonArray.length() > 0) {
                    tasks = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        task = new Task();
                        JSONObject taskJSON = jsonArray.getJSONObject(i);

                        task.setTaskName(taskJSON.optString("TaskName"));
                        task.setTaskComment(taskJSON.optString("TaskComment"));
                        task.setTimeTaskStart(taskJSON.optString("TimeTaskStart"));
                        task.setTimeTaskFinish(taskJSON.optString("TimeTaskFinish"));
                        task.setTimeForToDo(taskJSON.optString("TimeForToDo"));

                        tasks.add(task);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();

        }
        return tasks;
    }

    public void saveTasksToSharedPreferencesGSON(Context context, List<Task> tasks) {
        SharedPreferences settings;
        Editor editor;

        settings = context.getSharedPreferences(Constant.PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();

        Gson gson = new Gson();
        String gsonString = gson.toJson(tasks);

        editor.putString(Constant.PREFS_KEY, gsonString);
        editor.commit();

    }

    public ArrayList<Task> getTasksFromSharedPreferencesGSON(Context context) {
        SharedPreferences settings;
        List<Task> tasks;

        settings = context.getSharedPreferences(Constant.PREFS_NAME, Context.MODE_PRIVATE);

        if (settings.contains(Constant.PREFS_KEY)) {
            String jsonFavorites = settings.getString(Constant.PREFS_KEY, null);
            Gson gson = new Gson();
            Task[] arrayTasks = gson.fromJson(jsonFavorites,
                    Task[].class);

            tasks = Arrays.asList(arrayTasks);
            tasks = new ArrayList<Task>(tasks);
        } else
            return null;
        return (ArrayList<Task>) tasks;

    }

    public int getColorFromPreferences(Context context, String key, int defaultCcolor) {
        SharedPreferences settings;
        settings = PreferenceManager.getDefaultSharedPreferences(context);
        //int colorValue = defaultCcolor;

        if (settings.contains(key)) {

             defaultCcolor = settings.getInt(key, 0);
           // defaultCcolor = Color.parseColor(color);
        }

        return defaultCcolor;

    }

    public long getTimeAutoStopFromPreferences(Context context, String key) {
        SharedPreferences settings;
        settings = PreferenceManager.getDefaultSharedPreferences(context);
        Long defaultStop = 0l;

        if (settings.contains(key)) {

            try {
                int i = Integer.parseInt(settings.getString(key, "0"));
                defaultStop = ((long)i)*1000*60;
            } catch (NumberFormatException nfe) {
                defaultStop = 0l;
            }
        }

        return defaultStop;

    }
}
