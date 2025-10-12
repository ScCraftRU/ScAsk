package ru.sccraft.scask;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
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

    private void отправить_статистику() {
        class Поток extends AsyncTask<Void, Void, Intent> {

            @Override
            protected Intent doInBackground(Void... params) {
                Fe fe = new Fe(DoneActivity.this);
                String tittle = "User statistics\n";
                String разделитель = "=================================================================\n";
                String data = tittle + разделитель;
                //double процент_решения = (верно / вопросы.length) * 100;
                //data += "DONE: " + процент_решения + "%" + "\n" + разделитель;
                String решено = "";
                for (Question вопрос : вопросы) {
                    if (вопрос.проверить_ответ()) {
                        решено = getString(R.string.yes);
                    } else {
                        решено = getString(R.string.no);
                    }
                    data += вопрос.вопрос + "\n" + getString(R.string.addQuestion_answer) + " " + вопрос.получить_ответ() + "\n" + getString(R.string.done_right) + решено + "\n" + разделитель;
                }
                data = data + "END OF USER DATA";
                data = data.replace("true", getString(R.string.yes));
                data = data.replace("false", getString(R.string.no));

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, data);
                sendIntent.setType("text/plain");
                return sendIntent;
            }

            @Override
            protected void onPostExecute(Intent intent) {
                super.onPostExecute(intent);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        }
        Поток поток = new Поток();
        поток.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_done, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_share) {
            отправить_статистику();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        MainActivity.currentNightMode = newConfig.uiMode & Configuration.UI_MODE_NIGHT_MASK;

    }
}
