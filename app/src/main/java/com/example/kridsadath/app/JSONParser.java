package com.example.kridsadath.app;

/**
 * Created by KridsadaTh on 17/3/2558.
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
//import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class JSONParser{

    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";
    Context context;
    // constructor
    public JSONParser() {
    }
    // function get json from url
    // by making HTTP POST or GET mehtod
    public JSONObject makeHttpRequest(String url, String method,List<NameValuePair> params) {
        // Making HTTP request
        try {
            // check for request method
            if(method.equals("POST")){
                // request method is POST
                // defaultHttpClient
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpParams httpParameters = httpClient.getParams();
                HttpConnectionParams.setConnectionTimeout(httpParameters,3000);
                HttpConnectionParams.setSoTimeout(httpParameters,5000);
                //HttpConnectionParams.setTcpNoDelay(httpParameters, true);
                HttpPost httpPost = new HttpPost(url);
                httpPost.setEntity(new UrlEncodedFormEntity(params));

                Log.d(System.currentTimeMillis() + "", "checkLog");// Log to know the time diff
                //
                /*URL uri = new URL(url);
                HttpURLConnection connection = (HttpURLConnection)uri.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.connect();*/
                //
                HttpResponse httpResponse;
                httpResponse = httpClient.execute(httpPost);
                Log.d(System.currentTimeMillis()+"","checkLog");// Log to know the time diff
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();

            }/*else if(method.equals("GET")){
                // request method is GET
                DefaultHttpClient httpClient = new DefaultHttpClient();
                String paramString = URLEncodedUtils.format(params, "utf-8");
                url += "?" + paramString;
                HttpGet httpGet = new HttpGet(url);

                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();
            }*/
        } catch (UnsupportedEncodingException e) {
            Log.d("First catch","checkLog");
            return null;
            //e.printStackTrace();
        } catch (ClientProtocolException e) {
            Log.d("Second catch","checkLog");
            return null;
            //e.printStackTrace();
        } catch (IOException e) {
            Log.d("Third catch","checkLog");
            return null;
            //e.printStackTrace();
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }

        // try parse the string to a JSON object
        try {
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }
        // return JSON String
        return jObj;

    }
}
