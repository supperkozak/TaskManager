package com.example.taskmanager.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.example.taskmanager.R;
import com.example.taskmanager.interfases.YesNoListener;

public class MyAlertDialog extends DialogFragment{

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof YesNoListener)) {
            throw new ClassCastException();
        }
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.dialog_about_title)
                .setMessage(R.string.dialog_about_message)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((YesNoListener) getActivity()).onYesAlertDialog();
            }
        })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        return adb.create();
    }

    /**
     * Запрашивает разрешения на становку Голосового Поиска Google, отображая диалог. Если разрешение
     * получино - направляет пользователя в маркет.
     * @param Activity активити иниировавшая установку
     */

    public void installGoogleVoiceSearch(final Activity Activity) {

        // создаем диалог, который спросит у пользователя хочет ли он
        // установить Голосовой Поиск
        Dialog dialog = new AlertDialog.Builder(Activity)
                .setMessage(R.string.install)	// сообщение
                .setTitle(R.string.dialog_alert)	// заголовок диалога
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {	// положительная кнопка

                    // обработчик нажатия на кнопку Установить
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            // создаем Intent для открытия в маркете странички с приложением
                            // Голосовой Поиск имя пакета: com.google.android.voicesearch
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.voicesearch"));
                            // настраиваем флаги, чтобы маркет не папал к в историю нашего приложения (стек активити)
                            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                            // отправляем Intent
                            Activity.startActivity(intent);
                        } catch (Exception ex) {
                            // неудалось открыть маркет
                            // например из-за того что он не установлен
                            // ничего не подалаешь
                        }
                    }})

                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })	// негативная кнопка
                .create();

        dialog.show();	// показываем диалог
    }

    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }
}