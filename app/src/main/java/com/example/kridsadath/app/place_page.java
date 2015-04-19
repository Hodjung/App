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

import org.apache.http.NameValuePair;
import org.json.*;

/**
 * Created by KridsadaTh on 25/2/2558.
 */
public class place_page extends Activity {
    List<String> group;
    ListView buildingView;
    List<building> listBuilding;
    ArrayAdapter<String> adapterBuilding;
    String log="checkLog";
    Database db;
    public place_page(){}
    // Progress Dialog
    private ProgressDialog pDialog;
    private static String url_all_places="192.168.137.1/get_place.php";
    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();
    private static final String TAG_SUCCESS = "success";
    //private static final String TAG_PLACE = "place";
    private static final String TAG_ID = "id";
    private static final String TAG_PLACE = "place";
    private static final String TAG_NUMBER_FLOOR = "number_floor";
    // products JSONArray
    JSONArray all = null;
    SQLiteDatabase mDb;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new Database(this);
        /*mDb=db.getWritableDatabase();
        db.onUpgrade(mDb,1,1);*/
        setContentView(R.layout.manage_floor_page);
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
        Button back = (Button)findViewById(R.id.back);
        back.setVisibility(View.INVISIBLE);
        back.setOnClickListener(new View.OnClickListener() {
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
        new LoadAllProducts().execute();

        /*initPlace();
        initPagePlace();*/
    }
    class LoadAllProducts extends AsyncTask<String,String,String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(place_page.this);
            pDialog.setMessage("Loading All. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_all_places, "POST", params);
            // Check your log cat for JSON reponse
            Log.d("All Products: ", json.toString());
            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    all = json.getJSONArray("building");
                    // looping through All Products
                    for (int i = 0; i < all.length(); i++) {
                        JSONObject building = all.getJSONObject(i);
                        // Storing each json item in variable
                        String id = building.getString("id");
                        String name = building.getString("name");
                        String latitude = building.getString("latitude");
                        String longitude = building.getString("longitude");
                        db.addBuilding(new building(Integer.valueOf(id),name,Float.valueOf(latitude),Float.valueOf(longitude)));

                        JSONArray all_floor=json.getJSONArray("floor");
                        for (int j=0;j<all_floor.length();j++){
                            JSONObject floor = all_floor.getJSONObject(j);
                            String idF = floor.getString("id");
                            String nameF= floor.getString("name");
                            String buildingId = floor.getString("buildingId");
                            int buildingIdF=Integer.valueOf(buildingId);
                            String imageIdF = floor.getString("imageId");
                            db.addFloor(new floor(nameF,buildingIdF));

                            JSONArray all_room=json.getJSONArray("room");
                            for (int k=0;k<all_room.length();k++){
                                JSONObject room = all_room.getJSONObject(k);
                                String idR = room.getString("id");
                                String nameR = room.getString("name");
                                String detailR = room.getString("detail");
                                String heightR = room.getString("heightR");
                                String isClose = room.getString("isClose");
                                String width = room.getString("width");
                                String depth = room.getString("depth");
                                String range = room.getString("range");
                                String floorId = room.getString("floorId");
                                db.addRoom(new room(nameR,detailR,Integer.valueOf(floorId),Boolean.valueOf(isClose),Double.valueOf(heightR)
                                        ,Integer.valueOf(width),Integer.valueOf(depth),Integer.valueOf(range)));

                                JSONArray all_ble=json.getJSONArray("ble");
                                for (int l=0;l<all_ble.length();l++){
                                    JSONObject ble = all_ble.getJSONObject(l);
                                    String idB = ble.getString("id");
                                    String macB = ble.getString("macAddress");
                                    String roomIdB = ble.getString("roomId");
                                    String positionB = ble.getString("position");
                                    db.addBle(new ble(macB,Integer.valueOf(roomIdB),Integer.valueOf(positionB)));

                                }
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                    initPlace();
                    initPagePlace();
                 }
            });
        }
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
        Log.d("Add Place Page", log);
        adapterBuilding = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,android.R.id.text1,group);
        buildingView.setAdapter(adapterBuilding);
        buildingView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (listBuilding.size()>0){
                    Intent myIntent = new Intent(place_page.this,create_page.class);
                    myIntent.putExtra("placeId",String.valueOf(listBuilding.get(position).getId()));
                    startActivity(myIntent);
                    finish();
                }
                //swap_page(CREATING_PAGE);
            }
        });
        Log.d("Finish", log);
    }
}
