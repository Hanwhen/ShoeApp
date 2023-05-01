package com.example.shoeapp.views;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.shoeapp.R;
import com.example.shoeapp.setting.UserSettings;
import com.example.shoeapp.utils.model.ShoeCart;
import com.example.shoeapp.utils.model.ShoeItem;
import com.example.shoeapp.viewmodel.CartViewModel;

import java.util.ArrayList;
import java.util.List;


public class DetailedActivity extends AppCompatActivity {

    private ImageView shoeImageView;
    private TextView shoeNameTV, shoeBrandNameTV, shoePriceTV;
    private AppCompatButton addToCartBtn;
    private ShoeItem shoe;
    private CartViewModel viewModel;
    private List<ShoeCart> shoeCartList;
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

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);

        if(getSupportActionBar()!=null) getSupportActionBar().hide();

        shoe = getIntent().getParcelableExtra("shoeItem");
        initializeVariables();

        viewModel.getAllCartItems().observe(this, new Observer<List<ShoeCart>>() {
            @Override
            public void onChanged(List<ShoeCart> shoeCarts) {
                shoeCartList.addAll(shoeCarts);
            }
        });

        if (shoe != null) {
            setDataToWidgets();
        }

        addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertToRoom();
            }
        });

    }

    private void insertToRoom(){
        ShoeCart shoeCart = new ShoeCart();
        shoeCart.setShoeName(shoe.getShoeName());
        shoeCart.setShoeBrandName(shoe.getShoeBrandName());
        shoeCart.setShoePrice(shoe.getShoePrice());
        shoeCart.setShoeImage(shoe.getShoeImage());

        final int[] quantity = {1};
        final int[] id = new int[1];

        if (!shoeCartList.isEmpty()){
            for(int i=0;i<shoeCartList.size();i++){
                if (shoeCart.getShoeName().equals(shoeCartList.get(i).getShoeName())){
                    quantity[0] = shoeCartList.get(i).getQuantity();
                    quantity[0]++;
                    id[0] = shoeCartList.get(i).getId();
                }
            }
        }

        if (quantity[0]==1){
            shoeCart.setQuantity(quantity[0]);
            shoeCart.setTotalItemPrice(quantity[0]*shoeCart.getShoePrice());
            viewModel.insertCartItem(shoeCart);
        }else{

            viewModel.updateQuantity(id[0] ,quantity[0]);
            viewModel.updatePrice(id[0] , quantity[0]*shoeCart.getShoePrice());
        }

        startActivity(new Intent(DetailedActivity.this , CartActivity.class));
    }

    private void setDataToWidgets() {
        shoeNameTV.setText(shoe.getShoeName());
        shoeBrandNameTV.setText(shoe.getShoeBrandName());
        shoePriceTV.setText(String.valueOf(shoe.getShoePrice()));
        shoeImageView.setImageResource(shoe.getShoeImage());
    }

    private void initializeVariables() {

        shoeCartList = new ArrayList<>();
        shoeImageView = findViewById(R.id.detailActivityShoeIV);
        shoeNameTV = findViewById(R.id.detailActivityShoeNameTv);
        shoeBrandNameTV = findViewById(R.id.detailActivityShoeBrandNameTv);
        shoePriceTV = findViewById(R.id.detailActivityShoePriceTv);
        addToCartBtn = findViewById(R.id.detailActivityAddToCartBtn);

        viewModel = new ViewModelProvider(this).get(CartViewModel.class);
    }
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
//            parentView.setBackgroundColor(black);
//            themeSwitch.setChecked(true);
//        }
//        else
//        {
//            titleTV.setTextColor(black);
//            themeTV.setTextColor(black);
//            themeTV.setText("Light");
//            parentView.setBackgroundColor(white);
//            themeSwitch.setChecked(false);
//        }
//
//    }
}