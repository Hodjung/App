package com.example.kridsadath.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KridsadaTh on 21/4/2558.
 */
public class newFloor extends AsyncTask<String,String,String> {
    int numFloor,buildingId;
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
    public newFloor (Context context,int numFloor,int buildingId){
        this.context=context;
        this.numFloor=numFloor;
        this.buildingId=buildingId;
    }
    @Override
    protected void onPreExecute() {
        db = new Database(context);
        super.onPreExecute();
        pDialog = new ProgressDialog(this.context);
        pDialog.setMessage("Create Floor. Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    @Override
    protected String doInBackground(String... args) {
        for (int i=0;i<numFloor;i++) {
            List<NameValuePair> params_floor = new ArrayList<NameValuePair>();
            params_floor.add(new BasicNameValuePair("name","Floor "+String.valueOf(i+1)));
            params_floor.add(new BasicNameValuePair("buildingId", String.valueOf(buildingId)));
            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json_floor = jParser.makeHttpRequest(context.getString(R.string.url_save), "POST", params_floor);
            try {
                // Checking for SUCCESS TAG
                int success = json_floor.getInt("success");
                if (success == 1){}
                else {}
            } catch (JSONException e){
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        pDialog.dismiss();
    }
}