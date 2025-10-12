package ru.sccraft.scask;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
public class DisableADsActivity extends AppCompatActivity {

    private static final String LOG_TAG = "DisableADsActivity";
    Fe fe;
    Button buyButton;

    private String интендификатор_товара = "ru.sccraft.scask.disableads";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disable_ads);
        setTitle(getString(R.string.disableADs));
        setupActionBar();
        buyButton = findViewById(R.id.button_buy);
        fe = new Fe(this);
    }

    public void купить(String skuId) {
        Toast.makeText(getApplicationContext(), "Now it free! Click correct button :)", Toast.LENGTH_LONG).show();
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void восстановить() {
        //Зачем покупать, если можно с root файл создать... (или переименовать adid)
        fe.saveFile("scask-ads", "1");
        Log.i(LOG_TAG, "Реклама отключена!");
        Toast.makeText(getApplicationContext(), R.string.adsDisabled, Toast.LENGTH_SHORT).show();
        finish();
    }
    public void buy(View view) {
        купить(интендификатор_товара);
    }

    public void getItFree(View view) {
        восстановить();
    }
}