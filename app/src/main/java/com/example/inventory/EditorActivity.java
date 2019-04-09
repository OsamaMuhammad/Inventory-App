package com.example.inventory;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import com.example.inventory.data.ProductContract.ProductEntry;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = EditorActivity.class.getSimpleName();
    private static final int PRODUCT_ID_LOADER = 2;
    private static final int PICK_IMAGE_REQUEST = 4;
    private Spinner mTypeSpinner;
    private Button mIncrementButton;
    private Button mDecrementButtonl;
    private EditText mNameEditText;
    private EditText mQuantityEditText;
    private EditText mPriceEditText;
    private EditText mSupplierInfoEditText;
    private ImageView mProductPhoto;
    private int mType = ProductEntry.PRODUCT_TYPE_UNKNOWN;
    private byte[] mPhotoByteArray;
    private boolean mtouch = false;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mtouch = true;
            return false;
        }
    };
    private Uri mCurrentProductUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();

        if (mCurrentProductUri == null) {
            setTitle(R.string.title_add_product);
            invalidateOptionsMenu();
        } else {
            setTitle(R.string.title_edit_product);
            getSupportLoaderManager().initLoader(PRODUCT_ID_LOADER, null, this);

        }

        mTypeSpinner = (Spinner) findViewById(R.id.product_type_spinner);
        mIncrementButton = (Button) findViewById(R.id.increment_button);
        mDecrementButtonl = (Button) findViewById(R.id.decrement_button);
        mNameEditText = (EditText) findViewById(R.id.product_name_editText);
        mQuantityEditText = (EditText) findViewById(R.id.product_quantity_editText);
        mQuantityEditText.setText("0");
        mPriceEditText = (EditText) findViewById(R.id.product_price_editText);
        mPriceEditText.setText("0");
        mSupplierInfoEditText = (EditText) findViewById(R.id.supplier_info_editText);
        mProductPhoto = (ImageView) findViewById(R.id.product_image);
        setUpSpinner();

        mIncrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Integer quantity = Integer.valueOf(mQuantityEditText.getText().toString());
                quantity++;
                mQuantityEditText.setText(quantity.toString());
            }
        });

        mDecrementButtonl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer quantity = Integer.valueOf(mQuantityEditText.getText().toString());

                if (quantity <= 0) {
                    Toast.makeText(EditorActivity.this, "Quantity cannot be in negative", Toast.LENGTH_SHORT).show();
                } else {
                    quantity--;
                    mQuantityEditText.setText(quantity.toString());
                }
            }
        });

        mProductPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select picture"), PICK_IMAGE_REQUEST);
            }
        });
        mNameEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mIncrementButton.setOnTouchListener(mTouchListener);
        mDecrementButtonl.setOnTouchListener(mTouchListener);
        mSupplierInfoEditText.setOnTouchListener(mTouchListener);
        mProductPhoto.setOnTouchListener(mTouchListener);
        mTypeSpinner.setOnTouchListener(mTouchListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && intent != null && intent.getData() != null) {

            Uri uri = intent.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));

                mProductPhoto.setImageBitmap(bitmap);

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                mPhotoByteArray = bos.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (!mtouch) {
            super.onBackPressed();
            return;
        }


        showUnsavedChangesDialog();
    }


    private void showUnsavedChangesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_dialog_message);
        builder.setNegativeButton(R.string.dialog_action_keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        builder.setPositiveButton(R.string.dialog_action_discard, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_message);
        builder.setNegativeButton(R.string.dialog_action_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        builder.setPositiveButton(R.string.dialog_action_delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteProduct();
                finish();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentProductUri == null) {
            MenuItem item = menu.findItem(R.id.action_delete_product);
            item.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save_product:
                saveProduct();
                return true;
            case R.id.action_delete_product:
                showDeleteDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpSpinner() {

        ArrayAdapter productTypeArrayAdapter = ArrayAdapter.createFromResource(this, R.array.product_type_array, R.layout.support_simple_spinner_dropdown_item);

        productTypeArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        mTypeSpinner.setAdapter(productTypeArrayAdapter);

        mTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(R.string.product_food)) {
                        mType = ProductEntry.PRODUCT_TYPE_FOOD;
                    } else if (selection.equals(R.string.product_household)) {
                        mType = ProductEntry.PRODUCT_TYPE_HOUSEHOLD;

                    } else
                        mType = ProductEntry.PRODUCT_TYPE_UNKNOWN;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mType = ProductEntry.PRODUCT_TYPE_UNKNOWN;
            }
        });
    }

    private void saveProduct() {

        String name = mNameEditText.getText().toString().trim();
        String quantity = mQuantityEditText.getText().toString().trim();
        String price = mPriceEditText.getText().toString().trim();
        String supplierInfo = mSupplierInfoEditText.getText().toString().trim();

        if (mCurrentProductUri == null && TextUtils.isEmpty(name)
                && TextUtils.isEmpty(supplierInfo)) {
            return;
        } else if (TextUtils.isEmpty(name)) {
            Toast.makeText(EditorActivity.this, "Enter a valid name", Toast.LENGTH_SHORT).show();
        } else {
            ContentValues values = new ContentValues();
            values.put(ProductEntry.PRODUCT_NAME, name);
            values.put(ProductEntry.PRODUCT_QUANTITY, Integer.parseInt(mQuantityEditText.getText().toString()));
            values.put(ProductEntry.PRODUCT_PRICE, Integer.parseInt(mPriceEditText.getText().toString()));
            values.put(ProductEntry.PRODUCT_TYPE, mType);
            values.put(ProductEntry.PRODUCT_SUPPLIER_INFO, mSupplierInfoEditText.getText().toString());
            values.put(ProductEntry.PRODUCT_PHOTO, mPhotoByteArray);
            if (mCurrentProductUri == null) {
                Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);

                if (newUri == null) {
                    Toast.makeText(EditorActivity.this, "Product insertion failed", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EditorActivity.this, "Product successfully inserted", Toast.LENGTH_SHORT).show();

                }
            } else {

                int rowsUpdated = getContentResolver().update(mCurrentProductUri, values, null, null);

                if (rowsUpdated == 0) {
                    Toast.makeText(EditorActivity.this, "Product update failed", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EditorActivity.this, "Product updated successfully", Toast.LENGTH_SHORT).show();
                }
            }
            finish();
        }
    }


    private void deleteProduct() {
        int rowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);

        if (rowsDeleted == 0) {
            Toast.makeText(EditorActivity.this, "Product delete failed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(EditorActivity.this, "Product deleted", Toast.LENGTH_SHORT).show();

        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = new String[]{
                ProductEntry._ID,
                ProductEntry.PRODUCT_NAME,
                ProductEntry.PRODUCT_QUANTITY,
                ProductEntry.PRODUCT_PRICE,
                ProductEntry.PRODUCT_TYPE,
                ProductEntry.PRODUCT_SUPPLIER_INFO,
                ProductEntry.PRODUCT_PHOTO
        };

        return new CursorLoader(this, mCurrentProductUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            int nameColIndex = cursor.getColumnIndex(ProductEntry.PRODUCT_NAME);
            int quantityColIndex = cursor.getColumnIndex(ProductEntry.PRODUCT_QUANTITY);
            int priceColIndex = cursor.getColumnIndex(ProductEntry.PRODUCT_PRICE);
            int typeColIndex = cursor.getColumnIndex(ProductEntry.PRODUCT_TYPE);
            int supplierColIndex = cursor.getColumnIndex(ProductEntry.PRODUCT_SUPPLIER_INFO);
            int productPhotoIndex = cursor.getColumnIndex(ProductEntry.PRODUCT_PHOTO);

            String name = cursor.getString(nameColIndex);
            int quantity = cursor.getInt(quantityColIndex);
            int price = cursor.getInt(priceColIndex);
            int type = cursor.getInt(typeColIndex);
            String supplierInfo = cursor.getString(supplierColIndex);
            byte[] photoByteArray = cursor.getBlob(productPhotoIndex);


            mNameEditText.setText(name);
            mQuantityEditText.setText(Integer.toString(quantity));
            mPriceEditText.setText(Integer.toString(price));
            mSupplierInfoEditText.setText(supplierInfo);
            if (photoByteArray != null) {
                Bitmap bm = BitmapFactory.decodeByteArray(photoByteArray, 0, photoByteArray.length);
                mProductPhoto.setImageBitmap(bm);
            }

            switch (type) {
                case ProductEntry.PRODUCT_TYPE_UNKNOWN:
                    mTypeSpinner.setSelection(0);
                    break;
                case ProductEntry.PRODUCT_TYPE_FOOD:
                    mTypeSpinner.setSelection(1);
                    break;
                case ProductEntry.PRODUCT_TYPE_HOUSEHOLD:
                    mTypeSpinner.setSelection(2);
                    break;
            }


        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        mQuantityEditText.setText("");
        mPriceEditText.setText("");
        mSupplierInfoEditText.setText("");
    }
}

