package com.jadimo.testapp;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends Activity {

    NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;
    IntentFilter writeTagFilters[];
    boolean writeMode;
    Tag myTag;
    Context context;
    public String android_id;

    TextView tvNFCContent;
    TextView ID;
    TextView ID2;
    Button btnWrite;
    ProgressBar scan;
    ImageView Status;

    public void Send(View v) {
        try {
            myTag = getIntent().getParcelableExtra(NfcAdapter.EXTRA_TAG);
            write(myTag);
            Toast.makeText(context, R.string.WRITE_SUCCESS, Toast.LENGTH_LONG).show();
            Status.setImageResource(R.drawable.success);
            tvNFCContent.setText(R.string.Success);
            scan.setIndeterminate(false);
            scan.setProgress(100);
        } catch (IOException e) {
            Toast.makeText(context, R.string.WRITE_ERROR, Toast.LENGTH_LONG ).show();
            e.printStackTrace();
            tvNFCContent.setText(R.string.Error);
            Status.setImageResource(R.drawable.error);
            scan.setIndeterminate(false);
            scan.setProgress(0);
        } catch (FormatException e) {
            Toast.makeText(context, R.string.WRITE_ERROR, Toast.LENGTH_LONG ).show();
            e.printStackTrace();
            tvNFCContent.setText(R.string.Error);
            Status.setImageResource(R.drawable.error);
            scan.setIndeterminate(false);
            scan.setProgress(0);
        } catch (NullPointerException e) {
            Toast.makeText(context, R.string.NO_TAG, Toast.LENGTH_LONG).show();
            e.printStackTrace();
            tvNFCContent.setText(R.string.Tag_error);
            Status.setImageResource(R.drawable.wait);
            scan.setIndeterminate(false);
            scan.setProgress(0);
        }
    final Handler delay = new Handler();
        delay.postDelayed(new Runnable() {
        @Override
        public void run() {
            scan.setIndeterminate(true);
            scan.setProgress(50);
            tvNFCContent.setText(R.string.Waiting);
        }
    },3000);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        tvNFCContent = findViewById(R.id.nfc_contents);
        btnWrite = findViewById(R.id.button);
        ID = findViewById(R.id.IDdisplay);
        ID2 = findViewById(R.id.ID2);
        scan = findViewById(R.id.scanning);
        Status = findViewById(R.id.info);

        android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        ID2.setText(R.string.UniqueID);
        ID.setText(android_id);
        tvNFCContent.setText(R.string.Waiting);

        nfcAdapter = NfcAdapter.getDefaultAdapter(MainActivity.this);
        if (nfcAdapter == null) {
            tvNFCContent.setText(R.string.NO_NFC);
            scan.setIndeterminate(false);
            scan.setProgress(0);
            Status.setImageResource(R.drawable.error);
        }

        if (!nfcAdapter.isEnabled()){
            tvNFCContent.setText(R.string.NFC_OFF);
        }
        else {
            readFromIntent(getIntent());
        }

        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        IntentFilter techDetected = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        writeTagFilters = new IntentFilter[] { tagDetected, ndefDetected, techDetected };
    }

    private void readFromIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

            try {
                myTag = getIntent().getParcelableExtra(NfcAdapter.EXTRA_TAG);
                write(myTag);
                Toast.makeText(context, R.string.WRITE_SUCCESS, Toast.LENGTH_LONG).show();
                Status.setImageResource(R.drawable.success);
                tvNFCContent.setText(R.string.Success);
                scan.setIndeterminate(false);
                scan.setProgress(100);
            } catch (IOException e) {
                Toast.makeText(context, R.string.WRITE_ERROR, Toast.LENGTH_LONG ).show();
                e.printStackTrace();
                tvNFCContent.setText(R.string.Error);
                Status.setImageResource(R.drawable.error);
                scan.setIndeterminate(false);
                scan.setProgress(0);
            } catch (FormatException e) {
                Toast.makeText(context, R.string.WRITE_ERROR, Toast.LENGTH_LONG ).show();
                e.printStackTrace();
                tvNFCContent.setText(R.string.Error);
                Status.setImageResource(R.drawable.error);
                scan.setIndeterminate(false);
                scan.setProgress(0);
            } catch (NullPointerException e) {
                Toast.makeText(context, R.string.NO_TAG, Toast.LENGTH_LONG).show();
                e.printStackTrace();
                tvNFCContent.setText(R.string.Tag_error);
                Status.setImageResource(R.drawable.error);
                scan.setIndeterminate(false);
                scan.setProgress(0);
            }
        }
        final Handler delay = new Handler();
        delay.postDelayed(new Runnable() {
            @Override
            public void run() {
                scan.setIndeterminate(true);
                scan.setProgress(50);
                tvNFCContent.setText(R.string.Waiting);
                Status.setImageResource(R.drawable.wait);
            }
        },2000);
    }

    private void write(Tag tag) throws IOException, FormatException {
        String record = "news:" + android_id + "\0";
        NdefRecord recordNFC;
        recordNFC = NdefRecord.createUri(record);
        NdefMessage message = new NdefMessage(recordNFC);
        Ndef ndef = Ndef.get(tag);
        ndef.connect();
        ndef.writeNdefMessage(message);
        ndef.close();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        readFromIntent(intent);
    }

    @Override
    public void onPause(){
        super.onPause();
        WriteModeOff();
    }

    @Override
    public void onResume(){
        super.onResume();
        WriteModeOn();
    }

    private void WriteModeOn(){
        writeMode = true;
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, writeTagFilters, null);
    }

    private void WriteModeOff(){
        writeMode = false;
        nfcAdapter.disableForegroundDispatch(this);
    }

    public void About (View view){
            Intent i = new Intent(this, DisplayAboutActivity.class);
            startActivity(i);
    }
}



