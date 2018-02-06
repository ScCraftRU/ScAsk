package ru.sccraft.scask;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Создан пользователем alexandr 18.01.18 20:08, работающем в комманде ScCraft.
 */

public class Question {
    private static final String LOG_TAG = "ScAsk/Question";

    String вопрос;
    private byte тип; //0 - ДА/НЕТ; 1 = число; 2 = Строковый ответ

    private boolean логический_ответ = false;
    private int числовой_ответ;
    private String строковый_ответ = null;

    private boolean ответ_пользователя_логический;
    private int ответ_пользователя_число;
    private String ответ_пользователя_строка;
    private boolean отвечен = false;

    public Question(String вопрос, boolean ответ) {
        this.тип = 0;
        this.вопрос = вопрос;
        this.логический_ответ = ответ;
    }

    public Question(String вопрос, int ответ) {
        this.тип = 1;
        this.вопрос = вопрос;
        this.числовой_ответ = ответ;
    }

    public Question(String вопрос, String ответ) {
        this.тип = 2;
        this.вопрос = вопрос;
        this.строковый_ответ = ответ;
    }

    public boolean проверить_ответ() {
        if (!отвечен) return false;
        switch(тип) {
            case 0:
                return логический_ответ == ответ_пользователя_логический;
            case 1:
                return числовой_ответ == ответ_пользователя_число;
            case 2:
                return строковый_ответ.equals(ответ_пользователя_строка);
        }
        return false;
    }

    public byte получить_тип() {
        return тип;
    }

    public String toJSON() {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.toJson(this);
    }

    static Question fromJSON(String JSON) {
        Log.i(LOG_TAG, "Входящий JSON " + JSON);
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Question    q = gson.fromJson(JSON, Question.class);
        return q;
    }

    @Override
    public String toString() {
        return вопрос;
    }

    public void ответить(boolean ответ_пользователя) {
        ответ_пользователя_логический = ответ_пользователя;
        отвечен = true;
    }

    public void ответить(int ответ_пользователя) {
        ответ_пользователя_число = ответ_пользователя;
        отвечен = true;
    }

    public void ответить(String ответ_пользователя) {
        ответ_пользователя_строка = ответ_пользователя;
        отвечен = true;
    }

    public boolean решено() {
        return отвечен;
    }

    public Object получить_ответ() {
        switch (тип) {
            case 0:
                return логический_ответ;
            case 1:
                return числовой_ответ;
            case 2:
                return строковый_ответ;
        }
        return null;
    }

    public void сбросить_ответ() {
        отвечен = false;
    }
}
