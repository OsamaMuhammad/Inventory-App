package com.example.inventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.example.inventory.data.ProductContract.ProductEntry;

public class ProductProvider extends ContentProvider {

    private static final UriMatcher sUrimatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int PRODUCTS = 100;
    private static final int PRODUCT_ID = 101;
    private static String LOG_TAG = ProductProvider.class.getSimpleName();

    static {
        sUrimatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS, PRODUCTS);
        sUrimatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS + "/#", PRODUCT_ID);
    }

    private ProductDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new ProductDbHelper(getContext());
        return true;
    }


    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = null;

        int match = sUrimatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                cursor = db.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PRODUCT_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Cannot query unknown uri");
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = sUrimatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return ProductEntry.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return ProductEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown Uri " + uri + " with match " + match);
        }
    }


    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        int match = sUrimatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return insertProduct(uri, values);

            default:
                throw new IllegalArgumentException("Insertion is not possible");
        }
    }

    private Uri insertProduct(Uri uri, ContentValues values) {

        String name = values.getAsString(ProductEntry.PRODUCT_NAME);
        Integer quantity = values.getAsInteger(ProductEntry.PRODUCT_QUANTITY);
        Integer price = values.getAsInteger(ProductEntry.PRODUCT_PRICE);
        Integer type = values.getAsInteger(ProductEntry.PRODUCT_TYPE);

        if (name == null) {
            throw new IllegalArgumentException("Product requires a name");
        }
        if (quantity < 0 && quantity != null) {
            throw new IllegalArgumentException("Product requires valid quantity");
        }
        if (price < 0 && price != null) {
            throw new IllegalArgumentException("Product requires valid price");
        }
        if (!ProductEntry.isValidType(type)) {
            throw new IllegalArgumentException("Product requires valid type");
        }
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long id = db.insert(ProductEntry.TABLE_NAME, null, values);

        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row " + uri);
            return null;
        }

        if (id != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return ContentUris.withAppendedId(uri, id);

    }


    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int rowsDeleted;
        int match = sUrimatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                rowsDeleted = db.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PRODUCT_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = db.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalStateException("deletion is not supported for uri " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match = sUrimatcher.match(uri);

        switch (match) {
            case PRODUCTS:
                return updateProduct(uri, values, selection, selectionArgs);
            case PRODUCT_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for uri " + uri);
        }

    }

    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {


        if (values.containsKey(ProductEntry.PRODUCT_NAME)) {
            String name = values.getAsString(ProductEntry.PRODUCT_NAME);
            if (name == null || TextUtils.isEmpty(name)) {
                throw new IllegalArgumentException("Product requires a name");
            }
        }
        if (values.containsKey(ProductEntry.PRODUCT_QUANTITY)) {
            Integer quantity = values.getAsInteger(ProductEntry.PRODUCT_QUANTITY);
            if (quantity < 0 && quantity != null) {
                throw new IllegalArgumentException("Product requires valid quantity");
            }
        }
        if (values.containsKey(ProductEntry.PRODUCT_PRICE)) {
            Integer price = values.getAsInteger(ProductEntry.PRODUCT_PRICE);
            if (price < 0 && price != null) {
                throw new IllegalArgumentException("Product requires valid price");
            }
        }
        if (values.containsKey(ProductEntry.PRODUCT_TYPE)) {
            Integer type = values.getAsInteger(ProductEntry.PRODUCT_TYPE);
            if (!ProductEntry.isValidType(type) || type == null) {
                throw new IllegalArgumentException("Product requires valid type");
            }
        }
        if (values.size() == 0) {
            return 0;
        }
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int rowsUpdated = db.update(ProductEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }
}


