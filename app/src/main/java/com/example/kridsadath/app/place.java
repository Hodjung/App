package com.example.kridsadath.app;

import java.util.ArrayList;

/**
 * Created by KridsadaTh on 22/2/2558.
 */
public class place {
    int id,num_floor;
    String name;
    ArrayList<floor> floor;
    public place(String name,int n_floor){
        this.name=name;
        this.num_floor=n_floor;
    }
    public place(){}
    int getId(){return this.id;}
    String getName(){return this.name;}
    int getNumberFloor(){return this.num_floor;}
    void setId(int id){this.id=id;}
    void setName(String name){this.name=name;}
    void setNumberFloor(String num_floor){this.num_floor= Integer.parseInt(num_floor);}
}
