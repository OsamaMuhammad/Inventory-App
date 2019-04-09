package com.example.inventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import com.example.inventory.data.ProductContract.ProductEntry;

public class ProductDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = ProductDbHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "inventory.db";

    private static final int DATABASE_VERSION = 1;

    public ProductDbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_PRODUCTS_TABLE = "CREATE TABLE " + ProductEntry.TABLE_NAME + " ("
                + ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ProductEntry.PRODUCT_NAME + " TEXT NOT NULL, "
                + ProductEntry.PRODUCT_PRICE + " INTEGER NOT NULL, "
                + ProductEntry.PRODUCT_QUANTITY + " INTEGER NOT NULL, "
                + ProductEntry.PRODUCT_PHOTO + " BLOB, "
                + ProductEntry.PRODUCT_TYPE + " INTEGER, "
                + ProductEntry.PRODUCT_SUPPLIER_INFO + " TEXT )";

        db.execSQL(SQL_CREATE_PRODUCTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


}
