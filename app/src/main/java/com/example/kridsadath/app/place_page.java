package com.example.kridsadath.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.http.NameValuePair;
import org.json.*;

/**
 * Created by KridsadaTh on 25/2/2558.
 */
public class place_page extends Activity {
    List<String> group;
    ListView buildingView;
    String msLog="checkLog";
    List<building> listBuilding;
    ArrayAdapter<String> adapterBuilding;
    String log="checkLog";
    Database db;
    public place_page(){}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_floor_page);
        db = new Database(this);
        TextView name_Place = (TextView)findViewById(R.id.name_floor);
        name_Place.setText("Building");
        Button addBuilding = (Button)findViewById(R.id.addRoom);
        addBuilding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent=new Intent(place_page.this,initial_page.class);
                startActivity(myIntent);
                finish();
            }
        });
        Button refresh = (Button)findViewById(R.id.back);
        refresh.setText("refresh");
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent=new Intent(place_page.this,MainActivity.class);
                startActivity(myIntent);
                finish();
            }
        });
        buildingView = (ListView)findViewById(R.id.listView);
        //Load all from database server
        // Loading products in Background Thread

        initPlace();
        initPagePlace();
    }

    protected void initPlace() {
        Log.d("initPlace", log);
        group = new ArrayList<String>();
        listBuilding=db.getAllBuilding();
        if (listBuilding==null){
            Log.d("listPlace==null",log);
            group.add("Empty");
        }
        else {
            Log.d("listPlace not null",log);
            for (building pl:listBuilding){
                Log.d(pl.getName(), log);
                group.add(pl.getName());
            }
        }
    }
    protected void initPagePlace() {
        Log.d("Add Building Page", log);
        adapterBuilding = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,android.R.id.text1,group);
        buildingView.setAdapter(adapterBuilding);
        buildingView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (listBuilding.size() > 0) {
                    Log.d("select " + listBuilding.get(position).getId(), msLog);
                    Intent myIntent = new Intent(place_page.this, create_page.class);
                    myIntent.putExtra("buildingId", String.valueOf(listBuilding.get(position).getId()));
                    startActivity(myIntent);
                    finish();
                }
                //swap_page(CREATING_PAGE);
            }
        });
        Log.d("Finish", log);
    }
}
