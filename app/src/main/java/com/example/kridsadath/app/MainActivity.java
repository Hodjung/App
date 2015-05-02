package com.example.kridsadath.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;
import java.util.logging.Handler;

public class MainActivity extends Activity {
    public MainActivity() {}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("test", "checkLog");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_page);
        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Error");
        builder.setMessage("No internet connection");
        builder.setPositiveButton("Restart",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                startActivity(getIntent());
            }
        });
        String check="FAIL";
        try {
            check=new LoadAll(MainActivity.this).execute().get();
            Log.e("com.exam",check);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if (check.equals("OK")) {
            Intent myIntent = new Intent(MainActivity.this, place_page.class);
            startActivity(myIntent);
            finish();
        }
        else builder.show();
    }
}
