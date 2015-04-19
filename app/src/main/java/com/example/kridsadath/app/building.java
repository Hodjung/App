package com.example.kridsadath.app;

import java.util.ArrayList;

/**
 * Created by KridsadaTh on 22/2/2558.
 */
public class building {
    int id,num_floor;
    String name;
    public building(int id,String name,float latitude,float longitude){
        this.name=name;
    }
    public building(){}
    int getId(){return this.id;}
    String getName(){return this.name;}
    void setId(int id){this.id=id;}
    void setName(String name){this.name=name;}
    void setNumberFloor(String num_floor){this.num_floor= Integer.parseInt(num_floor);}

    public Float getLatitude() {
        return null;
    }
    public Float getLongitude() {
        return null;
    }
}
