package metrotaxi.project.in.metrotaxi.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import metrotaxi.project.in.metrotaxi.R;
import metrotaxi.project.in.metrotaxi.adapters.TripHistoryAdapter;
import metrotaxi.project.in.metrotaxi.models.TripDetailsModel;
import metrotaxi.project.in.metrotaxi.utils.AppConstants;
import metrotaxi.project.in.metrotaxi.webservices.RetrofitClient;
import metrotaxi.project.in.metrotaxi.webservices.WebServices;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TripHistoryActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    TripHistoryAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_history_);
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
        final ProgressDialog progressDialog = new ProgressDialog(TripHistoryActivity.this);
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
                    TripHistoryAdapter adapter = new TripHistoryAdapter(tripDetailsModels);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<TripDetailsModel>> call, Throwable t) {
                progressDialog.dismiss();
                t.printStackTrace();
                Toast.makeText(TripHistoryActivity.this, "network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_request_status) {
            Intent intent = new Intent(TripHistoryActivity.this, UserRequestStatusActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_new_trip) {
            Intent intent = new Intent(TripHistoryActivity.this, TripActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_history) {
            Intent intent = new Intent(TripHistoryActivity.this, TripHistoryActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(TripHistoryActivity.this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            SharedPreferences sharedPreferences = getSharedPreferences(AppConstants.SHARED_PREF, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(AppConstants.USER_NAME, "");
            editor.putString(AppConstants.USER_TYPE, "");
            editor.apply();
            Intent intent = new Intent(TripHistoryActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_recharge) {
            Intent intent = new Intent(TripHistoryActivity.this, UserRechargeActivity.class);
            startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
