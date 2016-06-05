package com.cyberprism.libin.artourist;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.SensorManager;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.cyberprism.libin.artourist.arhandler.AbstractArchitectCamActivity;
import com.cyberprism.libin.artourist.arhandler.ArchitectViewHolderInterface;
import com.cyberprism.libin.artourist.arhandler.LocationProvider;
import com.cyberprism.libin.artourist.utils.Globals;
import com.wikitude.architect.ArchitectView;
import com.wikitude.architect.StartupConfiguration;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Libin on 19-Mar-16.
 */
public class ARActivity extends AbstractArchitectCamActivity {

    public static String SEARCH_KEY = "SEARCHKEY";
    private long lastCalibrationToastShownTimeMillis = System.currentTimeMillis();

    protected Bitmap screenCapture = null;

    private static final int WIKITUDE_PERMISSIONS_REQUEST_EXTERNAL_STORAGE = 3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String searchKey = getIntent().getStringExtra(SEARCH_KEY);

        if (searchKey.equals("atm") || searchKey.equals("restaurant")){
            super.injectData(searchKey, 1000);
        }
        else {
            super.injectData(searchKey, 20000);
        }

    }

    @Override
    protected StartupConfiguration.CameraPosition getCameraPosition() {
        return StartupConfiguration.CameraPosition.DEFAULT;
    }

    @Override
    protected boolean hasGeo() {
        return true;
    }

    @Override
    protected boolean hasIR() {
        return false;
    }

    @Override
    public String getActivityTitle() {
        return "GeoAR";
    }

    @Override
    public String getARchitectWorldPath() {
        return "data/arpoi/index.html";
    }


    @Override
    public int getContentViewId() {
        return R.layout.cam_layout;
    }

    @Override
    public String getWikitudeSDKLicenseKey() {
        return Globals.WIKI_KEY;
    }

    @Override
    public int getArchitectViewId() {
        return R.id.architectView;
    }

    @Override
    public ILocationProvider getLocationProvider(LocationListener locationListener) {
        return new LocationProvider(this, locationListener);
    }

    @Override
    public ArchitectView.SensorAccuracyChangeListener getSensorAccuracyListener() {
        return new ArchitectView.SensorAccuracyChangeListener() {
            @Override
            public void onCompassAccuracyChanged( int accuracy ) {
				/* UNRELIABLE = 0, LOW = 1, MEDIUM = 2, HIGH = 3 */
                if ( accuracy < SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM && ARActivity.this != null && !ARActivity.this.isFinishing() && System.currentTimeMillis() - ARActivity.this.lastCalibrationToastShownTimeMillis > 5 * 1000) {
                    Toast.makeText(ARActivity.this, "Low accuracy", Toast.LENGTH_LONG).show();
                    ARActivity.this.lastCalibrationToastShownTimeMillis = System.currentTimeMillis();
                }
            }
        };
    }

    @Override
    public float getInitialCullingDistanceMeters() {
        return ArchitectViewHolderInterface.CULLING_DISTANCE_DEFAULT_METERS;
    }

    @Override
    public ArchitectView.ArchitectUrlListener getUrlListener() {
        return new ArchitectView.ArchitectUrlListener() {

            @Override
            public boolean urlWasInvoked(String uriString) {
                Uri invokedUri = Uri.parse(uriString);

                // pressed "More" button on POI-detail panel
                if ("markerselected".equalsIgnoreCase(invokedUri.getHost())) {
                    final Intent poiDetailIntent = new Intent(ARActivity.this, SamplePoiDetailActivity.class);
                    poiDetailIntent.putExtra(SamplePoiDetailActivity.EXTRAS_KEY_POI_ID, String.valueOf(invokedUri.getQueryParameter("id")) );
                    poiDetailIntent.putExtra(SamplePoiDetailActivity.EXTRAS_KEY_POI_TITILE, String.valueOf(invokedUri.getQueryParameter("title")) );
                    poiDetailIntent.putExtra(SamplePoiDetailActivity.EXTRAS_KEY_POI_DESCR, String.valueOf(invokedUri.getQueryParameter("description")) );
                    poiDetailIntent.putExtra(SamplePoiDetailActivity.LATITUDE_KEY, String.valueOf(invokedUri.getQueryParameter("latitude")) );
                    poiDetailIntent.putExtra(SamplePoiDetailActivity.LONGITUDE_KEY, String.valueOf(invokedUri.getQueryParameter("longitude")) );
                    poiDetailIntent.putExtra(SamplePoiDetailActivity.IMAGE_KEY, String.valueOf(invokedUri.getQueryParameter("image")) );
                    poiDetailIntent.putExtra(SamplePoiDetailActivity.IMAGE_RATING, String.valueOf(invokedUri.getQueryParameter("rating")) );
                    ARActivity.this.startActivity(poiDetailIntent);

                    Log.d("IMAGE", String.valueOf(invokedUri.getQueryParameter("image")));

                    return true;
                }

                // pressed snapshot button. check if host is button to fetch e.g. 'architectsdk://button?action=captureScreen', you may add more checks if more buttons are used inside AR scene
                else if ("button".equalsIgnoreCase(invokedUri.getHost())) {
                    ARActivity.this.architectView.captureScreen(ArchitectView.CaptureScreenCallback.CAPTURE_MODE_CAM_AND_WEBVIEW, new ArchitectView.CaptureScreenCallback() {

                        @Override
                        public void onScreenCaptured(final Bitmap screenCapture) {
                            if ( ContextCompat.checkSelfPermission(ARActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
                                ARActivity.this.screenCapture = screenCapture;
                                ActivityCompat.requestPermissions(ARActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WIKITUDE_PERMISSIONS_REQUEST_EXTERNAL_STORAGE);
                            } else {
                                ARActivity.this.saveScreenCaptureToExternalStorage(screenCapture);
                            }
                        }
                    });
                }
                return true;
            }
        };
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case WIKITUDE_PERMISSIONS_REQUEST_EXTERNAL_STORAGE: {
                if ( grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
                    this.saveScreenCaptureToExternalStorage(ARActivity.this.screenCapture);
                } else {
                    Toast.makeText(this, "Please allow access to external storage, otherwise the screen capture can not be saved.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    protected void saveScreenCaptureToExternalStorage(Bitmap screenCapture) {
        if ( screenCapture != null ) {
            // store screenCapture into external cache directory
            final File screenCaptureFile = new File(Environment.getExternalStorageDirectory().toString(), "screenCapture_" + System.currentTimeMillis() + ".jpg");

            // 1. Save bitmap to file & compress to jpeg. You may use PNG too
            try {

                final FileOutputStream out = new FileOutputStream(screenCaptureFile);
                screenCapture.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();

                // 2. create send intent
                final Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("image/jpg");
                share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(screenCaptureFile));

                // 3. launch intent-chooser
                final String chooserTitle = "Share Snaphot";
                ARActivity.this.startActivity(Intent.createChooser(share, chooserTitle));

            } catch (final Exception e) {
                // should not occur when all permissions are set
                ARActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // show toast message in case something went wrong
                        Toast.makeText(ARActivity.this, "Unexpected error, " + e, Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }
}
