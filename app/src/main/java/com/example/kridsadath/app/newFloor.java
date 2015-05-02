package com.example.kridsadath.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

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
    }

    @Override
    protected String doInBackground(String... args) {
        List<NameValuePair> root_params=new ArrayList<>();
        root_params.add(new BasicNameValuePair("buildingId", String.valueOf(buildingId)));
        root_params.add(new BasicNameValuePair("saveFloor",String.valueOf(numFloor)));
        for (int i=0;i<numFloor;i++) {
            root_params.add(new BasicNameValuePair("name".concat(String.valueOf(i)),"Floor ".concat(String.valueOf(i+1))));
        }
        Log.d(root_params.toString(),"checkLog");
        JSONObject json_floor = jParser.makeHttpRequest(context.getString(R.string.url_save), "POST", root_params);
        if (json_floor!=null) {
            try {
                // Checking for SUCCESS TAG
                int success = json_floor.getInt("success");
                if (success == 1) {
                    Log.d(json_floor.toString(), "checkLog");
                } else {
                    Log.d(json_floor.toString(), "checkLog");
                }
            } catch (JSONException e) {
                Toast toast = new Toast(context);
                toast.setText("Please check internet connection");
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.show();
                //e.printStackTrace();
            }
        }
        else return "FAIL";
        return "OK";
    }

    @Override
    protected void onPostExecute(String s) {

    }
}