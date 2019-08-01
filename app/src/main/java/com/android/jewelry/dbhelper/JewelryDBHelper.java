package com.android.jewelry.dbhelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import com.android.jewelry.dbmodel.CurrentModel;
import com.android.jewelry.dbmodel.DesignModel;
import com.android.jewelry.dbmodel.PartyModel;
import com.android.jewelry.dbmodel.RodiumModel;
import com.android.jewelry.dbmodel.WorkerModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by goutam on 3/5/16.
 */
public class JewelryDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "jewelry.db";
    //Table
    private static final String TABLE_MAIN = "main_table";
    private static final String TABLE_CURRENT = "current_table";
    private static final String TABLE_PARTY_LIST = "party_list";
    private static final String TABLE_WORKER = "worker_list";
    private static final String TABLE_RODIUM_WORKER = "rodium_worker";
    //Main Table Fields name
    private static final String KEY_MAIN_NAME = "design_name";
    private static final String KEY_MAIN_ID = "design_id";
    private static final String KEY_MAIN_COST = "design_cost";
    private static final String KEY_MAIN_URL = "design_url";
    private static final String KEY_MAIN_URI = "design_uri";
    private static final String KEY_MAIN_ISPRESENT = "ispresent";
    private static final String KEY_MAIN_ISSELECTED = "isselected";
    private static final String KEY_MAIN_ISFIRST ="isfirst";
    //Party Table Filds name
    private static final String KEY_PARTY_PARTY_NAME = "partyname";
    //worker Table Fields name
    private static final String KEY_WORKER_ID = "id";
    private static final String KEY_WORKER_name = "name";
    private static final String KEY_WORKER_address = "address";
    private static final String KEY_WORKER_phone = "phone";
    //Current table fields
    private static final String KEY_CURRENT_NAME = "design_name";
    private static final String KEY_CURRENT_SUB = "name_sub";
    //Rodium Worker
    private static final String KEY_RODIUMWORKER_NAME = "name";

    public JewelryDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_MAIN = "CREATE TABLE " + TABLE_MAIN + "("
                + KEY_MAIN_NAME + " TEXT PRIMARY KEY NOT NULL,"
                + KEY_MAIN_ID + " TEXT NOT NULL,"
                + KEY_MAIN_COST + " INTEGER NOT NULL,"
                + KEY_MAIN_URL + " TEXT NOT NULL,"
                + KEY_MAIN_URI + " TEXT NOT NULL,"
                + KEY_MAIN_ISPRESENT + " INTEGER NOT NULL,"
                + KEY_MAIN_ISSELECTED + " INTEGER NOT NULL,"
                + KEY_MAIN_ISFIRST + " INTEGER NOT NULL"
                +")";

        String CREATE_CURRENT = "CREATE TABLE " + TABLE_CURRENT + "("
                + KEY_CURRENT_NAME + " TEXT PRIMARY KEY NOT NULL,"
                + KEY_CURRENT_SUB + " INTEGER NOT NULL"
                +")";

        String CREATE_PARTY = "CREATE TABLE " + TABLE_PARTY_LIST +"("
                + KEY_PARTY_PARTY_NAME + " TEXT PRIMARY KEY NOT NULL"
                +")";
        String CREATE_WORKER = "CREATE TABLE " + TABLE_WORKER + "("
                + KEY_WORKER_ID + " INTEGER PRIMARY KEY NOT NULL,"
                + KEY_WORKER_name + " TEXT NOT NULL,"
                + KEY_WORKER_address + " TEXT NOT NULL,"
                + KEY_WORKER_phone + " TEXT NOT NULL"
                +")";
        String CREATE_RODIUM = "CREATE TABLE " + TABLE_RODIUM_WORKER + "("
                + KEY_RODIUMWORKER_NAME + " TEXT NOT NULL"
                +")";

        //Create Table
        db.execSQL(CREATE_MAIN);
        db.execSQL(CREATE_CURRENT);
        db.execSQL(CREATE_PARTY);
        db.execSQL(CREATE_WORKER);
        db.execSQL(CREATE_RODIUM);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String DROP_MAIN = "DROP TABLE IF EXISTS " + TABLE_MAIN;
        String DROP_PARTY = "DROP TABLE IF EXISTS " + TABLE_PARTY_LIST;
        String DROP_WORKER = "DROP TABLE IF EXISTS " + TABLE_WORKER;
        String DROP_CURRENT = "DROP TABLE IF EXISTS " + TABLE_CURRENT;
        String DROP_RODIUM = "DROP TABLE IF EXISTS " + TABLE_RODIUM_WORKER;

        db.execSQL(DROP_MAIN);
        db.execSQL(DROP_PARTY);
        db.execSQL(DROP_WORKER);
        db.execSQL(DROP_CURRENT);
        db.execSQL(DROP_RODIUM);
        onCreate(db);
    }

    //Sync Images
    public void syncTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        String DROP_MAIN = "DROP TABLE IF EXISTS " + TABLE_MAIN;
        db.execSQL(DROP_MAIN);
        onCreate(db);
        db.close();
    }


    //ADD Rodium in table
    public long addRodium(RodiumModel rodiumModel){
        long reply;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_RODIUMWORKER_NAME,rodiumModel.getName());
        reply = db.insertWithOnConflict(TABLE_RODIUM_WORKER,null,values, SQLiteDatabase.CONFLICT_IGNORE);
        db.close();
        return reply;
    }

    //GET ALL RodiumLIST
    public List<RodiumModel> getAllRodium(){
        List<RodiumModel> rodiumlist = new ArrayList<RodiumModel>();

        String selectQuery = "SELECT * FROM " + TABLE_RODIUM_WORKER ;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        Log.e("Cursor size",""+cursor.getCount());
        if (cursor.moveToFirst()) {
            do {
                RodiumModel rode = new RodiumModel();
                rode.setName(cursor.getString(0));
                rodiumlist.add(rode);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return rodiumlist;
    }

    //Add item in Main Table
    public long addDesignInMainTable(DesignModel design){
        long reply;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_MAIN_NAME,design.getDesignName());
        values.put(KEY_MAIN_ID,design.getDesignId());
        values.put(KEY_MAIN_COST,design.getDesignCost());
        values.put(KEY_MAIN_URL,design.getDesignUrl());
        values.put(KEY_MAIN_URI, design.getDesignUri());
        values.put(KEY_MAIN_ISPRESENT, design.getIspresent());
        values.put(KEY_MAIN_ISSELECTED,design.getSelected());
        values.put(KEY_MAIN_ISFIRST,design.getIsFirst());
        //db.insert(TABLE_USER, null, values);
        reply = db.insertWithOnConflict(TABLE_MAIN, null,
                values, SQLiteDatabase.CONFLICT_IGNORE);
        db.close();
        return reply;
    }

    //Get Design from SD Card
    public DesignModel checkImageInSDCard(String designName){
        SQLiteDatabase db = this.getWritableDatabase();
        DesignModel design = null;
        Cursor cursor = null;
        String SELECTION_QUERY = "SELECT * FROM " + TABLE_MAIN
                + " WHERE "
                + KEY_MAIN_NAME +  "=" + "'" + designName + "'";
        try {
            cursor = db.rawQuery(SELECTION_QUERY,null);
            if (cursor != null && cursor.moveToFirst()){
               design = new DesignModel(
                       cursor.getString(0),
                       cursor.getString(1),
                       cursor.getInt(2),
                       cursor.getString(3),
                       cursor.getString(4),
                       cursor.getInt(5),
                       cursor.getInt(6),
                       cursor.getInt(7));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        cursor.close();
        db.close();
        return design;
    }

    //Update URI in Main Table
    public long updateByName(String name, String uri){
        long reply;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_MAIN_URI, uri);
        values.put(KEY_MAIN_ISPRESENT, 1);
        reply = db.update(TABLE_MAIN, values, KEY_MAIN_NAME+"= '"+name+"'", null);
        return reply;
    }

    //Update URL and URI in Main Table
    public long updateURLnURI(String name, String url){
        long reply;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_MAIN_URL, url);
        reply = db.update(TABLE_MAIN, values, KEY_MAIN_NAME+"= '"+name+"'", null);
        return reply;
    }

    //Update URI in Main Table
    public long updateURI(String name, String uri){
        long reply;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_MAIN_URI, uri);
        reply = db.update(TABLE_MAIN, values, KEY_MAIN_NAME+"= '"+name+"'", null);
        return reply;
    }

    //Update Cost
    public int updateCost(String name, int cost){
        int reply;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_MAIN_COST, cost);
        reply = db.updateWithOnConflict(TABLE_MAIN, values, KEY_MAIN_NAME+"= '"+name+"'", null, SQLiteDatabase.CONFLICT_IGNORE);
        if (reply>0){
            Log.e("Updated Cost","Row updated>>"+name+" "+cost);
        }
        db.close();
        return reply;
    }
    //Update First Time
    public void updateFirstTime(String name, int firstVal){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_MAIN_ISFIRST, firstVal);
        db.updateWithOnConflict(TABLE_MAIN, values, KEY_MAIN_NAME+"= '"+name+"'", null, SQLiteDatabase.CONFLICT_IGNORE);
        if (firstVal>0){
            //Log.e("Updated First Val","Row updated>>"+name+" "+firstVal);
        }

    }

    //GET ALL FACE AS A LIST
    public List<DesignModel> getAllFace(){
        List<DesignModel> facelist = new ArrayList<DesignModel>();

        String selectQuery = "SELECT * FROM " + TABLE_CURRENT +
                " LEFT JOIN " + TABLE_MAIN + " ON " + TABLE_CURRENT + "." + KEY_CURRENT_NAME + " = " +
                TABLE_MAIN + "." + KEY_MAIN_NAME ;
        /*String selectQuery = "SELECT * FROM "   + TABLE_MAIN +"," +TABLE_CURRENT
                +" WHERE "
                +TABLE_MAIN+"."+KEY_MAIN_NAME +"="+TABLE_CURRENT+"."+KEY_CURRENT_NAME;*/

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                DesignModel face = new DesignModel();
                face.setDesignName(cursor.getString(2));
                face.setDesignId(cursor.getString(3));
                face.setDesignCost(cursor.getInt(4));
                face.setDesignUrl(cursor.getString(5));
                face.setDesignUri(cursor.getString(6));
                face.setIspresent(cursor.getInt(7));
                face.setSelected(cursor.getInt(8));
                facelist.add(face);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return facelist;
    }

    //Search a jewelry
    public List<DesignModel> findProduct(String productname) {
        //String query = "Select * FROM " + TABLE_MAIN + " WHERE " + KEY_MAIN_NAME + " ='" + productname + "'";
        List<DesignModel> designList = new ArrayList<DesignModel>();
        String query = "SELECT * FROM " + TABLE_CURRENT +
                " LEFT JOIN " + TABLE_MAIN + " ON " + TABLE_CURRENT + "." + KEY_CURRENT_NAME + " = " +
                TABLE_MAIN + "." + KEY_MAIN_NAME + " WHERE " + TABLE_MAIN + "." + KEY_MAIN_NAME + " LIKE '"+"%"+ productname +"%"+"'";
        Log.d("search Query",query);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst() && cursor != null) {
            do {
                //Log.e("Cursor", ""+cursor.getCount());
                DesignModel product = new DesignModel();
                product.setDesignName(cursor.getString(2));
                product.setDesignId(cursor.getString(3));
                product.setDesignCost(cursor.getInt(4));
                product.setDesignUrl(cursor.getString(5));
                product.setDesignUri(cursor.getString(6));
                product.setIspresent(cursor.getInt(7));
                product.setSelected(cursor.getInt(8));
                designList.add(product);
            }while (cursor.moveToNext());
        } else {
        }
        cursor.close();
        db.close();
        Log.e("#####Search Product",""+designList.size());
        return designList;
    }

    //Get downloaded status
    public int getDownloadedStatus(String productName){
        int downloadedStatus = 0;
        String query = "SELECT * FROM " + TABLE_MAIN + " WHERE " + KEY_MAIN_NAME + " ='" + productName + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        DesignModel product = new DesignModel();
        if (cursor.moveToFirst() && cursor != null){
            downloadedStatus = cursor.getInt(5);
        }
        return downloadedStatus;
    }

    //Get downloaded status
    public int getFirsttimeStatus(String productName){
        int downloadedStatus = 0;
        String query = "SELECT * FROM " + TABLE_MAIN + " WHERE " + KEY_MAIN_NAME + " ='" + productName + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        DesignModel product = new DesignModel();
        if (cursor.moveToFirst() && cursor != null){
            downloadedStatus = cursor.getInt(7);
        }
        Log.e("First Download status",productName+" "+downloadedStatus);
        return downloadedStatus;
    }

    //Get productName exist or not
    public int getProductExistorNot(String productName){
        int response = 0;
        String query = "SELECT * FROM " + TABLE_MAIN + " WHERE " + KEY_MAIN_NAME + " ='" + productName + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null){
            response = 1;
        }else {
            response = 0;
        }
        return response;
    }

    //
    public List<DesignModel> getDesignwithPrice(int fromprice, int toprice){
        List<DesignModel> facelist = new ArrayList<DesignModel>();

        /*String selectQuery = "SELECT * FROM " + TABLE_MAIN + " WHERE " + KEY_MAIN_COST +" >= " + fromprice + " AND "
                + KEY_MAIN_COST +" <= " + toprice;*/

        String selectQuery = "SELECT * FROM " + TABLE_CURRENT +
                " LEFT JOIN " + TABLE_MAIN + " ON " + TABLE_CURRENT + "." + KEY_CURRENT_NAME + " = " +
                TABLE_MAIN + "." + KEY_MAIN_NAME + " WHERE " + TABLE_MAIN + "." + KEY_MAIN_COST +" >= " + fromprice + " AND "
                + TABLE_MAIN + "."+ KEY_MAIN_COST +" <= " + toprice;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        Log.e("Size>>>>>>>",""+cursor.getCount());
        if (cursor.moveToFirst()) {
            do {
                DesignModel face = new DesignModel();
                face.setDesignName(cursor.getString(2));
                face.setDesignId(cursor.getString(3));
                face.setDesignCost(cursor.getInt(4));
                face.setDesignUrl(cursor.getString(5));
                face.setDesignUri(cursor.getString(6));
                face.setIspresent(cursor.getInt(7));
                face.setSelected(cursor.getInt(8));

                facelist.add(face);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return facelist;
    }

    //////////////PARTY Table Process

    //Add item in Main Table
    public long addParty(PartyModel party){
        long reply;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_PARTY_PARTY_NAME,party.getPartyName());
        reply = db.insertWithOnConflict(TABLE_PARTY_LIST, null,
                values, SQLiteDatabase.CONFLICT_IGNORE);
        db.close();
        return reply;
    }



    //GET ALL FACE AS A LIST
    public List<PartyModel> getAllParty(){
        List<PartyModel> facelist = new ArrayList<PartyModel>();
        String selectQuery = "SELECT * FROM " + TABLE_PARTY_LIST;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                PartyModel party = new PartyModel();
                party.setPartyName(cursor.getString(0));
                facelist.add(party);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return facelist;
    }

    ////////////WORKER Table process

    //Add item in Main Table
    public long addWorker(WorkerModel worker){
        long reply;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_WORKER_ID,worker.getId());
        values.put(KEY_WORKER_name,worker.getName());
        values.put(KEY_WORKER_address,worker.getAddress());
        values.put(KEY_WORKER_phone,worker.getPhone());
        reply = db.insertWithOnConflict(TABLE_WORKER, null,
                values, SQLiteDatabase.CONFLICT_IGNORE);
        db.close();
        return reply;
    }


    //Get all workers
    public List<WorkerModel> getAllWorkers(){
        List<WorkerModel> workerlist = new ArrayList<WorkerModel>();
        String selectQuery = "SELECT * FROM " + TABLE_WORKER;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                WorkerModel worker = new WorkerModel();
                worker.setId(cursor.getInt(0));
                worker.setName(cursor.getString(1));
                worker.setAddress(cursor.getString(2));
                worker.setPhone(cursor.getString(3));
                workerlist.add(worker);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return workerlist;
    }

    public long addCurrent(CurrentModel current){
        long reply;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_CURRENT_NAME,current.getDesignName());
        values.put(KEY_CURRENT_SUB,current.getSubName());
        reply = db.insertWithOnConflict(TABLE_CURRENT, null,
                values, SQLiteDatabase.CONFLICT_IGNORE);
        db.close();
        return reply;
    }

    //TRANCATE Current Table
    public void dropCurrent(){
        SQLiteDatabase db = this.getWritableDatabase();
        String DROP_CURRENT = "DROP TABLE IF EXISTS " + TABLE_CURRENT;
        db.execSQL(DROP_CURRENT);
        String CREATE_CURRENT = "CREATE TABLE " + TABLE_CURRENT + "("
                + KEY_CURRENT_NAME + " TEXT PRIMARY KEY NOT NULL,"
                + KEY_CURRENT_SUB + " INTEGER NOT NULL"
                +")";
        db.execSQL(CREATE_CURRENT);
        db.close();
    }

    //GET All Designs in sorted order
    public List<DesignModel> getAllSortedNamedImages(int lowername, int uppername){
        List<DesignModel> facelist = new ArrayList<DesignModel>();

        String selectQuery = "SELECT * FROM " + TABLE_CURRENT +
                " LEFT JOIN " + TABLE_MAIN + " ON " + TABLE_CURRENT + "." + KEY_CURRENT_NAME + " = "
                + TABLE_MAIN + "." + KEY_MAIN_NAME + " WHERE " + TABLE_CURRENT + "." + KEY_CURRENT_SUB +" >= " + lowername +
                " AND "
                + TABLE_CURRENT + "."+ KEY_CURRENT_SUB +" <= " + uppername;
        Log.e("Query >>>>",selectQuery);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        Log.e("Size>>>>>>>",""+cursor.getCount());
        if (cursor.moveToFirst()) {
            do {
                DesignModel face = new DesignModel();
                face.setDesignName(cursor.getString(2));
                face.setDesignId(cursor.getString(3));
                face.setDesignCost(cursor.getInt(4));
                face.setDesignUrl(cursor.getString(5));
                face.setDesignUri(cursor.getString(6));
                face.setIspresent(cursor.getInt(7));
                face.setSelected(cursor.getInt(8));

                facelist.add(face);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return facelist;
    }
}
