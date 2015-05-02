package com.example.kridsadath.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

/**
 * Created by KridsadaTh on 22/2/2558.
 */
public class initial_page extends Activity {
    int buildingId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.initial_page);

        Button btn_create=(Button)findViewById(R.id.place_create);
        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText txt = (EditText)findViewById(R.id.place_name);
                EditText txt2 = (EditText)findViewById(R.id.number_floor);
                if (!txt.getText().toString().isEmpty()&&!txt2.getText().toString().isEmpty()) {
                    add();
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
    public void add(){
        //
        EditText txt = (EditText)findViewById(R.id.place_name);
        EditText txt2 = (EditText)findViewById(R.id.number_floor);
        String name =txt.getText().toString();
        int n_floor= Integer.parseInt(txt2.getText().toString());
        newBuilding newBuilding=new newBuilding(initial_page.this,name,0,0);
        boolean isError=false;
        try {
            String check;
            check=newBuilding.execute().get();
            if (!check.equals("OK"))isError=true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        buildingId=newBuilding.getId();
        try {
            String check;
            check=new newFloor(initial_page.this,n_floor,buildingId).execute().get();
            if (!check.equals("OK"))isError=true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        try {
            String check;
            check=new LoadAll(initial_page.this).execute().get();
            if (!check.equals("OK"))isError=true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        Log.d(name+" "+n_floor,"checkLog");
        if (!isError) {
            Intent myIntent = new Intent(initial_page.this, create_page.class);
            myIntent.putExtra("buildingId", String.valueOf(buildingId));
            startActivity(myIntent);
            finish();
        }
        else Toast.makeText(initial_page.this,"No internet connection",Toast.LENGTH_SHORT);
    }
}
