package com.example.kridsadath.app;

/**
 * Created by KridsadaTh on 22/2/2558.
 */
public class ble {
    int id,roomId,position;
    String macId;
    public ble(String macId,int roomId,int position){
        this.macId=macId;
        this.roomId=roomId;
        this.position=position;
    }
    void setId(int id){this.id=id;}
    int getId(){return this.id;}
    String getMacId() {return this.macId;}
    int getRoomId() {return this.roomId;}
    int getPosition() {return this.position;}
}
