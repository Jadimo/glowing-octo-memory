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
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends Activity {

    public static final String NO_TAG = "Brak Tagu NFC w zasięgu!";
    public static final String WRITE_SUCCESS = "Operacja zakończona sukcesem!";
    public static final String WRITE_ERROR = "Napotkano problem, spróbuj ponownie.";
    NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;
    IntentFilter writeTagFilters[];
    boolean writeMode;
    Tag myTag;
    Context context;
    public String android_id;

    TextView tvNFCContent;
    TextView ID;
    Button btnWrite;

    public void Send(View v) {
        try {
            if(myTag ==null) {
                Toast.makeText(context, NO_TAG, Toast.LENGTH_SHORT).show();
            } else {
                write(myTag);
                Toast.makeText(context, WRITE_SUCCESS, Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Toast.makeText(context, WRITE_ERROR, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (FormatException e) {
            Toast.makeText(context, WRITE_ERROR, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        tvNFCContent = findViewById(R.id.nfc_contents);
        btnWrite = findViewById(R.id.button);
        ID = findViewById(R.id.IDdisplay);

        android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        ID.setText("Unikalny identyfikator:\n" + android_id);
        tvNFCContent.setText("Oczekiwanie na zbliżenie...");

        nfcAdapter = NfcAdapter.getDefaultAdapter(MainActivity.this);
        if (nfcAdapter == null) {
            Toast.makeText(this, "Brak wymaganego NFC.", Toast.LENGTH_LONG).show();
            finish();
        }



        readFromIntent(getIntent());

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

            tvNFCContent.setText("Tag w pobliżu ! Gotowość do działania");

            try {
                myTag = getIntent().getParcelableExtra(NfcAdapter.EXTRA_TAG);
                write(myTag);
                Toast.makeText(context, WRITE_SUCCESS, Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Toast.makeText(context, WRITE_ERROR, Toast.LENGTH_LONG ).show();
                e.printStackTrace();
            } catch (FormatException e) {
                Toast.makeText(context, WRITE_ERROR, Toast.LENGTH_LONG ).show();
                e.printStackTrace();
            } catch (NullPointerException e) {
                Toast.makeText(context, NO_TAG, Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
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
        if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())){
            myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        }
        else {
            tvNFCContent.setText("Zbliż urządzenie to identyfikatora !");
        }
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
}

