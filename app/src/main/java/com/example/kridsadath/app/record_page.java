package com.example.kridsadath.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by KridsadaTh on 24/2/2558.
 */
public class record_page extends Activity {
    int buildingId,floorId,roomId;
    floor currentFloor;
    room currentRoom;
    Database db;
    public record_page(){}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("record page","checkLog");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_page);
        Bundle intent = getIntent().getExtras();
        buildingId=Integer.valueOf(intent.getString("buildingId"));
        floorId=Integer.valueOf(intent.getString("floorId"));
        roomId=Integer.valueOf(intent.getString("roomId"));
        Log.d("buildingId="+buildingId+" floorId="+floorId+" roomId="+roomId,"checkLog");
        db=new Database(this);
        currentFloor=db.getFloor(floorId);
        currentRoom=db.getRoom(roomId);
        TextView name_floor=(TextView)findViewById(R.id.name_floor);
        name_floor.setText(currentFloor.getName());
        TextView name_room=(TextView)findViewById(R.id.roomName);
        name_room.setText(currentRoom.getName());
        Button ble=(Button)findViewById(R.id.menu1);
        Button corner=(Button)findViewById(R.id.menu2);
        /*Button door=(Button)findViewById(R.id.menu3);
        Button pin=(Button)findViewById(R.id.menu4);
        Button test=(Button)findViewById(R.id.menu5);*/
        ble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(record_page.this,scan_ble.class);
                myIntent.putExtra("buildingId",String.valueOf(buildingId));
                myIntent.putExtra("floorId",String.valueOf(floorId));
                myIntent.putExtra("roomId",String.valueOf(roomId));
                startActivity(myIntent);
                //finish();
                //swap_page(SCAN_PAGE);
            }
        });
        corner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(record_page.this,menu_ble.class);
                myIntent.putExtra("roomId",String.valueOf(roomId));
                myIntent.putExtra("menu","corner");
                startActivity(myIntent);
            }
        });
        /*door.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(record_page.this,menu_ble.class);
                myIntent.putExtra("roomId",String.valueOf(roomId));
                myIntent.putExtra("menu","door");
                startActivity(myIntent);
            }
        });
        pin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(record_page.this,menu_ble.class);
                myIntent.putExtra("roomId",String.valueOf(roomId));
                myIntent.putExtra("menu","pin");
                startActivity(myIntent);
            }
        });
        //test.setVisibility(View.INVISIBLE);
        test.setText("Position");
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(record_page.this,menu_ble.class);
                myIntent.putExtra("roomId",String.valueOf(roomId));
                myIntent.putExtra("menu","test");
                startActivity(myIntent);
            }
        });*/
        Button finish=(Button)findViewById(R.id.finish);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //swap_page(ADD_ROOM_PAGE);
                Intent myIntent=new Intent(record_page.this,manage_floor.class);
                myIntent.putExtra("buildingId",String.valueOf(buildingId));
                myIntent.putExtra("floorId",String.valueOf(floorId));
                startActivity(myIntent);
                finish();
            }
        });
    }
}
