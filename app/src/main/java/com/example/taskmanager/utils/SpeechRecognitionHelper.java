package com.example.taskmanager.utils;

import java.util.List;

import android.app.Activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.speech.RecognizerIntent;

import com.example.taskmanager.R;
import com.example.taskmanager.constant.Constant;

/**
 * Класс-помошник для распознавания речи
 */
public class SpeechRecognitionHelper {

	public MyAlertDialog mAlertDialog = new MyAlertDialog();
	static int extra;
	
	/**
	 * Запускает процесс распознавания. Проверяет наличие активити для распознования речи. 
	 * Если активити нет, отправляет пользователя в маркет установить Голосовой Поиск 
	 * Google. Если активи для распознования есть, то отправляет Intent для ее запуска.
	 * 
	 * @param AddTaskActivity активити, которая инициировала процесс распозвания
	 */
	public void run(Activity AddTaskActivity, int extra) {
		this.extra = extra;
		// проверяем есть ли активити для распознавания
		if (isSpeechRecognitionActivityPresented(AddTaskActivity)) {
			// если есть - запускаем распознавание
			startRecognitionActivity(AddTaskActivity);
		} else {
			// начинаем процесс установки
			mAlertDialog.installGoogleVoiceSearch(AddTaskActivity);
		}			
	}

	/** 
	 * Проверяет наличие активити способной выполнить распознавание речи
	 * 
	 * @param AddTaskActivity активити, которая запросила проверку
	 * @return true - если есть, false - если такой активити нет 
	 */
	private static boolean isSpeechRecognitionActivityPresented(Activity AddTaskActivity) {
		try {
			// получаем экземпляр менеджера пакетов
			PackageManager pm = AddTaskActivity.getPackageManager();
			// получаем список активити способных обработать запрос на распознавание
			List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
			
			if (activities.size() != 0) {	// если список не пустой	
				return true;				// то умеем распознавать речь
			}
		} catch (Exception e) {
			
		}
		
		return false; // не умеем распозновать речь
	}
	
	/**
	 * Отпавляет Intent с запросом на распознавание речи
	 * @param AddTaskActivity иниировавшая запрос активити
	 */
	private static void startRecognitionActivity(Activity AddTaskActivity) {
		
		// создаем Intent с действием RecognizerIntent.ACTION_RECOGNIZE_SPEECH
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		
		// добаляем дополнительные параметры:
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, R.string.speech);	// текстовая подсказка пользователю
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);	// модель распознавания
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);	// количество резальтатов, которое мы хотим получить
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ua-UA");

        // стартуем активити и ждем от нее результата
        AddTaskActivity.startActivityForResult(intent, extra);
	}
}
