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
    SharedPreferences settings;

    public SharedPreference() {
        super();
    }

    public int getColorFromPreferences(Context context, String key, int defaultCcolor) {

        settings = PreferenceManager.getDefaultSharedPreferences(context);
        //int colorValue = defaultCcolor;

        if (settings.contains(key)) {

             defaultCcolor = settings.getInt(key, defaultCcolor);
           // defaultCcolor = Color.parseColor(color);
        }

        return defaultCcolor;
    }

    public long getTimeAutoStopFromPreferences(Context context, String key) {
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

    public int getSortTypeFromPreferences(Context context, String key) {
        settings = PreferenceManager.getDefaultSharedPreferences(context);
        int defaultType = 0;
        if (settings.contains(key)) {
            try {
                defaultType = settings.getInt(key, defaultType);
            } catch (NumberFormatException nfe) {
                defaultType = 0;
            }
        }
        return defaultType;
    }

    public void putSortTypeToPreferences (Context context, String key, int value){
        settings.edit().putInt(key, value).apply();
    }
}
