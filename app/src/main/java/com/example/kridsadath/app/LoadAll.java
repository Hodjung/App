package com.example.kridsadath.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KridsadaTh on 21/4/2558.
 */
class LoadAll extends AsyncTask<String,String,String> {
    Context context;
    private ProgressDialog pDialog;
    private static final String TAG_SUCCESS = "success";
    // products JSONArray
    JSONArray all = null;
    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();
    String msLog="checkLog";
    Database db;
    SQLiteDatabase mDb;
    public LoadAll(Context context) {
        this.context=context;
    }

    @Override
    protected void onPreExecute() {
        db = new Database(context);
        mDb=db.getWritableDatabase();
        db.onUpgrade(mDb,1,1);
        super.onPreExecute();
        pDialog = new ProgressDialog(this.context);
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
        JSONObject json = jParser.makeHttpRequest(context.getString(R.string.url_get), "POST", params);
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
                /*Log.d("" + all.length(), msLog);
                Log.d(msLog+"",all.getJSONObject(0)+"");
                Log.d(msLog+"",all.getJSONObject(1)+"");
                Log.d(msLog+"",all.getJSONObject(2)+"");*/
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
        db.close();
        // dismiss the dialog after getting all products
        pDialog.dismiss();
    }
}
