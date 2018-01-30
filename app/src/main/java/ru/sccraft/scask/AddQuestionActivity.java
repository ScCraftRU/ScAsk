package ru.sccraft.scask;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import static android.text.InputType.TYPE_CLASS_NUMBER;
import static android.text.InputType.TYPE_CLASS_TEXT;

public class AddQuestionActivity extends AppCompatActivity {

    EditText вопрос, ответ;
    RadioGroup тип;
    SwitchCompat логический_ответ;
    private boolean логическое_значение = false;
    private byte тип_вопроса = 0;
    private Fe fe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);
        fe = new Fe(this);
        вопрос = findViewById(R.id.addQuestion_question);
        ответ = findViewById(R.id.addQuestion_answer);
        логический_ответ = findViewById(R.id.addQuestin_boolAnswer);
        тип = findViewById(R.id.addQuestion_tList);
        тип.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.addQuestion_t1) {
                    ответ.setText("");
                    ответ.setVisibility(View.GONE);
                    логический_ответ.setVisibility(View.VISIBLE);
                    тип_вопроса = 0;
                } else if (i == R.id.addQuestion_t2) {
                    логический_ответ.setVisibility(View.GONE);
                    ответ.setVisibility(View.VISIBLE);
                    ответ.setText("");
                    ответ.setInputType(TYPE_CLASS_NUMBER);
                    тип_вопроса = 1;
                } else if (i == R.id.addQuestion_t3) {
                    логический_ответ.setVisibility(View.GONE);
                    ответ.setVisibility(View.VISIBLE);
                    ответ.setInputType(TYPE_CLASS_TEXT);
                    тип_вопроса = 2;
                }
            }
        });
        логический_ответ.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                логическое_значение = b;
                if (b) {
                    логический_ответ.setText(R.string.yes);
                } else {
                    логический_ответ.setText(R.string.no);
                }
            }
        });
    }

    private void сохранить() {
        switch (тип_вопроса) {
            case 0:
                fe.saveFile((вопрос.getText().toString()) + ".json", new Question(вопрос.getText().toString(), логическое_значение).toJSON());
                break;
            case 1:
                try {
                    fe.saveFile((вопрос.getText().toString()) + ".json", new Question(вопрос.getText().toString(), Integer.parseInt(ответ.getText().toString())).toJSON());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "ERROR: Incorrect input data", Toast.LENGTH_LONG).show();
                    return;
                }
                break;
            case 2:
                fe.saveFile((вопрос.getText().toString() + ".json"), new Question(вопрос.getText().toString(), ответ.getText().toString()).toJSON());
                break;
        }
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_question, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {
            сохранить();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
