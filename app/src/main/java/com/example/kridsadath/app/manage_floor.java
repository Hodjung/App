package com.example.kridsadath.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KridsadaTh on 24/2/2558.
 */
public class manage_floor extends Activity {
    int floorId,buildingId;
    ArrayList<String> group;
    ListView planRoom;
    ArrayAdapter<String> adapterRoom;
    List<room> listRoom;
    room currentRoom;
    Database db;
    public manage_floor(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_floor_page);
        Bundle extras=getIntent().getExtras();
        floorId=Integer.parseInt(extras.getString("floorId"));
        buildingId=Integer.parseInt(extras.getString("buildingId"));
        Log.d("TEST:"+floorId,"checkLog");
        final floor currentFloor;
        db=new Database(this);
        Log.d("buildingId ID="+buildingId,"checkLog");
        currentFloor=db.getFloor(floorId);
        listRoom=db.getAllRoom(currentFloor.getId());
        planRoom = (ListView)findViewById(R.id.listView);
        initRoom();
        TextView name_floor = (TextView)findViewById(R.id.name_floor);
        name_floor.setText(currentFloor.getName());
        Button addRoom = (Button)findViewById(R.id.addRoom);
        addRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent=new Intent(manage_floor.this,config_room.class);
                myIntent.putExtra("buildingId",String.valueOf(buildingId));
                myIntent.putExtra("floorId",String.valueOf(currentFloor.getId()));
                startActivity(myIntent);
                finish();
            }
        });
        Button back = (Button)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent=new Intent(manage_floor.this,create_page.class);
                myIntent.putExtra("buildingId",String.valueOf(buildingId));
                startActivity(myIntent);
                finish();
            }
        });
    }

    protected void initPageRoom(){
        Log.d("initPageRoom","checkLog");
        final AlertDialog.Builder boxOption = new AlertDialog.Builder(this);
        final String[] option_header = new String[] { "Manage", "Rename", "Delete" };
        final ArrayAdapter<String> option = new ArrayAdapter<String>(this,android.R.layout.select_dialog_item, option_header);
        adapterRoom = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,android.R.id.text1,group);
        planRoom.setAdapter(adapterRoom);
        planRoom.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if (listRoom!=null){
                    boxOption.setTitle("Option : "+listRoom.get(position).getName());
                    boxOption.setAdapter(option,new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which==0){
                                currentRoom=listRoom.get(position);
                                Log.d("check"+buildingId+" "+floorId+" "+currentRoom.getId(),"checkLog");
                                Intent myIntent = new Intent(manage_floor.this,record_page.class);
                                myIntent.putExtra("buildingId",String.valueOf(buildingId));
                                myIntent.putExtra("floorId",String.valueOf(floorId));
                                myIntent.putExtra("roomId",String.valueOf(currentRoom.getId()));
                                startActivity(myIntent);
                                finish();
                                //swap_page(RECORD_PAGE);
                            }
                            else if (which==1) {
                                boxRename(listRoom.get(position));
                            }
                            else {
                                //TODO:delete
                                setView();
                            }
                        }
                    });
                    boxOption.show();
                }
            }
        });
    }
    protected void initRoom(){
        Log.d("initRoom","checkLog");
        group = new ArrayList<String>();
        listRoom=db.getAllRoom(floorId);
        if (listRoom==null){
            group.add("Empty");
        }
        else {
            for (room ro:listRoom){
                group.add(ro.getName());
            }
        }
        initPageRoom();
    }
    void boxRename (final room room){
        AlertDialog.Builder renameBox = new AlertDialog.Builder(this);
        renameBox.setTitle("Rename " + room.getName());
        renameBox.setMessage("Rename to");
        final EditText input = new EditText(this);
        final Database db=new Database(this);
        renameBox.setView(input);
        renameBox.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                room.setName(input.getText().toString());
                db.updateRoom(room);// not syn
                setView();
            }
        });
        renameBox.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        renameBox.show();
    }
    public void setView(){
        listRoom=db.getAllRoom(floorId);
        ArrayList<String> list=new ArrayList<String>();
        for (int i=0;i<listRoom.size();i++){
            list.add(listRoom.get(i).getName());
        }
        if (listRoom.size()==0)list.add("Empty");
        planRoom=(ListView)findViewById(R.id.listView);
        adapterRoom = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,android.R.id.text1,list);
        planRoom.setAdapter(adapterRoom);
    }
}
