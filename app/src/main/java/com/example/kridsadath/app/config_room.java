package com.example.kridsadath.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by KridsadaTh on 24/2/2558.
 */
public class config_room extends Activity {
    int buildingId,floorId;
    floor currentFloor;
    room currentRoom;
    public config_room(){}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Database db;
        db=new Database(this);
        setContentView(R.layout.config_room);
        Bundle intent=getIntent().getExtras();
        floorId=Integer.parseInt(intent.getString("floorId"));
        buildingId=Integer.parseInt(intent.getString("buildingId"));
        currentFloor=db.getFloor(floorId);
        TextView head = (TextView)findViewById(R.id.name_floor);
        head.setText(currentFloor.getName());
        CheckBox type = (CheckBox)findViewById(R.id.openOrClose);
        type.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                TextView roomName = (TextView) findViewById(R.id.roomName);
                if (isChecked) {
                    roomName.setVisibility(View.VISIBLE);
                } else {
                    roomName.setVisibility(View.INVISIBLE);
                }
            }
        });
        Button next = (Button)findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText heightFloor=(EditText)findViewById(R.id.floorHeight);
                EditText width=(EditText)findViewById(R.id.width);
                EditText height=(EditText)findViewById(R.id.height);
                EditText range=(EditText)findViewById(R.id.range);
                if (!heightFloor.getText().toString().isEmpty()&&
                        !width.getText().toString().isEmpty()&&
                        !height.getText().toString().isEmpty()&&
                        !range.getText().toString().isEmpty()) {
                    add();
                }
            }
        });
        Button back = (Button)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent=new Intent(config_room.this,manage_floor.class);
                myIntent.putExtra("buildingId",String.valueOf(buildingId));
                myIntent.putExtra("floorId",String.valueOf(floorId));
                startActivity(myIntent);
                finish();
            }
        });
    }
    public void add(){
        boolean isError=false;
        CheckBox checkBox=(CheckBox)findViewById(R.id.openOrClose);
        EditText name=(EditText)findViewById(R.id.roomName);
        EditText heightFloor=(EditText)findViewById(R.id.floorHeight);
        EditText width=(EditText)findViewById(R.id.width);
        EditText height=(EditText)findViewById(R.id.height);
        EditText range=(EditText)findViewById(R.id.range);
        int isclose;
        String nameR;
        if (!checkBox.isChecked()) {
            nameR="Path";
            isclose = 0;
        }
        else {
            nameR=name.getText().toString();
            isclose=1;
        }
        currentRoom = new room(nameR,"Coming Soon", currentFloor.getId(), String.valueOf(isclose)
                , Float.parseFloat(heightFloor.getText().toString()), Integer.parseInt(width.getText().toString())
                , Integer.parseInt(height.getText().toString()), Integer.parseInt(range.getText().toString()));
        newRoom newRoom=new newRoom(config_room.this,currentRoom);
        try {
            String check;
            check=newRoom.execute().get();
            if (!check.equals("OK"))isError=true;
        } catch (InterruptedException e) {
            Log.e("error","Interrupted");
            e.printStackTrace();
        } catch (ExecutionException e) {
            Log.e("error","Execution");
            e.printStackTrace();
        }
        try {
            new LoadAll(config_room.this).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if (!isError) {
            Intent myIntent = new Intent(config_room.this, record_page.class);
            myIntent.putExtra("buildingId", String.valueOf(buildingId));
            myIntent.putExtra("floorId", String.valueOf(floorId));
            myIntent.putExtra("roomId", String.valueOf(newRoom.getId()));
            Log.d("buildingId=" + buildingId + " floorId=" + floorId + " roomId=" + currentRoom.getId(), "checkLog");
            startActivity(myIntent);
            finish();
        }
    }
}
