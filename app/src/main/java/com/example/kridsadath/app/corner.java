package com.example.kridsadath.app;

/**
 * Created by KridsadaTh on 1/3/2558.
 */
public class corner {
    private int roomId;
    private float x,y;
    public corner(int a,float b,float c){
        roomId=a;
        x=b;
        y=c;
    }
    public float getX(){
        return x;
    }
    public float getY(){
        return y;
    }
}
