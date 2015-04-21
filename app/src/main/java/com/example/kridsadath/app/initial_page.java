package com.example.kridsadath.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.concurrent.ExecutionException;

/**
 * Created by KridsadaTh on 22/2/2558.
 */
public class initial_page extends Activity {
    private Database db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.initial_page);
        db=new Database(this);
        Button btn_create=(Button)findViewById(R.id.place_create);
        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText txt = (EditText)findViewById(R.id.place_name);
                EditText txt2 = (EditText)findViewById(R.id.number_floor);
                if (!txt.getText().toString().isEmpty()){
                    String name =txt.getText().toString();
                    int n_floor= Integer.parseInt(txt2.getText().toString());
                    try {
                        new newBuilding(initial_page.this,name,0,0).execute().get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    try {
                        new LoadAll(initial_page.this).execute().get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    try {
                        new newFloor(initial_page.this,n_floor,db.getBuildingId(name)).execute().get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    try {
                        new LoadAll(initial_page.this).execute().get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    int id=db.getBuildingId(name);
                    Log.d(name+" "+n_floor,"checkLog");
                    Intent myIntent=new Intent(initial_page.this,create_page.class);
                    myIntent.putExtra("buildingId",String.valueOf(id));
                    startActivity(myIntent);
                    finish();
                }
            }
        });
        Button btn_cancel=(Button)findViewById(R.id.cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back=new Intent(initial_page.this,place_page.class);
                startActivity(back);
                finish();
            }
        });
    }
}
