package com.mylocalshop.search.activity;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.mylocalshop.R;
import com.mylocalshop.search.model.ProductContent;

public class ProductDetailFragment extends Fragment {

    public static final String ARG_PRODUCT_ID = "product_id";

    private ProductContent.ProductItem product;


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
        View rootView = inflater.inflate(R.layout.product_detail, container, false);

        try {
            if (product != null) {
                ((ImageView) rootView.findViewById(R.id.product_image)).setImageBitmap(product.imgBitMap);
                ((TextView) rootView.findViewById(R.id.product_name)).setText(product.name);
                ((TextView) rootView.findViewById(R.id.product_price)).setText(getString(R.string.currency_symbol) + product.price);
                ((TextView) rootView.findViewById(R.id.product_address)).setText(product.address);
            }

        }  catch (Exception e){
            e.printStackTrace();
        }

        return rootView;
    }
}
