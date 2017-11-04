package metrotaxi.project.in.metrotaxi.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import metrotaxi.project.in.metrotaxi.R;
import metrotaxi.project.in.metrotaxi.models.UserModel;
import metrotaxi.project.in.metrotaxi.webservices.RetrofitClient;
import metrotaxi.project.in.metrotaxi.webservices.WebServices;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DriverRegisterActivity extends AppCompatActivity implements View.OnClickListener {
    EditText nameEditText;
    EditText emailEditText;
    EditText phoneNumberEditText;
    EditText usernameEditText;
    EditText passwordEditText;
    EditText browseLicenseEditText;
    EditText browseRCEditText;
    EditText vehicleEditText;
    Uri licenseFileUri;
    Uri rcFileUri;
    Uri profilePicUri;
    ImageView profilePicImageView;
    EditText vehicleNumberEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        nameEditText = (EditText) findViewById(R.id.nameEditText);
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        phoneNumberEditText = (EditText) findViewById(R.id.phoneEditText);
        usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        browseLicenseEditText = (EditText) findViewById(R.id.browseLicenseEditText);
        browseRCEditText = (EditText) findViewById(R.id.browseVehicleRcEditText);
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
        browseLicenseEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, 1);
                }
            }
        });
        browseRCEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, 2);
                }
            }
        });
        findViewById(R.id.registerButton).setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            licenseFileUri = data.getData();
            browseLicenseEditText.setText(licenseFileUri.getPath());
        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            rcFileUri = data.getData();
            browseRCEditText.setText(rcFileUri.getPath());
        } else if (requestCode == 3 && resultCode == RESULT_OK) {
            profilePicUri = data.getData();
            profilePicImageView.setImageURI(profilePicUri);
        }
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        register();
    }

    @Override
    public void onClick(View v) {
        System.out.println("Register Button Clicked ");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            register();
        } else {
            ActivityCompat.requestPermissions(DriverRegisterActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }

    private void register() {
        String name = nameEditText.getText().toString();
        final String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String emailId = emailEditText.getText().toString();
        String phone = phoneNumberEditText.getText().toString();
        String vehicleNumber = vehicleNumberEditText.getText().toString();

        boolean flag = true;
        if (name.equals("")) {
            flag = false;
            nameEditText.setError("Enter name");
        }
        if (username.equals("")) {
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
        if (licenseFileUri == null) {
            browseLicenseEditText.setError("Browse license");
        }
        if (licenseFileUri != null && rcFileUri != null && profilePicUri != null) {
            final ProgressDialog progressDialog = new ProgressDialog(DriverRegisterActivity.this);
            progressDialog.setTitle("Wait");
            progressDialog.setMessage("Connecting to server");
            progressDialog.show();
            String filePath = getRealPathFromURIPath(licenseFileUri, DriverRegisterActivity.this);
            File licenseFile = new File(filePath);
            RequestBody mFile = RequestBody.create(MediaType.parse("image/*"), licenseFile);
            MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file1", licenseFile.getName(), mFile);
            RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), licenseFile.getName());

            String rcFilePath = getRealPathFromURIPath(rcFileUri, DriverRegisterActivity.this);
            File rcFile = new File(rcFilePath);
            RequestBody rcFileBody = RequestBody.create(MediaType.parse("image/*"), rcFile);
            MultipartBody.Part fileToUpload1 = MultipartBody.Part.createFormData("file2", rcFile.getName(), rcFileBody);
            RequestBody filename1 = RequestBody.create(MediaType.parse("text/plain"), rcFile.getName());

            String profileFilePath = getRealPathFromURIPath(profilePicUri, DriverRegisterActivity.this);
            File profileFile = new File(profileFilePath);
            RequestBody profileFileBody = RequestBody.create(MediaType.parse("image/*"), profileFile);
            MultipartBody.Part fileToUpload2 = MultipartBody.Part.createFormData("file3", profileFile.getName(), profileFileBody);
            RequestBody filename2 = RequestBody.create(MediaType.parse("text/plain"), profileFile.getName());
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
            Call<UserModel> fileUpload = webServices.uploadFile(fileToUpload, fileToUpload1, fileToUpload2, data);
            fileUpload.enqueue(new Callback<UserModel>() {
                @Override
                public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                    progressDialog.dismiss();
                    UserModel userModel = response.body();

                    if (userModel != null) {
                        String responseMsg = userModel.getResponse();
                        Toast.makeText(DriverRegisterActivity.this, userModel.getResponse(), Toast.LENGTH_LONG).show();
                        if (responseMsg.equals("success")) {
                            Intent intent = new Intent(DriverRegisterActivity.this, LoginActivity.class);
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
}

