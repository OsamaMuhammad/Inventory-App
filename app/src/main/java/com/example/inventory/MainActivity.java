package com.example.inventory;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.example.inventory.data.ProductContract.ProductEntry;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    private static final int PRODUCT_LOADER = 1;
    private ProductCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent m = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(m);
            }
        });

        View emptyView = findViewById(R.id.empty_view);

        ListView productListView = (ListView) findViewById(R.id.list);
        productListView.setEmptyView(emptyView);
        mCursorAdapter = new ProductCursorAdapter(this, null);
        productListView.setAdapter(mCursorAdapter);

        productListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                Uri currentProductUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id);
                intent.setData(currentProductUri);
                startActivity(intent);
            }
        });


        /*productListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // add here your logic like this
                int lastItem = firstVisibleItem + visibleItemCount;
               if(lastItem==totalItemCount){
                   fab.hide();
               }
               else
                   fab.show();
            }
        });*/


        getSupportLoaderManager().initLoader(PRODUCT_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_inventory, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_insert_dummy_data:
                insertPet();
                return true;
            case R.id.action_delete_all_enteries:
                deleteAllPets();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void insertPet() {
        ContentValues values = new ContentValues();
        values.put(ProductEntry.PRODUCT_NAME, "Camera");
        values.put(ProductEntry.PRODUCT_QUANTITY, 5);
        values.put(ProductEntry.PRODUCT_PRICE, 100);
        values.put(ProductEntry.PRODUCT_TYPE, ProductEntry.PRODUCT_TYPE_UNKNOWN);

        Uri uri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);
    }

    private void deleteAllPets() {
        int rowsdeleted = getContentResolver().delete(ProductEntry.CONTENT_URI, null, null);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        String[] projection = new String[]{
                ProductEntry._ID,
                ProductEntry.PRODUCT_NAME,
                ProductEntry.PRODUCT_QUANTITY,
                ProductEntry.PRODUCT_PRICE,
                ProductEntry.PRODUCT_PHOTO
        };

        return new CursorLoader(this, ProductEntry.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        mCursorAdapter.swapCursor(cursor);

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}

