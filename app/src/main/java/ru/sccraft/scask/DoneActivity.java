package ru.sccraft.scask;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

public class DoneActivity extends AppCompatActivity {

    private String[] file;
    private Fe fe;
    private Question[] вопросы;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        file = fileList();
        fe = new Fe(this);
        setContentView(R.layout.activity_done);
        получить_вопросы();
        подситать_ответы();
    }

    private void получить_вопросы() {
        boolean имеются_файлы_JSON = false;
        {
            ArrayList<Question> al = new ArrayList<>();
            for (String файл : file) {
                if (файл.contains(".json")) {
                    al.add(Question.fromJSON(fe.getFile(файл)));
                    имеются_файлы_JSON = true;
                }
            }
            вопросы = al.toArray(new Question[al.size()]);
        }
        if (!имеются_файлы_JSON) {
            finish();
        }
    }

    private void подситать_ответы() {
    }
}
