package metrotaxi.project.in.metrotaxi.activities;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import metrotaxi.project.in.metrotaxi.R;
import metrotaxi.project.in.metrotaxi.adapters.DriverTripHistoryAdapter;
import metrotaxi.project.in.metrotaxi.models.TripDetailsModel;
import metrotaxi.project.in.metrotaxi.services.LocationService;
import metrotaxi.project.in.metrotaxi.utils.AppConstants;
import metrotaxi.project.in.metrotaxi.webservices.RetrofitClient;
import metrotaxi.project.in.metrotaxi.webservices.WebServices;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DriverTripHistoryActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_trip_history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        SharedPreferences sharedPreferences = getSharedPreferences(AppConstants.SHARED_PREF, MODE_PRIVATE);
        String username = sharedPreferences.getString(AppConstants.USER_NAME, "");
        Retrofit retrofit = new RetrofitClient().getRetrofitClient();
        WebServices webServices = retrofit.create(WebServices.class);
        Call<List<TripDetailsModel>> call = webServices.getUserHistory(username);
        final ProgressDialog progressDialog = new ProgressDialog(DriverTripHistoryActivity.this);
        progressDialog.setTitle("wait");
        progressDialog.show();
        call.enqueue(new Callback<List<TripDetailsModel>>() {
            @Override
            public void onResponse(Call<List<TripDetailsModel>> call, Response<List<TripDetailsModel>> response) {
                progressDialog.dismiss();
                List<TripDetailsModel> tripDetailsModels = response.body();
                if (tripDetailsModels != null) {
                    RecyclerView recyclerView = (RecyclerView) findViewById(R.id.historyRecyclerView);
                    RecyclerView.LayoutManager manager = new LinearLayoutManager(getApplicationContext());
                    recyclerView.setLayoutManager(manager);
                    DriverTripHistoryAdapter adapter = new DriverTripHistoryAdapter(tripDetailsModels);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<TripDetailsModel>> call, Throwable t) {
                progressDialog.dismiss();
                t.printStackTrace();
                Toast.makeText(DriverTripHistoryActivity.this, "network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.driver_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_start_location) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                int coarseLocationStatus = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
                int fineLocationStatus = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
                if (coarseLocationStatus != PackageManager.PERMISSION_GRANTED || coarseLocationStatus != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                } else {
                    Intent intent = new Intent(getApplicationContext(), LocationService.class);
                    startService(intent);
                }

            } else {
                Intent intent = new Intent(getApplicationContext(), LocationService.class);
                startService(intent);
            }
        } else {
            Intent intent = new Intent(getApplicationContext(), LocationService.class);
            stopService(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults != null && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(getApplicationContext(), LocationService.class);
                startService(intent);
            }
        }
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_driver_trip_history) {
            Intent intent = new Intent(DriverTripHistoryActivity.this, DriverTripHistoryActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_driver_logout) {
            SharedPreferences sharedPreferences = getSharedPreferences(AppConstants.SHARED_PREF, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(AppConstants.USER_NAME, "");
            editor.putString(AppConstants.USER_TYPE, "");
            editor.commit();
            Intent intent = new Intent(DriverTripHistoryActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_driver_settings) {
            Intent intent = new Intent(DriverTripHistoryActivity.this, DriverSettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_driver_current_trip) {
            Intent intent = new Intent(DriverTripHistoryActivity.this, DriverAlertActivity.class);
            startActivity(intent);
        }
            return true;
    }
}
