package com.example.kridsadath.app;

/**
 * Created by KridsadaTh on 17/3/2558.
 */
public class nearCorner {
    private int roomId,noCorner;
    private String macId;
    public nearCorner(int roomId,int noCorner, String macId) {
        this.roomId=roomId;
        this.noCorner=noCorner;
        this.macId=macId;
    }
    public int getRoomId(){
        return roomId;
    }
    public int getNoCorner(){
        return noCorner;
    }
    public String getMacId(){
        return macId;
    }
}
