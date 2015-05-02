package com.example.kridsadath.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
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
class newBuilding extends AsyncTask<String,String,String> {
    String name;
    int id;
    float latitude,longitude;
    Context context;
    // products JSONArray
    JSONArray all = null;
    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();
    ProgressDialog pDialog;
    public newBuilding(Context context,String name,float latitude,float longitude) {
        this.context=context;
        this.name=name;
        this.latitude=latitude;
        this.longitude=longitude;
        this.id=0;
    }
    public int getId(){
        return this.id;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Sending to server. Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
    }
    @Override
    protected String doInBackground(String... args) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("name", this.name));
        params.add(new BasicNameValuePair("latitude",String.valueOf(this.latitude)));
        params.add(new BasicNameValuePair("longitude",String.valueOf(this.longitude)));
        // getting JSON Object
        // Note that create product url accepts POST method
        //Log.d(params.toString(),"checkLog");
        JSONObject json = jParser.makeHttpRequest(context.getString(R.string.url_save), "POST", params);
        //Log.d(json.toString(),"checkLog");
        // Check your log cat for JSON reponse
        if (json!=null) {
            try {
                // Checking for SUCCESS TAG
                int success = json.getInt("success");
                if (success == 1) {
                    this.id = Integer.parseInt(json.getString("lastId"));
                } else {
                    this.id = 0;
                }
            } catch (JSONException e) {
                Toast toast = new Toast(context);
                toast.setText("Please check internet connection");
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.show();
                //e.printStackTrace();
            }
        }
        else {
            return "ERROR";
        }
        return "OK";
    }
    @Override
    protected void onPostExecute(String s) {
        pDialog.dismiss();
    }
}
