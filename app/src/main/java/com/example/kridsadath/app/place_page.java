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
    ListView placeView;
    List<place> listPlace;
    ArrayAdapter<String> adapterPlace;
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
    JSONArray place = null;
    SQLiteDatabase mDb;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new Database(this);
        /*mDb=db.getWritableDatabase();
        db.onUpgrade(mDb,1,1);*/
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
        placeView = (ListView)findViewById(R.id.listView);
        //Load all from database server
        // Loading products in Background Thread
        //new LoadAllProducts().execute();
        initPlace();
        initPagePlace();
    }
    class LoadAllProducts extends AsyncTask<String,String,String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(place_page.this);
            pDialog.setMessage("Loading products. Please wait...");
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
                    place = json.getJSONArray("places");

                    // looping through All Products
                    for (int i = 0; i < place.length(); i++) {
                        JSONObject c = place.getJSONObject(i);
                        // Storing each json item in variable
                        String id = c.getString("id");
                        String place = c.getString("place");
                        int number_floor = Integer.parseInt(c.getString("number_floor"));
                        db.addPlace(new place(place,number_floor));
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
