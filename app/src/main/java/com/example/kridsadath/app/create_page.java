package com.example.kridsadath.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by KridsadaTh on 22/2/2558.
 */
public class create_page extends Activity {
    String log="checkLog";
    int placeId;
    place currentPlace;
    String placeName;
    Database db;
    private List<floor> floor;
    ListView listView;
    ArrayAdapter<String> adapter;
    private ProgressDialog pDialog;
    JSONParser jParser = new JSONParser();
    JSONArray products = null;
    static String url_create_ble="http://192.168.137.1/create_ble.php";
    static String url_create_corner="http://192.168.137.1/create_corner.php";
    static String url_create_door="http://192.168.137.1/create_door.php";
    static String url_create_floor="http://192.168.137.1/create_floor.php";
    static String url_create_near_corner="http://192.168.137.1/create_near_corner.php";
    static String url_create_pin="http://192.168.137.1/create_pin.php";
    static String url_create_place="http://192.168.137.1/create_place.php";
    static String url_create_room="http://192.168.137.1/create_room.php";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_page);
        Bundle extras=getIntent().getExtras();
        placeId=Integer.parseInt(extras.getString("placeId"));
        TextView place=(TextView)findViewById(R.id.root_text);
        db=new Database(this);
        currentPlace=db.getPlace(placeId);
        placeName=currentPlace.getName();
        place.setText(placeName);
        setView();
        final AlertDialog.Builder boxOption = new AlertDialog.Builder(this);
        final String[] option_header = new String[] { "Manage", "Rename" };
        final ArrayAdapter<String> option = new ArrayAdapter<String>(this,android.R.layout.select_dialog_item, option_header);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                boxOption.setTitle("Option : "+floor.get(position).getName());
                boxOption.setAdapter(option,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which==0){
                            Intent myIntent=new Intent(create_page.this,manage_floor.class);
                            myIntent.putExtra("placeId",String.valueOf(placeId));
                            myIntent.putExtra("floorId",String.valueOf(floor.get(position).getId()));
                            Log.d(floor.get(position).getId()+"","checkLog");
                            startActivity(myIntent);
                            finish();
                        }
                        else {
                            boxRename(floor.get(position));
                        }
                    }
                });
                boxOption.show();
            }
        });
        Button btn_confirm=(Button)findViewById(R.id.confirm);
        Button btn_delete=(Button)findViewById(R.id.delete);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Add all to database server
                //new CreatePlace().execute();
                //Check Exist In Database***
                //If not ,Create***
                //Else Update***
                Intent intent=new Intent(create_page.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i=0;i<floor.size();i++){
                    List<room> room=db.getAllRoom(floor.get(i).getId());
                    if (room!=null) {
                        for (int j = 0; j < room.size(); j++) {
                            List<ble> ble = db.getAllBle(room.get(j).getId());
                            if (ble!=null) {
                                for (int k = 0; k < ble.size(); k++) {
                                    db.deleteBle(ble.get(k));
                                }
                            }
                            db.deleteRoom(room.get(j));
                        }
                    }
                    db.deleteFloor(floor.get(i));
                }
                db.deletePlace(placeId);
                Intent intent=new Intent(create_page.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    class CreatePlace extends AsyncTask<String,String,String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(create_page.this);
            pDialog.setMessage("Sending Place to Server. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id", String.valueOf(currentPlace.getId())));
            params.add(new BasicNameValuePair("place", currentPlace.getName()));
            params.add(new BasicNameValuePair("number_floor", String.valueOf(currentPlace.getNumberFloor())));
            // getting JSON Object
            // Note that create product url accepts POST method
            Log.d(params.toString(),"checkLog");
            JSONObject json = jParser.makeHttpRequest(url_create_place, "POST", params);
            Log.d(json.toString(),"checkLog");
            // Check your log cat for JSON reponse
            try {
                // Checking for SUCCESS TAG
                int success = json.getInt("success");
                if (success == 1){}
                else {}
            } catch (JSONException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            pDialog.dismiss();
            new CreateFloor().execute();
        }
    }
    class CreateFloor extends AsyncTask<String,String,String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(create_page.this);
            pDialog.setMessage("Sending Floor to Server. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            List<floor> listFloor=new ArrayList<floor>();
            listFloor=db.getAllFloor(currentPlace.getId());
            if (listFloor!=null){
                for (int i=0;i<listFloor.size();i++) {
                    List<NameValuePair> params_floor = new ArrayList<NameValuePair>();
                    params_floor.add(new BasicNameValuePair("id", String.valueOf(listFloor.get(i).getId())));
                    params_floor.add(new BasicNameValuePair("floor", listFloor.get(i).getName()));
                    params_floor.add(new BasicNameValuePair("placeId", String.valueOf(listFloor.get(i).getPlaceId())));
                    // getting JSON Object
                    // Note that create product url accepts POST method
                    JSONObject json_floor = jParser.makeHttpRequest(url_create_floor, "POST", params_floor);
                    try {
                        // Checking for SUCCESS TAG
                        int success = json_floor.getInt("success");
                        if (success == 1){}
                        else {}
                    } catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            pDialog.dismiss();
            new CreateRoom().execute();
        }
    }
    class CreateRoom extends AsyncTask<String,String,String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(create_page.this);
            pDialog.setMessage("Sending Room to Server. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            List<floor> listFloor=new ArrayList<floor>();
            listFloor=db.getAllFloor(currentPlace.getId());
            if (listFloor!=null) {
                for (int i = 0; i < listFloor.size(); i++) {
                    List<room> listRoom = new ArrayList<room>();
                    listRoom = db.getAllRoom(listFloor.get(i).getId());
                    if (listRoom != null) {
                        for (int j = 0; j < listRoom.size(); j++) {
                            List<NameValuePair> params_room = new ArrayList<NameValuePair>();
                            params_room.add(new BasicNameValuePair("id", String.valueOf(listRoom.get(j).getId())));
                            params_room.add(new BasicNameValuePair("room", listRoom.get(j).getName()));
                            params_room.add(new BasicNameValuePair("detail", listRoom.get(j).getDetail()));
                            params_room.add(new BasicNameValuePair("heightFloor", String.valueOf(listRoom.get(j).getHeightFloor())));
                            params_room.add(new BasicNameValuePair("isClose", String.valueOf(listRoom.get(j).getIsClose())));
                            params_room.add(new BasicNameValuePair("width", String.valueOf(listRoom.get(j).getWidth())));
                            params_room.add(new BasicNameValuePair("height", String.valueOf(listRoom.get(j).getHeight())));
                            params_room.add(new BasicNameValuePair("rangeBetween", String.valueOf(listRoom.get(j).getRange())));
                            params_room.add(new BasicNameValuePair("floorId", String.valueOf(listRoom.get(j).getFloorId())));
                            // getting JSON Object
                            // Note that create product url accepts POST method
                            Log.d(params_room.toString(),"checkLog");
                            JSONObject json_room = jParser.makeHttpRequest(url_create_room, "POST", params_room);
                            Log.d(json_room.toString(),"checkLog");
                            try {
                                // Checking for SUCCESS TAG
                                int success = json_room.getInt("success");
                                if (success == 1) {
                                } else {
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            pDialog.dismiss();
            new CreateBle().execute();
        }
    }
    class CreateBle extends AsyncTask<String,String,String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(create_page.this);
            pDialog.setMessage("Sending Ble to Server. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            List<floor> listFloor=new ArrayList<floor>();
            listFloor=db.getAllFloor(currentPlace.getId());
            if (listFloor!=null) {
                for (int i = 0; i < listFloor.size(); i++) {
                    List<room> listRoom = new ArrayList<room>();
                    listRoom = db.getAllRoom(listFloor.get(i).getId());
                    if (listRoom != null) {
                        for (int j = 0; j < listRoom.size(); j++) {
                            List<ble> listBle=new ArrayList<ble>();
                            listBle=db.getAllBle(listRoom.get(j).getId());
                            if (listBle!=null){
                                for (int k=0;k<listBle.size();k++){
                                    List<NameValuePair> params_ble = new ArrayList<NameValuePair>();
                                    params_ble.add(new BasicNameValuePair("id", String.valueOf(listBle.get(k).getId())));
                                    params_ble.add(new BasicNameValuePair("macAddress", listBle.get(k).getMacId()));
                                    params_ble.add(new BasicNameValuePair("roomId", String.valueOf(listBle.get(k).getRoomId())));
                                    params_ble.add(new BasicNameValuePair("position", String.valueOf(listBle.get(k).getPosition())));
                                    // getting JSON Object
                                    // Note that create product url accepts POST method
                                    JSONObject json_ble = jParser.makeHttpRequest(url_create_ble, "POST", params_ble);
                                    try {
                                        // Checking for SUCCESS TAG
                                        int success = json_ble.getInt("success");
                                        if (success == 1) {
                                        } else {
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            pDialog.dismiss();
            new CreateCorner().execute();
        }
    }
    class CreateCorner extends AsyncTask<String,String,String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(create_page.this);
            pDialog.setMessage("Sending Corner to Server. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            List<floor> listFloor=new ArrayList<floor>();
            listFloor=db.getAllFloor(currentPlace.getId());
            if (listFloor!=null) {
                for (int i = 0; i < listFloor.size(); i++) {
                    List<room> listRoom = new ArrayList<room>();
                    listRoom = db.getAllRoom(listFloor.get(i).getId());
                    if (listRoom != null) {
                        for (int j = 0; j < listRoom.size(); j++) {
                            List<corner> listCorner=new ArrayList<corner>();
                            listCorner=db.getCorner(listRoom.get(j).getId());
                            if (listCorner!=null){
                                for (int k=0;k<listCorner.size();k++){
                                    List<NameValuePair> params_corner = new ArrayList<NameValuePair>();
                                    params_corner.add(new BasicNameValuePair("roomId", String.valueOf(listCorner.get(k).getRoomId())));
                                    params_corner.add(new BasicNameValuePair("noCorner", String.valueOf(listCorner.get(k).getRoomId())));
                                    params_corner.add(new BasicNameValuePair("x", String.valueOf(listCorner.get(i).getX())));
                                    params_corner.add(new BasicNameValuePair("y", String.valueOf(listCorner.get(i).getY())));
                                    // getting JSON Object
                                    // Note that create product url accepts POST method
                                    JSONObject json_corner = jParser.makeHttpRequest(url_create_corner, "POST", params_corner);
                                    try {
                                        // Checking for SUCCESS TAG
                                        int success = json_corner.getInt("success");
                                        if (success == 1) {
                                        } else {
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            pDialog.dismiss();
            new CreatePin().execute();
        }
    }
    class CreatePin extends AsyncTask<String,String,String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(create_page.this);
            pDialog.setMessage("Sending Pin to Server. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            List<floor> listFloor=new ArrayList<floor>();
            listFloor=db.getAllFloor(currentPlace.getId());
            if (listFloor!=null) {
                for (int i = 0; i < listFloor.size(); i++) {
                    List<room> listRoom = new ArrayList<room>();
                    listRoom = db.getAllRoom(listFloor.get(i).getId());
                    if (listRoom != null) {
                        Log.d(listRoom.toString(),"checkLog");
                        for (int j = 0; j < listRoom.size(); j++) {
                            List<pin> listPin=new ArrayList<pin>();
                            Log.d("get Pin "+listRoom.get(j).getId()+" "+listRoom.size(),"checkLog");
                            listPin=db.getPin(listRoom.get(j).getId());
                            if (listPin!=null){
                                Log.d("Pin != null","checkLog");
                                for (int k=0;k<listPin.size();k++){
                                    List<NameValuePair> params_pin = new ArrayList<NameValuePair>();
                                    params_pin.add(new BasicNameValuePair("roomId", String.valueOf(listPin.get(k).getRoomId())));
                                    params_pin.add(new BasicNameValuePair("detail", listPin.get(k).getDetail()));
                                    params_pin.add(new BasicNameValuePair("x", String.valueOf(listPin.get(k).getX())));
                                    params_pin.add(new BasicNameValuePair("y", String.valueOf(listPin.get(k).getY())));
                                    // getting JSON Object
                                    // Note that create product url accepts POST method
                                    JSONObject json_pin = jParser.makeHttpRequest(url_create_pin, "POST", params_pin);
                                    try {
                                        // Checking for SUCCESS TAG
                                        int success = json_pin.getInt("success");
                                        if (success == 1) {
                                        } else {
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            pDialog.dismiss();
            Intent intent=new Intent(create_page.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
    void boxRename (final floor floor){
        AlertDialog.Builder renameBox = new AlertDialog.Builder(this);
        renameBox.setTitle("Rename " + floor.getName());
        renameBox.setMessage("Rename to");
        final EditText input = new EditText(this);
        final Database db=new Database(this);
        renameBox.setView(input);
        renameBox.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                floor.setName(input.getText().toString());
                db.updateFloor(floor);
                setView();
            }
        });
        renameBox.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        renameBox.show();
    }
    public void setView(){
        floor=db.getAllFloor(placeId);
        ArrayList<String> list=new ArrayList<String>();
        for (int i=0;i<floor.size();i++){
            list.add(floor.get(i).getName());
        }
        if (floor.size()==0)list.add("Empty");
        listView=(ListView)findViewById(R.id.listView);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,android.R.id.text1,list);
        listView.setAdapter(adapter);
    }
}
