package ru.sccraft.scask;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static int currentNightMode = 0;
    private static final String LOG_TAG = "ScAsk/MainActivity";
    Question[] q;
    ListView lw;
    String[] file;
    private Fe fe;
    private boolean показывать_диалог = true;
    private MenuItem экспорт, завершить;
    private boolean разрешить_использование_интендификатора = false;

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
        if (savedInstanceState != null) {
            показывать_диалог = savedInstanceState.getBoolean("showDialog");
        }
        String рекламаID = fe.getFile("adid");
        if (рекламаID.contains("1")) {
            разрешить_использование_интендификатора = true;
        } else {
            разрешить_использование_интендификатора = false;
        }
    }

    private void запросить_интендификатор() {
        androidx.appcompat.app.AlertDialog.Builder диалог = new androidx.appcompat.app.AlertDialog.Builder(this);
        диалог.setTitle(R.string.intendificatorReqest)
                .setMessage(R.string.intendificatorMessage)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        fe.saveFile("adid", "1");
                        разрешить_использование_интендификатора = true;
                    }
                })
                .setNegativeButton(R.string.about, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                        startActivity(intent);
                    }
                });
        диалог.show();
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
            if (показывать_диалог) предложить_скачать_вопросы();
            String[] текст_ошибки = getResources().getStringArray(R.array.noQuestions);
            setTitle(текст_ошибки[0]);
            ArrayAdapter<String> адаптер = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, текст_ошибки);
            lw.setAdapter(адаптер);
            lw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                }
            });
            lw.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    return false;
                }
            });
        } else {
            QuestionAdapter adapter = new QuestionAdapter(this, q);
            lw.setAdapter(adapter);
            lw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if(разрешить_использование_интендификатора) {
                        if (!q[i].решено()) {
                            Intent intent = new Intent(MainActivity.this, QuestionActivity.class);
                            intent.putExtra("question", q[i].toJSON());
                            startActivity(intent);
                        }
                    } else {
                        запросить_интендификатор();
                    }
                }
            });
            lw.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    deleteFile(q[position].вопрос + ".json");
                    file = fileList();
                    обновить_список_вопросов();
                    return true;
                }
            });
            int решено = 0;
            for (Question вопрос : q) {
                if (вопрос.проверить_ответ()) {
                    решено++;
                }
            }
            setTitle(getString(R.string.mainActivity_decided) + " " + решено + " " + getString(R.string.mainActivity_decided_of) + " " + q.length);
        }
        обновить_меню();
    }

    private void обновить_меню() {
        if (экспорт == null) return;
        if (q.length == 0) {
            экспорт.setVisible(false);
            завершить.setVisible(false);
        } else {
            экспорт.setVisible(true);
            завершить.setVisible(true);
        }
    }

    private void предложить_скачать_вопросы() {
        AlertDialog.Builder диалог = new AlertDialog.Builder(this);
        диалог.setTitle(R.string.noQuestions)
                .setMessage(R.string.mainActivity_dialogMessage)
                .setCancelable(true)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        показывать_диалог = false;
                        Intent intent = new Intent(MainActivity.this, DownloadActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        показывать_диалог = false;
                    }
                })
                .show();
    }

    private void экспортировать_вопросы() {
        @SuppressLint("StaticFieldLeak")
        class Поток extends AsyncTask<Void, Void, Intent> {

            @Override
            protected Intent doInBackground(Void... params) {
                Log.i(LOG_TAG, "запущен экспорт вопросов");
                Fe fe = new Fe(MainActivity.this);
                String tittle = "This data ONLY for ScAsk server!\nMore information on http://sccraft.ru/index.php/guide/12-scask/6-howtocreateserver\n";
                String разделитель = "=================================================================\n";
                StringBuilder data = new StringBuilder(tittle + разделитель);
                for (String aFile : file) {
                    if (aFile.contains(".json")) {
                        data.append(fe.getFile(aFile)).append("\n");
                    }
                }
                data.append(разделитель);
                data.append("END OF SERVER DATA");

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, data.toString());
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
        экспорт = menu.findItem(R.id.action_export);
        завершить = menu.findItem(R.id.action_done);
        обновить_меню();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_export) {
            экспортировать_вопросы();
            return true;
        } else if (id == R.id.action_done) {
            завершить();
            return true;
        } else if (id == R.id.action_help) {
            Intent intent2 = new Intent(MainActivity.this, HelpActivity.class);
            startActivity(intent2);
            return true;
        } else if (id == R.id.action_about) {
            Intent intent1 = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent1);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putBoolean("showDialog", показывать_диалог);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("showDialog", показывать_диалог);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        currentNightMode = newConfig.uiMode & Configuration.UI_MODE_NIGHT_MASK;

    }
}
