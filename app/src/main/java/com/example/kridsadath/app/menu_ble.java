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
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by KridsadaTh on 1/3/2558.
 */

public class menu_ble extends Activity {
    int MAX_N=3;
    double MAX_CIRCLE=5;
    int roomId , w , h ;
    Database db;
    room currentRoom;
    DrawLine line;
    float plotX,plotY,oldPlotX,oldPlotY;
    List<DrawInLayout> device;
    List<ble> listBle,sortedBle;
    Button btn1,btn2,btn3;
    String con;
    int menu;
    RelativeLayout layout;
    List<Integer> listCount;
    List<Integer> listRssi;
    List<corner> listCorner;
    List<Integer> listDoor;
    List<Integer> listPin;
    List<DrawCircle> listCircle;
    double DISTANCE=1.0; //ระยะทดแทน step ละไม่เกิน 0.5
    double BETWEEN_DEVICE;
    double HEIGHT_OF_FLOOR;
    int NUMBER_DATA=4;
    boolean isPosition;
    int rssiMaxUnit=-80;
    float a,b,c,d;
    DrawInLayout user;
    int wLayout, hLayout;
    int scale , begin ;
    int unitX,unitY,oldUnitX,oldUnitY;
    double oldX,oldY;
    double uTargX,uTargY,uCurX,uCurY;
    TextView textX,textY;
    double realX,realY;
    int countN;
    int noCorner;
    public menu_ble(){}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cdpt);
        Bundle extras=getIntent().getExtras();
        roomId=Integer.parseInt(extras.getString("roomId"));
        con=extras.getString("menu");
        textX=(TextView)findViewById(R.id.x);
        textY=(TextView)findViewById(R.id.y);
        if (con.equals("corner"))
            menu=1;
        else if (con.equals("door"))
            menu=2;
        else if (con.equals("pin"))
            menu=3;
        else menu=4;
        db=new Database(this);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }
        listBle=db.getAllBle(roomId);
        for (int i=0;i<listBle.size();i++){
            Log.d(listBle.get(i).getId()+" "+listBle.get(i).getMacId()+" "+listBle.get(i).getRoomId()+" "+listBle.get(i).getPosition(),"checkLog");
        }
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
            countN=0;
            oldX=0;
            oldY=0;
            uTargX = -1000;
            uTargY = -1000;
            rssiMaxUnit=-100;
            listCount=new ArrayList<Integer>();
            listRssi=new ArrayList<Integer>();
            listCircle=new ArrayList<DrawCircle>();
            for (int i=0;i<listBle.size();i++){
                listCount.add(0);
                listRssi.add(-100);
            }
            currentRoom = db.getRoom(roomId);
            HEIGHT_OF_FLOOR=currentRoom.getHeightFloor();
            BETWEEN_DEVICE=currentRoom.getRange();
            w = currentRoom.getWidth();
            h = currentRoom.getHeight();
            layout = (RelativeLayout) findViewById(R.id.layout);
            scale = 100;
            begin = 100;
            if (w > 4 || h > 7) {
                scale = 50;
                begin = 50;
            }
            //TODO:Responsive Scale
            wLayout = (w * scale) + 100;
            hLayout = (h * scale) + 100;
            drawDevicePlan();
            line = new DrawLine(this, layout);
            btn1=(Button)findViewById(R.id.btn1);
            btn2=(Button)findViewById(R.id.btn2);
            btn3=(Button)findViewById(R.id.btn3);
            TextView txt=(TextView)findViewById(R.id.menuName);
            realX=-1;
            realY=-1;
            unitX=-1;
            unitY=-1;
            user=new DrawInLayout(this,layout,R.drawable.user);
            user.setStickSize(20, 20);
            user.setLayoutSize(wLayout, hLayout);
            user.setLayoutAlpha(100);
            user.setStickAlpha(100);
            user.drawPoint(wLayout/2,hLayout/2);
            for (int i=0;i<4;i++) {
                listCircle.add(new DrawCircle(this, layout));
            }
            listCorner=db.getCorner(roomId);
            if (listCorner!=null){
                for (int i=0;i<listCorner.size();i++){
                    line.add_point(listCorner.get(i).getX(),listCorner.get(i).getY(),
                            (listCorner.get(i).getX()*scale/4)+100,
                            (listCorner.get(i).getY()*scale/4)+100);
                }
                line.drawLine(false);
            }
            switch (menu){
                case 1: corner();
                    txt.setText("Add Corner");
                    break;
                case 2: door();
                    txt.setText("Add Door");
                    break;
                case 3: pin();
                    txt.setText("Add Pin");
                    break;
                case 4: test();
                    txt.setText("Test Position");
                    break;
            }
        }
    }
    void corner(){
        /*layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                line.add_point(event.getX()*4/100,event.getY()*4/100,event.getX(),event.getY());
                line.drawLine(true);
                return false;
            }
        });*/
        noCorner=0;
        line.add_point(-2,-2,50,50);
        line.add_point((w-1)*4+2,-2,(w-1)*100+150,50);
        line.add_point((w-1)*4+2,(h-1)*4+2,(w-1)*100+150,(h-1)*100+150);
        line.add_point(-2,(h-1)*4+2,50,(h-1)*100+150);
        line.add_point(-2,-2,50,50);
        line.drawLine(false);
        btn1.setVisibility(View.INVISIBLE);
        btn1.setText("Scan Corner "+noCorner);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btn1.getText() == "Scan Corner 0") {
                    btn1.setText("Scanning Corner "+noCorner);
                    btn2.setVisibility(View.VISIBLE);
                    startScan();
                }
            }
        });
        btn2.setVisibility(View.INVISIBLE);
        btn2.setText("Back 1 Step");
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (line.getSize() == 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(menu_ble.this);
                    builder.setTitle("Error");
                    builder.setMessage("Can't Back : No Corner");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.show();
                } else {
                    btn3.setText("Connect");
                    line.remove_last_point();
                    line.drawLine(true);
                }
            }
        });
        //btn3.setText("Connect");
        btn3.setText("Confirm");
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btn3.getText() == "Connect") {
                    if (line.getSize()>2) {
                        stopScan();
                        line.add_point(line.getFirstX(), line.getFirstY(),line.getFirstRealX(),line.getFirstRealY());
                        line.drawLine(true);
                        btn3.setText("Confirm");
                    }
                    else {
                        AlertDialog.Builder builder=new AlertDialog.Builder(menu_ble.this);
                        builder.setTitle("Error");
                        builder.setMessage("Can't Connect : Need more Corner");
                        builder.setPositiveButton("OK",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        builder.show();
                    }
                }
                else {
                    //Add Corner To Room , Update Room
                    db.deleteCorner(roomId);
                    for (int i=0;i<line.getSize();i++){
                        int id=db.addCorner(roomId,i,line.getX(i),line.getY(i));
                        Log.d(id+":"+roomId+" "+i+" "+line.getX(i)+" "+line.getY(i),"checkLog");
                    }
                    finish();
                }
            }
        });
    }
    void door(){
        final List<DrawInLayout> door=new ArrayList<DrawInLayout>();
        btn1.setText("Start Scan");
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btn1.getText() == "Start Scan") {
                    startScan();
                    btn1.setText("Add Door");
                } else {
                    door.add(new DrawInLayout(menu_ble.this, layout, R.drawable.door));
                    door.get(door.size()-1).setLayoutAlpha(100);
                    door.get(door.size()-1).setStickAlpha(100);
                    door.get(door.size()-1).setLayoutSize(wLayout, hLayout);
                    door.get(door.size()-1).setStickSize(20, 20);
                    door.get(door.size()-1).drawPoint((float) realX, (float) realY);
                }
            }
        });
        btn2.setText("Remove");
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (door.size()>0) {
                    door.get(door.size()-1).delete();
                    door.remove(door.get(door.size()-1));
                }
            }
        });
        btn3.setText("Confirm");
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopScan();
                for (int i=0;i<door.size();i++){
                    db.addDoor(roomId, (float)oldX, (float)oldY);
                }
                finish();
            }
        });
        if (listCorner==null){
            //show Error
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setTitle("Error");
            builder.setMessage("Need corner");
            builder.setPositiveButton("OK",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.show();
        }
    }
    void pin(){
        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return false;
            }
        });
        final List<DrawInLayout> pin=new ArrayList<DrawInLayout>();
        btn1.setVisibility(View.INVISIBLE);
        btn1.setText("Start Scan");
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btn1.getText() == "Start Scan") {
                    startScan();
                    btn1.setText("Add Pin");
                } else {
                    pin.add(new DrawInLayout(menu_ble.this, layout, R.drawable.pin));
                    pin.get(pin.size() - 1).setLayoutAlpha(100);
                    pin.get(pin.size() - 1).setStickAlpha(100);
                    pin.get(pin.size() - 1).setLayoutSize(wLayout, hLayout);
                    pin.get(pin.size() - 1).setStickSize(20, 20);
                    pin.get(pin.size() - 1).drawPoint(plotX, plotY);
                    //db.addPin(roomId, "Detail", (float)oldX, (float)oldY);
                }
            }
        });
        btn2.setText("Remove");
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pin.size()>0) {
                    pin.get(pin.size() - 1).delete();
                    pin.remove(pin.size()-1);
                }
            }
        });
        btn3.setText("Confirm");
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopScan();
                for (int i=0;i<pin.size();i++){
                    //add pin to database
                }
                finish();
            }
        });
    }
    void test(){
        btn1.setText("Start");
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btn1.getText()=="Start"){
                    btn1.setText("Stop");
                    startScan();
                }
                else {
                    btn1.setText("Start");
                    for (int i=0;i<listBle.size();i++){
                        listCount.set(i,0);
                        listRssi.set(i,-100);
                    }
                    stopScan();
                }
            }
        });
        btn2.setVisibility(View.INVISIBLE);
        btn3.setText("Back");
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopScan();
                finish();
            }
        });
    }
    void drawDevicePlan(){
        device = new ArrayList<DrawInLayout>();
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                device.add(new DrawInLayout(this, layout, R.drawable.device));
                device.get(device.size() - 1).setLayoutSize(wLayout, hLayout);
                device.get(device.size() - 1).setLayoutAlpha(100);
                device.get(device.size() - 1).setStickSize(25, 25);
                device.get(device.size() - 1).setStickAlpha(100);
                device.get(device.size() - 1).drawPoint((i * scale) + begin, (j * scale) + begin);
            }
        }
    }
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothAdapter.LeScanCallback mLeScanCallback =new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {//HMSoft
                    if (device.getName().equals("UT9")) {
                        check(device.getAddress(), rssi);
                        //Log.d(device.getName()+""+rssi,"checkLog");
                    }
                    //else Log.d(device.getName()+"","checkLog");
                    userWalk();
                }
            });
        }
    };
    public void check(String macId,int rssi){
        int p=-1;
        for (int i = 0; i < listBle.size(); i++) {
            if (listBle.get(i).getMacId().equals(macId)) {
                p = i;
                break;
            }
        }
        if (p!=-1){
            countN++;
            isPosition=false;
            listCount.set(p,listCount.get(p)+1);
            if (rssi>listRssi.get(p))
                listRssi.set(p,rssi);
            /*for (int i=0;i<w-1;i++) {
                for (int j=0;j<h-1;j++){
                    if (listCount.get(i*w+j)>3
                            &&listCount.get(i*w+j+1)>3
                            &&listCount.get((i+1)*w+j)>3
                            &&listCount.get((i+1)*w+j+1)>3){
                        //save unitX unitY
                        unitX=j;
                        unitY=i;
                        //send to position
                        stopScan();
                        Log.d(""+(i*w+j)+(i*w+j+1)+((i+1)*w+j)+((i+1)*w+j+1),"checkLog");
                        Log.d("" + listRssi.get(i * w + j) + listRssi.get(i * w + j + 1) + listRssi.get((i + 1) * w + j) + listRssi.get((i + 1) * w + j + 1), "checkLog");
                        position(listRssi.get(i * w + j), listRssi.get(i * w + j + 1), listRssi.get((i + 1) * w + j), listRssi.get((i + 1) * w + j + 1));
                        listRssi.set(i * w + j, -100);
                        listRssi.set(i*w+j+1,-100);
                        listRssi.set((i+1)*w+j,-100);
                        listRssi.set((i+1)*w+j+1,-100);
                        listCount.set(i*w+j,0);
                        listCount.set(i*w+j+1,0);
                        listCount.set((i+1)*w+j,0);
                        listCount.set((i + 1) * w + j + 1, 0);
                        startScan();
                    }
                }
            }*/
            /*for (int i=0;i<h-1;i++) {
                for (int j=0;j<w-1;j++){
                    if (listCount.get(i*w+j)>MAX_N
                            &&listCount.get(i*w+j+1)>MAX_N
                            &&listCount.get((i+1)*w+j)>MAX_N
                            &&listCount.get((i+1)*w+j+1)>MAX_N){
                        //save unitX unitY
                        unitX=j;
                        unitY=i;
                        //send to position
                        stopScan();
                        Log.d(""+(i*w+j)+(i*w+j+1)+((i+1)*w+j)+((i+1)*w+j+1),"checkLog");
                        Log.d("" + listRssi.get(i * w + j) + listRssi.get(i * w + j + 1) + listRssi.get((i + 1) * w + j) + listRssi.get((i + 1) * w + j + 1), "checkLog");
                        position(listRssi.get(i * w + j), listRssi.get(i * w + j + 1), listRssi.get((i + 1) * w + j), listRssi.get((i + 1) * w + j + 1));
                        for (int k=0;k<listRssi.size();k++){
                            listRssi.set(k,-100);
                            listCount.set(k,0);
                        }
                        startScan();
                    }
                }
            }*/
            if (countN>100) {
                stopScan();
                int maxUnit=-10000;
                for (int i = 0; i < h - 1; i++) {
                    for (int j = 0; j < w - 1; j++) {
                        if (maxUnit<listRssi.get(i * w + j)
                                + listRssi.get(i * w + j + 1)
                                + listRssi.get((i + 1) * w + j)
                                + listRssi.get((i + 1) * w + j + 1)){
                            maxUnit=listRssi.get(i * w + j) + listRssi.get(i * w + j + 1) + listRssi.get((i + 1) * w + j) + listRssi.get((i + 1) * w + j + 1);
                            unitX=j;
                            unitY=i;
                        }
                        /*if (maxUnit<listCount.get(i * w + j) + listCount.get(i * w + j + 1) + listCount.get((i + 1) * w + j) + listCount.get((i + 1) * w + j + 1)) {
                            maxUnit=listCount.get(i * w + j) + listCount.get(i * w + j + 1) + listCount.get((i + 1) * w + j) + listCount.get((i + 1) * w + j + 1);
                            //save unitX unitY
                            unitX = j;
                            unitY = i;
                        }*/
                    }
                }
                position(listRssi.get(unitY * w + unitX), listRssi.get(unitY * w + unitX + 1), listRssi.get((unitY + 1) * w + unitX), listRssi.get((unitY + 1) * w + unitX + 1));
                for (int k = 0; k < listRssi.size(); k++) {
                    listRssi.set(k, -100);
                    listCount.set(k, 0);
                }
                countN=0;
                startScan();
            }
        }
    }
    public void userWalk(){
        if(uTargX>uCurX) uCurX+=0.8;
        else uCurX-=0.8;
        if(uTargY>uCurY) uCurY+=0.8;
        else uCurY-=0.8;
        user.drawPoint((float) uCurX, (float) uCurY);
    }
    public void position(int a,int b,int c,int d){
        double da,db,dc,dd;
        da=functionDistance(a);
        if (da>MAX_CIRCLE)da=MAX_CIRCLE;
        db=functionDistance(b);
        if (db>MAX_CIRCLE)db=MAX_CIRCLE;
        dc=functionDistance(c);
        if (dc>MAX_CIRCLE)dc=MAX_CIRCLE;
        dd=functionDistance(d);
        if (dd>MAX_CIRCLE)dd=MAX_CIRCLE;
        double x,y;

        if (da+db>4&&dc+dd>4){
            x=(functionF(da,db)+functionF(dc,dd))/2;
        }
        else if (da+db>4&&dc+dd<4){
            x=functionF(da,db)/2+1;
        }
        else if (da+db<4&&dc+dd>4){
            x=functionF(dc,dd)/2+1;
        }
        else {
            x=2;
        }
        if (da+dc>4&&db+dd>4) {
            y = (functionF(da, dc) + functionF(db, dd)) / 2;
        }
        else if (da+dc>4&&db+dd<4) {
            y = functionF(da, dc)/2+1;
        }
        else if (da+dc<4&&db+dd>4) {
            y = functionF(db, dd)/2+1;
        }
        else y=2;
        //double scale=200/BETWEEN_DEVICE;
        realX=(x*scale/4)+begin+(unitX*scale);
        realY=(y*scale/4)+begin+(unitY*scale);
        listCircle.get(0).setXYR(begin+(unitX*scale),begin+(unitY*scale),(float)da*scale/4);
        listCircle.get(1).setXYR(begin+(unitX*scale)+100,begin+(unitY*scale),(float)db*scale/4);
        listCircle.get(2).setXYR(begin+(unitX*scale),begin+(unitY*scale)+100,(float)dc*scale/4);
        listCircle.get(3).setXYR(begin+(unitX*scale)+100,begin+(unitY*scale)+100,(float)dd*scale/4);
        for (int i=0;i<4;i++){
            listCircle.get(i).drawCircle();
        }
        /*realX=begin+(unitX*scale)+50;
        realY=begin+(unitY*scale)+50;*/

        //
        oldX=x;
        oldY=y;
        if(uTargX < -900 && uTargY < -900){
            uCurX = realX;
            uCurY = realY;
        }
        uTargX = realX;
        uTargY = realY;
        //user.drawPoint((float) realX, (float) realY);
        //
        /*if (oldX==0&&oldY==0) {
            oldX = x;
            oldY = y;
            user.drawPoint((float) realX+(unitX*scale/4), (float) realY+(unitY*scale/4));
            Log.d(""+realX+" "+realY,"checkLog");
        }
        else {
            double dx=x-oldX,dy=y-oldY;
            double dSqrt=Math.sqrt((dx*dx)+(dy*dy));
            if (x<-1.75||x>5.25||y<-1.75||y>5.25){
                realX=(oldX*scale/4)+100;
                realY=(oldY*scale/4)+100;
                Log.d("Out Range","checkLog");
            }
            else if (dSqrt>DISTANCE){
                double newX,newY;
                newX=(DISTANCE*dx/dSqrt)+oldX;
                newY=(DISTANCE*dy/dSqrt)+oldY;
                oldX=newX;
                oldY=newY;
                realX=(newX*scale/4)+100;
                realY=(newY*scale/4)+100;
                Log.d("too long","checkLog");
            }
            else {
                oldX=x;
                oldY=y;
                Log.d("plot","checkLog");
            }
            user.drawPoint((float) realX+(unitX*scale/4), (float) realY+(unitY*scale/4));
            Log.d("X,Y "+oldX+" "+oldY,"checkLog");
            Log.d("DRAW X,Y "+realX+" "+realY,"checkLog");
        }*/
        DecimalFormat df=new DecimalFormat("0.00");
        //df.format();
        textX.setText("X :"+df.format(x)+ " >"+df.format(realX)+" UnitX="+unitX+" UnitY="+unitY);
        textY.setText("Y :"+df.format(y)+ " >"+df.format(realY)+" UnitX="+unitX+" UnitY="+unitY);
        //user.drawPoint(0,0);
    }
    public double functionF(double a,double b){
        return (((a*a)-(b*b)+(HEIGHT_OF_FLOOR*HEIGHT_OF_FLOOR))/(2*HEIGHT_OF_FLOOR));
    }
    //-0.0029  0.7114  -31.621
    public double functionDistance(double a){
        a*=-1;
        return ((-0.0029*a*a)+(0.7114*a)-31.621);
        //return 21.3*Math.log1p(a)-86.361;
    }
    protected void startScan(){
        //Log.d("Start Scan", log);
        //btn.setText("Stop");
        mBluetoothAdapter.startLeScan(mLeScanCallback);
    }
    protected void stopScan(){
        //Log.d("Stop Scan",log);
        //btn.setText("Start");
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
    }
}
