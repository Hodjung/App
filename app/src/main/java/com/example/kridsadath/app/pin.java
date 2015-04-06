package com.example.kridsadath.app;

public class pin{
    private int roomId;
    private String detail;
    private float x,y;
    public pin(int roomId,String a,float b,float c){
        this.roomId=roomId;
        detail=a;
        x=b;
        y=c;
    }
    public int getRoomId(){
        return roomId;
    }
    public String getDetail(){
        return detail;
    }
    public float getX(){
        return x;
    }
    public float getY(){
        return y;
    }
}
