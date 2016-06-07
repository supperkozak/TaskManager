package com.example.taskmanager.utils;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.example.taskmanager.constant.Constant;
import com.example.taskmanager.model.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SharedPreference {

    public SharedPreference() {
        super();
    }

    public void saveTasksToSharedPreferences(Context context, List<Task> tasks) {
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

    public ArrayList<Task> getJson(Context context) {
        SharedPreferences settings;
        String json;
        ArrayList<Task> tasks = null;
        Task task;

        settings = context.getSharedPreferences(Constant.PREFS_NAME, Context.MODE_PRIVATE);
        json = settings.getString(Constant.PREFS_KEY, null);

        try {
            if (json != null) {
                JSONArray jsonArray = new JSONArray(json);

                if (jsonArray != null) {
                    tasks = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        task = new Task();
                        JSONObject pdtObj = jsonArray.getJSONObject(i);

                        task.setTaskName(pdtObj.optString("TaskName"));
                        task.setTaskComment(pdtObj.optString("TaskComment"));
                        task.setTimeTaskStart(pdtObj.optString("TimeTaskStart"));
                        task.setTimeTaskFinish(pdtObj.optString("TimeTaskFinish"));
                        task.setTimeForToDo(pdtObj.optString("TimeForToDo"));

                        tasks.add(task);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();

        }
        return tasks;
    }
}