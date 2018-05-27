package com.jadimo.nfcib;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class DisplayAboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_about);
        TextView abt = findViewById(R.id.AboutText);
        TextView authors = findViewById(R.id.AboutAuthors);
        abt.setText(R.string.About);
        authors.setText(R.string.Authors);
    }
}
