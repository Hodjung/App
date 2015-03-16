package com.example.kridsadath.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KridsadaTh on 25/2/2558.
 */
public class place_page extends Activity {
    List<String> group;
    ListView placeView;
    List<place> listPlace;
    ArrayAdapter<String> adapterPlace;
    String log="checkLog";
    Database db;
    public place_page(){}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_floor_page);
        TextView name_Place = (TextView)findViewById(R.id.name_floor);
        name_Place.setText("Place");
        Button addPlace = (Button)findViewById(R.id.addRoom);
        addPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent=new Intent(place_page.this,initial_page.class);
                startActivity(myIntent);
                finish();
            }
        });
        Button back = (Button)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent=new Intent(place_page.this,MainActivity.class);
                startActivity(myIntent);
                finish();
            }
        });
        db=new Database(this);
        placeView = (ListView)findViewById(R.id.listView);
        initPlace();
        initPagePlace();
    }
    protected void initPlace() {
        Log.d("initPlace", log);
        group = new ArrayList<String>();
        listPlace=db.getAllPlaces();
        if (listPlace==null){
            Log.d("listPlace==null",log);
            group.add("Empty");
        }
        else {
            Log.d("listPlace not null",log);
            for (place pl:listPlace){
                Log.d(pl.getName(), log);
                group.add(pl.getName());
            }
        }
    }
    protected void initPagePlace() {
        Log.d("Add Place Page", log);
        adapterPlace = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,android.R.id.text1,group);
        placeView.setAdapter(adapterPlace);
        placeView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (listPlace.size()>0){
                    Intent myIntent = new Intent(place_page.this,create_page.class);
                    myIntent.putExtra("placeId",String.valueOf(listPlace.get(position).getId()));
                    startActivity(myIntent);
                    finish();
                }
                //swap_page(CREATING_PAGE);
            }
        });
        Log.d("Finish", log);
    }
}
