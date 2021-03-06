package com.example.kridsadath.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by KridsadaTh on 1/3/2558.
 */

public class menu_ble extends Activity {
    int roomId  ;
    List<ble> listBle;
    Button btn1,btn2,btn3;
    RelativeLayout layout;
    List<nearCorner> listNearCorner;
    int countN;
    int noCorner;
    List<TextView> text;
    Database db;
    private ProgressDialog pDialog;
    JSONParser jParser = new JSONParser();
    public menu_ble(){}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cdpt);
        Bundle extras=getIntent().getExtras();
        roomId=Integer.parseInt(extras.getString("roomId"));
        text=new ArrayList<TextView>();
        text.add((TextView)findViewById(R.id.textView1));
        text.add((TextView) findViewById(R.id.textView2));
        text.add((TextView) findViewById(R.id.textView3));
        text.add((TextView) findViewById(R.id.textView4));
        listNearCorner=new ArrayList<nearCorner>();
        db=new Database(this);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter==null){
            Toast toast=Toast.makeText(this,"Bluetooth not available",Toast.LENGTH_SHORT);
            toast.show();
            finish();
        }
        else if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }
        listBle=db.getAllBle(roomId);
        if (listBle==null){
            Log.d("listBle=null","checkLog");
            AlertDialog.Builder box = new AlertDialog.Builder(this);
            box.setTitle("Error");
            box.setMessage("Scan Ble First");
            box.setPositiveButton("OK",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            box.show();
        }
        else {
            btn1=(Button)findViewById(R.id.btn1);
            btn2=(Button)findViewById(R.id.btn2);
            btn3=(Button)findViewById(R.id.btn3);
            TextView txt=(TextView)findViewById(R.id.menuName);
            txt.setText("Scan Corner");
            corner();
        }
    }
    void corner(){
        noCorner=0;
        //btn1.setVisibility(View.INVISIBLE);
        btn1.setText("Scan BLE At Left-Up");
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!btn1.getText().equals("Send Table Corner")) {
                    text.get(noCorner).setText("Scanning ...");
                    startScan();
                }
                else {
                    sendAndFinish();
                }
            }
        });
        btn2.setVisibility(View.INVISIBLE);
        btn3.setVisibility(View.INVISIBLE);
    }
    void sendAndFinish(){
        Log.d(listNearCorner.size()+" size in button","checkLog");
        saveBLE saveBLE=new saveBLE();
        //saveBLE.setList(listNearCorner);
        try {
            String check;
            check=saveBLE.execute().get();
            if (check.equals("OK"))
                finish();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
    private boolean isFinish;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothAdapter.LeScanCallback mLeScanCallback =new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {//HMSoft
                    if (device.getName()!=null) {
                            if (device.getName().equals("UT9")) {
                                countN++;
                                if (noCorner<4&&!isFinish) {
                                    text.get(noCorner).setText("Scanning ... " + countN + " %");
                                    addNearlyDevice(device.getAddress(), rssi);
                                }
                                if (countN>99&&!isFinish&&noCorner<4) {
                                    isFinish=true;
                                    stopScan();
                                    text.get(noCorner).setText("Position : "+noCorner+" Finish");
                                    noCorner++;
                                    switch (noCorner){
                                        case 0: btn1.setText("Scan BLE At Left-Up");
                                            break;
                                        case 1: btn1.setText("Scan BLE At Right-Up");
                                            break;
                                        case 2: btn1.setText("Scan BLE At Right-Down");
                                            break;
                                        case 3: btn1.setText("Scan BLE At Left-Down");
                                            break;
                                        default: btn1.setText("Send Table Corner");
                                            break;
                                    }
                                }
                            }
                    }
                }
            });
        }
    };
    public void addNearlyDevice(String macId,int rssi){
        int i;
        if (rssi>-80) {
            for (i = 0; i < listBle.size(); i++) {
                //Log.d(macId+" == "+listBle.get(i).getMacId(),"checkLog");
                if (macId.equals(listBle.get(i).getMacId())) {
                    break;
                }
            }
            if (i == listBle.size()) {
                int j = 0;
                //Log.d(listNearCorner.size()+" size in function","checkLog");
                for (j = 0; j < listNearCorner.size(); j++) {
                    Log.d(listNearCorner.get(j).getMacId() + " = " + macId, "checkLog");
                    if (listNearCorner.get(j).getMacId().equals(macId) && listNearCorner.get(j).getPosition() == noCorner) {
                        break;
                    }
                }
                if (j == listNearCorner.size()) {
                    Log.d("Add listNearCorner", "checkLog");
                    listNearCorner.add(new nearCorner(macId, roomId, noCorner));
                } else {
                    Log.d("Not Add listNearCorner", "checkLog");
                }
            }
        }
    }

    protected void startScan(){
        //Log.d("Start Scan", log);
        //btn.setText("Stop");
        countN=0;
        isFinish=false;
        mBluetoothAdapter.startLeScan(mLeScanCallback);
    }
    protected void stopScan(){
        //Log.d("Stop Scan",log);
        //btn.setText("Start");
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
    }
    class saveBLE extends AsyncTask<String,String,String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(menu_ble.this);
            pDialog.setMessage("Sending Ble to Server. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            Log.d(listNearCorner.size()+" Size of listNearCorner","checkLog");
            List<NameValuePair> root_params=new ArrayList<NameValuePair>();
            root_params.add(new BasicNameValuePair("roomId",String.valueOf(roomId)));
            root_params.add(new BasicNameValuePair("saveCorner",String.valueOf(listNearCorner.size())));
            for (int i=0;i<listNearCorner.size();i++) {
                root_params.add(new BasicNameValuePair("macAddress".concat(String.valueOf(i)),listNearCorner.get(i).getMacId()));
                root_params.add(new BasicNameValuePair("positionCorner".concat(String.valueOf(i)),String.valueOf(listNearCorner.get(i).getPosition())));
            }
            JSONObject json = jParser.makeHttpRequest(menu_ble.this.getString(R.string.url_save), "POST", root_params);
            if (json!=null) {
                try {
                    // Checking for SUCCESS TAG
                    int success = json.getInt("success");
                    Log.i("checkLog",json.toString());
                    /*if (success == 1) {
                    } else {
                    }*/
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else return "FAIL";
            return "OK";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                new LoadAll(menu_ble.this).execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            pDialog.dismiss();
        }
    }
}
