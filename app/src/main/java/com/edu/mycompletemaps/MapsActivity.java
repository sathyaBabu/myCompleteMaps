package com.edu.mycompletemaps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

/*
Place
  // https://developers.google.com/places/android-sdk/place-details
  Whats app

https://maps.googleapis.com/maps/api/place/findplacefromtext/json?input=Museum%20of%20Contemporary%20Art%20Australia&inputtype=textquery&fields=photos,formatted_address,name,rating,opening_hours,geometry&key=AIzaSyAS3J7-MNzokLKmAPjKRzuj4cGjjV-wkRw
 */
// FragmentActivity
public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private final static int PLACE_PICKER_REQUEST = 123;

    private GoogleMap mMap;


    private static final String TAG = "tag";




    private LocationManager locationManager;
    private LocationListener locationListener;

    //private GoogleApiClient mgoogleApiClient;
    private GoogleApiClient googleApiClient;

    //LocationRequest locationRequest;


    Geocoder geocoder;


    Marker marker;
    Circle circle;

    double travelSpeed = 0;


    LatLng finalAdddresssPos, addressPos;

    boolean setDestination = false;

    List<Address> address1;  // package..


    EditText editText, addresstext, finalAddressText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check for the PlayServices if it is ava.. on the emulator /cell continue else stop the app


        if( GooglePlayServicesAvailable()){
            setContentView(R.layout.activity_maps);
        } else {
            Toast.makeText(this, "Please install the Right Play Service Version..", Toast.LENGTH_SHORT).show();
        }


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if( mMap != null){


            mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(Marker marker) {

                }

                @Override
                public void onMarkerDrag(Marker marker) {

                    Geocoder geoCoder = new Geocoder(MapsActivity.this);
                    //  geoCoder = new Geocoder(MapsActivity.this);
                    LatLng latlng = marker.getPosition();
                    double lat = latlng.latitude;
                    double lng = latlng.longitude;

                    List<android.location.Address> list = null;

                    try {
                        list = geoCoder.getFromLocation(lat, lng, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    android.location.Address address = list.get(0);

                    marker.setTitle(address.getLocality());
                    marker.showInfoWindow();
                    Log.d("Stag", "onMarkerDragStart..." + marker.getPosition().latitude + "..." +
                            marker.getPosition().longitude);

                    Toast.makeText(MapsActivity.this, "Locality : " + address.getLocality(), Toast.LENGTH_SHORT).show();


                }
                @Override
                public void onMarkerDragEnd(Marker marker) {
                    // Refractor this function to MoveMarkerInfo add this function in onMarkerDragStart and try it...
                    Geocoder geoCoder = new Geocoder(MapsActivity.this);
                    //  geoCoder = new Geocoder(MapsActivity.this);
                    LatLng latlng = marker.getPosition();
                    double lat = latlng.latitude;
                    double lng = latlng.longitude;

                    List<android.location.Address> list = null;

                    try {
                        list = geoCoder.getFromLocation(lat, lng, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    android.location.Address address = list.get(0);

                    marker.setTitle(address.getLocality());
                    marker.showInfoWindow();

                }
            });



/////////////////////////////


            // impliment methods
            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {


                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {


                    View view = getLayoutInflater().inflate(R.layout.marker_info, null);

                    TextView viewLocality = (TextView) view.findViewById(R.id.textViewLocality);
                    TextView viewLat = (TextView) view.findViewById(R.id.textViewLat);
                    TextView viewLng = (TextView) view.findViewById(R.id.textViewLng);
                    TextView viewSnippet = (TextView) view.findViewById(R.id.textViewSnippet);

                    LatLng latLng = marker.getPosition();

                    viewLocality.setText(marker.getTitle());
                    viewLat.setText("Latitude : " + latLng.latitude);
                    viewLng.setText("Longitude : " + latLng.longitude);
                    viewSnippet.setText(marker.getSnippet());

                    return view;
                }
            });

        }
    }

    private boolean GooglePlayServicesAvailable() {


///////////   Lets check for the play service installed...



            GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
            int isAvailable = googleApiAvailability.isGooglePlayServicesAvailable(this);

            if (isAvailable == ConnectionResult.SUCCESS) {
                return true;
            } else if (googleApiAvailability.isUserResolvableError(isAvailable)) {
                Dialog dialog = googleApiAvailability.getErrorDialog(this, isAvailable, 0);
                dialog.show();
            } else {
                Toast.makeText(this, "Oops!! cant load the play services...", Toast.LENGTH_SHORT).show();
            }

            return false;



    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        // 12.9778791,77.5904463
        LatLng myLocation = new LatLng(12.9778791, 77.5904463);
        mMap.addMarker(new MarkerOptions().position(myLocation).title("Marker in Bangalore"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }


        mMap.getUiSettings().setZoomControlsEnabled(true);  // +/-
        mMap.setMyLocationEnabled(true);

        runTime_Permissions();  // Location manager..
    }

    public void getDirections(View view) {


//        // Mysore 12.3106368,76.5656492
//        // Ooty 11.4118505,76.658402
//                String uri = "http://maps.google.com/maps?f=d&hl=en&sMysore="+12.3106368+","+76.5656492
//                        +"&dOoty="+11.4118505+","+76.658402;
//                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
//                startActivity(Intent.createChooser(intent, "Select an application"));

       addresstext       = (EditText) findViewById(R.id.addressEditText);
       finalAddressText  = (EditText) findViewById(R.id.finalAddressEditText);

        showMapFromLocation(addresstext.getText().toString(), finalAddressText.getText().toString());
    }

    ////////////////////////////
    private void showMapFromLocation(String src, String dest) {

        double srcLat = 0, srcLng = 0, destLat = 0, destLng = 0;

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            if (true) {   //mMaps
                List<Address> srcAddresses = geocoder.getFromLocationName(src,
                        1);
                if (srcAddresses.size() > 0) {
                    Address location = srcAddresses.get(0);
                    srcLat = location.getLatitude();
                    srcLng = location.getLongitude();
                }
                List<Address> destAddresses = geocoder.getFromLocationName(
                        dest, 1);
                if (destAddresses.size() > 0) {
                    Address location = destAddresses.get(0);
                    destLat = location.getLatitude();
                    destLng = location.getLongitude();
                }
                String desLocation = "&daddr=" + Double.toString(destLat) + ","
                        + Double.toString(destLng);
                String currLocation = "saddr=" + Double.toString(srcLat) + ","
                        + Double.toString(srcLng);
                // "d" means driving car, "w" means walking "r" means by bus
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?" + currLocation
                                + desLocation + "&dirflg=d"));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        & Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                intent.setClassName("com.google.android.apps.maps",
                        "com.google.android.maps.MapsActivity");
                startActivity(intent);

                String whatsAppMessage = "http://maps.google.com/maps?" + currLocation + desLocation + "&dirflg=d";

                // PHASE II Sending My travel src , dest location to whats ap
                //
                //
               // sendWhatsAppMessage(whatsAppMessage);
                // Send the data to server...( x, y )
                // Any reg user can reterive the data from server....

                //////  Send a message or location to  A PARTICULAR CONTACT FROM UR CONTACTS AS FOLLOWS

//                                        try {
//
//
//                                            Intent i = new Intent(Intent.ACTION_VIEW);
//
//                                            String url = "https://api.whatsapp.com/send?phone="+ "+919448384716" +"&text=" +
//                                                    URLEncoder.encode("My MSG for u", "UTF-8");
//                                            i.setPackage("com.whatsapp");
//                                            i.setData(Uri.parse(url));
//
//                                                startActivity(i);
//
//                                        } catch (Exception e){
//                                            e.printStackTrace();
//                                        }

                ////////


            }


        } catch (IOException e) {
            Log.e(TAG, "Error when showing google map directions, E: " + e);
        } catch (Exception e) {
            Log.e(TAG, "Error when showing google map directions, E: " + e);
        }
    }

    // Whats app program receiving the src and dest to display the travelling point...
    public void sendWhatsAppMessage(String whatsAppMessage) {
        // String whatsAppMessage = "http://maps.google.com/maps?saddr=" + latitude + "," + longitude;
        Intent sendIntent = new Intent();

//        Uri uri = Uri.parse("smsto:" + "1234567890");
//        Intent i = new Intent(Intent.ACTION_SENDTO, uri);

        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, whatsAppMessage);
        sendIntent.setType("text/plain");
        sendIntent.setPackage("com.whatsapp");
        startActivity(sendIntent);

        /*
        Uri uri = Uri.parse("smsto:" + number);
    Intent i = new Intent(Intent.ACTION_SENDTO, uri);
    i.setPackage("com.whatsapp");
    startActivity(Intent.createChooser(i, ""));
         */
    }

    public void LocationToReach(View view) {

        editText = (EditText) findViewById(R.id.locationToReach);
        String location = editText.getText().toString();

        Geocoder geocoder = new Geocoder(this);
        List<Address> list = null;


        try {
            list = geocoder.getFromLocationName(location, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Address address = list.get(0);

        String locality = address.getLocality();
        Toast.makeText(this, "Locality : " + locality, Toast.LENGTH_SHORT).show();

        double lat = address.getLatitude();
        double lng = address.getLongitude();
       //// address.getPhone(); // null


        goToLocationZoom(lat, lng, 16);


        setMarker(locality, lat, lng);


    }

    private void setMarker(String locality, double lat, double lng) {

        if (marker != null) {

            marker.remove();
            marker = null;
        }

        MarkerOptions markerOptions = new MarkerOptions()
                .title(locality)
                .position(new LatLng(lat, lng))
                .snippet("My favourite place")
                .draggable(true)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.giftrap));

        marker = mMap.addMarker(markerOptions); // Display the marker of that location

        circle = DrawCircle(new LatLng(lat, lng));

        mMap.setOnCircleClickListener(new GoogleMap.OnCircleClickListener() {
            @Override
            public void onCircleClick(Circle circle) {

                // Get my places.
                //
                int strokeColor = circle.getStrokeColor() ^ 0x00ffffff;
                circle.setStrokeColor(strokeColor);

            }
        });


    }

    private Circle DrawCircle(LatLng latLng) {
        CircleOptions circleOptions = new CircleOptions()
                .center(latLng)
                .radius(100)
                .strokeWidth(7)
                .strokeColor(Color.GREEN)
                .fillColor(Color.argb(25, 255, 255, 255))
                .clickable(true);

        return mMap.addCircle(circleOptions);

    }

    private void goToLocationZoom(double lat, double lng, double zoom) {
        LatLng latLng = new LatLng(lat, lng);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, (float) zoom);
        mMap.moveCamera(cameraUpdate);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        switch (item.getItemId()) {


            case R.id.mapTypeNone:
                mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                break;

            case R.id.mapTypeNormal:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                break;
            case R.id.mapTypeSatellite:

                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

                break;

            case R.id.mapTypeybrid:

                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

                break;

            case R.id.mapTypeTerrain:

                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

                break;

            case R.id.locationManagerStart:

                EnableLocationManager();


                break;

            case R.id.locationManagerStop:

                // Mysore 12.3106368,76.5656492
                // Ooty 11.4118505,76.658402
//                String uri = "http://maps.google.com/maps?f=d&hl=en&sMysore="+12.3106368+","+76.5656492
//                        +"&dOoty="+11.4118505+","+76.658402;
//                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
//                startActivity(Intent.createChooser(intent, "Select an application"));


               DestroyLocationManager();
//                 Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("https://maps.googleapis.com/maps/api/place/textsearch/json?query=123+main+street&location=42.3675294,-71.186966&radius=10000"));
//
//                 startActivity(intent);

                break;

            case R.id.findPlace:


                PlacePicker();

                break;


            case R.id.listPlace :
                // Search for restaurants in San Francisco
                Uri gmmIntentUri = Uri.parse("geo:12.9778791, 77.5904463?q=restaurants");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
                break;

        }


        return super.onOptionsItemSelected(item);
    }

    private void PlacePicker() {
        // https://developers.google.com/places/android-sdk/place-details

        // https://maps.googleapis.com/maps/api/place/findplacefromtext/json?input=Museum%20of%20Contemporary%20Art%20Australia&inputtype=textquery&fields=photos,formatted_address,name,rating,opening_hours,geometry&key=AIzaSyAS3J7-MNzokLKmAPjKRzuj4cGjjV-wkRw

        // https://maps.googleapis.com/maps/api/place/textsearch/json?query=123+main+street&location=42.3675294,-71.186966&radius=10000&key=AIzaSyAS3J7-MNzokLKmAPjKRzuj4cGjjV-wkRw
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            // for activty
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
            // for fragment
            //startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PLACE_PICKER_REQUEST:
                    Place place = PlacePicker.getPlace(this, data);
                    // Place Info Place: Garuda Mall Ph +91 80 4069 8857   080 4069 8857 Web http://www.garudamall.in/
                    String placeName = String.format("Place: %s", place.getName()+" Ph "+place.getPhoneNumber()+"  Web "+place.getWebsiteUri());
                    Log.d("tag"," Place Info "+placeName);
                    double latitude = place.getLatLng().latitude;
                    double longitude = place.getLatLng().longitude;

                    LatLng coordinate = new LatLng(latitude, longitude);
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(coordinate);
                    markerOptions.title(placeName); //Here Total Address is address which you want to show on marker
                    mMap.clear();  ////
                    markerOptions.icon(
                            BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

                    markerOptions.getPosition();
                  Marker  mCurrLocationMarker = mMap.addMarker(markerOptions);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(coordinate));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

            }
        }
    }

    private boolean runTime_Permissions() {

        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, 100);

            return true;

        }
        return false;


    }

    private void DestroyLocationManager() {

        if( locationManager != null){
            locationManager.removeUpdates(locationListener);
        }

    }

    private void EnableLocationManager() {


        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {



                //calculating the speed with getSpeed method it returns speed in m/s so we are converting it into kmph

                travelSpeed = location.getSpeed() * 3.6;  //  or // 18 / 5;
                editText.setText(" Travel Speed : "+new DecimalFormat("#.##").format(travelSpeed) + " km/hr");


                LatLng myLocation = new LatLng(location.getLatitude(),location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);


            }
        };

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, locationListener);


    }
}
/*
Circle circle = mGoogleMap.addCircle(new CircleOptions().center(new LatLng(latitude, longitude)).radius(getRadiusInMeters()).strokeColor(Color.RED));
        circle.setVisible(true);
        getZoomLevel(circle);
////////////////

public int getZoomLevel(Circle circle) {
if (circle != null){
    double radius = circle.getRadius();
    double scale = radius / 500;
    zoomLevel =(int) (16 - Math.log(scale) / Math.log(2));
}
return zoomLevel;
}
 */