package com.example.kridsadath.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KridsadaTh on 22/2/2558.
 */
final class Database extends SQLiteOpenHelper {
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "placesManager";

    // buildingManager table name
    private static final String TABLE_BUILDING = "building";
    private static final String TABLE_FLOOR = "floor";
    private static final String TABLE_ROOM = "room";
    private static final String TABLE_BLE = "ble";
    private static final String TABLE_CORNER = "corner";
    private static final String TABLE_PIN = "pin";

    // Building Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    //private static final String KEY_FLOOR = "number_floor";

    // Floors Table Columns names
    //private static final String KEY_ID = "id";
    //private static final String KEY_NAME = "name";
    private static final String KEY_BUILDING_ID = "buildingId";
    private static final String KEY_IMAGE_ID = "imageId";

    // Rooms Table Columns names
    //private static final String KEY_ID = "id";
    //private static final String KEY_NAME = "name";
    private static final String KEY_DETAIL = "detail";
    private static final String KEY_HEIGHT = "height";
    private static final String KEY_IS_CLOSE = "isClose";
    private static final String KEY_WIDTH = "width";
    private static final String KEY_DEPTH = "depth";
    private static final String KEY_RANGE = "range";
    private static final String KEY_FLOOR_ID = "floorId";

    // BLE Table Columns names
    //private static final String KEY_ID = "id";
    private static final String KEY_MAC = "macAddress";
    private static final String KEY_ROOM_ID = "roomId";
    private static final String KEY_POSITION = "position";

    // Corner Table Columns names
    //private static final String KEY_MAC = "macAddress";
    //private static final String KEY_ROOM_ID = "roomId";
    //private static final String KEY_POSITION = "x";

    // Pin Table Columns names
    //private static final String KEY_ID = "id";
    //private static final String KEY_FLOOR_ID = "floorId";
    //private static final String KEY_NAME = "name";
    private static final String KEY_INFO = "information";
    private static final String KEY_TYPE = "type";
    private static final String KEY_X = "x";
    private static final String KEY_Y = "y";


    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("onCreateInDatabase", "checkLog");
        String CREATE_BUILDING_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_BUILDING + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_NAME + " TEXT," + KEY_LATITUDE + " FLOAT," + KEY_LONGITUDE + " FLOAT)";
        db.execSQL(CREATE_BUILDING_TABLE);

        String CREATE_FLOOR_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_FLOOR + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_NAME + " TEXT," + KEY_BUILDING_ID + " INTEGER," +KEY_IMAGE_ID+ " TIMESTAMP DEFAULT CURRENT_TIMESTAMP )";
        db.execSQL(CREATE_FLOOR_TABLE);

        String CREATE_ROOM_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_ROOM + "("+KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+KEY_NAME+
                " TEXT,"+KEY_DETAIL+" TEXT,"+KEY_HEIGHT+" INTEGER,"+KEY_IS_CLOSE+" BOOLEAN,"+KEY_WIDTH+" INTEGER,"+KEY_DEPTH+
                " INTEGER,"+KEY_RANGE+" INTEGER,"+KEY_FLOOR_ID+" INTEGER)";
        db.execSQL(CREATE_ROOM_TABLE);

        String CREATE_BLE_TABLE = "CREATE TABLE IF NOT EXISTS "+TABLE_BLE+"("+KEY_ID+" INTEGER PRIMARY KEY,"+KEY_MAC+" TEXT,"+KEY_ROOM_ID+" INTEGER,"+KEY_POSITION+" INTEGER)";
        db.execSQL(CREATE_BLE_TABLE);

        String CREATE_CORNER_TABLE="CREATE TABLE IF NOT EXISTS "+TABLE_CORNER+"("+KEY_ID+" INTEGER PRIMARY KEY,"+KEY_MAC+" TEXT,"+KEY_ROOM_ID+" INTEGER,"+KEY_POSITION+" INTEGER)";
        db.execSQL(CREATE_CORNER_TABLE);

        /*String CREATE_PIN_TABLE="CREATE TABLE IF NOT EXISTS "+TABLE_PIN+"("+KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+KEY_FLOOR_ID+
                " INTEGER,"+KEY_NAME+" TEXT,"+KEY_INFO+" TEXT,"+KEY_TYPE+" INTEGER,"+KEY_X+" FLOAT,"+KEY_Y+" FLOAT)";
        db.execSQL(CREATE_PIN_TABLE);*/

        //Log.d("Finish on Create Database","checkLog");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("onUpdate","checkLog");
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUILDING);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FLOOR);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROOM);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BLE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CORNER);
        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_PIN);
        // Create tables again
        onCreate(db);
    }
    int addCorner(int roomId,String macAddress,int position){
        int id=0;
        ContentValues values = new ContentValues();
        values.put(KEY_ROOM_ID,roomId);
        values.put(KEY_MAC,macAddress);
        values.put(KEY_POSITION,position);
        SQLiteDatabase db = this.getWritableDatabase();
        id=(int)db.insert(TABLE_CORNER,null,values);
        db.close();
        return id;
    }
    void deleteCorner(int roomId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CORNER, KEY_ROOM_ID + " = ?",
                new String[] { String.valueOf(roomId) });
        db.close();
    }
    public List<corner> getCorner(int roomId){
        SQLiteDatabase db=this.getReadableDatabase();
        List<corner> list=null;
        String selectQuery = "SELECT * FROM "+TABLE_CORNER+" WHERE "+KEY_ROOM_ID+"=?";
        Cursor cursor = db.rawQuery(selectQuery,new String[]{String.valueOf(roomId)});
        if (cursor.moveToFirst()){
            list=new ArrayList<corner>();
            do {
                list.add(new corner(Integer.parseInt(cursor.getString(1))));
                list.get(list.size()-1).setNoCorner(Integer.parseInt(cursor.getString(2)));
                list.get(list.size()-1).setX(Integer.parseInt(cursor.getString(3)));
                list.get(list.size()-1).setY(Integer.parseInt(cursor.getString(4)));
            }while(cursor.moveToNext());
        }
        return list;
    }
    /**
     * All CRUD(Create, Read, Update, Delete) Operations FOR BLE----------------------------------
     */
    public int addBle(ble ble){
        Log.d("Add BLE "+ble.getMacId(),"checkLog");
        SQLiteDatabase db = this.getWritableDatabase();
        //0id INTEGER PRIMARY KEY AUTOINCREMENT,1room TEXT,2detail TEXT,3heightFloor INTEGER,4isClose BOOLEAN,
        //5width INTEGER,6height INTEGER,7rangeBle INTEGER,8floorId INTEGER
        ContentValues values = new ContentValues();
        values.put(KEY_ID, ble.getId());
        values.put(KEY_MAC, ble.getMacId());
        values.put(KEY_ROOM_ID, ble.getRoomId());
        values.put(KEY_POSITION, ble.getPosition());

        // Inserting Row
        int id = (int)db.insert(TABLE_BLE, null, values);
        db.close(); // Closing database connection
        Log.d("id="+id,"checkLog");
        return id;
    }
    /*ble getBle(String macAdd) {
        SQLiteDatabase db = this.getReadableDatabase();
        Log.d("getBle "+macAdd,"checkLog");
        Cursor cursor = db.query(TABLE_BLE, new String[] { KEY_ID, KEY_MAC, KEY_ROOM_ID,KEY_POSITION },
                KEY_MAC + "=?",new String[] {macAdd},null,null,null,null);
        //String selectQuery = "SELECT * FROM " +TABLE_BLE;//+" WHERE id=?";
        //Cursor cursor = db.rawQuery(selectQuery,new String[]{"0"});
        ble ble=null;
        if (cursor != null){
            Log.d("cursor not null "+cursor.getColumnName(0)+cursor.getColumnName(1)+cursor.getColumnName(2)+cursor.getColumnName(3),"checkLog");
            cursor.moveToFirst();
            Log.d("cursor move to first"+cursor.getCount(),"checkLog");
            Log.d(cursor.getString(0)+cursor.getString(1)+cursor.getString(2)+cursor.getString(3),"checkLog");
            //String name=cursor.getString(1);
            Log.d("finish","checkLog");
            ble = new ble(cursor.getString(1),Integer.parseInt(cursor.getString(2)),cursor.getString(3));
            Log.d("new ble","checkLog");
            ble.setId(Integer.parseInt(cursor.getString(0)));
            Log.d("return ble","checkLog");
        }
        Log.d("---------------","checkLog");
        return ble;
    }*/
    // Getting All rooms
    public List<ble> getAllBle(int roomId) {
        List<ble> bleList = null;
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_BLE + " WHERE roomId=?";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(roomId)});
        if (cursor.moveToFirst()) {
            bleList=new ArrayList<ble>();
            do {
                ble ble = new ble(cursor.getString(1),Integer.parseInt(cursor.getString(2)),cursor.getString(3));
                ble.setId(Integer.parseInt(cursor.getString(0)));

                bleList.add(ble);
            } while (cursor.moveToNext());
        }
        // return room list
        return bleList;
    }

    // Updating single floor
    /*public int updateRoom(room room) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_FLOOR_NAME, room.getName());
        values.put(KEY_PLACE_ID, room.getFloorId());
        Log.d(room.getId()+" "+room.getName()+" "+room.getFloorId(),"checkLog");
        // updating row
        return db.update(TABLE_FLOORS, values, KEY_ID + " = ?",new String[] { String.valueOf(room.getId()) });
    }*/

    // Deleting single ble
    public void deleteBle(ble ble) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_BLE, KEY_ID + " = ?",new String[] { String.valueOf(ble.getId())});
        db.close();
    }
    /**
     * All CRUD(Create, Read, Update, Delete) Operations FOR ROOM----------------------------------
     */
    int addRoom(room room){
        Log.d("Add Room","checkLog");
        SQLiteDatabase db = this.getWritableDatabase();
        //0id INTEGER PRIMARY KEY AUTOINCREMENT,1room TEXT,2detail TEXT,3heightFloor INTEGER,4isClose BOOLEAN,
        //5width INTEGER,6height INTEGER,7rangeBle INTEGER,8floorId INTEGER
        ContentValues values = new ContentValues();
        values.put(KEY_ID, room.getId());
        values.put(KEY_NAME, room.getName());
        values.put(KEY_DETAIL, room.getDetail());
        values.put(KEY_HEIGHT, room.getHeightFloor());
        values.put(KEY_IS_CLOSE, room.getIsClose());
        values.put(KEY_WIDTH, room.getWidth());
        values.put(KEY_DEPTH, room.getHeight());
        values.put(KEY_RANGE, room.getRange());
        values.put(KEY_FLOOR_ID, room.getFloorId());

        // Inserting Row
        int id = (int)db.insert(TABLE_ROOM, null, values);
        db.close(); // Closing database connection
        return id;
    }

    room getRoom(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_ROOM + " WHERE "+KEY_ID+"=?";
        Cursor cursor = db.rawQuery(selectQuery,new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()){
            room get_room = new room(Integer.parseInt(cursor.getString(0)),cursor.getString(1),cursor.getString(2),Integer.parseInt(cursor.getString(8)),
                    Boolean.parseBoolean(cursor.getString(4)),Double.parseDouble(cursor.getString(3)),
                    Integer.parseInt(cursor.getString(5)),Integer.parseInt(cursor.getString(6)),
                    Integer.parseInt(cursor.getString(7)));
            return get_room;
        }
        return null;
    }
    // Getting All rooms
    public List<room> getAllRoom(int floorId) {
        List<room> roomList = null;
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_ROOM + " WHERE floorId=?";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(floorId)});
        /*Cursor cursor = db.query(TABLE_FLOORS, new String[] { KEY_ID, KEY_FLOOR_NAME, KEY_PLACE_ID },
                KEY_PLACE_ID + "=?",new String[] { String.valueOf(placeId) }, null, null, null, null);*/

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            roomList=new ArrayList<room>();
            do {//0id INTEGER PRIMARY KEY AUTOINCREMENT,1room TEXT,2detail TEXT,3heightFloor INTEGER,4isClose BOOLEAN,
                //5width INTEGER,6height INTEGER,7rangeBle INTEGER,8floorId INTEGER
                //--------------------------------------------------------------------------
                //name,detail,floorId,isClose,heightFloor,width,height,rangeOfDevice
                //String,String,int,boolean,double,int,int,int
                room room = new room(Integer.parseInt(cursor.getString(0)),cursor.getString(1),cursor.getString(2),Integer.parseInt(cursor.getString(8)),
                        Boolean.parseBoolean(cursor.getString(4)),Double.parseDouble(cursor.getString(3)),
                        Integer.parseInt(cursor.getString(5)),Integer.parseInt(cursor.getString(6)),
                        Integer.parseInt(cursor.getString(7)));
                // Adding floor to list
                roomList.add(room);
            } while (cursor.moveToNext());
        }
        // return room list
        return roomList;
    }

    // Updating single floor
    public int updateRoom(room room) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, room.getName());
        Log.d(room.getId()+" "+room.getName()+" "+room.getId(),"checkLog");
        // updating row
        return db.update(TABLE_ROOM, values, KEY_ID + " = ?",new String[] { String.valueOf(room.getId()) });
    }

    // Deleting single floor
    public void deleteRoom(room room) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ROOM, KEY_ID + " = ?",new String[] { String.valueOf(room.getId())});
        db.close();
    }
    /**
     * All CRUD(Create, Read, Update, Delete) Operations FOR FLOOR----------------------------------
     */
    int addFloor(floor floor){
        Log.d("Add Floor","checkLog");
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, floor.getId());
        values.put(KEY_NAME, floor.getName());
        values.put(KEY_BUILDING_ID, floor.getPlaceId());

        // Inserting Row
        int n;
        n=(int)db.insert(TABLE_FLOOR, null, values);
        db.close(); // Closing database connection
        return n;
    }

    floor getFloor(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_FLOOR + " WHERE "+KEY_ID+"=?";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(id)});
        if (cursor != null)
            cursor.moveToFirst();
        floor floor = new floor(Integer.parseInt(cursor.getString(0)),cursor.getString(1),Integer.parseInt(cursor.getString(2)),cursor.getString(3));
        return floor;
    }
    // Getting All places
    public List<floor> getAllFloor(int placeId) {
        List<floor> floorList = null;
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_FLOOR + " WHERE buildingId=?";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(placeId)});
        /*Cursor cursor = db.query(TABLE_FLOORS, new String[] { KEY_ID, KEY_FLOOR_NAME, KEY_PLACE_ID },
                KEY_PLACE_ID + "=?",new String[] { String.valueOf(placeId) }, null, null, null, null);*/

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            floorList=new ArrayList<floor>();
            do {
                floor floor = new floor(Integer.parseInt(cursor.getString(0)),cursor.getString(1),Integer.parseInt(cursor.getString(2)),cursor.getString(3));
                // Adding floor to list
                floorList.add(floor);
            } while (cursor.moveToNext());
        }

        // return contact list
        return floorList;
    }

    // Updating single floor
    public int updateFloor(floor floor) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, floor.getName());
        values.put(KEY_BUILDING_ID, floor.getPlaceId());
        Log.d(floor.getId()+" "+floor.getName()+" "+floor.getPlaceId(),"checkLog");
        // updating row
        return db.update(TABLE_FLOOR, values, KEY_ID + " = ?",new String[] { String.valueOf(floor.getId()) });
    }

    // Deleting single floor
    public void deleteFloor(floor floor) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FLOOR, KEY_ID + " = ?",new String[] { String.valueOf(floor.getId())});
        db.close();
    }
    /**
     * All CRUD(Create, Read, Update, Delete) Operations FOR Building----------------------------------
     */

    // Adding new Building
    int addBuilding(building building) {
        Log.d("Add Building","checkLog");
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, building.getId());
        values.put(KEY_NAME, building.getName()); // Contact Name
        values.put(KEY_LATITUDE, building.getLatitude());
        values.put(KEY_LONGITUDE, building.getLongitude());

        // Inserting Row
        int id;
        id= (int) db.insert(TABLE_BUILDING, null, values);
        Log.d("Building ID ="+id,"checkLog");
        db.close(); // Closing database connection
        return id;
    }

    // Getting single place
    building getBuilding(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_BUILDING +" WHERE "+KEY_ID+"=?";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(id)});
        /*Cursor cursor = db.query(TABLE_BUILDING, new String[] { KEY_ID, KEY_NAME, KEY_LATITUDE, KEY_LONGITUDE},
                KEY_ID + "=?",new String[] { String.valueOf(id) }, null, null, null, null);*/
        if (cursor.moveToFirst()){
            Log.d("NOT NULL"+selectQuery,"checkLog");
            Log.d("data:"+cursor.getString(0)+cursor.getString(1)+cursor.getString(2)+cursor.getString(3),"checkLog");
            return new building(Integer.parseInt(cursor.getString(0)),cursor.getString(1),Float.parseFloat(cursor.getString(2)),Float.parseFloat(cursor.getString(3)));
        }
        else {
            Log.d("NULL","checkLog");
            return null;
        }
    }

    // Getting All Building
    public List<building> getAllBuilding() {
        List<building> buildingList = new ArrayList<building>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_BUILDING;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                building building = new building();
                building.setId(Integer.parseInt(cursor.getString(0)));
                building.setName(cursor.getString(1));
                //building.setNumberFloor(cursor.getString(2));
                // Adding contact to list
                buildingList.add(building);
            } while (cursor.moveToNext());
        }
        // return place list
        return buildingList;
    }

    // Updating single place
    public int updateBuilding(building building) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, building.getName());
        //values.put(KEY_PH_NO, place.getPhoneNumber());
        // updating row
        return db.update(TABLE_BUILDING, values, KEY_ID + " = ?",
                new String[] { String.valueOf(building.getId()) });
    }

    // Deleting single place
    public void deleteBuilding(int building) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_BUILDING, KEY_ID + " = ?",
                new String[] { String.valueOf(building) });
        db.close();
    }
    public int getBuildingId(String name){
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_BUILDING +" WHERE "+KEY_NAME+"=?";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(name)});
        /*Cursor cursor = db.query(TABLE_BUILDING, new String[] { KEY_ID, KEY_NAME, KEY_LATITUDE, KEY_LONGITUDE},
                KEY_ID + "=?",new String[] { String.valueOf(id) }, null, null, null, null);*/
        if (cursor.moveToFirst()){
            Log.d("NOT NULL"+selectQuery,"checkLog");
            Log.d("data:"+cursor.getString(0)+cursor.getString(1)+cursor.getString(2)+cursor.getString(3),"checkLog");
            return Integer.parseInt(cursor.getString(0));
        }
        return 0;
    }
}
