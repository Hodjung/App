package com.example.kridsadath.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KridsadaTh on 27/4/2558.
 */
class newRoom extends AsyncTask<String,String,String> {
    room currentRoom;
    Context context;
    JSONParser jParser = new JSONParser();
    newRoom(Context context,room room){
        this.context=context;
        this.currentRoom=room;
    }
    ProgressDialog pDialog;
    public int getId(){
        return this.currentRoom.getId();
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Sending Room to Server. Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
    }
    @Override
    protected String doInBackground(String... args) {
        List<NameValuePair> params_room = new ArrayList<NameValuePair>();
        params_room.add(new BasicNameValuePair("name", currentRoom.getName()));
        params_room.add(new BasicNameValuePair("detail", currentRoom.getDetail()));
        params_room.add(new BasicNameValuePair("height", String.valueOf(currentRoom.getHeightFloor())));
        params_room.add(new BasicNameValuePair("isClose", String.valueOf(currentRoom.getIsClose())));
        params_room.add(new BasicNameValuePair("width", String.valueOf(currentRoom.getWidth())));
        params_room.add(new BasicNameValuePair("depth", String.valueOf(currentRoom.getHeight())));
        params_room.add(new BasicNameValuePair("range", String.valueOf(currentRoom.getRange())));
        params_room.add(new BasicNameValuePair("floorId", String.valueOf(currentRoom.getFloorId())));
        // getting JSON Object
        // Note that create product url accepts POST method
        Log.d(params_room.toString(), "checkLog");
        JSONObject json_room = jParser.makeHttpRequest(context.getString(R.string.url_save), "POST", params_room);
        Log.d(String.valueOf(json_room),"checkLog");
        if (json_room!=null) {
            try {
                // Checking for SUCCESS TAG
                int success = json_room.getInt("success");
                if (success == 1) {
                    currentRoom.setId(Integer.parseInt(json_room.getString("lastId")));
                } else {
                    currentRoom.setId(0);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else {
            return "FAIL";
        }
        return "OK";
    }
    @Override
    protected void onPostExecute(String s) {
        pDialog.dismiss();
    }
}
