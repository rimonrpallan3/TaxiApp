package metrotaxi.project.in.metrotaxi.activities;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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


public class DriverAlertActivity extends AppCompatActivity implements LocationListener, NavigationView.OnNavigationItemSelectedListener {
    TextView fromTextView;
    TextView toTextView;
    TextView userTextView;
    TextView phoneTextView;
    TextView cashTextView;
    TextView distanceTextView;
    TextView acceptTextView;
    TextView rejectTextView;
    LocationManager locationManager;
    double srcLat;
    double srcLong;
    double destLat;
    double destLong;
    double currentLocLat;
    double currentLocLong;
    double destLat1;
    double destLong1;
    Intent intent;
    ProgressDialog locationDialog;
    String paymentMode = "";
    String[] permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_alert);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        fromTextView = (TextView) findViewById(R.id.fromTextView);
        toTextView = (TextView) findViewById(R.id.toTextView);
        userTextView = (TextView) findViewById(R.id.userTextView);
        phoneTextView = (TextView) findViewById(R.id.phoneTextView);
        cashTextView = (TextView) findViewById(R.id.cashTextView);
        distanceTextView = (TextView) findViewById(R.id.distTextView);
        acceptTextView = (TextView) findViewById(R.id.acceptTextView);
        rejectTextView = (TextView) findViewById(R.id.rejectTextView);
        intent = getIntent();
        Log.e("intent: ", intent + "");
        if (intent != null) {
            final String id = intent.getStringExtra("id");
            if (id != null) {
                final String json = intent.getStringExtra("json");
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(json);
                    if (jsonObject != null) {
                        srcLat = Double.parseDouble(jsonObject.getString("src_lat"));
                        srcLong = Double.parseDouble(jsonObject.getString("src_long"));
                        destLat = Double.parseDouble(jsonObject.getString("dest_lat"));
                        destLong = Double.parseDouble(jsonObject.getString("dest_long"));
                        userTextView.setText("Customer: " + jsonObject.getString("user"));
                        fromTextView.setText("From: " + jsonObject.getString("from"));
                        toTextView.setText("To: " + jsonObject.getString("to_address"));
                        final  String phone = jsonObject.getString("phone");
                        phoneTextView.setText("Call: " + phone);
                        phoneTextView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                                startActivity(intent);
                            }
                        });
                        cashTextView.setText("Cash: " + jsonObject.getString("cash"));
                        distanceTextView.setText("Distance: " + jsonObject.getString("distance"));
                        fromTextView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                locationDialog = new ProgressDialog(DriverAlertActivity.this);
                                locationDialog.setTitle("Wait");
                                locationDialog.setMessage("Getting location");
                                locationDialog.show();
                                destLat1 = srcLat;
                                destLong1 = srcLong;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    if (ActivityCompat.checkSelfPermission(DriverAlertActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                            ActivityCompat.checkSelfPermission(DriverAlertActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                        requestPermissions(permissions, 1);
                                    } else {
                                        locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER, 0, 0, DriverAlertActivity.this);
                                        locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 0, 0, DriverAlertActivity.this);
                                    }
                                } else {
                                    locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER, 0, 0, DriverAlertActivity.this);
                                    locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 0, 0, DriverAlertActivity.this);
                                }
                            }
                        });

                        toTextView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                destLat1 = destLat;
                                destLong1 = destLong;
                                Uri uri = Uri.parse("http://maps.google.com/maps?"
                                        + "saddr=" + srcLat + "," + srcLong + "&daddr=" + destLat1 + "," + destLong1);
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                                startActivity(intent);
                            }
                        });
                        Retrofit retrofit = new RetrofitClient().getRetrofitClient();
                        final WebServices webServices = retrofit.create(WebServices.class);

                        acceptTextView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final ProgressDialog progressDialog = new ProgressDialog(DriverAlertActivity.this);
                                progressDialog.setTitle("wait");
                                progressDialog.show();
                                webServices.acceptRejectTrip(id, "accept").enqueue(new Callback<UserModel>() {
                                    @Override
                                    public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                                        Log.e("response accept: ", response.body().getResponse());
                                        Toast.makeText(DriverAlertActivity.this, response.body().getResponse(), Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                        if (response.body().getResponse().equals("success")) {
                                            SharedPreferences sharedPreferences = getSharedPreferences(AppConstants.SHARED_PREF, MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString("driver_trip", json);
                                            editor.apply();
                                        }
                                        finish();
                                    }

                                    @Override
                                    public void onFailure(Call<UserModel> call, Throwable t) {
                                        progressDialog.dismiss();
                                        t.printStackTrace();
                                        Toast.makeText(DriverAlertActivity.this, "error", Toast.LENGTH_SHORT).show();

                                    }
                                });
                            }
                        });

                        rejectTextView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final ProgressDialog progressDialog = new ProgressDialog(DriverAlertActivity.this);
                                progressDialog.setTitle("wait");
                                progressDialog.show();
                                webServices.acceptRejectTrip(id, "reject").enqueue(new Callback<UserModel>() {
                                    @Override
                                    public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                                        progressDialog.dismiss();
                                        Toast.makeText(DriverAlertActivity.this, response.body().getResponse(), Toast.LENGTH_SHORT).show();
                                        finish();
                                    }

                                    @Override
                                    public void onFailure(Call<UserModel> call, Throwable t) {
                                        progressDialog.dismiss();
                                        t.printStackTrace();
                                        Toast.makeText(DriverAlertActivity.this, "error", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                SharedPreferences sharedPreferences = getSharedPreferences(AppConstants.SHARED_PREF, MODE_PRIVATE);
                String driverTrip = sharedPreferences.getString("driver_trip", "");
                if (!driverTrip.equals("")) {
                    try {
                        JSONObject jsonObject = new JSONObject(driverTrip);
                        if (jsonObject != null) {
                            final String tripId = jsonObject.getString("id");
                            srcLat = Double.parseDouble(jsonObject.getString("src_lat"));
                            srcLong = Double.parseDouble(jsonObject.getString("src_long"));
                            destLat = Double.parseDouble(jsonObject.getString("dest_lat"));
                            destLong = Double.parseDouble(jsonObject.getString("dest_long"));
                            userTextView.setText("Customer: " + jsonObject.getString("user"));
                            fromTextView.setText("From: " + jsonObject.getString("from"));
                            toTextView.setText("To: " + jsonObject.getString("to_address"));
                            phoneTextView.setText("Call: " + jsonObject.getString("phone"));
                            final String phone = jsonObject.getString("phone");
                            phoneTextView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                                    startActivity(intent);
                                }
                            });
                            cashTextView.setText("Cash: " + jsonObject.getString("cash"));
                            distanceTextView.setText("Distance: " + jsonObject.getString("distance"));
                            fromTextView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    locationDialog = new ProgressDialog(DriverAlertActivity.this);
                                    locationDialog.setTitle("Wait");
                                    locationDialog.setMessage("Getting location");
                                    locationDialog.show();
                                    destLat1 = srcLat;
                                    destLong1 = srcLong;
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        if (ActivityCompat.checkSelfPermission(DriverAlertActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                                ActivityCompat.checkSelfPermission(DriverAlertActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                            requestPermissions(permissions, 1);
                                        } else {
                                            locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER, 0, 0, DriverAlertActivity.this);
                                            locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 0, 0, DriverAlertActivity.this);
                                        }
                                    } else {
                                        locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER, 0, 0, DriverAlertActivity.this);
                                        locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 0, 0, DriverAlertActivity.this);
                                    }
                                }
                            });

                            toTextView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    destLat1 = destLat;
                                    destLong1 = destLong;
                                    Uri uri = Uri.parse("http://maps.google.com/maps?"
                                            + "saddr=" + srcLat + "," + srcLong + "&daddr=" + destLat1 + "," + destLong1);
                                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                    intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                                    startActivity(intent);
                                }
                            });
                            String tripStatus = sharedPreferences.getString("trip_status", "");
                            if (tripStatus.equals("start")) {
                                acceptTextView.setVisibility(View.INVISIBLE);
                            }
                            acceptTextView.setText("Start");
                            rejectTextView.setText("Stop");
                            Retrofit retrofit = new RetrofitClient().getRetrofitClient();
                            final WebServices webServices = retrofit.create(WebServices.class);
                            acceptTextView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    final ProgressDialog progressDialog = new ProgressDialog(DriverAlertActivity.this);
                                    progressDialog.setTitle("wait");
                                    progressDialog.show();
                                    webServices.startStopTrip(tripId, "start", "").enqueue(new Callback<UserModel>() {
                                        @Override
                                        public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                                            Toast.makeText(DriverAlertActivity.this, response.body().getResponse(), Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                            if (response.body().getResponse().equals("success")) {
                                                Toast.makeText(DriverAlertActivity.this, response.body().getResponse(), Toast.LENGTH_SHORT).show();
                                                acceptTextView.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                                                acceptTextView.setVisibility(View.INVISIBLE);
                                                SharedPreferences sharedPreferences1 = getSharedPreferences(AppConstants.SHARED_PREF, MODE_PRIVATE);
                                                SharedPreferences.Editor editor = sharedPreferences1.edit();
                                                editor.putString("trip_status", "start");
                                                editor.apply();

                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<UserModel> call, Throwable t) {
                                            progressDialog.dismiss();
                                            t.printStackTrace();
                                            Toast.makeText(DriverAlertActivity.this, "error", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });

                            rejectTextView.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    final Dialog dialog = new Dialog(DriverAlertActivity.this);
                                    dialog.setContentView(R.layout.payment_mode_dialog);
                                    dialog.show();
                                    dialog.findViewById(R.id.onlineRadioButton).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            paymentMode = "online";
                                        }
                                    });
                                    dialog.findViewById(R.id.cashRadioButton).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            paymentMode = "cash";
                                        }
                                    });
                                    dialog.findViewById(R.id.submitButton).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            final ProgressDialog progressDialog = new ProgressDialog(DriverAlertActivity.this);
                                            progressDialog.setTitle("wait");
                                            progressDialog.show();
                                            webServices.startStopTrip(tripId, "stop", paymentMode).enqueue(new Callback<UserModel>() {
                                                @Override
                                                public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                                                    Toast.makeText(DriverAlertActivity.this, response.body().getResponse(), Toast.LENGTH_SHORT).show();
                                                    progressDialog.dismiss();
                                                    if (response.body().getResponse().equals("success")) {
                                                        acceptTextView.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                                                        SharedPreferences sharedPreferences = getSharedPreferences(AppConstants.SHARED_PREF, MODE_PRIVATE);
                                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                                        editor.putString("driver_trip", "");
                                                        editor.putString("trip_status", "");
                                                        editor.apply();
                                                        dialog.dismiss();
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<UserModel> call, Throwable t) {
                                                    t.printStackTrace();
                                                    progressDialog.dismiss();
                                                    Toast.makeText(DriverAlertActivity.this, "error", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    });

                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    acceptTextView.setVisibility(View.INVISIBLE);
                    rejectTextView.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER, 0, 0, DriverAlertActivity.this);
                locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 0, 0, DriverAlertActivity.this);
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            if (locationDialog != null) {
                locationDialog.dismiss();
            }
            currentLocLat = location.getLatitude();
            currentLocLong = location.getLongitude();
            Uri uri = Uri.parse("http://maps.google.com/maps?"
                    + "saddr=" + currentLocLat + "," + currentLocLong + "&daddr=" + destLat1 + "," + destLong1);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
            startActivity(intent);
            locationManager.removeUpdates(DriverAlertActivity.this);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_driver_trip_history) {
            Intent intent = new Intent(DriverAlertActivity.this, DriverTripHistoryActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_driver_logout) {
            SharedPreferences sharedPreferences = getSharedPreferences(AppConstants.SHARED_PREF, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(AppConstants.USER_NAME, "");
            editor.putString(AppConstants.USER_TYPE, "");
            editor.commit();
            Intent intent = new Intent(DriverAlertActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_driver_settings) {
            Intent intent = new Intent(DriverAlertActivity.this, DriverSettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_driver_current_trip) {

        }
        return true;
    }
}
