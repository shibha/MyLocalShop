package com.mylocalshop.post.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mylocalshop.R;
import com.mylocalshop.post.dao.Product;
import com.mylocalshop.post.service.FetchAddressIntentService;
import com.mylocalshop.search.activity.ProductsListActivity;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PostProductActivity extends AppCompatActivity {

    private AddressResultReceiver mReceiver;
    private FusedLocationProviderClient mFusedLocationClient;
    private String mAddress = null;
    private String mName = null;
    private String imgFileUri = null;
    private Location mLastLocation;
    private static final int REQUEST_LOCATION_PERMISSION_CODE = 34;
    private static final String TAG = "PostProductActivity";
    private Uri mFileUri = null;
    private static final String KEY_FILE_URI = "key_file_uri";
    private List<String> tags = new ArrayList<>();
    private boolean isPriceEntered = false;
    private boolean isNameEntered = false;
    private boolean isMinOneTagAdded = false;
    private boolean isAddressEntered = false;
    private ProgressBar mProgressBarDialog;
    private LinearLayout product_name_layout;
    private LinearLayout product_price_layout;
    private LinearLayout product_loc_layout;
    private LinearLayout product_tags_layout;
    private ChipGroup tags_list;
    private Button post_button;

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == REQUEST_LOCATION_PERMISSION_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                getAddress();
            }
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (!checkPermissions()) {
            requestPermissions();
        } else {
            getAddress();
        }
    }

    @Override
    public void onBackPressed() {
        Intent prevActivity = new Intent(this, ProductsListActivity.class);
        startActivity(prevActivity);
    }


    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void getAddress() {

        mFusedLocationClient.getLastLocation()
            .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                if (location == null) {
                    Log.w(TAG, "Did not get location");
                        return;
                    }

                    mLastLocation = location;

                    startIntentService();

                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "getLastLocation:onFailure", e);
                    }
                });
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(PostProductActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_LOCATION_PERMISSION_CODE);

    }

    /**
     * Creates an intent, adds location data to it as an extra, and starts the intent service for
     * fetching an address.
     */
    private void startIntentService() {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(getString(R.string.intent_extra_loc), mLastLocation);
        startService(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_product);
        final Button postProduct = findViewById(R.id.post_button);
        final EditText nameTextView = findViewById(R.id.product_name);

        nameTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s != null && s.length() > 0){
                    if(isPriceEntered && isMinOneTagAdded && isAddressEntered){
                        postProduct.setEnabled(true);
                    }
                    isNameEntered = true;
                }else{
                    postProduct.setEnabled(false);
                    isNameEntered = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        IntentFilter addressFetchedIntentFilter = new IntentFilter(FetchAddressIntentService.ACTION_RESP);
        addressFetchedIntentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        mReceiver = new AddressResultReceiver();
        registerReceiver(mReceiver, addressFetchedIntentFilter);
        Intent activityIntent = getIntent();
        mName = activityIntent.getStringExtra(getString(R.string.product_name_textview));
        final EditText priceTextView = findViewById(R.id.product_price);
        priceTextView.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);


        priceTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s != null && s.length() > 0){
                    if(isNameEntered && isMinOneTagAdded && isAddressEntered){
                        postProduct.setEnabled(true);
                    }
                    isPriceEntered = true;
                }else{
                    postProduct.setEnabled(false);
                    isPriceEntered = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        EditText addressTextView = findViewById(R.id.product_address);
        addressTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s != null && s.length() > 0){
                    if(isNameEntered && isMinOneTagAdded && isMinOneTagAdded){
                        postProduct.setEnabled(true);
                    }
                    isAddressEntered = true;
                }else{
                    postProduct.setEnabled(false);
                    isAddressEntered = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (mName != null) {
            nameTextView.setText(mName);
        } else {
            Calendar rightNow = Calendar.getInstance();
            int hour = rightNow.get(Calendar.HOUR_OF_DAY);
            String defaultProductName = "ENTER PRODUCT NAME " + hour;
            mName = defaultProductName;
            nameTextView.setText(defaultProductName);
        }

        final EditText newTag = findViewById(R.id.new_tag);
        Button addTagButton = findViewById(R.id.tags_add_button);

        addTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newTag.getText() == null || newTag.getText().toString().trim() == "") {
                    return;
                }
                final ChipGroup chipGrp = findViewById(R.id.tags_list);
                final Chip chip = new Chip(chipGrp.getContext());
                chip.setCloseIconVisible(true);
                chip.setOnCloseIconClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CharSequence text = chip.getText();
                        tags.remove(text.toString());
                        chipGrp.removeView(chip);

                        if (tags != null && tags.size() > 0) {
                            if (isNameEntered && isPriceEntered && isAddressEntered) {
                                postProduct.setEnabled(true);
                            }
                            isMinOneTagAdded = true;
                        } else {
                            postProduct.setEnabled(false);
                            isMinOneTagAdded = false;
                        }
                    }
                });


                Editable text = newTag.getText();
                chip.setText(text.toString().trim());
                tags.add(text.toString().trim());
                chipGrp.addView(chip);
                newTag.setText("");

                if (tags != null && tags.size() > 0) {
                    if (isNameEntered && isPriceEntered && isAddressEntered) {
                        postProduct.setEnabled(true);
                    }
                    isMinOneTagAdded = true;
                } else {
                    postProduct.setEnabled(false);
                    isMinOneTagAdded = false;
                }
            }
        });

        Uri fileUri = getIntent().getParcelableExtra(getString(R.string.file_uri));

        if (fileUri != null) {
            imgFileUri = fileUri.toString();
        }

        postProduct.setEnabled(false);
        final AppCompatActivity thisActivity = this;
        postProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference();
            DatabaseReference productsRef = ref.child(getString(R.string.table_name_products));

            final float price = Float.parseFloat(priceTextView.getText().toString());
            productsRef.push().setValue(new Product(nameTextView.getText().toString(), mAddress, imgFileUri, price, tags));
            Intent productListActivityIntent = new Intent(thisActivity, ProductsListActivity.class);
            startActivity(productListActivityIntent);
            }
        });

        mProgressBarDialog = findViewById(R.id.progressBar);
        product_name_layout = findViewById(R.id.product_name_container_layout);
        product_price_layout = findViewById(R.id.product_price_container_layout);
        product_loc_layout = findViewById(R.id.product_loc_container_layout);
        product_tags_layout = findViewById(R.id.product_tag_container_layout);
        tags_list = findViewById(R.id.tags_list);
        post_button = findViewById(R.id.post_button);
        if(imgFileUri == null){
            mProgressBarDialog.setVisibility(View.VISIBLE);
            product_name_layout.setVisibility(View.INVISIBLE);
            product_price_layout.setVisibility(View.INVISIBLE);
            product_loc_layout.setVisibility(View.INVISIBLE);
            product_tags_layout.setVisibility(View.INVISIBLE);
            tags_list.setVisibility(View.INVISIBLE);
            post_button.setVisibility(View.INVISIBLE);
        }else{
            ViewGroup parent = (ViewGroup) mProgressBarDialog.getParent();
            parent.removeView(mProgressBarDialog);
            product_name_layout.setVisibility(View.VISIBLE);
            product_price_layout.setVisibility(View.VISIBLE);
            product_loc_layout.setVisibility(View.VISIBLE);
            product_tags_layout.setVisibility(View.VISIBLE);
            tags_list.setVisibility(View.VISIBLE);
            post_button.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle out) {
        super.onSaveInstanceState(out);
        out.putParcelable(KEY_FILE_URI, mFileUri);
    }

    /**
     * Receiver for data sent from FetchAddressIntentService.
     */
    private class AddressResultReceiver extends BroadcastReceiver {

        /**
         * Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            // Display the address string or an error message sent from the intent service.
            mAddress = intent.getStringExtra(getString(R.string.intent_address));
            if(mAddress != null && mAddress.length() > 0 ){
                isAddressEntered = true;
            }
            EditText addressTextView = findViewById(R.id.product_address);
            addressTextView.setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            addressTextView.setText(mAddress);
        }
    }
}
