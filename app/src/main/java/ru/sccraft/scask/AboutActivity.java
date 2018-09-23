package ru.sccraft.scask;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    int versionCode = 0;
    String versionName;
    TextView vc, vn;
    private AlertDialog.Builder ad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setupActionBar();
        setTitle(getString(R.string.about));
        try {
            versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        try {
            versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        vc = (TextView) findViewById(R.id.aboutVersionCode);
        vn = (TextView) findViewById(R.id.aboutVN);
        vc.setText("" + versionCode);
        vn.setText(versionName);
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public void rateAPP(View view) {
        showDialog(1);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == 1) {
            String title = getString(R.string.rateApp);
            String message = getString(R.string.goToGooglePlayQestion);
            String button1String = getString(R.string.yes);
            String button2String = getString(R.string.no);

            ad = new AlertDialog.Builder(AboutActivity.this);
            ad.setTitle(title);  // заголовок
            ad.setMessage(message); // сообщение
            ad.setPositiveButton(button1String, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int arg1) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("market://details?id=ru.sccraft.scask"));
                        startActivity(intent);
                    }catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });
            ad.setNegativeButton(button2String, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int arg1) {
                }
            });
            ad.setCancelable(true);
            ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
                public void onCancel(DialogInterface dialog) {
                }
            });
            return ad.create();
        }
        return super.onCreateDialog(id);
    }

    public void viewLicense(View view) {
        Uri ссылка_на_лицензию = Uri.parse("https://github.com/sashaqwert/ScAsk/blob/master/LICENSE");
        Intent открыть_лицензию_в_браузере = new Intent(Intent.ACTION_VIEW, ссылка_на_лицензию);
        startActivity(открыть_лицензию_в_браузере);
    }

    public void viewSourceCode(View view) {
        Uri ссылка_на_исходный_код = Uri.parse("https://github.com/sashaqwert/ScAsk");
        Intent открыть_исходный_код_в_браузере = new Intent(Intent.ACTION_VIEW, ссылка_на_исходный_код);
        startActivity(открыть_исходный_код_в_браузере);
    }

    public void privacyPolicy(View view) {
        Uri ссылка_на_политику_конфиденциальности = Uri.parse("http://sccraft.ru/android-app/scask/privacy/");
        Intent открыть_политику_конфиденциальности_в_браузере = new Intent(Intent.ACTION_VIEW, ссылка_на_политику_конфиденциальности);
        startActivity(открыть_политику_конфиденциальности_в_браузере);
    }
}
