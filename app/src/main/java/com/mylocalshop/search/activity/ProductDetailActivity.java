package com.mylocalshop.search.activity;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mylocalshop.R;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBar;
import androidx.core.content.ContextCompat;
import android.view.MenuItem;

public class ProductDetailActivity extends AppCompatActivity {

    private ProductDetailFragment fragment;
    private IsProductFavLocallyReceiver isProductFavIntentReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        final FloatingActionButton fab = findViewById(R.id.fab);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putString(ProductDetailFragment.ARG_PRODUCT_ID,
                    getIntent().getStringExtra(ProductDetailFragment.ARG_PRODUCT_ID));
            fragment = new ProductDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.item_detail_container, fragment)
                    .commit();

        }

//        final AppCompatActivity thisActivity = this;

//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ProductContent.ProductItem currentProduct = fragment.product;
//                if (!currentProduct.favorite) {
//                    fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
//                            android.R.drawable.btn_star_big_on));
//                } else {
//                    fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
//                            android.R.drawable.btn_star_big_off));
//                }
//                currentProduct.favorite = !currentProduct.favorite;
//                Intent markFavoriteIntent = new Intent(thisActivity, MarkProductFavoriteService.class);
//                markFavoriteIntent.putExtra(getString(R.string.id), currentProduct.id);
//                markFavoriteIntent.putExtra(getString(R.string.name), currentProduct.name);
//                markFavoriteIntent.putExtra(getString(R.string.address), currentProduct.address);
//                markFavoriteIntent.putExtra(getString(R.string.price), Float.parseFloat(currentProduct.price));
//                markFavoriteIntent.putExtra(getString(R.string.favorite), currentProduct.favorite);
//                startService(markFavoriteIntent);
//            }
//        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
             navigateUpTo(new Intent(this, ProductsListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class IsProductFavLocallyReceiver extends BroadcastReceiver {
        private String TAG = "IsProductFavLocallyReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "in side IsProductFavLocallyReceiver for "  );

            fragment.product.favorite = intent.getBooleanExtra(getString(R.string.total_fav), false);
            Log.d(TAG, "product.favorite "  + fragment.product.favorite);

            if(fragment.product.favorite) {
                Log.d(TAG, "icon is .favorite btn_star_big_on" );

                ((FloatingActionButton) fragment.rootView.findViewById(R.id.fab)).setImageDrawable(
                        ContextCompat.getDrawable(getApplicationContext(), android.R.drawable.btn_star_big_on));
            }else {
                Log.d(TAG, "icon is .favorite btn_star_big_off" );

                ((FloatingActionButton) fragment.rootView.findViewById(R.id.fab)).setImageDrawable(
                        ContextCompat.getDrawable(getApplicationContext(), android.R.drawable.btn_star_big_off));
            }

        }
    }

}
