package com.cyberprism.libin.artourist;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.cyberprism.libin.artourist.utils.Globals;
import com.cyberprism.libin.artourist.utils.WebServiceClient;
import com.cyberprism.libin.artourist.utils.WikiSearch;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

public class SamplePoiDetailActivity extends FragmentActivity implements OnMapReadyCallback {

	private GoogleMap mMap;
    private LocationManager locationManager;

	public static final String EXTRAS_KEY_POI_ID = "id";
	public static final String EXTRAS_KEY_POI_TITILE = "title";
	public static final String EXTRAS_KEY_POI_DESCR = "description";
    public static final String LATITUDE_KEY = "LATITUDE";
    public static final String LONGITUDE_KEY = "LONGITUDE";
    public static final String IMAGE_KEY = "IMAGE";
    public static final String IMAGE_RATING = "RATING";

    WebView webView;
    SharedPreferences preferences;

    ProgressDialog progressDialog;
    ImageView imageView;
    LinearLayout layoutRating;

    RatingBar ratingBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.sample_poidetail);

        preferences = getSharedPreferences(Globals.PREFERENCE_NAME, Context.MODE_PRIVATE);
        String ip = preferences.getString(Globals.IP_KEY, "10.0.2.2");
		
		((TextView)findViewById(R.id.poi_id)).setText(getIntent().getExtras().getString(EXTRAS_KEY_POI_ID));
		((TextView)findViewById(R.id.poi_title)).setText(getIntent().getExtras().getString(EXTRAS_KEY_POI_TITILE));
		((TextView)findViewById(R.id.poi_description)).setText(  getIntent().getExtras().getString(EXTRAS_KEY_POI_DESCR) );
        ((TextView)findViewById(R.id.textViewRating)).setText(  getIntent().getExtras().getString(IMAGE_RATING) );

        webView = (WebView)findViewById(R.id.webViewWiki);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        layoutRating = (LinearLayout)findViewById(R.id.layoutRating);

        imageView = (ImageView)findViewById(R.id.imageViewLocation);

        ratingBar = (RatingBar)findViewById(R.id.ratingBar);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, final float rating, boolean fromUser) {
                new AlertDialog.Builder(SamplePoiDetailActivity.this)
                        .setTitle("Rate Now")
                        .setMessage("Give a rating of " + rating)
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new AddRatingAsync("1", getIntent().getExtras().getString(EXTRAS_KEY_POI_ID), rating + "").execute();
                            }
                        })
                        .setNegativeButton("no", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).create().show();
            }
        });


        Picasso.with(SamplePoiDetailActivity.this).load("http://" + ip + ":8080/ARTourist/media/" + getIntent().getExtras().getString(IMAGE_KEY)).into(imageView);

        new SearchWikiAsync(getIntent().getExtras().getString(EXTRAS_KEY_POI_TITILE)).execute();

        if(getIntent().getExtras().getString(IMAGE_KEY).equals("noimage.png")){
            ratingBar.setVisibility(View.GONE);
            layoutRating.setVisibility(View.GONE);
        }
	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(SamplePoiDetailActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(SamplePoiDetailActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            LocationManager locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null) {

                double latitude = Double.parseDouble(getIntent().getExtras().getString(LATITUDE_KEY));;
                double longitude = Double.parseDouble(getIntent().getExtras().getString(LONGITUDE_KEY));;
                Log.d("old", "lat :  " + latitude);
                Log.d("old", "long :  " + longitude);

                MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude)).title(getIntent().getExtras().getString(EXTRAS_KEY_POI_TITILE));
                mMap.addMarker(marker);

                CameraPosition cameraPosition = new CameraPosition.Builder().target(
                        new LatLng(latitude, longitude)).zoom(12).build();

                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                googleMap.setMyLocationEnabled(true);
            }
        }
    }

    class SearchWikiAsync extends AsyncTask<Void, Void, String>{

        String searchTerm;
        public SearchWikiAsync(String term){
            this.searchTerm = term;
        }
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(SamplePoiDetailActivity.this);
            progressDialog.setTitle("Searching Wiki");
            progressDialog.setMessage("please wait..!");
            progressDialog.setIndeterminate(true);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = WikiSearch.search(searchTerm);
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("Wiki Result", result);

            webView.loadDataWithBaseURL("", result, "text/html", "UTF-8", "");
            progressDialog.dismiss();
            super.onPostExecute(result);
        }
    }

    class AddRatingAsync extends AsyncTask<Void, Void, Void>{

        String userId, placeId, rating;

        public AddRatingAsync(String userId, String placeId, String rating){
            this.placeId = placeId;
            this.userId = userId;
            this.rating = rating;

            Log.d("Data", placeId + ", " + userId + ", " + rating);
        }
        @Override
        protected Void doInBackground(Void... params) {

            WebServiceClient.addRating(userId, placeId, rating);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}
