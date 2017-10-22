package metrotaxi.project.in.metrotaxi.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import metrotaxi.project.in.metrotaxi.R;
import metrotaxi.project.in.metrotaxi.models.UserModel;
import metrotaxi.project.in.metrotaxi.utils.AppConstants;
import metrotaxi.project.in.metrotaxi.webservices.RetrofitClient;
import metrotaxi.project.in.metrotaxi.webservices.WebServices;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    EditText nameEditText;
    EditText emailEditText;
    EditText phoneNumberEditText;
    EditText usernameEditText;
    EditText passwordEditText;
    EditText addressEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        nameEditText = (EditText) findViewById(R.id.nameEditText);
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        phoneNumberEditText = (EditText) findViewById(R.id.phoneEditText);
        usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        addressEditText = (EditText) findViewById(R.id.addressEditText);
        findViewById(R.id.updateInfoButton).setOnClickListener(this);
        updateFields();
    }

    private void updateFields() {
        Retrofit retrofit = RetrofitClient.getRetrofitClient();
        WebServices webServices = retrofit.create(WebServices.class);
        SharedPreferences sharedPreferences = getSharedPreferences(AppConstants.SHARED_PREF, MODE_PRIVATE);
        String userName = sharedPreferences.getString(AppConstants.USER_NAME, "");
        Call<UserModel> call = webServices.getUserInfo(userName);
        final ProgressDialog progressDialog = new ProgressDialog(SettingsActivity.this);
        progressDialog.setTitle("wait");
        progressDialog.show();
        call.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                progressDialog.dismiss();
                UserModel userModel = response.body();
                if (userModel != null) {
                    nameEditText.setText(userModel.getName());
                    usernameEditText.setText(userModel.getUsername());
                    passwordEditText.setText(userModel.getPassword());
                    emailEditText.setText(userModel.getEmail());
                    phoneNumberEditText.setText(userModel.getPhoneNumber());
                    addressEditText.setText(userModel.getAddress());
                }
            }

            @Override
            public void onFailure(Call<UserModel> call, Throwable t) {
                progressDialog.dismiss();
                t.printStackTrace();
                Toast.makeText(SettingsActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        Retrofit retrofit = new RetrofitClient().getRetrofitClient();
        WebServices webServices = retrofit.create(WebServices.class);
        UserModel userModel = new UserModel();
        userModel.setUsername(usernameEditText.getText().toString());
        userModel.setPassword(passwordEditText.getText().toString());
        userModel.setName(nameEditText.getText().toString());
        userModel.setEmail(emailEditText.getText().toString());
        userModel.setPhoneNumber(phoneNumberEditText.getText().toString());
        userModel.setAddress(addressEditText.getText().toString());
        Call<UserModel> call = webServices.updateProfile(userModel);
        final ProgressDialog progressDialog = new ProgressDialog(SettingsActivity.this);
        progressDialog.setTitle("wait");
        progressDialog.show();
        call.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                String res = response.body().getResponse();
                progressDialog.dismiss();
                if (res.trim().equals("success")) {
                    Toast.makeText(SettingsActivity.this, "Successfully updated", Toast.LENGTH_SHORT).show();
                    updateFields();
                } else {
                    Toast.makeText(SettingsActivity.this, res, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserModel> call, Throwable t) {
                progressDialog.dismiss();
                t.printStackTrace();
                Toast.makeText(SettingsActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_request_status) {
            Intent intent = new Intent(SettingsActivity.this, UserRequestStatusActivity.class);
            startActivity(intent);
        }  else if (id == R.id.nav_new_trip) {
            Intent intent = new Intent(SettingsActivity.this, TripActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_history) {
            Intent intent = new Intent(SettingsActivity.this, TripHistoryActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(SettingsActivity.this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            SharedPreferences sharedPreferences = getSharedPreferences(AppConstants.SHARED_PREF, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(AppConstants.USER_NAME, "");
            editor.putString(AppConstants.USER_TYPE, "");
            editor.apply();
            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_recharge) {
            Intent intent = new Intent(SettingsActivity.this, UserRechargeActivity.class);
            startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
