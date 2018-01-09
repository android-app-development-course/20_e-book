package com.example.gecko.ereader;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by GecKo on 2017/12/6.
 */

public class Add extends AppCompatActivity implements View.OnClickListener{
    private EditText etname;
    private EditText etname1;
    private Button add;
    private Button search;
    MyHepler myHepler;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addnew);
        myHepler=new MyHepler(this,"informatione",null,1);
        init();

    }

    private void init(){
        etname = findViewById(R.id.etname);
        etname1 = findViewById(R.id.etname1);
        add = findViewById(R.id.add);
        search = findViewById(R.id.search);
        add.setOnClickListener(this);
        search.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        String name;
        String name1;
        SQLiteDatabase db;
        ContentValues values;
        switch (view.getId()){
            case R.id.add:
                name=etname.getText().toString();
                name1=etname1.getText().toString();
                db=myHepler.getWritableDatabase();
                values=new ContentValues();
                values.put("name",name);
                values.put("name1",name1);
                db.insert("informatione",null,values);
                db.close();
                Intent i =new Intent(Add.this,MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                break;
            case R.id.search:
                Intent i1 =new Intent(Add.this,MainActivity.class);
                i1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i1);
        }

    }



}
