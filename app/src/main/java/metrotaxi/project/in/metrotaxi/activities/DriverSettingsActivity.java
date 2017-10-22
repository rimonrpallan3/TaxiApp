package metrotaxi.project.in.metrotaxi.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import metrotaxi.project.in.metrotaxi.R;
import metrotaxi.project.in.metrotaxi.models.UserModel;
import metrotaxi.project.in.metrotaxi.utils.AppConstants;
import metrotaxi.project.in.metrotaxi.webservices.RetrofitClient;
import metrotaxi.project.in.metrotaxi.webservices.WebServices;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DriverSettingsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    EditText nameEditText;
    EditText emailEditText;
    EditText phoneNumberEditText;
    EditText usernameEditText;
    EditText passwordEditText;
    ImageView licenseImageView;
    ImageView rcImageView;
    Uri licenseFileUri;
    Uri rcFileUri;
    Uri profilePicUri;
    ImageView profilePicImageView;
    EditText vehicleNumberEditText;
    SharedPreferences sharedPreferences;
    String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        sharedPreferences = getSharedPreferences(AppConstants.SHARED_PREF, MODE_PRIVATE);
        username = sharedPreferences.getString(AppConstants.USER_NAME, "");
        nameEditText = (EditText) findViewById(R.id.nameEditText);
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        phoneNumberEditText = (EditText) findViewById(R.id.phoneEditText);
        usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        licenseImageView = (ImageView) findViewById(R.id.licenseImageView);
        rcImageView = (ImageView) findViewById(R.id.rcImageView);
        profilePicImageView = (ImageView) findViewById(R.id.profilePicImageView);
        vehicleNumberEditText = (EditText) findViewById(R.id.vehicleNumberEditText);
        profilePicImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 3);
            }
        });
//        licenseImageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                    Intent intent = new Intent(Intent.ACTION_PICK);
//                    intent.setType("image/*");
//                    startActivityForResult(intent, 1);
//
//            }
//        });
        rcImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 2);
            }
        });
        findViewById(R.id.registerButton).setOnClickListener(this);
        getUserInfo();

    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_driver_trip_history) {
            Intent intent = new Intent(DriverSettingsActivity.this, DriverTripHistoryActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_driver_logout) {
            SharedPreferences sharedPreferences = getSharedPreferences(AppConstants.SHARED_PREF, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(AppConstants.USER_NAME, "");
            editor.putString(AppConstants.USER_TYPE, "");
            editor.commit();
            Intent intent = new Intent(DriverSettingsActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_driver_settings) {
            Intent intent = new Intent(DriverSettingsActivity.this, DriverSettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_driver_current_trip) {
            Intent intent = new Intent(DriverSettingsActivity.this, DriverAlertActivity.class);
            startActivity(intent);
        }
        return true;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            licenseFileUri = data.getData();
            licenseImageView.setImageURI(licenseFileUri);
        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            rcFileUri = data.getData();
            rcImageView.setImageURI(rcFileUri);
        } else if (requestCode == 3 && resultCode == RESULT_OK) {
            profilePicUri = data.getData();
            profilePicImageView.setImageURI(profilePicUri);
        }
    }
    @Override
    public void onClick(View v) {
            String name = nameEditText.getText().toString();
            final String username1 = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            String emailId = emailEditText.getText().toString();
            String phone = phoneNumberEditText.getText().toString();
            String vehicleNumber = vehicleNumberEditText.getText().toString();

            boolean flag = true;
            if (name.equals("")) {
                flag = false;
                nameEditText.setError("Enter name");
            }
            if (username1.equals("")) {
                flag = false;
                usernameEditText.setError("Enter username");
            }
            if (password.equals("")) {
                flag = false;
                passwordEditText.setError("Enter password");
            }
            if (emailId.equals("")) {
                flag = false;
                emailEditText.setError("Enter email id");
            }
            if (vehicleNumber.equals("")) {
                flag = false;
                vehicleNumberEditText.setError("Enter vehicle number");
            }
            if (phone.equals("")) {
                flag = false;
                phoneNumberEditText.setError("Enter phone number");
            }
        MultipartBody.Part fileToUpload2 = null;
        MultipartBody.Part fileToUpload = null;
        MultipartBody.Part fileToUpload1 = null;
            if (licenseFileUri != null) {
                String filePath = getRealPathFromURIPath(licenseFileUri, DriverSettingsActivity.this);
                File licenseFile = new File(filePath);
                RequestBody mFile = RequestBody.create(MediaType.parse("image/*"), licenseFile);
                fileToUpload = MultipartBody.Part.createFormData("file1", licenseFile.getName(), mFile);
                RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), licenseFile.getName());
            }
            if (rcFileUri != null) {
                String rcFilePath = getRealPathFromURIPath(rcFileUri, DriverSettingsActivity.this);
                File rcFile = new File(rcFilePath);
                RequestBody rcFileBody = RequestBody.create(MediaType.parse("image/*"), rcFile);
                fileToUpload1 = MultipartBody.Part.createFormData("file2", rcFile.getName(), rcFileBody);
                RequestBody filename1 = RequestBody.create(MediaType.parse("text/plain"), rcFile.getName());
            }
            if (profilePicUri != null) {
                String profileFilePath = getRealPathFromURIPath(profilePicUri, DriverSettingsActivity.this);
                File profileFile = new File(profileFilePath);
                RequestBody profileFileBody = RequestBody.create(MediaType.parse("image/*"), profileFile);
                fileToUpload2 = MultipartBody.Part.createFormData("file3", profileFile.getName(), profileFileBody);
                RequestBody filename2 = RequestBody.create(MediaType.parse("text/plain"), profileFile.getName());
            }
            if (flag) {
        final ProgressDialog progressDialog = new ProgressDialog(DriverSettingsActivity.this);
        progressDialog.setTitle("Wait");
        progressDialog.setMessage("Connecting to server");
        progressDialog.show();
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("name", nameEditText.getText().toString());
                    jsonObject.put("email", emailEditText.getText().toString());
                    jsonObject.put("phone_number", phoneNumberEditText.getText().toString());
                    jsonObject.put("user_name", usernameEditText.getText().toString());
                    jsonObject.put("password", passwordEditText.getText().toString());
                    jsonObject.put("vehicle_number", vehicleNumberEditText.getText().toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody data = RequestBody.create(MediaType.parse("text/plain"), jsonObject.toString());
                Retrofit retrofit = new RetrofitClient().getRetrofitClient();
                WebServices webServices = retrofit.create(WebServices.class);
                Call<UserModel> fileUpload = webServices.driverProfileUpdate(fileToUpload, fileToUpload1, fileToUpload2, data);

                fileUpload.enqueue(new Callback<UserModel>() {
                    @Override
                    public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                        progressDialog.dismiss();
                        UserModel userModel = response.body();

                        if (userModel != null) {
                            String responseMsg = userModel.getResponse();
                            Toast.makeText(DriverSettingsActivity.this, userModel.getResponse(), Toast.LENGTH_LONG).show();
                            if (responseMsg.equals("success")) {
                                Intent intent = new Intent(DriverSettingsActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<UserModel> call, Throwable t) {
                        progressDialog.dismiss();
                        t.printStackTrace();
                        Log.e("Error: ", "Error " + t.getMessage());
                    }
                });
        }
    }

    private void getUserInfo() {
        Retrofit retrofit = RetrofitClient.getRetrofitClient();
        WebServices webServices = retrofit.create(WebServices.class);
        webServices.getDriverInfo(username).enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                UserModel userModel1 = response.body();
                if (userModel1 != null) {
                    nameEditText.setText(userModel1.getName());
                    emailEditText.setText(userModel1.getEmail());
                    phoneNumberEditText.setText(userModel1.getPhoneNumber());
                    vehicleNumberEditText.setText(userModel1.getVehicleNumber());
                    usernameEditText.setText(userModel1.getUsername());
                    passwordEditText.setText(userModel1.getPassword());
                    Picasso.with(DriverSettingsActivity.this)
                            .load(AppConstants.URL + userModel1.getProfileUrl())
                            .skipMemoryCache()
                            .placeholder(R.drawable.profile_pic)
                            .error(R.drawable.profile_pic)
                            .into(profilePicImageView);

                    Picasso.with(DriverSettingsActivity.this)
                            .load(AppConstants.URL + userModel1.getRcUrl())
                            .placeholder(R.drawable.profile_pic)
                            .error(R.drawable.profile_pic)
                            .skipMemoryCache()
                            .into(rcImageView);
                    Picasso.with(DriverSettingsActivity.this)
                            .load(AppConstants.URL + userModel1.getLicenseUrl())
                            .placeholder(R.drawable.profile_pic)
                            .error(R.drawable.profile_pic)
                            .skipMemoryCache()
                            .into(licenseImageView);

                }
            }

            @Override
            public void onFailure(Call<UserModel> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(DriverSettingsActivity.this, "Retrofit onFailure", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private String getRealPathFromURIPath(Uri contentURI, Activity activity) {
        Cursor cursor = activity.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }
}
