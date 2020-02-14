package com.example.alisha_androidlabassign;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class AddLocation extends AppCompatActivity implements OnMapReadyCallback{


    private static final String TAG = "MAPCHECK";
    GoogleMap mMap;
    Marker homeMarker;
    Marker destMarker;
    String address;
    public  int abc;

    Polyline line;

    private FusedLocationProviderClient mFusedLocationClient;
    LocationCallback locationCallback;
    LocationRequest locationRequest;
    private final int REQUEST_CODE = 1;

    public final int RADIUS = 1500;
    double latitude, longitude;
    double dest_lat, dest_lng;

    double dir_lat;
    double dir_lon;


    public static boolean directionRequested;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);


        Spinner spinner = findViewById(R.id.mapTypes_spinner);



        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                        break;
                    case 1:
                        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                        break;
                    case 2:
                        mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                        break;
                    case 3:
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        break;
                    case 4:
                        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                        break;
                    default:
                        break;

                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        initMap();
        getUserLocation();
        if (!checkPermissions())
            requestPermissions();
        else {
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        }


    }

            private boolean checkPermissions() {
                int permissionState = ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
                return permissionState == PackageManager.PERMISSION_GRANTED;
            }

            private void requestPermissions() {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            }


            private void initMap() {
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(AddLocation.this);
            }


            private void getUserLocation() {
                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(AddLocation.this);
                locationRequest = new LocationRequest();
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                locationRequest.setInterval(5000);
                locationRequest.setFastestInterval(3000);
                locationRequest.setSmallestDisplacement(100);
                setHomeMarker();
            }


            @Override
            public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
                if (requestCode == REQUEST_CODE) {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        setHomeMarker();
                        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

                    }
                }
            }

            private void setHomeMarker() {
                locationCallback = new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        for (Location location : locationResult.getLocations()) {
                            LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());


                            latitude = userLocation.latitude;
                            longitude = userLocation.longitude;
                            if (homeMarker != null)
                                homeMarker.remove();

                            CameraPosition cameraPosition = CameraPosition.builder()
                                    .target(new LatLng(userLocation.latitude, userLocation.longitude))
                                    .zoom(15)
                                    .bearing(0)
                                    .tilt(45)
                                    .build();
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                            homeMarker = mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location")
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));

                        }
                    }
                };

            }


            public void onClick(View view) {
                Object[] dataTransfer;
                String url;
                switch (view.getId()) {
                    case R.id.restaurant_btn:
                        // getUrl a method that we build
                        url = getUrl(latitude, longitude, "restaurant");
                        dataTransfer = new Object[2];
                        dataTransfer[0] = mMap;
                        dataTransfer[1] = url;

                        GetNearByPlaces getNearbyPlacesData = new GetNearByPlaces();
                        // execute asynchronously
                        getNearbyPlacesData.execute(dataTransfer);
                        Toast.makeText(AddLocation.this, "restaurants", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.cafe_btn:
                        // getUrl a method that we build
                        url = getUrl(latitude, longitude, "Cafe");
                        dataTransfer = new Object[2];
                        dataTransfer[0] = mMap;
                        dataTransfer[1] = url;

                        GetNearByPlaces getNearbyPlacesDataS = new GetNearByPlaces();
                        // execute asynchronously
                        getNearbyPlacesDataS.execute(dataTransfer);
                        Toast.makeText(AddLocation.this, "Cafe", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.museums_btn:
                        // getUrl a method that we build
                        url = getUrl(latitude, longitude, "museums");
                        dataTransfer = new Object[2];
                        dataTransfer[0] = mMap;
                        dataTransfer[1] = url;

                        GetNearByPlaces getNearbyPlacesDataM = new GetNearByPlaces();
                        // execute asynchronously
                        getNearbyPlacesDataM.execute(dataTransfer);
                        Toast.makeText(AddLocation.this, "Museums", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.clear:
                        mMap.clear();
                        break;

                    case R.id.distance_btn:
                    case R.id.direction_btn:
                        dataTransfer = new Object[4];
                        url = getDirectionUrl();
                        System.out.println(url);
                        dataTransfer[0] = mMap;
                        dataTransfer[1] = url;

                        dataTransfer[2] = new LatLng(dest_lat, dest_lng);
                        dataTransfer[3] = new LatLng(latitude, longitude);

                        GetDirections getDirectionsData = new GetDirections();
                        // execute asynchronously
                        getDirectionsData.execute(dataTransfer);
                        if (view.getId() == R.id.distance_btn)
                            directionRequested = false;
                        else
                            directionRequested = true;
                        break;

                    case R.id.Favourite_btn:
                        addLocation();
                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                }
            }

            private String getDirectionUrl() {
                StringBuilder googleDirectionUrl = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
                googleDirectionUrl.append("origin=" + latitude + "," + longitude);
                googleDirectionUrl.append("&destination=" + dest_lat + "," + dest_lng);
                googleDirectionUrl.append("&key=" + "AIzaSyAKK6fQ-6Lv2irP4EEndez3cBKzz5nKVL0");
                Log.d("", "getDirectionUrl: " + googleDirectionUrl);
                return googleDirectionUrl.toString();
            }

            private String getUrl(double latitude, double longitude, String nearbyPlace) {
                StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
                googlePlaceUrl.append("location=" + latitude + "," + longitude);
                googlePlaceUrl.append("&radius=" + RADIUS);
                googlePlaceUrl.append("&type=" + nearbyPlace);
                googlePlaceUrl.append("&key=" + "AIzaSyAKK6fQ-6Lv2irP4EEndez3cBKzz5nKVL0");
                Log.d("", "getUrl: " + googlePlaceUrl);
                return googlePlaceUrl.toString();

            }


            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);


                Intent intent = getIntent();
                int abc = intent.getIntExtra("location",-1);
                if (abc != -1)
                {
                    mMap.addMarker(new MarkerOptions().position(new LatLng(FavPlace_Address.selected_place.get(abc).getLatitude(),FavPlace_Address.selected_place.get(abc).getLongitude()))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
//                    destMarker.setTitle(address);
                }


                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(final Marker marker) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(AddLocation.this);
                        builder.setTitle("Are you sure");
                        builder.setPositiveButton("Destination", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dest_lat = marker.getPosition().latitude;
                                dest_lng = marker.getPosition().longitude;

                                  dir_lat = dest_lat;
                                  dir_lon = dest_lng;
                                  address = marker.getTitle();

                            }
                        });

                        builder.setNegativeButton("Source", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                latitude = marker.getPosition().latitude;
                                longitude = marker.getPosition().longitude;
                            }
                        });
                        builder.create().show();
                        return false;
                    }
                });


                mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(LatLng latLng) {

//                        if (destMarker != null) {
//                            destMarker.remove();
//                        }

                        MarkerOptions options = new MarkerOptions().position(latLng).snippet("Favourite Place")
                                .draggable(true);
//                        if (destMarker != null) {
//                            destMarker.remove();
//                        }
                        destMarker = mMap.addMarker(options);
                        dest_lat = destMarker.getPosition().latitude;
                        dest_lng = destMarker.getPosition().longitude;


                        // we will write code to fetch the address
                        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                        try {

                            List<Address> addresses = geocoder.getFromLocation(destMarker.getPosition().latitude, destMarker.getPosition().longitude, 1);

                            Address a = addresses.get(0);

                            address = a.getAddressLine(0);

                            destMarker.setTitle(address);
                            // Log.i(TAG,"onMap" + place_name);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    }
                });
    }



            private void addLocation() {

                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd  hh:mm:ss");
                String date = dateFormat.format(calendar.getTime());
                FavPlace_Address b = new FavPlace_Address(dest_lat,dest_lng,address,date);
                FavPlace_Address.selected_place.add(b);

                Toast.makeText(AddLocation.this, "Place added to favourite", Toast.LENGTH_SHORT).show();
            }


}
