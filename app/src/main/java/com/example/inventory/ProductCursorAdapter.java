package com.example.inventory;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inventory.data.ProductContract.ProductEntry;

public class ProductCursorAdapter extends CursorAdapter {


    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(final View view, final Context context, final Cursor cursor) {

        TextView name = (TextView) view.findViewById(R.id.product_name);
        TextView quantity = (TextView) view.findViewById(R.id.product_quantity);
        TextView price = (TextView) view.findViewById(R.id.product_price);
        ImageView photo = (ImageView) view.findViewById(R.id.product_image);
        Button sellButton=(Button)view.findViewById(R.id.sell_button);
        int nameColIndex = cursor.getColumnIndex(ProductEntry.PRODUCT_NAME);
        final int quantityColIndex = cursor.getColumnIndex(ProductEntry.PRODUCT_QUANTITY);
        int priceColIndex = cursor.getColumnIndex(ProductEntry.PRODUCT_PRICE);
        int photoColIndex = cursor.getColumnIndex(ProductEntry.PRODUCT_PHOTO);
        final int idColIndex=cursor.getColumnIndex(ProductEntry._ID);
        view.setTag(cursor.getPosition());

        long id=Long.valueOf(view.getTag().toString());


        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cursor.moveToPosition(Integer.valueOf(view.getTag().toString()));
                int quantity=cursor.getInt(quantityColIndex);
                long currentId=cursor.getInt(idColIndex);


                if(quantity>0){
                    quantity--;
                    ContentValues values=new ContentValues();
                    values.put(ProductEntry.PRODUCT_QUANTITY,quantity);
                    Uri currentUri= ContentUris.withAppendedId(ProductEntry.CONTENT_URI,currentId);
                    context.getContentResolver().update(currentUri,values,null,null);
                }
                else{
                    Toast.makeText(context,"Product out of stock!",Toast.LENGTH_SHORT).show();
                }
            }
        });


        name.setText(cursor.getString(nameColIndex));
        quantity.setText(cursor.getString(quantityColIndex));
        price.setText(cursor.getString(priceColIndex));

        byte[] bytes = cursor.getBlob(photoColIndex);
        if (bytes != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            photo.setImageBitmap(bitmap);
        }

    }
}
