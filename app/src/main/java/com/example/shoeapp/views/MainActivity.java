package com.example.shoeapp.views;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.splashscreen.SplashScreenViewProvider;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.shoeapp.R;
import com.example.shoeapp.utils.adapter.ShoeItemAdapter;
import com.example.shoeapp.utils.model.ShoeCart;
import com.example.shoeapp.utils.model.ShoeItem;
import com.example.shoeapp.viewmodel.CartViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import android.window.SplashScreen;

public class MainActivity extends AppCompatActivity implements ShoeItemAdapter.ShoeClickedListeners {

    private RecyclerView recyclerView;
    private List<ShoeItem> shoeItemList;
    private ShoeItemAdapter adapter;
    private CartViewModel viewModel;
    private List<ShoeCart> shoeCartList;
    private CoordinatorLayout coordinatorLayout;
    private ImageView cartImageView;
    private ImageView settingImageView;

    boolean nightMODE;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        sharedPreferences = getSharedPreferences("MODE", Context.MODE_PRIVATE);
        nightMODE = sharedPreferences.getBoolean("night",false); //light mode is default mode

        if(nightMODE) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        sharedPreferences = getSharedPreferences("MODE", Context.MODE_PRIVATE);
        nightMODE = sharedPreferences.getBoolean("night",false); //light mode is default mode

        if(nightMODE) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(getSupportActionBar()!=null) getSupportActionBar().hide();

        initializeVariables();
        setUpList();


        adapter.setShoeItemList(shoeItemList);
        recyclerView.setAdapter(adapter);

        cartImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, CartActivity.class));
            }
        });


        settingImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SettingActivity.class));
            }
        });


    }



    @Override
    protected void onResume() {
        super.onResume();

        viewModel.getAllCartItems().observe(this, new Observer<List<ShoeCart>>() {
            @Override
            public void onChanged(List<ShoeCart> shoeCarts) {
                shoeCartList.addAll(shoeCarts);
            }
        });
    }

    private void setUpList() {
        shoeItemList.add(new ShoeItem("Nike Revolution", "Nike", R.drawable.nike_revolution_road, 15));
        shoeItemList.add(new ShoeItem("Nike Flex Run 2021", "NIKE", R.drawable.flex_run_road_running, 20));
        shoeItemList.add(new ShoeItem("Court Zoom Vapor", "NIKE", R.drawable.nikecourt_zoom_vapor_cage, 18));
        shoeItemList.add(new ShoeItem("EQ21 Run COLD.RDY", "ADIDAS", R.drawable.adidas_eq_run, 16.5));
        shoeItemList.add(new ShoeItem("Adidas Ozelia", "ADIDAS", R.drawable.adidas_ozelia_shoes_grey, 20));
        shoeItemList.add(new ShoeItem("Adidas Questar", "ADIDAS", R.drawable.adidas_questar_shoes, 22));
        shoeItemList.add(new ShoeItem("Adidas Questar", "ADIDAS", R.drawable.adidas_questar_shoes, 12));
        shoeItemList.add(new ShoeItem("Adidas Ultraboost", "ADIDAS", R.drawable.adidas_ultraboost, 15));

    }

    private void initializeVariables() {

        cartImageView = findViewById(R.id.cartIv);
        settingImageView = findViewById(R.id.setting);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        shoeCartList = new ArrayList<>();
        viewModel = new ViewModelProvider(this).get(CartViewModel.class);
        shoeItemList = new ArrayList<>();
        recyclerView = findViewById(R.id.mainRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        adapter = new ShoeItemAdapter(this);

    }

    @Override
    public void onCardClicked(ShoeItem shoe) {

        Intent intent = new Intent(MainActivity.this, DetailedActivity.class);
        intent.putExtra("shoeItem", shoe);
        startActivity(intent);
    }

    @Override
    public void onAddToCartBtnClicked(ShoeItem shoeItem) {
        ShoeCart shoeCart = new ShoeCart();
        shoeCart.setShoeName(shoeItem.getShoeName());
        shoeCart.setShoeBrandName(shoeItem.getShoeBrandName());
        shoeCart.setShoePrice(shoeItem.getShoePrice());
        shoeCart.setShoeImage(shoeItem.getShoeImage());

        final int[] quantity = {1};
        final int[] id = new int[1];

        if (!shoeCartList.isEmpty()) {
            for (int i = 0; i < shoeCartList.size(); i++) {
                if (shoeCart.getShoeName().equals(shoeCartList.get(i).getShoeName())) {
                    quantity[0] = shoeCartList.get(i).getQuantity();
                    quantity[0]++;
                    id[0] = shoeCartList.get(i).getId();
                }
            }
        }

        Log.d("TAG", "onAddToCartBtnClicked: " + quantity[0]);

        if (quantity[0] == 1) {
            shoeCart.setQuantity(quantity[0]);
            shoeCart.setTotalItemPrice(quantity[0] * shoeCart.getShoePrice());
            viewModel.insertCartItem(shoeCart);
        } else {
            viewModel.updateQuantity(id[0], quantity[0]);
            viewModel.updatePrice(id[0], quantity[0] * shoeCart.getShoePrice());
        }

        makeSnackBar("Item Added To Cart");
    }

    private void makeSnackBar(String msg) {
        Snackbar.make(coordinatorLayout, msg, Snackbar.LENGTH_SHORT)
                .setAction("Go to Cart", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(MainActivity.this, CartActivity.class));
                    }
                }).show();
    }

//





//    private void loadSharedPreferences()
//    {
//        SharedPreferences sharedPreferences = getSharedPreferences(UserSettings.PREFERENCES, MODE_PRIVATE);
//        String theme = sharedPreferences.getString(UserSettings.CUSTOM_THEME, UserSettings.LIGHT_THEME);
//        settings.setCustomTheme(theme);
//        updateView();
//    }
//    private void updateView()
//    {
//        final int black = ContextCompat.getColor(this, R.color.black);
//        final int white = ContextCompat.getColor(this, R.color.white);
//
//        if(settings.getCustomTheme().equals(UserSettings.DARK_THEME))
//        {
//            titleTV.setTextColor(white);
//            themeTV.setTextColor(white);
//            themeTV.setText("Dark");
//            coordinatorLayout.setBackgroundColor(black);
//            themeSwitch.setChecked(true);
//        }
//        else
//        {
//            titleTV.setTextColor(black);
//            themeTV.setTextColor(black);
//            themeTV.setText("Light");
//            coordinatorLayout.setBackgroundColor(white);
//            themeSwitch.setChecked(false);
//        }
//
//    }
}
