package com.cyberprism.libin.artourist;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.cyberprism.libin.artourist.utils.Globals;

public class MainActivity extends AppCompatActivity {

    SharedPreferences preferences;
    EditText editTextIP;

    ImageView imageViewATM;
    ImageView imageViewHotels;
    ImageView imageViewPlaces;
    ImageView imageViewRestaurents;
    ImageView imageViewShopping;
    ImageView imageViewTransit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = getSharedPreferences(Globals.PREFERENCE_NAME, Context.MODE_PRIVATE);
        String ip = preferences.getString(Globals.IP_KEY, "10.0.2.2");
        Globals.setServer(ip);

        imageViewATM = (ImageView)findViewById(R.id.imageViewATM);
        imageViewHotels = (ImageView)findViewById(R.id.imageViewHotels);
        imageViewPlaces = (ImageView)findViewById(R.id.imageViewPlaces);
        imageViewRestaurents = (ImageView)findViewById(R.id.imageViewRestaurents);
        imageViewShopping = (ImageView)findViewById(R.id.imageViewShopping);
        imageViewTransit = (ImageView)findViewById(R.id.imageViewTravels);

        imageViewATM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ARActivity.class);
                intent.putExtra(ARActivity.SEARCH_KEY, "atm");
                startActivity(intent);
            }
        });

        imageViewHotels.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ARActivity.class);
                intent.putExtra(ARActivity.SEARCH_KEY, "lodging");
                startActivity(intent);
            }
        });

        imageViewPlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ARActivity.class);
                intent.putExtra(ARActivity.SEARCH_KEY, "museum|zoo");
                startActivity(intent);
            }
        });

        imageViewRestaurents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ARActivity.class);
                intent.putExtra(ARActivity.SEARCH_KEY, "restaurant");
                startActivity(intent);
            }
        });

        imageViewShopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ARActivity.class);
                intent.putExtra(ARActivity.SEARCH_KEY, "shopping_mall");
                startActivity(intent);
            }
        });

        imageViewTransit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ARActivity.class);
                intent.putExtra(ARActivity.SEARCH_KEY, "travel_agency|transit_station");
                startActivity(intent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_settings){
            String IP = preferences.getString(Globals.IP_KEY, "10.0.2.2");
            editTextIP = new EditText(MainActivity.this);
            editTextIP.setText(IP);

            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("SET IP")
                    .setView(editTextIP)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String ip = editTextIP.getText().toString();
                            preferences.edit().putString(Globals.IP_KEY, ip).apply();

                            Globals.setServer(ip);
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        }
        else if(id == R.id.action_add){
            Intent intent = new Intent(MainActivity.this, DataActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
