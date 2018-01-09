package com.example.gecko.ereader;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by GecKo on 2017/12/6.
 */

public class Show extends AppCompatActivity {
    private TextView info1;
    private TextView info2;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info);
        Bundle b=getIntent().getExtras();
        info1 = findViewById(R.id.info1);
        info2 = findViewById(R.id.info2);
        String ming = b.getString("name");
        String ming1 =b.getString("name1");
        info1.setText(ming);
        info2.setText(ming1);
    }
}