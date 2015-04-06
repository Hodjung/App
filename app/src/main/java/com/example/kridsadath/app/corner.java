package com.example.kridsadath.app;

/**
 * Created by KridsadaTh on 1/3/2558.
 */
public class corner {
    private int roomId,noCorner;
    private float x,y;
    public corner(int a){
        roomId=a;
    }
    public int getRoomId(){
        return roomId;
    }
    public void setX(int a){
        x=a;
    }
    public void setY(int a){
        y=a;
    }
    public float getX() {
        return x;
    }
    public float getY() {
        return y;
    }
    public void setNoCorner(int i) {
        this.noCorner=i;
    }
}
