package ru.sccraft.scask;

import android.content.DialogInterface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

import ru.sccraft.scask.util.IabHelper;
import ru.sccraft.scask.util.IabResult;
import ru.sccraft.scask.util.Inventory;
import ru.sccraft.scask.util.Purchase;

public class DisableADsActivity extends AppCompatActivity {

    private static final String LOG_TAG = "DisableADsActivity";
    Fe fe;
    Button buyButton;
    IabHelper mHelper;
    boolean adsDisabled = false;
    private boolean показывать_сообщение;
    private String TAG = "DisableADsActivity";
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        @Override
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            if (result.isFailure()) {
                Log.e(TAG, "Ошибка покупки: " + result);

                AlertDialog.Builder ad = new AlertDialog.Builder(DisableADsActivity.this);
                ad.setTitle("ERROR");  // заголовок
                ad.setMessage("" +result);
                ad.setPositiveButton(getString(R.string.close), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                ad.setCancelable(true);
                ad.show();
                return;
            }
            else if (purchase.getSku().equals("ru.sccraft.scask.disableads")) {
                adsDisabled = true;
                // consume the gas and update the UI
                adsDisabled = true;
                fe.saveFile("scask-ads", "1");
                Log.i(LOG_TAG, "Реклама отключена!");
                Toast.makeText(getApplicationContext(), getString(R.string.adsDisabled), Toast.LENGTH_LONG).show();
            }
        }
    };

    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {

            if (result.isFailure()) {
                // handle error here
                Log.e(LOG_TAG, "" + result);
            } else {
                String цена = inventory.getSkuDetails("ru.sccraft.scask.disableads").getPrice();
                if (!показывать_сообщение) buyButton.setText(buyButton.getText().toString() + " (" + цена + ")");
                // does the user have the premium upgrade?
                adsDisabled = inventory.hasPurchase("ru.sccraft.scask.disableads");
                // update UI accordingly
                if (adsDisabled) {
                    fe.saveFile("scask-ads", "1");
                    Toast.makeText(getApplicationContext(), getString(R.string.adsDisabled), Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    if (показывать_сообщение) Toast.makeText(getApplicationContext(), getString(R.string.notBuyed), Toast.LENGTH_SHORT).show();
                    показывать_сообщение = true;
                }
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disable_ads);
        setTitle(getString(R.string.disableADs));
        setupActionBar();
        buyButton = (Button) findViewById(R.id.button_buy);
        fe = new Fe(this);
        String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqIf1bvAgEBiGYQ4+U/ZQGxrToHeedXLqsCNr1kfhH2Ho+IpcL6B8sfdc8o0X4co4g0ZrTikokmZuWRLGiA29D+iQQbLPWOUTz26pCZAZRo149Fr2gH1wNi02GA/In460/i7EArQ6COsh1EDuBFKoArVTg5URJMAgGtn3tVUK6UNVdn31hrJ6taTTqz1IAV83zP08X5IB8iyiJofDF1Go0wZBy20YRPf+jV0jRwkvnadvhf8DJZ0fpheQGnQnub3VFt9bddfm6z0UVnEnDCBUeAwbqo5YL/GCwV4UmVGnxCw38h9hyw+rMRXllLysF9Qp82/m603kT4BZH3HDeKpPowIDAQAB";
        показывать_сообщение = false;

        // compute your public key and store it in base64EncodedPublicKey
        mHelper = new IabHelper(this, base64EncodedPublicKey);
        mHelper.enableDebugLogging(true, "In-app-billing");
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    // Oh no, there was a problem.
                    Log.e(TAG, "Problem setting up In-app Billing: " + result);

                    AlertDialog.Builder ad = new AlertDialog.Builder(DisableADsActivity.this);
                    ad.setTitle("ERROR");  // заголовок
                    ad.setMessage("" + result);
                    ad.setPositiveButton(getString(R.string.close), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    ad.setCancelable(true);
                    ad.show();
                }
                // Hooray, IAB is fully set up!
                восстановить();
            }
        });
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
        if (mHelper != null) try {
            mHelper.dispose();
        } catch (IabHelper.IabAsyncInProgressException | IllegalArgumentException e) {
            e.printStackTrace();
        }
        mHelper = null;
    }

    private void купить() {
        try {
            mHelper.launchPurchaseFlow(this, "ru.sccraft.scask.disableads", 10001, mPurchaseFinishedListener, "qwertyuiop");
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
            Toast.makeText(getApplication(), R.string.unavableInThisMoment, Toast.LENGTH_LONG).show();
        } catch (IllegalStateException ex) {
            ex.printStackTrace();
        }
    }

    private void восстановить() {
        ArrayList<String> товары = new ArrayList<>(1);
        товары.add("ru.sccraft.scask.disableads");
        try {
            mHelper.queryInventoryAsync(true,товары, null, mGotInventoryListener);
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
            Toast.makeText(getApplication(), R.string.unavableInThisMoment, Toast.LENGTH_LONG).show();
        } catch (IllegalStateException ex) {
            ex.printStackTrace();
        }
    }
    public void buy(View view) {
        купить();
    }

    public void restore(View view) {
        восстановить();
    }
}
