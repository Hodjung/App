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

    // placesManager table name
    private static final String TABLE_PLACES = "places";
    private static final String TABLE_FLOORS = "floors";
    private static final String TABLE_ROOMS = "rooms";
    private static final String TABLE_BLES = "bles";
    private static final String TABLE_CORNERS = "corner";
    private static final String TABLE_DOORS = "doors";
    private static final String TABLE_PINS = "pins";

    // Places Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_PLACE = "place";
    private static final String KEY_FLOOR = "number_floor";
    // Floors Table Columns names
    //private static final String KEY_ID = "id";
    private static final String KEY_FLOOR_NAME = "floor";
    private static final String KEY_PLACE_ID = "placeId";
    // Rooms Table Columns names
    //private static final String KEY_ID = "id";
    private static final String KEY_ROOM_NAME = "room";
    private static final String KEY_DETAIL = "detail";
    private static final String KEY_HEIGHT_FLOOR = "heightFloor";
    private static final String KEY_IS_CLOSE = "isClose";
    private static final String KEY_WIDTH = "width";
    private static final String KEY_HEIGHT = "height";
    private static final String KEY_RANGE = "range";
    private static final String KEY_FLOOR_ID = "floorId";
    // BLE Table Columns names
    //private static final String KEY_ID = "id";
    private static final String KEY_BLE_NAME = "macAddress";
    private static final String KEY_ROOM_ID = "roomId";
    private static final String KEY_POSITION = "position";
    // Corner Table Columns names
    //private static final String KEY_ID = "id";
    //private static final String KEY_ROOM_ID = "roomId";
    private static final String KEY_NO_CORNER = "noCorner";
    private static final String KEY_X = "x";
    private static final String KEY_Y = "y";


    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("onCreateInDatabase", "checkLog");
        String CREATE_PLACES_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_PLACES + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_PLACE + " TEXT," + KEY_FLOOR + " INTEGER" + ")";
        db.execSQL(CREATE_PLACES_TABLE);

        String CREATE_FLOORS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_FLOORS + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_FLOOR_NAME + " TEXT," + KEY_PLACE_ID + " INTEGER" + ")";
        db.execSQL(CREATE_FLOORS_TABLE);

        String CREATE_ROOMS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_ROOMS + "("+KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+KEY_ROOM_NAME+
                " TEXT,"+KEY_DETAIL+" TEXT,"+KEY_HEIGHT_FLOOR+" INTEGER,"+KEY_IS_CLOSE+" BOOLEAN,"+KEY_WIDTH+" INTEGER,"+KEY_HEIGHT+
                " INTEGER,"+KEY_RANGE+" INTEGER,"+KEY_FLOOR_ID+" INTEGER)";
        db.execSQL(CREATE_ROOMS_TABLE);

        String CREATE_BLES_TABLE = "CREATE TABLE IF NOT EXISTS "+TABLE_BLES+"("+KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+KEY_BLE_NAME+
                " TEXT,"+KEY_ROOM_ID+" INTEGER,"+KEY_POSITION+" TEXT)";
        db.execSQL(CREATE_BLES_TABLE);

        String CREATE_CORNER_TABLE="CREATE TABLE IF NOT EXISTS "+TABLE_CORNERS+"("+KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+KEY_ROOM_ID+
                " INTEGER,"+KEY_NO_CORNER+" INTEGER,"+KEY_X+" FLOAT,"+KEY_Y+" FLOAT)";
        db.execSQL(CREATE_CORNER_TABLE);

        String CREATE_DOOR_TABLE="CREATE TABLE IF NOT EXISTS "+TABLE_DOORS+"("+KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+KEY_ROOM_ID+
                " INTEGER,"+KEY_X+" FLOAT,"+KEY_Y+" FLOAT)";
        db.execSQL(CREATE_DOOR_TABLE);

        String CREATE_PIN_TABLE="CREATE TABLE IF NOT EXISTS "+TABLE_PINS+"("+KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+KEY_ROOM_ID+
                " INTEGER,"+KEY_DETAIL+" TEXT,"+KEY_X+" FLOAT,"+KEY_Y+" FLOAT)";
        db.execSQL(CREATE_PIN_TABLE);

        String CREATE_NEAR_CORNER_TABLE="CREATE TABLE IF NOT EXISTS nearCorners(id INTEGER PRIMARY KEY AUTOINCREMENT,roomId INTEGER,noCorner INTEGER,macAddress TEXT)";
        db.execSQL(CREATE_NEAR_CORNER_TABLE);

        /*db.execSQL("INSERT INTO "+ TABLE_PLACES +" (" + KEY_PLACE + ", " + KEY_FLOOR
                + ") VALUES ('Fudge', 95, 750);");*/
        Log.d("Finish on Create Database","checkLog");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("onUpdate","checkLog");
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLACES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FLOORS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROOMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BLES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CORNERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOORS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PINS);
        db.execSQL("DROP TABLE IF EXISTS nearCorners");
        // Create tables again
        onCreate(db);
    }
    void addNearCorner(nearCorner nearCorner){
        ContentValues values = new ContentValues();
        values.put("roomId",nearCorner.getRoomId());
        values.put("noCorner",nearCorner.getNoCorner());
        values.put("macAddress",nearCorner.getMacId());
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert("nearCorner",null,values);
        db.close();
    }
    int addPin(int roomId,String detail,float x,float y){
        int id=0;
        ContentValues values = new ContentValues();
        values.put(KEY_ROOM_ID,roomId);
        values.put(KEY_DETAIL,detail);
        values.put(KEY_X,x);
        values.put(KEY_Y,y);
        SQLiteDatabase db = this.getWritableDatabase();
        id= (int) db.insert(TABLE_PINS,null,values);
        db.close();
        return id;
    }
    void deletePin(int roomId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PINS, "roomId" + " = ?",
                new String[] { String.valueOf(roomId) });
        db.close();
    }
    public List<pin> getPin(int roomId){
        SQLiteDatabase db=this.getReadableDatabase();
        List<pin> list=null;
        String selectQuery = "SELECT * FROM "+TABLE_PINS+" WHERE "+KEY_ROOM_ID+"=?";
        Cursor cursor = db.rawQuery(selectQuery,new String[]{String.valueOf(roomId)});

        if (cursor.moveToFirst()){
            list=new ArrayList<pin>();
            do {
                list.add(new pin(Integer.parseInt(cursor.getString(1)),cursor.getString(2),Float.parseFloat(cursor.getString(3)),Float.parseFloat(cursor.getString(4))));
            }while(cursor.moveToNext());
        }
        return list;
    }
    int addDoor(int roomId,float x,float y){
        int id=0;
        ContentValues values = new ContentValues();
        values.put(KEY_ROOM_ID,roomId);
        values.put(KEY_X,x);
        values.put(KEY_Y,y);
        SQLiteDatabase db = this.getWritableDatabase();
        id= (int) db.insert(TABLE_DOORS,null,values);
        db.close();
        return id;
    }
    void deleteDoor(int doorId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CORNERS, KEY_ID + " = ?",
                new String[] { String.valueOf(doorId) });
        db.close();
    }
    public List<door> getDoor(int roomId){
        SQLiteDatabase db=this.getReadableDatabase();
        List<door> list=null;
        String selectQuery = "SELECT * FROM "+TABLE_CORNERS+" WHERE "+KEY_ROOM_ID+"=?";
        Cursor cursor = db.rawQuery(selectQuery,new String[]{String.valueOf(roomId)});
        if (cursor.moveToFirst()){
            list=new ArrayList<door>();
            do {
                list.add(new door(Integer.parseInt(cursor.getString(2)),Integer.parseInt(cursor.getString(3))));
            }while(cursor.moveToNext());
        }
        return list;
    }
    int addCorner(int roomId,int noCorner,float x,float y){
        int id=0;
        ContentValues values = new ContentValues();
        values.put(KEY_ROOM_ID,roomId);
        values.put(KEY_NO_CORNER,noCorner);
        values.put(KEY_X,x);
        values.put(KEY_Y,y);
        SQLiteDatabase db = this.getWritableDatabase();
        id=(int)db.insert(TABLE_CORNERS,null,values);
        db.close();
        return id;
    }
    void deleteCorner(int roomId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CORNERS, KEY_ROOM_ID + " = ?",
                new String[] { String.valueOf(roomId) });
        db.close();
    }
    public List<corner> getCorner(int roomId){
        SQLiteDatabase db=this.getReadableDatabase();
        List<corner> list=null;
        String selectQuery = "SELECT * FROM "+TABLE_CORNERS+" WHERE "+KEY_ROOM_ID+"=?";
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
        values.put(KEY_BLE_NAME, ble.getMacId());
        values.put(KEY_ROOM_ID, ble.getRoomId());
        values.put(KEY_POSITION, ble.getPosition());

        // Inserting Row
        int id = (int)db.insert(TABLE_BLES, null, values);
        db.close(); // Closing database connection
        Log.d("id="+id,"checkLog");
        return id;
    }

    ble getBle(String macAdd) {
        SQLiteDatabase db = this.getReadableDatabase();
        Log.d("getBle "+macAdd,"checkLog");
        Cursor cursor = db.query(TABLE_BLES, new String[] { KEY_ID, KEY_BLE_NAME, KEY_ROOM_ID,KEY_POSITION },
                KEY_BLE_NAME + "=?",new String[] {macAdd},null,null,null,null);
        /*String selectQuery = "SELECT * FROM " +TABLE_BLE;//+" WHERE id=?";
        Cursor cursor = db.rawQuery(selectQuery,new String[]{"0"});*/
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
    }
    // Getting All rooms
    public List<ble> getAllBle(int roomId) {
        List<ble> bleList = null;
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_BLES + " WHERE roomId=?";

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

    // Deleting single floor
    public void deleteBle(ble ble) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_BLES, KEY_ID + " = ?",new String[] { String.valueOf(ble.getId())});
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
        values.put(KEY_ROOM_NAME, room.getName());
        values.put(KEY_DETAIL, room.getDetail());
        values.put(KEY_HEIGHT_FLOOR, room.getHeightFloor());
        values.put(KEY_IS_CLOSE, room.getIsClose());
        values.put(KEY_WIDTH, room.getWidth());
        values.put(KEY_HEIGHT, room.getHeight());
        values.put(KEY_RANGE, room.getRange());
        values.put(KEY_FLOOR_ID, room.getFloorId());

        // Inserting Row
        int id = (int)db.insert(TABLE_ROOMS, null, values);
        db.close(); // Closing database connection
        return id;
    }

    room getRoom(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_ROOMS + " WHERE "+KEY_ID+"=?";
        Cursor cursor = db.rawQuery(selectQuery,new String[]{String.valueOf(id)});
        if (cursor != null)
            cursor.moveToFirst();

        room get_room = new room(cursor.getString(1),cursor.getString(2),Integer.parseInt(cursor.getString(8)),
                Boolean.parseBoolean(cursor.getString(4)),Double.parseDouble(cursor.getString(3)),
                Integer.parseInt(cursor.getString(5)),Integer.parseInt(cursor.getString(6)),
                Integer.parseInt(cursor.getString(7)));
        get_room.setId(Integer.parseInt(cursor.getString(0)));
        return get_room;
    }
    // Getting All rooms
    public List<room> getAllRoom(int floorId) {
        List<room> roomList = null;
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_ROOMS + " WHERE floorId=?";

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
                room room = new room(cursor.getString(1),cursor.getString(2),Integer.parseInt(cursor.getString(8)),
                        Boolean.parseBoolean(cursor.getString(4)),Double.parseDouble(cursor.getString(3)),
                        Integer.parseInt(cursor.getString(5)),Integer.parseInt(cursor.getString(6)),
                        Integer.parseInt(cursor.getString(7)));
                room.setId(Integer.parseInt(cursor.getString(0)));
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
        values.put(KEY_ROOM_NAME, room.getName());
        Log.d(room.getId()+" "+room.getName()+" "+room.getId(),"checkLog");
        // updating row
        return db.update(TABLE_ROOMS, values, KEY_ID + " = ?",new String[] { String.valueOf(room.getId()) });
    }

    // Deleting single floor
    public void deleteRoom(room room) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ROOMS, KEY_ID + " = ?",new String[] { String.valueOf(room.getId())});
        db.close();
    }
    /**
     * All CRUD(Create, Read, Update, Delete) Operations FOR FLOOR----------------------------------
     */
    int addFloor(floor floor){
        Log.d("Add Floor","checkLog");
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_FLOOR_NAME, floor.getName());
        values.put(KEY_PLACE_ID, floor.getPlaceId());

        // Inserting Row
        int n;
        n=(int)db.insert(TABLE_FLOORS, null, values);
        db.close(); // Closing database connection
        return n;
    }

    floor getFloor(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_FLOORS + " WHERE "+KEY_ID+"=?";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(id)});
        if (cursor != null)
            cursor.moveToFirst();
        floor floor = new floor(cursor.getString(1),Integer.parseInt(cursor.getString(2)));
        floor.setId(Integer.parseInt(cursor.getString(0)));
        return floor;
    }
    // Getting All places
    public List<floor> getAllFloor(int placeId) {
        List<floor> floorList = null;
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_FLOORS + " WHERE placeId=?";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(placeId)});
        /*Cursor cursor = db.query(TABLE_FLOORS, new String[] { KEY_ID, KEY_FLOOR_NAME, KEY_PLACE_ID },
                KEY_PLACE_ID + "=?",new String[] { String.valueOf(placeId) }, null, null, null, null);*/

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            floorList=new ArrayList<floor>();
            do {
                floor floor = new floor(cursor.getString(1),Integer.parseInt(cursor.getString(2)));
                floor.setId(Integer.parseInt(cursor.getString(0)));
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
        values.put(KEY_FLOOR_NAME, floor.getName());
        values.put(KEY_PLACE_ID, floor.getPlaceId());
        Log.d(floor.getId()+" "+floor.getName()+" "+floor.getPlaceId(),"checkLog");
        // updating row
        return db.update(TABLE_FLOORS, values, KEY_ID + " = ?",new String[] { String.valueOf(floor.getId()) });
    }

    // Deleting single floor
    public void deleteFloor(floor floor) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FLOORS, KEY_ID + " = ?",new String[] { String.valueOf(floor.getId())});
        db.close();
    }
    /**
     * All CRUD(Create, Read, Update, Delete) Operations FOR PLACE----------------------------------
     */

    // Adding new place
    int addPlace(place place) {
        Log.d("Add Place","checkLog");
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PLACE, place.getName()); // Contact Name
        values.put(KEY_FLOOR, place.getNumberFloor());

        // Inserting Row
        int id;
        id= (int) db.insert(TABLE_PLACES, null, values);
        Log.d("Place ID ="+id,"checkLog");
        db.close(); // Closing database connection
        return id;
    }

    // Getting single place
    place getPlace(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_PLACES, new String[] { KEY_ID, KEY_PLACE, KEY_FLOOR },
                KEY_ID + "=?",new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        //place contact = new place(Integer.parseInt(cursor.getString(0)),cursor.getString(1), cursor.getString(2));
        // return contact
        place place = new place(cursor.getString(1),Integer.parseInt(cursor.getString(2)));
        place.setId(Integer.parseInt(cursor.getString(0)));
        return place;
    }

    // Getting All places
    public List<place> getAllPlaces() {
        List<place> placeList = new ArrayList<place>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_PLACES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                place place = new place();
                place.setId(Integer.parseInt(cursor.getString(0)));
                place.setName(cursor.getString(1));
                place.setNumberFloor(cursor.getString(2));
                // Adding contact to list
                placeList.add(place);
            } while (cursor.moveToNext());
        }
        // return place list
        return placeList;
    }

    // Updating single place
    public int updatePlace(place place) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PLACE, place.getName());
        //values.put(KEY_PH_NO, place.getPhoneNumber());
        // updating row
        return db.update(TABLE_PLACES, values, KEY_ID + " = ?",
                new String[] { String.valueOf(place.getId()) });
    }

    // Deleting single place
    public void deletePlace(int place) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PLACES, KEY_ID + " = ?",
                new String[] { String.valueOf(place) });
        db.close();
    }

    // Getting places Count
    public int getPlacesCount() {
        String countQuery = "SELECT  * FROM " + TABLE_PLACES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }
}
