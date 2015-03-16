package com.example.kridsadath.app;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
    SQLiteDatabase mDb;
    public MainActivity(){}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_page);
        Button create=(Button)findViewById(R.id.create);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent=new Intent(MainActivity.this,initial_page.class);
                startActivity(myIntent);
                finish();
            }
        });
        Button update=(Button)findViewById(R.id.update);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent=new Intent(MainActivity.this,place_page.class);
                startActivity(myIntent);
                finish();
            }
        });
        /*Database db;
        db = new Database(this);
        mDb=db.getWritableDatabase();
        db.onUpgrade(mDb,1,1);*/
    }
}