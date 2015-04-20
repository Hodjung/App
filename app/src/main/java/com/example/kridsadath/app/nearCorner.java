package com.example.kridsadath.app;

/**
 * Created by KridsadaTh on 17/3/2558.
 */
public class nearCorner {
    private int roomId,position;
    private String macId;
    public nearCorner(String macId,int roomId,int position) {
        this.macId=macId;
        this.roomId=roomId;
        this.position=position;
    }
    public String getMacId(){
        return macId;
    }
    public int getRoomId(){
        return roomId;
    }
    public int getPosition(){
        return position;
    }
}
