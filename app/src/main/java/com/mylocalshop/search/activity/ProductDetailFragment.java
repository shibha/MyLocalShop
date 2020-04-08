package com.mylocalshop.search.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mylocalshop.R;
import com.mylocalshop.common.database.service.MarkProductFavoriteService;
import com.mylocalshop.search.view.model.ProductContent;

public class ProductDetailFragment extends Fragment {

    public static final String ARG_PRODUCT_ID = "product_id";
    public ProductContent.ProductItem product;
    public View rootView;

    public ProductDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_PRODUCT_ID)) {
            product = ProductContent.PRODUCT_MAP.get(getArguments().getString(ARG_PRODUCT_ID));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.product_detail, container, false);
        try {
            if (product != null) {
                ((ImageView) rootView.findViewById(R.id.product_image)).setImageBitmap(product.imgBitMap);
                ((TextView) rootView.findViewById(R.id.product_name)).setText(product.name);
                ((TextView) rootView.findViewById(R.id.product_price)).setText(getString(R.string.currency_symbol) + product.price);
                ((TextView) rootView.findViewById(R.id.product_address)).setText(product.address);
                FloatingActionButton fab = ((FloatingActionButton)rootView.findViewById(R.id.fab));
                if(product.favorite){
                    System.out.println("SETTING STAR ON");
                    fab.setImageDrawable(
                            ContextCompat.getDrawable(getActivity().getApplicationContext(), android.R.drawable.btn_star_big_on));
                }
                else{
                    System.out.println("SETTING STAR OFF");

                    fab.setImageDrawable(
                            ContextCompat.getDrawable(getActivity().getApplicationContext(), android.R.drawable.btn_star_big_off));
                }
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ProductContent.ProductItem currentProduct = product;
                        Context appContext = getActivity().getApplicationContext();
                        currentProduct.favorite = !currentProduct.favorite;
                        if (currentProduct.favorite) {
                            fab.setImageDrawable(ContextCompat.getDrawable(appContext,
                                    android.R.drawable.btn_star_big_on));
                        } else {
                            fab.setImageDrawable(ContextCompat.getDrawable(appContext,
                                    android.R.drawable.btn_star_big_off));
                        }
                        Intent markFavoriteIntent = new Intent(appContext, MarkProductFavoriteService.class);
                        markFavoriteIntent.putExtra(getString(R.string.id), currentProduct.id);
                        markFavoriteIntent.putExtra(getString(R.string.name), currentProduct.name);
                        markFavoriteIntent.putExtra(getString(R.string.address), currentProduct.address);
                        markFavoriteIntent.putExtra(getString(R.string.price), Float.parseFloat(currentProduct.price));
                        markFavoriteIntent.putExtra(getString(R.string.favorite), currentProduct.favorite);
                        getActivity().startService(markFavoriteIntent);
                    }
                });

            }

        }  catch (Exception e){
            e.printStackTrace();
        }

        return rootView;
    }
}
