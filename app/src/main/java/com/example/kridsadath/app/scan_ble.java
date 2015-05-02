package com.example.kridsadath.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by KridsadaTh on 24/2/2558.
 */
public class scan_ble extends Activity {
    public scan_ble(){}
    int roomId;
    int numberPosition;
    int currentPosition;
    String log="checkLog";
    List<ble> listBle;
    ArrayList<String> group;
    Database db;
    JSONParser jParser = new JSONParser();
    private ProgressDialog pDialog;
    private List<BluetoothDevice> deviceList;
    private List<Integer> rssiList;
    private List<Integer> countRssiList;
    private List<Integer> isSetNumber;
    private BluetoothDevice lastDevice;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {//UT9
                            if (device.getName()!=null) { //TODO:ดักเนาไว้นะจ้ะ
                                Log.e("error", "dev:" + device.toString());
                                if (device.getName().equals("UT9")) {
                                    int positionInList = positionInList(device);
                                    receiveSignalAndSetPositionDevice(positionInList, rssi);
                                    if (currentPosition == numberPosition) {
                                        stopScan();
                                        finishScan();
                                    }
                                }
                                else Log.d("Other device :"+device.getName(),log);
                            }
                        }
                    });
                }
            };
    private Button start;
    RelativeLayout layout;
    int w,h;
    List<DrawInLayout> device,listSave;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_ble_page);
        Bundle extras=getIntent().getExtras();
        roomId=Integer.parseInt(extras.getString("roomId"));
        db=new Database(this);
        room rm =db.getRoom(roomId);
        w=rm.getWidth();
        h=rm.getHeight();
        Display display = getWindowManager().getDefaultDisplay();
        int wScreen = display.getWidth();
        int hScreen = display.getHeight();
        Log.d(wScreen + "*" + hScreen + "   " + w + "*" + h, log);
        numberPosition=w*h;
        currentPosition=0;
        deviceList=new ArrayList<BluetoothDevice>();
        rssiList=new ArrayList<Integer>();
        countRssiList=new ArrayList<Integer>();
        isSetNumber=new ArrayList<Integer>();
        listBle = new ArrayList<ble>();
        lastDevice=null;
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
        start=(Button)findViewById(R.id.start);
        start.setText("Start");
        Button backStep=(Button)findViewById(R.id.backStep);
        Button light=(Button)findViewById(R.id.light);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (start.getText().toString().equals("Start")) {
                    startScan();
                }
                else if (start.getText().toString().equals("Stop")) {
                    stopScan();
                }
                else {
                    //save ble to database
                    listSave=new ArrayList<DrawInLayout>();
                    for (int i=0;i<numberPosition;i++){
                        int j;
                        for (j=0;j<numberPosition;j++) {
                            if (device.get(j).getPosition().equals(String.valueOf(i))){
                                break;
                            }
                        }
                        String name=device.get(j).getMacId();
                        //name=name.replace(":","");
                        listSave.add(device.get(j));
                        //db.addBle(new ble(name,roomId,device.get(j).getPosition()));
                        Log.d(name+""+roomId+device.get(j).getPosition(),"checkLog");
                    }
                    send();
                    //swap_page(RECORD_PAGE);
                }
            }
        });
        backStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backStep();
            }
        });
        light.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopScan();
                checkLastDevice();
            }
        });
        layout=(RelativeLayout)findViewById(R.id.layout);
        int scale=100,begin=50;
        if (w>4||h>7){
            scale=50;
            begin=25;
        }
        int wLayout,hLayout;
        wLayout=w*scale;
        hLayout=h*scale;
        device=new ArrayList<DrawInLayout>();
        for (int i=0;i<h;i++) {
            for (int j=0;j<w;j++) {
                device.add(new DrawInLayout(this, layout, R.drawable.noncheck));
                device.get(device.size() - 1).setLayoutSize(wLayout, hLayout);
                device.get(device.size() - 1).setLayoutAlpha(100);
                device.get(device.size() - 1).setStickSize(25, 25);
                device.get(device.size() - 1).setStickAlpha(100);
                if (i%2==0) {
                    device.get(device.size() - 1).setPosition(i*w, j);
                    device.get(device.size() - 1).drawPoint((j * scale) + begin, (i * scale) + begin);
                }
                else {
                    device.get(device.size() - 1).setPosition(i*w, w - 1 - j);
                    device.get(device.size() - 1).drawPoint(((w - 1 - j) * scale) + begin, (i * scale) + begin);
                }
                Log.d(device.get(device.size()-1).getPosition()+" ---","checkLog");
            }
        }
    }
    public void send(){
        try {
            String check;
            check=new saveBLE().execute().get();
            if (check.equals("OK"))
                finish();
            else Toast.makeText(scan_ble.this,"Error sending",Toast.LENGTH_SHORT);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        /*List<NameValuePair> root_params=new ArrayList<NameValuePair>();
        JSONObject json = jParser.makeHttpRequest(scan_ble.this.getString(R.string.url_save), "POST", root_params);*/
    }
    class saveBLE extends AsyncTask<String,String,String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(scan_ble.this);
            pDialog.setMessage("Loading All. Please wait...");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            List<NameValuePair> root_params=new ArrayList<NameValuePair>();
            root_params.add(new BasicNameValuePair("roomId",String.valueOf(roomId)));
            root_params.add(new BasicNameValuePair("saveBle",String.valueOf(listSave.size())));
            for (int i=0;i<listSave.size();i++) {
                root_params.add(new BasicNameValuePair("macAddress".concat(String.valueOf(i)),listSave.get(i).getMacId()));
                root_params.add(new BasicNameValuePair("positionBle".concat(String.valueOf(i)), listSave.get(i).getPosition()));
            }
            JSONObject json = jParser.makeHttpRequest(scan_ble.this.getString(R.string.url_save), "POST", root_params);
            if (json!=null) {
                try {
                    // Checking for SUCCESS TAG
                    int success = json.getInt("success");
                    if (success == 1) {
                        Log.d("Success=1", "checkLog");
                    } else {
                        Log.d("Success=other", "checkLog");
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
            super.onPostExecute(s);
            try {
                new LoadAll(scan_ble.this).execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            pDialog.dismiss();
        }
    }
    private void finishScan() {
        start.setText("Confirm");
    }
    protected void receiveSignalAndSetPositionDevice(int positionInList,int rssi){
        String name=deviceList.get(positionInList).getAddress();
        name=name.replace(":","");
        Log.d(name+" "+rssi+" count = "+countRssiList.get(positionInList)+" max ="+rssiList.get(positionInList),log);
        if (countRssiList.get(positionInList)==8){
            if (rssiList.get(positionInList)>-60&&currentPosition<numberPosition){
                if (isSetNumber.get(positionInList)==-1) {
                    Log.d(deviceList.get(positionInList)+" "+rssi+" SET",log);
                    setPosition(positionInList);
                }
            }
            rssiList.set(positionInList,-100);
            countRssiList.set(positionInList,0);
        }
        if (rssiList.get(positionInList)<rssi) {
            rssiList.set(positionInList,rssi);
        }
        int count=countRssiList.get(positionInList)+1;
        countRssiList.set(positionInList,count);
    }
    protected void setPosition(int positionList){
        Log.d("Start Set", log);
        isSetNumber.set(positionList, currentPosition);
        lastDevice=deviceList.get(positionList);
        device.get(currentPosition).setStickAndReDraw(R.drawable.device);
        device.get(currentPosition).setMacId(deviceList.get(positionList).getAddress());
        currentPosition++;
    }
    protected int positionInList(BluetoothDevice currentDevice){
        Log.d("positionInList",log);
        if (deviceList.size()==0){
        }
        else {
            for (int i=0;i<deviceList.size();i++){
                if(currentDevice.getAddress().equals(deviceList.get(i).getAddress())){
                    return i;
                }
            }
        }
        deviceList.add(currentDevice);
        isSetNumber.add(-1);
        rssiList.add(-100);
        countRssiList.add(0);
        Log.d("Finish",log);
        return 0;//deviceList.size()-1;
    }
    protected void backStep(){
        Log.d(currentPosition+" from "+numberPosition,log);
        if (lastDevice!=null) {
            currentPosition--;
            int n=isSetNumber.indexOf(currentPosition);
            if (currentPosition==0) {
                lastDevice = null;
            }
            else {
                lastDevice=deviceList.get(n);
            }
            isSetNumber.set(n,-1);
            device.get(currentPosition).setStickAndReDraw(R.drawable.noncheck);
        }
        else {
            Log.d("last device == null","checkLog");
            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Don't have Last Step")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }
    protected void checkLastDevice(){
        if (lastDevice!=null) {
            lastDevice.createBond();
        }
        else {
            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Don't have Last Device")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }
    protected void startScan(){
        start.setText("Stop");
        //mBluetoothAdapter.getBluetoothLeScanner().startScan(scanCallback);
        mBluetoothAdapter.startLeScan(mLeScanCallback);
    }
    protected void stopScan(){
        //mBluetoothAdapter.getBluetoothLeScanner().stopScan(scanCallback);
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
        if (start.getText()!="Confirm")
            start.setText("Start");
    }
}
