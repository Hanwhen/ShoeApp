package com.example.shoeapp.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;


import com.example.shoeapp.setting.UserSettings;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.example.shoeapp.R;
public class SettingActivity extends AppCompatActivity {
    BroadcastReceiver broadcastReceiver; //NEEDED
    Switch switcher;
    boolean nightMODE;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        //NEED THIS
        broadcastReceiver = new ConnectionReceiver(); //NEEDED
        registerNetworkBroadCast(); //NEEDED

       if(getSupportActionBar()!=null) getSupportActionBar().hide();

        switcher =findViewById(R.id.switcher);


        //we use shard preference to save mode if exit the app and go back again
        sharedPreferences = getSharedPreferences("MODE", Context.MODE_PRIVATE);
        nightMODE = sharedPreferences.getBoolean("night",false); //light mode is default mode


        if(nightMODE) {
            switcher.setChecked(true);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }


        switcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nightMODE){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    editor = sharedPreferences.edit();
                    editor.putBoolean("night", false);
                }
                else{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    editor=sharedPreferences.edit();
                    editor.putBoolean("night", true);
                }
                editor.apply();
            }
        });
    }
    //NEEDED
    public void registerNetworkBroadCast() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }
    //NEEDED
    protected void unregisterNetwork(){
        try{
            unregisterReceiver(broadcastReceiver);
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }
    }
}