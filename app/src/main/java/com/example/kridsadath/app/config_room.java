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
    Database db;
    private ProgressDialog pDialog;
    JSONParser jParser = new JSONParser();
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
                TextView roomName = (TextView)findViewById(R.id.roomName);
                if (isChecked){
                    roomName.setVisibility(View.VISIBLE);
                }
                else {
                    roomName.setVisibility(View.INVISIBLE);
                }
            }
        });
        Button next = (Button)findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox checkBox=(CheckBox)findViewById(R.id.openOrClose);
                EditText name=(EditText)findViewById(R.id.roomName);
                EditText heightFloor=(EditText)findViewById(R.id.floorHeight);
                EditText width=(EditText)findViewById(R.id.width);
                EditText height=(EditText)findViewById(R.id.height);
                EditText range=(EditText)findViewById(R.id.range);
                if (!checkBox.isChecked())name.setText("Path");
                if (!heightFloor.getText().toString().isEmpty()||
                        !width.getText().toString().isEmpty()||
                        !height.getText().toString().isEmpty()||
                        !range.getText().toString().isEmpty()) {
                    currentRoom = new room(name.getText().toString(),"Coming Soon", currentFloor.getId(), checkBox.isChecked()
                            , Float.parseFloat(heightFloor.getText().toString()), Integer.parseInt(width.getText().toString())
                            , Integer.parseInt(height.getText().toString()), Integer.parseInt(range.getText().toString()));
                    //currentRoom.setId(db.addRoom(currentRoom));
                    try {
                        new newRoom().execute().get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    try {
                        new LoadAll(config_room.this).execute().get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    Intent myIntent=new Intent(config_room.this,record_page.class);
                    myIntent.putExtra("buildingId",String.valueOf(buildingId));
                    myIntent.putExtra("floorId",String.valueOf(floorId));
                    myIntent.putExtra("roomId",String.valueOf(currentRoom.getId()));
                    Log.d("buildingId="+buildingId+" floorId="+floorId+" roomId="+currentRoom.getId(),"checkLog");
                    startActivity(myIntent);
                    finish();
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
    class newRoom extends AsyncTask<String,String,String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(config_room.this);
            pDialog.setMessage("Sending Room to Server. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            List<NameValuePair> params_room = new ArrayList<NameValuePair>();
            params_room.add(new BasicNameValuePair("name", currentRoom.getName()));
            params_room.add(new BasicNameValuePair("detail", currentRoom.getDetail()));
            params_room.add(new BasicNameValuePair("height", String.valueOf(currentRoom.getHeightFloor())));
            params_room.add(new BasicNameValuePair("isClose", String.valueOf(currentRoom.getIsClose())));
            params_room.add(new BasicNameValuePair("width", String.valueOf(currentRoom.getWidth())));
            params_room.add(new BasicNameValuePair("depth", String.valueOf(currentRoom.getHeight())));
            params_room.add(new BasicNameValuePair("range", String.valueOf(currentRoom.getRange())));
            params_room.add(new BasicNameValuePair("floorId", String.valueOf(currentRoom.getFloorId())));
            // getting JSON Object
            // Note that create product url accepts POST method
            Log.d("checkLog",params_room.toString());
            JSONObject json_room = jParser.makeHttpRequest(config_room.this.getString(R.string.url_save), "POST", params_room);
            Log.d("checkLog", String.valueOf(json_room));
            try {
                // Checking for SUCCESS TAG
                int success = json_room.getInt("success");
                currentRoom.setId(json_room.getInt("lastId"));
                if (success == 1) {
                } else {
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            pDialog.dismiss();
        }
    }
}
