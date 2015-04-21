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
class newBuilding extends AsyncTask<String,String,String> {
    String name;
    float latitude,longitude;
    Context context;
    private ProgressDialog pDialog;
    private static final String TAG_SUCCESS = "success";
    // products JSONArray
    JSONArray all = null;
    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();
    Database db;
    public newBuilding(Context context,String name,float latitude,float longitude) {
        this.context=context;
        this.name=name;
        this.latitude=latitude;
        this.longitude=longitude;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        db = new Database(context);
        pDialog = new ProgressDialog(this.context);
        pDialog.setMessage("Create Building. Please wait...");
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
        try {
            // Checking for SUCCESS TAG
            //int success = json.getInt("success");
            return json.getString("id");
            /*if (success == 1){}
            else {}*/
        } catch (JSONException e){
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
