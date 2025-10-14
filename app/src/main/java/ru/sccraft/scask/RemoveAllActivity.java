package ru.sccraft.scask;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.PersistableBundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class RemoveAllActivity extends AppCompatActivity {

    private Removator r;
    private AlertDialog.Builder ad;
    boolean asked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_remove_all);
        setTitle(getString(R.string.pleaseWait));
        if (r == null) {
            r = new Removator(this);
        }else{
            r.link(this);
        }

        String title = getString(R.string.removeAllDialogTitle);
        String message = getString(R.string.removeAllDialogContent);
        String button1String = getString(R.string.yes);
        String button2String = getString(R.string.no);

        ad = new AlertDialog.Builder(RemoveAllActivity.this);
        ad.setTitle(title);  // заголовок
        ad.setMessage(message); // сообщение
        ad.setPositiveButton(button1String, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                r.execute();
            }
        });
        ad.setNegativeButton(button2String, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                finish();
            }
        });
        ad.setCancelable(true);
        ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!asked) ad.show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putBoolean("ASKED", asked);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        asked = savedInstanceState.getBoolean("ASKED");
    }

    @Override
    public Object getLastCustomNonConfigurationInstance() {
        return r;
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    private class Removator extends AsyncTask<Void, Void, Void> {

        RemoveAllActivity a;
        String[] file;

        Removator(RemoveAllActivity a) {
            this.a = a;
            file = fileList();
        }

        void link(RemoveAllActivity activity) {
            a = activity;
        }

        @Override
        protected Void doInBackground(Void... params) {
            for (String файл : file) {
                if (!((файл.equals("instant-run"))||(файл.equals("scask-ads")))) {
                    deleteFile(файл);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(getApplicationContext(), a.getString(R.string.done), Toast.LENGTH_SHORT).show();
            a.finish();
        }
    }
}
