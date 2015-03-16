package com.example.kridsadath.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by KridsadaTh on 24/2/2558.
 */
public class config_room extends Activity {
    int placeId,floorId;
    floor currentFloor;
    room currentRoom;
    public config_room(){}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Database db;
        db=new Database(this);
        setContentView(R.layout.config_room);
        Bundle intent=getIntent().getExtras();
        floorId=Integer.parseInt(intent.getString("floorId"));
        placeId=Integer.parseInt(intent.getString("placeId"));
        currentFloor=db.getFloor(floorId);
        TextView head = (TextView)findViewById(R.id.name_floor);
        head.setText(currentFloor.getName());
        CheckBox type = (CheckBox)findViewById(R.id.openOrClose);
        type.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                TextView roomName = (TextView)findViewById(R.id.roomName);
                if (isChecked){
                    roomName.setVisibility(View.VISIBLE);
                }
                else {
                    roomName.setVisibility(View.INVISIBLE);
                }
            }
        });
        Button next = (Button)findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox checkBox=(CheckBox)findViewById(R.id.openOrClose);
                EditText name=(EditText)findViewById(R.id.roomName);
                EditText heightFloor=(EditText)findViewById(R.id.floorHeight);
                EditText width=(EditText)findViewById(R.id.width);
                EditText height=(EditText)findViewById(R.id.height);
                EditText range=(EditText)findViewById(R.id.range);
                if (!checkBox.isChecked())name.setText("Path");
                if (!heightFloor.getText().toString().isEmpty()||
                        !width.getText().toString().isEmpty()||
                        !height.getText().toString().isEmpty()||
                        !range.getText().toString().isEmpty()) {
                    currentRoom = new room(name.getText().toString(), "", currentFloor.getId(), checkBox.isChecked()
                            , Float.parseFloat(heightFloor.getText().toString()), Integer.parseInt(width.getText().toString())
                            , Integer.parseInt(height.getText().toString()), Integer.parseInt(range.getText().toString()));
                    currentRoom.setId(db.addRoom(currentRoom));
                    Intent myIntent=new Intent(config_room.this,record_page.class);
                    myIntent.putExtra("placeId",String.valueOf(placeId));
                    myIntent.putExtra("floorId",String.valueOf(floorId));
                    myIntent.putExtra("roomId",String.valueOf(currentRoom.getId()));
                    Log.d("placeId="+placeId+" floorId="+floorId+" roomId="+currentRoom.getId(),"checkLog");
                    startActivity(myIntent);
                    finish();
                }
            }
        });
        Button back = (Button)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent=new Intent(config_room.this,manage_floor.class);
                myIntent.putExtra("placeId",String.valueOf(placeId));
                myIntent.putExtra("floorId",String.valueOf(floorId));
                startActivity(myIntent);
                finish();
            }
        });
    }

}
