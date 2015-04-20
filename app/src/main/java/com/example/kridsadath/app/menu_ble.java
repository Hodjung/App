package com.example.kridsadath.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by KridsadaTh on 1/3/2558.
 */

public class menu_ble extends Activity {
    int roomId  ;
    Database db;
    List<ble> listBle;
    Button btn1,btn2,btn3;
    RelativeLayout layout;
    List<nearCorner> listNearCorner;
    int countN;
    int noCorner;
    List<TextView> text;
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
        db=new Database(this);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
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
        btn1.setVisibility(View.INVISIBLE);
        btn1.setText("Scan BLE At Left-Up");
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!btn1.getText().equals("Send Table Corner")) {
                    text.get(noCorner).setText("Scanning ...");
                    startScan();
                }
                else {
                    //send Data
                    //finish()
                }
            }
        });
        btn2.setVisibility(View.INVISIBLE);
        btn3.setVisibility(View.INVISIBLE);
    }
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
                                text.get(noCorner).setText("Scanning ... "+countN+" %");
                                addNearlyDevice(device.getAddress());
                                if (countN>99) {
                                    text.get(noCorner).setText("Position : "+noCorner+" Finish");
                                    noCorner++;
                                    stopScan();
                                }
                            }
                    }
                }
            });
        }
    };
    public void addNearlyDevice(String macId){
        int i;
        for (i=0;i<listBle.size();i++){
            Log.d(macId+" == "+listBle.get(i).getMacId(),"checkLog");
            if (macId.equals(listBle.get(i).getMacId())){
                break;
            }
        }
        if (i==listBle.size()){
            int j;
            for (j=0;j<listNearCorner.size();j++){
                if (listNearCorner.get(j).getMacId().equals(macId)){
                    break;
                }
            }
            if (j==listNearCorner.size()) {
                listNearCorner.add(new nearCorner(macId,roomId,noCorner));
            }
        }
    }

    protected void startScan(){
        //Log.d("Start Scan", log);
        //btn.setText("Stop");
        listNearCorner=null;
        mBluetoothAdapter.startLeScan(mLeScanCallback);
    }
    protected void stopScan(){
        //Log.d("Stop Scan",log);
        //btn.setText("Start");
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
        switch (noCorner){
            case 0: btn1.setText("Scan BLE At Left-Up");
                break;
            case 1: btn1.setText("Scan BLE At Right-Up");
                break;
            case 2: btn1.setText("Scan BLE At Right-Down");
                break;
            case 3: btn1.setText("Scan BLE At Left-Down");
                break;
            case 4: btn1.setText("Send Table Corner");
                break;
        }
    }
}
