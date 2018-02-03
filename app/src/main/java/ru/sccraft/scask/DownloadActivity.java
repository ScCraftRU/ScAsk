package ru.sccraft.scask;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
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
        SharedPreferences настройки;

        Get(DownloadActivity activity) {
            a = activity;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            настройки = PreferenceManager.getDefaultSharedPreferences(a);
            String server = настройки.getString("settings_sync_server", "http://sccraft.ru/android-app/scask/questions");
            String[] JSON = NetGet.getMultiLine(server + ".scask");
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
