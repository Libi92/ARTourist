package com.cyberprism.libin.artourist;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cyberprism.libin.artourist.utils.WebServiceClient;

public class DataActivity extends AppCompatActivity {

    EditText editTextName;
    EditText editTextDetails;
    EditText editTextLatitude;
    EditText editTextLongitude;

    LocationManager locationManager;
    Location location;

    Button buttonLocation;
    Button buttonSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextDetails = (EditText) findViewById(R.id.editTextDetails);
        editTextLatitude = (EditText) findViewById(R.id.editTextLatitude);
        editTextLongitude = (EditText) findViewById(R.id.editTextLongitude);

        buttonLocation = (Button) findViewById(R.id.buttonLocation);
        buttonSave = (Button) findViewById(R.id.buttonSave);

        buttonLocation.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                try {
                    if (ActivityCompat.checkSelfPermission(DataActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(DataActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for Activity#requestPermissions for more details.
                        return;
                    }
                }
                catch (Exception ex){}
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if(location == null){
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }

                if(location != null){
                    editTextLatitude.setText(location.getLatitude() + "");
                    editTextLongitude.setText(location.getLongitude() + "");
                }
                else {
                    Toast.makeText(getApplicationContext(), "Cannot get location", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editTextName.getText().toString();
                String details = editTextDetails.getText().toString();
                String latitude = editTextLatitude.getText().toString();
                String longitude = editTextLongitude.getText().toString();

                new AddLocationAsync(name, details, latitude, longitude).execute();
            }
        });

    }

    class AddLocationAsync extends AsyncTask<Void, Void, Void>{

        String name, details, latitude, longitude;
        public AddLocationAsync(String name,String details, String latitude, String longitude){
            this.name = name;
            this.details = details;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        protected Void doInBackground(Void... params) {
            WebServiceClient.addLocation(name, details, latitude, longitude);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(getApplicationContext(), "Location Added", Toast.LENGTH_SHORT).show();
        }
    }

}
