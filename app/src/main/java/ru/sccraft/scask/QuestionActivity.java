package ru.sccraft.scask;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import static android.text.InputType.TYPE_CLASS_NUMBER;
import static android.text.InputType.TYPE_CLASS_TEXT;

public class QuestionActivity extends AppCompatActivity {

    Question вопрос;
    LinearLayout логический_ответ;
    EditText ответ;
    Button сохранить_ответ;
    TextView вывод_вопроса;
    private Fe fe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        fe = new Fe(this);
        вопрос = Question.fromJSON(getIntent().getStringExtra("question"));
        логический_ответ = findViewById(R.id.questionActivity_yesNO);
        ответ = findViewById(R.id.questionActivity_answer);
        сохранить_ответ = findViewById(R.id.questionActivity_saveAnswer);
        вывод_вопроса = findViewById(R.id.questionActivity_question);
        вывод_вопроса.setText(вопрос.toString());

        switch (вопрос.получить_тип()) {
            case 0:
                ответ.setVisibility(View.GONE);
                сохранить_ответ.setVisibility(View.GONE);
                логический_ответ.setVisibility(View.VISIBLE);
                break;
            case 1:
                логический_ответ.setVisibility(View.GONE);
                ответ.setVisibility(View.VISIBLE);
                сохранить_ответ.setVisibility(View.VISIBLE);
                ответ.setInputType(TYPE_CLASS_NUMBER);
                break;
            case 2:
                логический_ответ.setVisibility(View.GONE);
                ответ.setVisibility(View.VISIBLE);
                сохранить_ответ.setVisibility(View.VISIBLE);
                ответ.setInputType(TYPE_CLASS_TEXT);
                break;
        }

        // Load an ad into the AdMob banner view.
        AdView adView = findViewById(R.id.adView);
        if (!fe.getFile("scask-ads").contains("1")) {
            adView.setVisibility(View.VISIBLE);
            AdRequest adRequest = new AdRequest.Builder().setRequestAgent("android_studio:ad_template").build();
            adView.loadAd(adRequest);
        } else {
            adView.setVisibility(View.GONE);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_question, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_export:
                экспортировать();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void clickYES(View view) {
        вопрос.ответить(true);
        fe.saveFile(вопрос.вопрос + ".json", вопрос.toJSON());
        finish();
    }

    public void clickNO(View view) {
        вопрос.ответить(false);
        fe.saveFile(вопрос.вопрос + ".json", вопрос.toJSON());
        finish();
    }

    public void saveAnswer(View view) {
        String ответ = this.ответ.getText().toString();
        switch (вопрос.получить_тип()) {
            case 1:
                try {
                    int i = Integer.parseInt(ответ);
                    вопрос.ответить(i);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    вопрос.ответить(-12345);
                }
                break;
            case 2:
                вопрос.ответить(ответ);
                break;
        }
        fe.saveFile(вопрос.вопрос + ".json", вопрос.toJSON());
        finish();
    }

    private void экспортировать() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, вопрос.toJSON());
        sendIntent.setType("text/plain");
        if (sendIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(sendIntent);
        }
    }
}
