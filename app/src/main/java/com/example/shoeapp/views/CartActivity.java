package com.example.shoeapp.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


import com.example.shoeapp.R;
import com.example.shoeapp.setting.UserSettings;
import com.example.shoeapp.utils.adapter.CartAdapter;
import com.example.shoeapp.utils.model.ShoeCart;
import com.example.shoeapp.viewmodel.CartViewModel;


import java.util.List;

public class CartActivity extends AppCompatActivity implements CartAdapter.CartClickedListeners {

    private RecyclerView recyclerView;
    private CartViewModel cartViewModel;
    private TextView totalCartPriceTv, textView;
    private AppCompatButton checkoutBtn;
    private CardView cardView;
    private CartAdapter cartAdapter;

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
        setContentView(R.layout.activity_cart);

        if(getSupportActionBar()!=null) getSupportActionBar().hide();

        initializeVariables();

        cartViewModel.getAllCartItems().observe(this, new Observer<List<ShoeCart>>() {
            @Override
            public void onChanged(List<ShoeCart> shoeCarts) {
                double price = 0;
                cartAdapter.setShoeCartList(shoeCarts);
                for (int i=0;i<shoeCarts.size();i++){
                    price = price + shoeCarts.get(i).getTotalItemPrice();
                }
                totalCartPriceTv.setText(String.valueOf(price));
            }
        });

        checkoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cartViewModel.deleteAllCartItems();
                textView.setVisibility(View.INVISIBLE);
                checkoutBtn.setVisibility(View.INVISIBLE);
                totalCartPriceTv.setVisibility(View.INVISIBLE);
                cardView.setVisibility(View.VISIBLE);
            }
        });
    }


    private void initializeVariables() {

        cartAdapter = new CartAdapter(this);
        textView = findViewById(R.id.textView2);
        cardView = findViewById(R.id.cartActivityCardView);
        totalCartPriceTv = findViewById(R.id.cartActivityTotalPriceTv);
        checkoutBtn = findViewById(R.id.cartActivityCheckoutBtn);
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
        recyclerView = findViewById(R.id.cartRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(cartAdapter);

    }

    @Override
    public void onDeleteClicked(ShoeCart shoeCart) {
        cartViewModel.deleteCartItem(shoeCart);
    }

    @Override
    public void onPlusClicked(ShoeCart shoeCart) {
        int quantity = shoeCart.getQuantity() + 1;
        cartViewModel.updateQuantity(shoeCart.getId() , quantity);
        cartViewModel.updatePrice(shoeCart.getId() , quantity*shoeCart.getShoePrice());
        cartAdapter.notifyDataSetChanged();
    }

    @Override
    public void onMinusClicked(ShoeCart shoeCart) {
        int quantity = shoeCart.getQuantity() - 1;
        if (quantity != 0){
            cartViewModel.updateQuantity(shoeCart.getId() , quantity);
            cartViewModel.updatePrice(shoeCart.getId() , quantity*shoeCart.getShoePrice());
            cartAdapter.notifyDataSetChanged();
        }else{
            cartViewModel.deleteCartItem(shoeCart);
        }

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