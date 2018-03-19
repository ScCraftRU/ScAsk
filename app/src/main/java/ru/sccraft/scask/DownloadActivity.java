package ru.sccraft.scask;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.gson.JsonSyntaxException;

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
    private class Get extends AsyncTask<Void, Void, Boolean> {

        DownloadActivity a;
        SharedPreferences настройки;

        Get(DownloadActivity activity) {
            a = activity;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            настройки = PreferenceManager.getDefaultSharedPreferences(a);
            String server = настройки.getString("settings_sync_server", a.getString(R.string.sync_server));
            String[] JSON = NetGet.getMultiLine(server + ".scask");
            if (JSON.length == 0) return false;
            try {
                for (String json : JSON) {
                    Question вопрос = Question.fromJSON(json);
                    a.fe.saveFile(вопрос.вопрос + ".json", вопрос.toJSON());
                }
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        void link(DownloadActivity a) {
            this.a = a;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {
                Toast.makeText(getApplicationContext(), R.string.done, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), R.string.error, Toast.LENGTH_SHORT).show();
            }
            a.finish();
        }
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return get;
    }
}
