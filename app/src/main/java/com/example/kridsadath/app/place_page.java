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
    String msLog="checkLog";
    List<building> listBuilding;
    ArrayAdapter<String> adapterBuilding;
    String log="checkLog";
    Database db;
    public place_page(){}
    // Progress Dialog
    private ProgressDialog pDialog;
    private static String url_get="http://192.168.137.1/get.php";
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
        mDb=db.getWritableDatabase();
        db.onUpgrade(mDb,1,1);
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
        Button refresh = (Button)findViewById(R.id.back);
        refresh.setText("refresh");
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent=new Intent(place_page.this,place_page.class);
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
            JSONObject json = jParser.makeHttpRequest(url_get, "POST", params);
            // Check your log cat for JSON reponse
            //Log.d("All Products: ", json.toString());
            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    all = json.getJSONArray("building");
                    // looping through All Products
                    Log.d(""+all.length(),msLog);
                    Log.d(msLog+"",all.getJSONObject(0)+"");
                    Log.d(msLog+"",all.getJSONObject(1)+"");
                    Log.d(msLog+"",all.getJSONObject(2)+"");
                    int i;
                    for (i = 0; i < all.length(); i++) {
                        JSONObject build = all.getJSONObject(i);
                        Log.d("i = "+i+" "+build.getString("id")+" "+build.getString("name"),msLog);
                        // Storing each json item in variable
                        String id = build.getString("id");
                        String name = build.getString("name");
                        String latitude = build.getString("latitude");
                        String longitude = build.getString("longitude");
                        db.addBuilding(new building(Integer.valueOf(id),name,Float.valueOf(latitude),Float.valueOf(longitude)));

                        JSONArray all_floor=build.getJSONArray("floor");
                        int j;
                        for (j=0;j<all_floor.length();j++){
                            JSONObject floor = all_floor.getJSONObject(j);
                            String idF = floor.getString("id");
                            String nameF= floor.getString("name");
                            String buildingId = floor.getString("buildingId");
                            int buildingIdF=Integer.valueOf(buildingId);
                            String imageIdF = floor.getString("imageId");
                            db.addFloor(new floor(Integer.valueOf(idF),nameF,buildingIdF,imageIdF));

                            JSONArray all_room=floor.getJSONArray("room");
                            int k;
                            for (k=0;k<all_room.length();k++){
                                JSONObject room = all_room.getJSONObject(k);
                                String idR = room.getString("id");
                                String nameR = room.getString("name");
                                String detailR = room.getString("detail");
                                String heightR = room.getString("height");
                                String isClose = room.getString("isClose");
                                String width = room.getString("width");
                                String depth = room.getString("depth");
                                String range = room.getString("range");
                                String floorId = room.getString("floorId");
                                db.addRoom(new room(Integer.valueOf(idR),nameR,detailR,Integer.valueOf(floorId),Boolean.valueOf(isClose),Double.valueOf(heightR)
                                        ,Integer.valueOf(width),Integer.valueOf(depth),Integer.valueOf(range)));

                                JSONArray all_ble=room.getJSONArray("ble");
                                int l;
                                for (l=0;l<all_ble.length();l++){
                                    JSONObject ble = all_ble.getJSONObject(l);
                                    String idB = ble.getString("id");
                                    String macB = ble.getString("macAddress");
                                    String roomIdB = ble.getString("roomId");
                                    String positionB = ble.getString("position");
                                    db.addBle(new ble(Integer.valueOf(idB),macB,Integer.valueOf(roomIdB),positionB));

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
                    Log.d("select "+listBuilding.get(position).getId(),msLog);
                    Intent myIntent = new Intent(place_page.this,create_page.class);
                    myIntent.putExtra("buildingId",String.valueOf(listBuilding.get(position).getId()));
                    startActivity(myIntent);
                    finish();
                }
                //swap_page(CREATING_PAGE);
            }
        });
        db.close();
        Log.d("Finish", log);
    }
}
