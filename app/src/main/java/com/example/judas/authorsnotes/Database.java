package com.example.judas.authorsnotes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static com.example.judas.authorsnotes.NoteBase.DB_TABLE;


/**
 * Created by judas on 10/31/2017.
 */

public class Database {


    private Context context;
    private SQLHelper helper;
    private SQLiteDatabase db;
    protected static final String DB_NAME = "products.db";

    protected static final int DB_VERSION = 4;
    protected static final String DB_TABLE = "inventory";

    private static final String NOTE_ID_COL = "_id";
    protected static final String HASH_COL = "Hashtag";
    protected static final String NOTE_CHAR_COL = "Notes";
    protected static final String DATE_ADD_COL = "Date";

    private static final String DB_TAG = "DatabaseManager" ;
    private static final String SQL_TAG = "SQLHelper" ;

    public Database(Context c) {
        this.context = c;
        helper = new SQLHelper(c);
        this.db = helper.getWritableDatabase();
    }

    public void close() {
        helper.close(); //Closes the database - very important!

    }


    public int getQuantityForProduct(String productName) {
        String [] cols = {NOTE_CHAR_COL};
        String selection = HASH_COL + " =?";
        String[] selectionArgs = { productName};

        Cursor cursor = db.query(DB_TABLE, cols, selection, selectionArgs, null,null,null);

        if (cursor.getCount() == 1){
            cursor.moveToFirst();
            int quantity = cursor.getInt(0);
            cursor.close();
            return quantity;
        } else {
            return -1; // better way to indicate product not found??
        }
    }

    public boolean updateQuantity(String name, int newQuantity){
        ContentValues updateProduct = new ContentValues();
        updateProduct.put(NOTE_CHAR_COL, newQuantity);
        String[] whereArgs = {name};
        String where = HASH_COL + " =?";

        int rowsChanged = db.update(DB_TABLE, updateProduct, where,whereArgs);

        Log.i(DB_TAG, "Update " + name + " new quantity " + newQuantity +
                " rows modified " + rowsChanged);

        if ( rowsChanged > 0) {
            return true; //if at least one row changed
        } return false;
    }
    public boolean deleteProduct(long productId){
        String[] whereArgs = {Long.toString(productId)};
        String where = "_id = ?";
        int rowsDeleted = db.delete(DB_TABLE,where, whereArgs);

        Log.i(DB_TAG, "Delete " + productId + " rows deleted:" + rowsDeleted);

        if (rowsDeleted ==1) {
            return true; //should be 1 row deleted
        }
        return false; //nothing has been deleted so the primary key was likely not assigned
    }

    public Cursor getCursorAll() {
        return db.query(DB_TABLE, null, null, null, null, null, HASH_COL);
    }

    public boolean addProduct(String name, int quantity){
        ContentValues newProduct = new ContentValues();
        newProduct.put(HASH_COL, name);
        newProduct.put(NOTE_CHAR_COL, quantity);
        try{
            db.insertOrThrow(DB_TABLE,null,newProduct);
            return true;
        } catch (SQLiteConstraintException sqlce) {
            Log.e(DB_TAG, "error inserting data into table." +
                    "Name:" + name + " quantity:" + quantity, sqlce);
            return false;
        }
    }

    public class SQLHelper extends SQLiteOpenHelper {
        public SQLHelper(Context c){
            super(c, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            //Table contains a primary key column, _id which autoincrements - saves you setting the value
            //Having a primary key column is almost always a good idea. In this app, the _id column is used by
            //the list CursorAdapter data source to figure out what to put in the list, and to uniquely identify each element
            //Name column, String
            //Quantity column, int

            String createTable = "CREATE TABLE " + DB_TABLE +
                    " (" + NOTE_ID_COL +" TEXT UNIQUE, " + NOTE_CHAR_COL +" TEXT, " + HASH_COL + " TEXT " + DATE_ADD_COL + " DATE)";

            Log.d(SQL_TAG, createTable);
            db.execSQL(createTable);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
            onCreate(db);
            Log.w(SQL_TAG, "Upgrade table - drop and recreate it");
        }
    }
}
