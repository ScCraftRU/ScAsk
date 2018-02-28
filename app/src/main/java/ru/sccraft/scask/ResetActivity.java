package ru.sccraft.scask;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class ResetActivity extends AppCompatActivity {

    private Fe fe;
    private Поток поток;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);
        fe = new Fe(this);
        поток = (Поток) getLastCustomNonConfigurationInstance();
        if (поток == null) {
            поток = new Поток(this);
            поток.execute();
        } else {
            поток.link(this);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class Поток extends AsyncTask<Void, Void, Void> {

        ResetActivity a;

        Поток(ResetActivity a) {
            this.a = a;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            String[] file = a.fileList();
            for (String файл : file) {
                if (файл.contains(".json")) {
                    Question вопрос = Question.fromJSON(fe.getFile(файл));
                    вопрос.сбросить_ответ();
                    fe.saveFile(вопрос.вопрос + ".json", вопрос.toJSON());
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(getApplicationContext(), R.string.done, Toast.LENGTH_SHORT).show();
            a.finish();
        }

        void link(ResetActivity a) {
            this.a = a;
        }
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return поток;
    }
}
