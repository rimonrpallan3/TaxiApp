package metrotaxi.project.in.metrotaxi.activities;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import metrotaxi.project.in.metrotaxi.R;
import metrotaxi.project.in.metrotaxi.models.AutoChargeModel;
import metrotaxi.project.in.metrotaxi.models.GetPaths;
import metrotaxi.project.in.metrotaxi.models.Leg;
import metrotaxi.project.in.metrotaxi.models.RechargeModel;
import metrotaxi.project.in.metrotaxi.models.Route;
import metrotaxi.project.in.metrotaxi.models.Step;
import metrotaxi.project.in.metrotaxi.models.TripDetailsModel;
import metrotaxi.project.in.metrotaxi.models.UserModel;
import metrotaxi.project.in.metrotaxi.utils.AppConstants;
import metrotaxi.project.in.metrotaxi.webservices.RetrofitClient;
import metrotaxi.project.in.metrotaxi.webservices.WebServices;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TripActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener, NavigationView.OnNavigationItemSelectedListener {
    TextView cashTextView;
    private GoogleMap mMap;
    EditText sourceEditText;
    EditText destEditText;
    LocationManager locationManager;
    String provider;
    int cashPerKM;
    int cashMinimum;
    int cash = 0;
    double distance = 0.0;
    double sourceLat = 0.0;
    double sourceLong = 0.0;
    double destLat = 0.0;
    double destLong = 0.0;
    PolylineOptions polylineOptions;
    String username;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        cashTextView = (TextView) findViewById(R.id.cashTextView);
        sharedPreferences = getSharedPreferences(AppConstants.SHARED_PREF, MODE_PRIVATE);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        provider = String.valueOf(locationManager.getBestProvider(criteria, true));
        username = sharedPreferences.getString(AppConstants.USER_NAME, "");
        Retrofit retrofit = RetrofitClient.getRetrofitClient();
        WebServices webServices = retrofit.create(WebServices.class);
        final ProgressDialog progressDialog = new ProgressDialog(TripActivity.this);
        progressDialog.setTitle("wait");
        progressDialog.show();
        webServices.updateFee().enqueue(new Callback<AutoChargeModel>() {
            @Override
            public void onResponse(Call<AutoChargeModel> call, Response<AutoChargeModel> response) {
                progressDialog.dismiss();
                AutoChargeModel model = response.body();
                if (model != null) {
                    cashPerKM = model.getAfterMinimum();
                    cashMinimum = model.getMinimumCharge();
                    Log.e(cashPerKM + ": ", "" + cashMinimum);
                }
            }

            @Override
            public void onFailure(Call<AutoChargeModel> call, Throwable t) {
                progressDialog.dismiss();
                t.printStackTrace();
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
            } else {
                locationManager.requestLocationUpdates(provider, 0, 0, this);
            }
        } else {
            locationManager.requestLocationUpdates(provider, 0, 0, this);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        sourceEditText = ((EditText) autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_input));
        sourceEditText.setHint("Source");
        sourceEditText.setTextSize(15f);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                final LatLng latLng = place.getLatLng();
                sourceLat = latLng.latitude;
                sourceLong = latLng.longitude;
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
                mMap.animateCamera(cameraUpdate);
                drawPath();
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.

            }
        });
        PlaceAutocompleteFragment destFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.dest_autocomplete_fragment);
        destEditText = (EditText) destFragment.getView().findViewById(R.id.place_autocomplete_search_input);
        destEditText.setHint("Destination");
        destEditText.setTextSize(15f);
        destFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                LatLng latLng = place.getLatLng();
                destLat = latLng.latitude;
                destLong = latLng.longitude;
                drawPath();
            }

            @Override
            public void onError(Status status) {

            }
        });
        findViewById(R.id.requestTaxiButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Retrofit retrofit = new RetrofitClient().getRetrofitClient();
                WebServices webServices = retrofit.create(WebServices.class);
                TripDetailsModel tripDetailsModel = new TripDetailsModel();
                tripDetailsModel.setUser(username);
                tripDetailsModel.setDistance(distance);
                tripDetailsModel.setSrcLat(sourceLat);
                tripDetailsModel.setSrcLong(sourceLong);
                tripDetailsModel.setDestLat(destLat);
                tripDetailsModel.setDestLong(destLong);
                tripDetailsModel.setCash(cash);
                tripDetailsModel.setFrom(sourceEditText.getText().toString());
                tripDetailsModel.setTo(destEditText.getText().toString());
                Call<TripDetailsModel> call = webServices.userRequestTrip(tripDetailsModel);
                final ProgressDialog progressDialog = new ProgressDialog(TripActivity.this);
                progressDialog.setTitle("wait");
                progressDialog.show();
                call.enqueue(new Callback<TripDetailsModel>() {
                    @Override
                    public void onResponse(Call<TripDetailsModel> call, Response<TripDetailsModel> response) {
                        progressDialog.dismiss();
                        TripDetailsModel model = response.body();
                        Toast.makeText(TripActivity.this, model.getStatus(), Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onFailure(Call<TripDetailsModel> call, Throwable t) {
                        progressDialog.dismiss();
                        t.printStackTrace();
                        Toast.makeText(TripActivity.this, "Network error", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        this, R.raw.map_style));
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(final LatLng latLng) {
                final Dialog dialog = new Dialog(TripActivity.this);
                dialog.setContentView(R.layout.source_dest_dialog);
                dialog.show();
                dialog.findViewById(R.id.setSourceButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sourceLat = latLng.latitude;
                        sourceLong = latLng.longitude;
                        Geocoder geocoder = new Geocoder(TripActivity.this, Locale.getDefault());
                        try {
                            String place = "";
                            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                            for (Address address : addresses) {
                                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                                    if (place.length() > 0) {
                                        place = place + ",";
                                    }
                                    place = place + address.getAddressLine(i);
                                }
                                sourceEditText.setText(place);
                                drawPath();
                                dialog.dismiss();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                dialog.findViewById(R.id.setDestButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        destLat = latLng.latitude;
                        destLong = latLng.longitude;
                        Geocoder geocoder = new Geocoder(TripActivity.this, Locale.getDefault());
                        try {
                            String place = "";
                            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                            for (Address address : addresses) {
                                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                                    if (place.length() > 0) {
                                        place = place + ",";
                                    }
                                    place = place + address.getAddressLine(i);
                                }
                                destEditText.setText(place);
                                dialog.dismiss();
                                drawPath();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                mMap.setMyLocationEnabled(true);
            }
        } else {
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (mMap != null) {
                    mMap.setMyLocationEnabled(true);
                }
                locationManager.requestLocationUpdates(provider, 0, 0, TripActivity.this);
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (mMap != null) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
            mMap.animateCamera(cameraUpdate);
            locationManager.removeUpdates(TripActivity.this);
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }

    private void drawPath() {
        if (destLat > 0.0 && destLong > 0.0 && sourceLat > 0.0 && sourceLong > 0.0) {
            if (mMap != null) {
                mMap.clear();
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(sourceLat, sourceLong))
                        .title("From"))
                        .setIcon(BitmapDescriptorFactory.fromResource(R.drawable.src_location));
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(destLat, destLong))
                        .title("To"))
                        .setIcon(BitmapDescriptorFactory.fromResource(R.drawable.dest_location));
                Retrofit retrofit = new RetrofitClient().getRetrofitClientPath();
                WebServices webServices = retrofit.create(WebServices.class);
                Call<GetPaths> call = webServices.getPaths(sourceLat + "," + sourceLong, destLat + "," + destLong, "false");

                call.enqueue(new Callback<GetPaths>() {
                    @Override
                    public void onResponse(Call<GetPaths> call, Response<GetPaths> response) {
                        GetPaths getPaths = response.body();
                        List<List<HashMap<String, String>>> route = new ArrayList<List<HashMap<String, String>>>();
                        List<Route> routes = getPaths.getRoutes();
                        for (int i = 0; i < routes.size(); i++) {
                            List<HashMap<String, String>> path = new ArrayList<HashMap<String, String>>();
                            List<Leg> legs = routes.get(i).getLegs();
                            for (int j = 0; j < legs.size(); j++) {
                                List<Step> steps = legs.get(j).getSteps();
                                for (int k = 0; k < steps.size(); k++) {
                                    String polyline = steps.get(k).getPolyline().getPoints();
                                    List<LatLng> latLngs = decodePoly(polyline);
                                    for (int l = 0; l < latLngs.size(); l++) {
                                        HashMap<String, String> hm = new HashMap<String, String>();
                                        hm.put("lat",
                                                Double.toString(((LatLng) latLngs.get(l)).latitude));
                                        hm.put("lng",
                                                Double.toString(((LatLng) latLngs.get(l)).longitude));
                                        path.add(hm);
                                    }
                                }
                            }
                            route.add(path);
                        }
                        ArrayList<LatLng> points = null;
                        PolylineOptions polyLineOptions = null;

                        // traversing through routes
                        double dist = 0;
                        Location srcLoc = new Location("");
                        Location destLoc = new Location("");
                        for (int i = 0; i < routes.size(); i++) {
                            points = new ArrayList<LatLng>();
                            polyLineOptions = new PolylineOptions();
                            List<HashMap<String, String>> path = route.get(i);
                            for (int j = 0; j < path.size(); j++) {
                                HashMap<String, String> point = path.get(j);
                                double lat = Double.parseDouble(point.get("lat"));
                                double lng = Double.parseDouble(point.get("lng"));
                                if (destLoc.getLatitude() > 0) {
                                    destLoc.setLatitude(srcLoc.getLatitude());
                                    destLoc.setLongitude(srcLoc.getLongitude());
                                } else {
                                    destLoc.setLatitude(lat);
                                    destLoc.setLongitude(lng);
                                }
                                srcLoc.setLatitude(lat);
                                srcLoc.setLongitude(lng);
                                dist = dist + destLoc.distanceTo(srcLoc);
                                LatLng position = new LatLng(lat, lng);
                                points.add(position);
                            }
                            polyLineOptions.addAll(points);
                            polyLineOptions.width(10);
                            polyLineOptions.color(Color.DKGRAY);
                        }
                        if (polyLineOptions != null) {
                            mMap.addPolyline(polyLineOptions);
                            distance = Math.floor((dist / 1000));
                            Log.e("distance: ", distance + "");
                            if (distance <= 1) {
                                cash = cashMinimum;
                            } else {
                                cash = cashMinimum + (int) Math.floor((distance - 1) * cashPerKM);
                            }
                            //cash = (int) Math.floor((dist/1000) * cashPerKM);
                            cashTextView.setText("CASH: " + cash);
                        } else {
                            Toast.makeText(TripActivity.this, "Could not find path", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<GetPaths> call, Throwable t) {
                        Toast.makeText(TripActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_request_status) {
            Intent intent = new Intent(TripActivity.this, UserRequestStatusActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_new_trip) {
            Intent intent = new Intent(TripActivity.this, TripActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_history) {
            Intent intent = new Intent(TripActivity.this, TripHistoryActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(TripActivity.this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            SharedPreferences sharedPreferences = getSharedPreferences(AppConstants.SHARED_PREF, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(AppConstants.USER_NAME, "");
            editor.putString(AppConstants.USER_TYPE, "");
            editor.apply();
            Intent intent = new Intent(TripActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_recharge) {
            Intent intent = new Intent(TripActivity.this, UserRechargeActivity.class);
            startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
