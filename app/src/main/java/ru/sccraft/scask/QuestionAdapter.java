package ru.sccraft.scask;

import android.content.Context;
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

    QuestionAdapter(Context context, Question[] вопросы) {
        this.context = context;
        this.вопросы = вопросы;
        lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        if (вопрос.решено()) {
            if (вопрос.проверить_ответ()) {
                вывод_вопроса.setTextColor(вью.getResources().getColor(R.color.colorYes));
            } else {
                вывод_вопроса.setTextColor(вью.getResources().getColor(R.color.colorAccent));
            }
        }
        return вью;
    }

    private Question получить_вопрос(int позиция) {
        return (Question) getItem(позиция);
    }
}
