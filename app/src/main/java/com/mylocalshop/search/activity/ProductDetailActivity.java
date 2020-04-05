package com.mylocalshop.search.activity;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.mylocalshop.R;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBar;
import androidx.core.content.ContextCompat;

import android.view.MenuItem;


public class ProductDetailActivity extends AppCompatActivity {

    private boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        final FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFavorite) {
                    fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                            android.R.drawable.btn_star_big_on));
                } else {
                    fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                            android.R.drawable.btn_star_big_off));
                }
                isFavorite = !isFavorite;
            }
        });

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putString(ProductDetailFragment.ARG_PRODUCT_ID,
                    getIntent().getStringExtra(ProductDetailFragment.ARG_PRODUCT_ID));
            ProductDetailFragment fragment = new ProductDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.item_detail_container, fragment)
                    .commit();
        }
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
}
