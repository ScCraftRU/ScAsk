package ru.sccraft.scask;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class DoneActivity extends AppCompatActivity {

    private String[] file;
    private Fe fe;
    private Question[] вопросы;
    private int верно;
    private int неверно;
    private int пропущено;

    TextView базовая_статистика;
    ListView подробная_статистика;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        file = fileList();
        fe = new Fe(this);
        setContentView(R.layout.activity_done);
        базовая_статистика = findViewById(R.id.done_baseStat);
        подробная_статистика = findViewById(R.id.done_lw);
        получить_вопросы();
        подситать_ответы();
        вывести_статистику();
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
        верно = 0;
        неверно = 0;
        пропущено = 0;

        for (Question вопрос : вопросы) {
            if (!вопрос.решено()) {
                пропущено++;
                continue;
            }
            if (вопрос.проверить_ответ()) {
                верно++;
            } else {
                неверно++;
            }
        }
    }

    private void вывести_статистику() {
        базовая_статистика.setText(getString(R.string.done_right) + верно + "\n" + getString(R.string.done_incorrect) + неверно + "\n" + getString(R.string.done_escape) + пропущено);
        QuestionAdapter адаптер = new QuestionAdapter(this, вопросы);
        подробная_статистика.setAdapter(адаптер);
        подробная_статистика.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                String ответ = вопросы[i].получить_ответ().toString();
                ответ = ответ.replace("true", getString(R.string.yes));
                ответ = ответ.replace("false", getString(R.string.no));
                Toast.makeText(getApplicationContext(), ответ, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }
}
