package com.example.shoeapp.views;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.shoeapp.R;

public class ConnectionReceiverActivity extends BroadcastReceiver {
    Context mContext;
    Dialog dialog;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        if (isConnected(context)) {
            Toast.makeText(context, "Internet connected, you are now going to view our inventory", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Not Connected, Please turn on your WIFI", Toast.LENGTH_SHORT).show();
            showDialog();
        }
    }

    public boolean isConnected(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            return (networkInfo != null && networkInfo.isConnected());
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }
    public void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.alert_dialog, null);
        builder.setView(view);

        dialog = builder.create();
        dialog.setCancelable(false); // Disable dialog dismissal by pressing outside of the dialog

        Button fixButton = view.findViewById(R.id.fix_button);
        fixButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isConnected(mContext)) {
                    dialog.dismiss(); // Dismiss the dialog once connected
                } else {
                    Toast.makeText(mContext, "Please fix your WiFi first", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();

        // Check for network connection status every 2 seconds
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isConnected(mContext)) {
                    fixButton.setEnabled(true); // Enable the button once connected
                    handler.removeCallbacks(this); // Stop checking for connection status
                } else {
                    handler.postDelayed(this, 2000); // Check again in 2 seconds
                }
            }
        }, 2000); // Start checking after 2 seconds

        fixButton.setEnabled(false); // Disable the button initially
    }
}
