package com.example.kridsadath.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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
 * Created by KridsadaTh on 22/2/2558.
 */
public class create_page extends Activity {
    String log="checkLog";
    int placeId;
    String placeName;
    Database db;
    private List<floor> floor;
    ListView listView;
    ArrayAdapter<String> adapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_page);
        Bundle extras=getIntent().getExtras();
        placeId=Integer.parseInt(extras.getString("placeId"));
        TextView place=(TextView)findViewById(R.id.root_text);
        db=new Database(this);
        placeName=db.getPlace(placeId).getName();
        place.setText(placeName);
        setView();
        final AlertDialog.Builder boxOption = new AlertDialog.Builder(this);
        final String[] option_header = new String[] { "Manage", "Rename" };
        final ArrayAdapter<String> option = new ArrayAdapter<String>(this,android.R.layout.select_dialog_item, option_header);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                boxOption.setTitle("Option : "+floor.get(position).getName());
                boxOption.setAdapter(option,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which==0){
                            Intent myIntent=new Intent(create_page.this,manage_floor.class);
                            myIntent.putExtra("placeId",String.valueOf(placeId));
                            myIntent.putExtra("floorId",String.valueOf(floor.get(position).getId()));
                            Log.d(floor.get(position).getId()+"","checkLog");
                            startActivity(myIntent);
                            finish();
                        }
                        else {
                            boxRename(floor.get(position));
                        }
                    }
                });
                boxOption.show();
            }
        });
        Button btn_confirm=(Button)findViewById(R.id.confirm);
        Button btn_delete=(Button)findViewById(R.id.delete);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(create_page.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i=0;i<floor.size();i++){
                    List<room> room=db.getAllRoom(floor.get(i).getId());
                    if (room!=null) {
                        for (int j = 0; j < room.size(); j++) {
                            List<ble> ble = db.getAllBle(room.get(j).getId());
                            if (ble!=null) {
                                for (int k = 0; k < ble.size(); k++) {
                                    db.deleteBle(ble.get(k));
                                }
                            }
                            db.deleteRoom(room.get(j));
                        }
                    }
                    db.deleteFloor(floor.get(i));
                }
                db.deletePlace(placeId);
                Intent intent=new Intent(create_page.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    void boxRename (final floor floor){
        AlertDialog.Builder renameBox = new AlertDialog.Builder(this);
        renameBox.setTitle("Rename " + floor.getName());
        renameBox.setMessage("Rename to");
        final EditText input = new EditText(this);
        final Database db=new Database(this);
        renameBox.setView(input);
        renameBox.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                floor.setName(input.getText().toString());
                db.updateFloor(floor);
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
        floor=db.getAllFloor(placeId);
        ArrayList<String> list=new ArrayList<String>();
        for (int i=0;i<floor.size();i++){
            list.add(floor.get(i).getName());
        }
        if (floor.size()==0)list.add("Empty");
        listView=(ListView)findViewById(R.id.listView);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,android.R.id.text1,list);
        listView.setAdapter(adapter);
    }
}
