package ru.sccraft.scask;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "ScAsk/MainActivity";
    Question[] q;
    ListView lw;
    String[] file;
    private Fe fe;
    private int решено;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddQuestionActivity.class);
                startActivity(intent);
            }
        });
        fe = new Fe(this);
        lw = findViewById(R.id.listView_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        file = fileList();
        обновить_список_вопросов();
    }

    private void обновить_список_вопросов() {
        boolean имеются_файлы_JSON = false;
        {
            ArrayList<Question> al = new ArrayList<>();
            for (String файл : file) {
                if (файл.contains(".json")) {
                    al.add(Question.fromJSON(fe.getFile(файл)));
                    имеются_файлы_JSON = true;
                }
            }
            q = al.toArray(new Question[al.size()]);
        }
        if (!имеются_файлы_JSON) {
            //Нет вопросов
        } else {
            String[] s = new String[q.length];
            for (int i = 0; i < q.length; i++) {
                s[i] = q[i].вопрос;
            }
            //ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, s);
            QuestionAdapter adapter = new QuestionAdapter(this, q);
            lw.setAdapter(adapter);
            lw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(MainActivity.this, QuestionActivity.class);
                    intent.putExtra("question", q[i].toJSON());
                    startActivity(intent);
                }
            });
        }
        решено = 0;
        for (Question вопрос : q) {
            if (вопрос.проверить_ответ()) {
                решено++;
            }
        }
        setTitle(getString(R.string.mainActivity_decided) + " " + решено + " " + getString(R.string.mainActivity_decided_of) + " " + q.length);
    }

    private void экспортировать_файлы() {
        class Поток extends AsyncTask<Void, Void, Intent> {

            @Override
            protected Intent doInBackground(Void... params) {
                Log.i(LOG_TAG, "запущен экспорт файлов");
                Fe fe = new Fe(MainActivity.this);
                String tittle = "This data ONLY for ScAsk server!\nMore information on http://sccraft.ru/index.php/guide/12-scask/6-howtocreateserver\n";
                String разделитель = "=================================================================\n";
                String data = tittle + разделитель;
                for (String aFile : file) {
                    if (!(aFile.equals("instant-run"))) {
                        data = data + aFile + "\n" + fe.getFile(aFile) + "\n" + разделитель;
                    }
                }
                data = data + "END OF SERVER DATA";

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, data);
                sendIntent.setType("text/plain");
                Log.i(LOG_TAG, "Экспорт файлов завершён! Пользователю необходимо выбрать приложение, в которое будет проведён экспорт.");
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

    private void завершить() {
        Intent intent = new Intent(MainActivity.this, DoneActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_settings:
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_export:
                экспортировать_файлы();
                return true;
            case R.id.action_done:
                завершить();
        }

        return super.onOptionsItemSelected(item);
    }
}
