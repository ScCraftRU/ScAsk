package ru.sccraft.scask;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Создан пользователем alexandr 21.01.18 12:01, работающем в комманде ScCraft.
 */

public class QuestionAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater lInflater;
    private Question[] вопросы;
    private SharedPreferences настройки;

    QuestionAdapter(Context context, Question[] вопросы) {
        this.context = context;
        this.вопросы = вопросы;
        lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        настройки = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public int getCount() {
        return вопросы.length;
    }

    @Override
    public Object getItem(int i) {
        return вопросы[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        // используем созданные, но не используемые view
        View вью = view;
        if (вью == null) {
            вью = lInflater.inflate(R.layout.item_question, viewGroup, false);
        }
        Question вопрос = получить_вопрос(i);

        TextView вывод_вопроса = вью.findViewById(R.id.adapter_item);
        boolean показывать_номера_вопросов = настройки.getBoolean("settings_showQuestionsNumber", true);
        if (показывать_номера_вопросов) {
            вывод_вопроса.setText((i + 1) + ") " + вопрос.вопрос);
        } else {
            вывод_вопроса.setText(вопрос.вопрос);
        }
        if (вопрос.решено()) {
            if (вопрос.проверить_ответ()) {
                вывод_вопроса.setTextColor(вью.getResources().getColor(R.color.colorYes));
            } else {
                вывод_вопроса.setTextColor(вью.getResources().getColor(R.color.colorAccent));
            }
        } else {
            switch (MainActivity.currentNightMode) {
                case Configuration.UI_MODE_NIGHT_NO:
                    // Night mode is not active, we're using the light theme
                    вывод_вопроса.setTextColor(вью.getResources().getColor(android.R.color.black));
                    break;
                case Configuration.UI_MODE_NIGHT_YES:
                    // Night mode is active, we're using dark theme
                    вывод_вопроса.setTextColor(вью.getResources().getColor(android.R.color.white));
                    break;
            }
        }
        return вью;
    }

    private Question получить_вопрос(int позиция) {
        return (Question) getItem(позиция);
    }
}
