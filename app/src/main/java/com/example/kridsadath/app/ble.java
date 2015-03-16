package com.example.kridsadath.app;

/**
 * Created by KridsadaTh on 22/2/2558.
 */
public class ble {
    int id,roomId;
    String macId,position;
    public ble(String macId,int roomId,String position){
        this.macId=macId;
        this.roomId=roomId;
        this.position=position;
    }
    void setId(int id){this.id=id;}
    int getId(){return this.id;}
    String getMacId() {return this.macId;}
    int getRoomId() {return this.roomId;}
    String getPosition() {return this.position;}
}
