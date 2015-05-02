package com.example.kridsadath.app;

/**
 * Created by KridsadaTh on 22/2/2558.
 */
public class room{
    int id,floorId;
    double heightFloor;
    int width,height,rangeOfDevice;
    String name,detail,isClose;
    public room(int id,String name,String detail,int floorId,String isClose,double heightFloor,int width,int height,int rangeOfDevice){
        this.id=id;
        this.floorId=floorId;
        this.isClose=isClose;
        this.name=name;
        this.detail=detail;
        this.heightFloor=heightFloor;
        this.height=height;
        this.width=width;
        this.rangeOfDevice=rangeOfDevice;
    }
    public room(String name,String detail,int floorId,String isClose,double heightFloor,int width,int height,int rangeOfDevice){
        this.floorId=floorId;
        this.isClose=isClose;
        this.name=name;
        this.detail=detail;//TODO:none
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
    public int getIsClose() {
        return Integer.parseInt(this.isClose);
    }
    public double getHeightFloor() {return this.heightFloor;}
    public int getWidth() {return this.width;}
    public int getHeight() {return this.height;}
    public int getRange() {return this.rangeOfDevice;}
    public String getDetail() {return this.detail;}
}
