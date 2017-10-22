package metrotaxi.project.in.metrotaxi.activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import metrotaxi.project.in.metrotaxi.R;
import metrotaxi.project.in.metrotaxi.models.UserModel;
import metrotaxi.project.in.metrotaxi.utils.AppConstants;
import metrotaxi.project.in.metrotaxi.webservices.RetrofitClient;
import metrotaxi.project.in.metrotaxi.webservices.WebServices;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UserRequestStatusActivity extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {
    String driverUserName;
    SupportMapFragment mapFragment;
    Handler handler;
    RatingBar ratingBar;
    GoogleMap mMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_request_status);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.driverLocationMap);
        mapFragment.getMapAsync(this);
        TextView driverTextView = (TextView) findViewById(R.id.driverTextView);
        TextView vehicleNumberTextView = (TextView) findViewById(R.id.vehicleNumberTextView);
        TextView contactTextView = (TextView) findViewById(R.id.contactTextView);
        ImageView imageView = (ImageView) findViewById(R.id.driverImageView);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        SharedPreferences sharedPreferences = getSharedPreferences(AppConstants.SHARED_PREF, MODE_PRIVATE);
        String json = sharedPreferences.getString("trip_user", "");
        if (!json.equals("")) {
            JSONObject jobj = null;
            try {
                jobj = new JSONObject(json);
                if (jobj != null) {
                    driverTextView.setText("Driver: " + jobj.getString("driver"));
                    vehicleNumberTextView.setText("Vehicle No: " + jobj.getString("vehicle_number"));
                    final String phone = jobj.getString("phone");
                    contactTextView.setText("Call: " + phone);
                    driverUserName = jobj.getString("username");
                    String ratingString = jobj.getString("rating");
                    if (ratingString != null && !ratingString.equals("null")) {
                        float rating = Float.parseFloat(ratingString);
                        ratingBar.setRating(rating);
                    }
                    Picasso.with(UserRequestStatusActivity.this)
                            .load(AppConstants.URL + jobj.getString("image_url"))
                            .placeholder(R.drawable.profile_pic)
                            .error(R.drawable.profile_pic)
                            .into(imageView);
                    contactTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                            startActivity(intent);
                        }
                    });
                        handler = new Handler();
                        Retrofit retrofit = RetrofitClient.getRetrofitClient();
                        final WebServices webServices = retrofit.create(WebServices.class);
                        final Runnable r = new Runnable() {
                            public void run() {
                                webServices.getDriverLocation(driverUserName).enqueue(new Callback<UserModel>() {
                                    @Override
                                    public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                                        UserModel model1 = response.body();
                                        if (model1 != null) {
                                            double lat = model1.getLat();
                                            double longi = model1.getLongi();
                                            if (mMap != null) {
                                                mMap.clear();
                                                MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(lat, longi)).title("Driver location")
                                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                                mMap.addMarker(markerOptions);
                                                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(lat, longi), 15);
                                                mMap.animateCamera(cameraUpdate);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<UserModel> call, Throwable t) {
                                        t.printStackTrace();
                                        Toast.makeText(UserRequestStatusActivity.this, "Error in map", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                handler.postDelayed(this, 1000);
                            }
                        };

                        handler.postDelayed(r, 1000);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        handler.removeCallbacksAndMessages(null);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_request_status) {
            Intent intent = new Intent(UserRequestStatusActivity.this, UserRequestStatusActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_new_trip) {
            Intent intent = new Intent(UserRequestStatusActivity.this, TripActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_history) {
            Intent intent = new Intent(UserRequestStatusActivity.this, TripHistoryActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(UserRequestStatusActivity.this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            SharedPreferences sharedPreferences = getSharedPreferences(AppConstants.SHARED_PREF, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(AppConstants.USER_NAME, "");
            editor.putString(AppConstants.USER_TYPE, "");
            editor.apply();
            Intent intent = new Intent(UserRequestStatusActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_recharge) {
            Intent intent = new Intent(UserRequestStatusActivity.this, UserRechargeActivity.class);
            startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
