package com.mylocalshop.search.activity;

import android.Manifest;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mylocalshop.R;
import com.mylocalshop.post.dao.Product;
import com.mylocalshop.post.service.ImageUploadService;
import com.mylocalshop.search.factory.ViewModelFactory;
import com.mylocalshop.search.view.model.ProductContent;
import com.mylocalshop.search.view.model.ProductsViewModel;
import com.mylocalshop.widget.TotalProductsOnShopWidgetProvider;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProductsListActivity extends AppCompatActivity {

    private static final int RC_TAKE_PICTURE = 1;
    private static final int RC_WRITE_PICTURE = 2;
    private static final int RC_READ_PICTURE = 3;
    private static final int CAMERA_DONE = 100;
    private static final String TAG = "ProductsListActivity";
    private ProgressBar mProgressBarDialog;
    private FrameLayout productListContainerLayout;
    private FirebaseAuth mAuth;
    private Uri photoURI;
    private ProductsListActivity contextActivity;
    public List<Product> localFavProducts;

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private SimpleItemRecyclerViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        signInAnonymously();
        setContentView(R.layout.activity_product_list);
        mProgressBarDialog = findViewById(R.id.progressBar);

        productListContainerLayout = findViewById(R.id.productListContainerLayout);

        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkPermission(Manifest.permission.CAMERA)) {
                    requestPermission(Manifest.permission.CAMERA, RC_TAKE_PICTURE);
                } else {
                    storePictureOnDevice();
                }
            }
        });

        if (findViewById(R.id.item_detail_container) != null) {
            mTwoPane = true;
        }
        contextActivity = this;

        ViewModelFactory viewModelFactoryInstance = new ViewModelFactory(this.getApplication());
        ProductsViewModel viewModel =
                ViewModelProviders.of(this, viewModelFactoryInstance).get(ProductsViewModel.class);


        viewModel.getFavProducts().observe(this, new Observer<List<Product>>() {
            @Override
            public void onChanged(@Nullable List<Product> favProducts) {
                contextActivity.localFavProducts = favProducts;
            }
        });

        new LoadFirebaseData().execute();
        SearchView searchView = findViewById(R.id.simpleSearchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String searchText) {
                if(searchText != null && searchText.trim().equals("")){
                    return false;
                }
                mAdapter.filter(searchText);
                return false;
            }
        });

        mProgressBarDialog.setVisibility(View.VISIBLE);
        productListContainerLayout.setVisibility(View.INVISIBLE);

        AppWidgetManager appWidgetManager =
                getApplicationContext().getSystemService(AppWidgetManager.class);
        ComponentName myProvider =
                new ComponentName(getApplicationContext(), TotalProductsOnShopWidgetProvider.class);



        if (appWidgetManager.isRequestPinAppWidgetSupported()) {
            Intent pinnedWidgetCallbackIntent = new Intent();

            PendingIntent successCallback = PendingIntent.getBroadcast(getApplicationContext(), 0,
                    pinnedWidgetCallbackIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            appWidgetManager.requestPinAppWidget(myProvider, null, successCallback);
        }
    }



    @Override
    public void onSaveInstanceState(Bundle out) {
        super.onSaveInstanceState(out);
    }

    @Override
    public void onRestoreInstanceState(Bundle restoredInstance) {
        super.onRestoreInstanceState(restoredInstance);
    }

    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {


        if (requestCode == RC_TAKE_PICTURE) {
            if (grantResults.length <= 0) {
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                storePictureOnDevice();
            }
        }

        if (requestCode == RC_WRITE_PICTURE) {
            if (grantResults.length <= 0) {
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                storePictureOnDevice();
            }
        }

        if (requestCode == RC_READ_PICTURE) {
            if (grantResults.length <= 0) {
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startService(new Intent(this, ImageUploadService.class)
                        .putExtra(getString(R.string.file_uri), photoURI)
                        .setAction(getString(R.string.action_upload)));
            }
        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat(getString(R.string.day_format)).format(new Date());
        String imageFileName = getString(R.string.image_file_prefix) + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, getString(R.string.image_file_format),
                storageDir
        );

        return image;
    }

    private void signInAnonymously() {
        mAuth.signInAnonymously()
                .addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Log.d(TAG, "signInAnonymously:SUCCESS");
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.e(TAG, "signInAnonymously:FAILURE", exception);
                    }
                });
    }

    private void startCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        startActivityForResult(intent, CAMERA_DONE);
    }


    private void setUpRecycleView() {
        View recyclerView = findViewById(R.id.item_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);
    }

    private void storePictureOnDevice() {
        if (!checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, RC_WRITE_PICTURE);
        } else {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            photoURI = FileProvider.getUriForFile(getApplicationContext(),
                    getString(R.string.image_file_provider),
                    photoFile);

            startCamera();
        }
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        mAdapter = new SimpleItemRecyclerViewAdapter(this, ProductContent.PRODUCTS, mTwoPane);
        recyclerView.setAdapter(mAdapter);
        mProgressBarDialog.setVisibility(View.INVISIBLE);
        productListContainerLayout.setVisibility(View.VISIBLE);
    }

    private void requestPermission(String permission, int code) {
        ActivityCompat.requestPermissions(this, new String[]{permission}, code);
    }

    private boolean checkPermission(String permission) {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                permission);
        return (permissionState == PackageManager.PERMISSION_GRANTED);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_DONE) {
            if (!checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE, RC_READ_PICTURE);
            } else {
                if(resultCode == 0){
                    Intent postProduct = new Intent(this, ProductsListActivity.class);
                    startActivity(postProduct);
                }else{
                    startService(new Intent(this, ImageUploadService.class)
                            .putExtra(getString(R.string.file_uri), photoURI)
                            .setAction(getString(R.string.action_upload)));
                }
            }
        }
    }



    private class LoadFirebaseData extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... voids) {
            signInAnonymously();
            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference dbReference = database.getReference();
            DatabaseReference ref = dbReference.child(getString(R.string.imgSaveFolderName));
            ref.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                    Product newProduct = dataSnapshot.getValue(Product.class);
                    if(ProductContent.PRODUCT_MAP.get(prevChildKey) == null){
                        boolean isLocallyFav = checkIfFavorite(prevChildKey);
                        newProduct.setId(prevChildKey);
                        newProduct.setFavorite(isLocallyFav);
                        ProductContent.addItem(convertToViewModel(newProduct, prevChildKey));
                    }
                    setUpRecycleView();
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }

                private ProductContent.ProductItem convertToViewModel(Product product, String prevProductId) {
                    ProductContent.ProductItem item = new ProductContent.ProductItem(prevProductId, product.getName() ,
                            String.valueOf(product.getPrice()), product.getImageUri(), product.getAddress(),
                            product.isFavorite());
                    return item;

                }

                private boolean  checkIfFavorite(String localDbPrimaryKey){

                    if(contextActivity.localFavProducts == null || contextActivity.localFavProducts.size() == 0){
                        return false;
                    }

                    for(Product currentProduct : contextActivity.localFavProducts){
                        if(currentProduct.getId().equals(localDbPrimaryKey)){
                            return true;
                        }
                    }
                    return false;
                }
            });
            return 0;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
        }

    }

    public class ImageLoadAsyncTask extends AsyncTask<Integer, Integer, Bitmap> {

        private String url;
        private ImageView imgView;
        private ProductContent.ProductItem product;

        public ImageLoadAsyncTask(String url, ImageView productImg, ProductContent.ProductItem product) {
            this.url = url;
            this.imgView = productImg;
            this.product = product;
        }

        @Override
        protected Bitmap doInBackground(Integer... params) {
            if(url == null) {
                return null;
            }
            try {
                URL urlConnection = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlConnection.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                int targetW = params[0];
                int targetH = params[1];

                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bmOptions.inJustDecodeBounds = true;

                int photoW = bmOptions.outWidth;
                int photoH = bmOptions.outHeight;

                bmOptions.inJustDecodeBounds = false;
                bmOptions.inSampleSize = Math.min(photoW/targetW, photoH/targetH);;

                Bitmap imgBitmMap = BitmapFactory.decodeStream(input, null, bmOptions);
                product.setImgBitMap(imgBitmMap);
                return imgBitmMap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if(result == null) {
                return;
            }
            super.onPostExecute(result);
            imgView.setImageBitmap(result);
        }
    }

    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        // Filter Class
        public void filter(String charText) {
            if(masterSet.size() <= 0 || charText.length() == 0){
                mValues = masterSet;
                return;
            }

            List<ProductContent.ProductItem> copy = new ArrayList<>();
            charText = charText.toLowerCase(Locale.getDefault());

                for (ProductContent.ProductItem item : masterSet) {
                    if (containsWord(item.name,charText)) {
                        copy.add(item);
                    }
                }

            mValues = copy;
            notifyDataSetChanged();

        }

        private boolean containsWord(String name, String charText) {
            String[] spaceSeparatedWords = null;
            if(name != null && name.length() != 0) {
                spaceSeparatedWords = name.split(" ");
            }

            if(spaceSeparatedWords == null || spaceSeparatedWords.length == 0){
                return name.contains(charText);
            }

            for(String currentWord : spaceSeparatedWords){

                currentWord = currentWord.trim();
                currentWord = currentWord.toLowerCase(Locale.getDefault());
                if(currentWord.contains(charText)){
                    return true;
                }
            }
            return false;
        }

        private final ProductsListActivity mParentActivity;
        private List<ProductContent.ProductItem> mValues;
        private List<ProductContent.ProductItem> masterSet;

        private final boolean mTwoPane;

        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProductContent.ProductItem item = (ProductContent.ProductItem) view.getTag();
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putString(ProductDetailFragment.ARG_PRODUCT_ID, item.id);
                    ProductDetailFragment fragment = new ProductDetailFragment();
                    fragment.setArguments(arguments);
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.item_detail_container, fragment)
                            .commit();
                } else {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, ProductDetailActivity.class);
                    intent.putExtra(ProductDetailFragment.ARG_PRODUCT_ID, item.id);

                    context.startActivity(intent);
                }
            }
        };


        SimpleItemRecyclerViewAdapter(ProductsListActivity parent,
                                      List<ProductContent.ProductItem> items,
                                      boolean twoPane ) {
            mValues = items;
            masterSet = items;
            mParentActivity = parent;
            mTwoPane = twoPane;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.product_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            ProductContent.ProductItem currentProduct = mValues.get(position);

            holder.mIdView.setText(currentProduct.name);
            holder.mPrice.setText(mParentActivity.getString(R.string.currency_symbol) + currentProduct.price);
            String uri = currentProduct.imgUri;

            ViewGroup.LayoutParams imgLayoutParams = holder.productImg.getLayoutParams();

            mParentActivity.new ImageLoadAsyncTask(uri, holder.productImg, currentProduct).
                    execute(imgLayoutParams.width, imgLayoutParams.height);

            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
            if(currentProduct.favorite){
                holder.isFavIcon.setImageDrawable(ContextCompat.getDrawable(
                        mParentActivity.getApplicationContext(), android.R.drawable.btn_star_big_on));
            }else{
                holder.isFavIcon.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mIdView;
            final TextView mPrice;
            final ImageView productImg;
            final ImageView isFavIcon;

            ViewHolder(View view) {
                super(view);
                mIdView = view.findViewById(R.id.name);
                mPrice = view.findViewById(R.id.price);
                productImg = view.findViewById(R.id.img);
                isFavIcon = view.findViewById(R.id.isFavIcon);
            }
        }
    }
}
