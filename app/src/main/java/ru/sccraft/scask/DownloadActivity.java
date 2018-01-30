package ru.sccraft.scask;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class DownloadActivity extends AppCompatActivity {

    private Fe fe;
    private Get get;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        setTitle(R.string.downloadActivity_title);
        fe = new Fe (this);
        get = (Get) getLastCustomNonConfigurationInstance();
        if (get == null) {
            get = new Get(this);
            get.execute();
        } else {
            get.link(this);
        }
    }

    @Override
    public void onBackPressed() {
    }

    @SuppressLint("StaticFieldLeak")
    private class Get extends AsyncTask<Void, Void, Void> {

        DownloadActivity a;

        Get(DownloadActivity activity) {
            a = activity;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            String[] JSON = NetGet.getMultiLine("http://sccraft.ru/android-app/scask/questions.scask");
            for (String json : JSON) {
                Question вопрос = Question.fromJSON(json);
                a.fe.saveFile(вопрос.вопрос + ".json", вопрос.toJSON());
            }
            return null;
        }

        void link(DownloadActivity a) {
            this.a = a;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            a.finish();
        }
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return get;
    }
}
