package com.example.kridsadath.app;

import java.util.ArrayList;

/**
 * Created by KridsadaTh on 22/2/2558.
 */
public class floor{
    int id,placeId;
    String name,imageId;
    public floor(int id,String name,int placeId,String imageId){
        this.id=id;
        this.name=name;
        this.placeId=placeId;
        this.imageId=imageId;
    }
    public floor(String name,int placeId,String imageId){
        this.name=name;
        this.placeId=placeId;
        this.imageId=imageId;
    }

    public floor(floor floor) {
        this.setId(floor.getId());
        this.name=floor.getName();
        this.placeId=floor.getPlaceId();
    }

    void setId(int n){
        this.id=n;
    }
    void setName(String name){
        this.name=name;
    }
    String getName(){
        return name;
    }
    int getId(){
        return id;
    }
    int getPlaceId(){return placeId;}
    String getImageId(){return imageId;}
}

