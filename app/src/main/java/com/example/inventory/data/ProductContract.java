package com.example.inventory.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class ProductContract {

    public static final String CONTENT_AUTHORITY = "com.example.inventory";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_PRODUCTS = "products";
    private ProductContract() {

    }

    public final static class ProductEntry implements BaseColumns {


        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);

        public final static String TABLE_NAME = "products";

        public final static String _ID = "_id";

        public final static String PRODUCT_NAME = "name";

        public final static String PRODUCT_QUANTITY = "quantity";

        public final static String PRODUCT_PRICE = "price";

        public final static String PRODUCT_SUPPLIER_INFO = "supplier_info";

        public final static String PRODUCT_PHOTO = "photo";

        public static final String PRODUCT_TYPE = "product_type";

        public static final int PRODUCT_TYPE_UNKNOWN = 0;

        public static final int PRODUCT_TYPE_FOOD = 1;

        public static final int PRODUCT_TYPE_HOUSEHOLD = 2;

        //MIME TYPE FOR CONTENT URI...
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + CONTENT_AUTHORITY + PATH_PRODUCTS;

        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + CONTENT_AUTHORITY + PATH_PRODUCTS;

        public static boolean isValidType(int type) {
            if (type == PRODUCT_TYPE_UNKNOWN || type == PRODUCT_TYPE_FOOD || type == PRODUCT_TYPE_HOUSEHOLD) {
                return true;
            }
            return false;
        }

    }

}
