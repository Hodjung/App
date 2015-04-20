package com.example.kridsadath.app;

import java.util.ArrayList;

/**
 * Created by KridsadaTh on 22/2/2558.
 */
public class building {
    int id;
    float latitude,longitude;
    String name;
    public building(int id,String name,float latitude,float longitude){
        this.id=id;
        this.name=name;
        this.latitude=latitude;
        this.longitude=longitude;
    }
    public building(String name,float latitude,float longitude){
        this.name=name;
        this.latitude=latitude;
        this.longitude=longitude;
    }
    public building(){}
    int getId(){return this.id;}
    String getName(){return this.name;}
    void setId(int id){this.id=id;}
    void setName(String name){this.name=name;}
    public Float getLatitude() {
        return this.latitude;
    }
    public Float getLongitude() {
        return this.longitude;
    }
}
