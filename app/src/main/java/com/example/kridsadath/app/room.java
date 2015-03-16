package com.example.kridsadath.app;

/**
 * Created by KridsadaTh on 22/2/2558.
 */
public class room{
    int id,floorId;
    boolean isClose;
    double heightFloor;
    int width,height,rangeOfDevice;
    String name,detail;
    public room(String name,String detail,int floorId,boolean isClose,double heightFloor,int width,int height,int rangeOfDevice){
        this.floorId=floorId;
        this.isClose=isClose;
        this.name=name;
        this.detail=detail;
        this.heightFloor=heightFloor;
        this.height=height;
        this.width=width;
        this.rangeOfDevice=rangeOfDevice;
    }
    int getId(){return this.id;}
    int getFloorId() {return this.floorId;}
    String getName() {return this.name;}
    void setId(int id){this.id=id;}
    void setName(String name){this.name=name;}
    public Boolean getIsClose() {return this.isClose;}
    public double getHeightFloor() {return this.heightFloor;}
    public int getWidth() {return this.width;}
    public int getHeight() {return this.height;}
    public int getRange() {return this.rangeOfDevice;}
    public String getDetail() {return this.detail;}
}
