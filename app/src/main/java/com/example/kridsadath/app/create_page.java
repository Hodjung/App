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
    int buildingId;
    building currentBuilding=null;
    Database db;
    private List<floor> floor;
    ListView listView;
    ArrayAdapter<String> adapter;
    private ProgressDialog pDialog;
    JSONParser jParser = new JSONParser();
    //JSONArray products = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_page);
        Bundle extras=getIntent().getExtras();
        buildingId=Integer.parseInt(extras.getString("buildingId"));
        TextView building=(TextView)findViewById(R.id.root_text);
        Log.d(buildingId+"",log);
        db=new Database(this);
        currentBuilding=db.getBuilding(buildingId);
        building.setText(currentBuilding.getName());
        Log.d("before set view",log);
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
                            myIntent.putExtra("buildingId",String.valueOf(buildingId));
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
        Button btn_delete=(Button)findViewById(R.id.delete);
        btn_delete.setVisibility(View.INVISIBLE);
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
                db.deleteBuilding(buildingId);
                Intent intent=new Intent(create_page.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    /*class CreateCorner extends AsyncTask<String,String,String>{
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
            listFloor=db.getAllFloor(currentBuilding.getId());
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
                                    JSONObject json_corner = jParser.makeHttpRequest(url_save, "POST", params_corner);
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
            //new CreatePin().execute();
        }
    }
*/
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
        floor=db.getAllFloor(buildingId);
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
