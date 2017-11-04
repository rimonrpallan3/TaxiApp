package metrotaxi.project.in.metrotaxi.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

import metrotaxi.project.in.metrotaxi.R;
import metrotaxi.project.in.metrotaxi.adapters.DriverTripHistoryAdapter;
import metrotaxi.project.in.metrotaxi.models.UserModel;
import metrotaxi.project.in.metrotaxi.utils.AppConstants;
import metrotaxi.project.in.metrotaxi.webservices.RetrofitClient;
import metrotaxi.project.in.metrotaxi.webservices.WebServices;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    EditText usernameEditText;
    EditText passwordEditText;
    SharedPreferences sharedPreferences;
    String fcm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fcm = FirebaseInstanceId.getInstance().getToken();
        sharedPreferences = getSharedPreferences(AppConstants.SHARED_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(AppConstants.FCM_ID, fcm);
        editor.apply();
        String username = sharedPreferences.getString(AppConstants.USER_NAME, "");
        if (username.equals("")) {
            setContentView(R.layout.activity_login);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            usernameEditText = (EditText) findViewById(R.id.usernameEditText);
            passwordEditText = (EditText) findViewById(R.id.passwordEditText);
            findViewById(R.id.loginButton).setOnClickListener(this);
            findViewById(R.id.forgotPasswordTextView).setOnClickListener(this);
            findViewById(R.id.registerTextView).setOnClickListener(this);
            findViewById(R.id.newDriverTextView).setOnClickListener(this);
        } else {
            String userType = sharedPreferences.getString(AppConstants.USER_TYPE, "");
            if (userType.equals("user")) {
                Intent intent = new Intent(LoginActivity.this, TripActivity.class);
                startActivity(intent);
            } else if (userType.equals("driver")){
                Intent intent = new Intent(LoginActivity.this, DriverSettingsActivity.class);
                startActivity(intent);
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.loginButton) {
            Retrofit retrofit = new RetrofitClient().getRetrofitClient();
            WebServices webServices = retrofit.create(WebServices.class);
            final UserModel userModel = new UserModel();
            userModel.setUsername(usernameEditText.getText().toString());
            userModel.setPassword(passwordEditText.getText().toString());
            userModel.setFcm(fcm);
            Call<UserModel> call = webServices.loginUser(userModel);
            final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setTitle("wait");
            progressDialog.show();
            call.enqueue(new Callback<UserModel>() {
                @Override
                public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                    UserModel res = response.body();
                    progressDialog.dismiss();
                    Log.e("response: ", res.getResponse());
                    if (res.getResponse().equals("success")) {
                        SharedPreferences sharedPreferences = getSharedPreferences(AppConstants.SHARED_PREF, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(AppConstants.USER_NAME, userModel.getUsername());
                        editor.putString(AppConstants.USER_TYPE, res.getUserType());
                        editor.apply();
                        if (res.getUserType().equals("user")) {
                            Intent intent = new Intent(LoginActivity.this, TripActivity.class);
                            startActivity(intent);
                        } else if(res.getUserType().equals("driver")) {
                            Intent intent = new Intent(LoginActivity.this, DriverSettingsActivity.class);
                            startActivity(intent);
                        }
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Invalid UserName and Password", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<UserModel> call, Throwable t) {
                    progressDialog.dismiss();
                    t.printStackTrace();
                    Toast.makeText(LoginActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                }
            });
//            Intent intent = new Intent(LoginActivity.this, TripActivity.class);
//            startActivity(intent);
        } else if (id == R.id.forgotPasswordTextView) {

        } else if (id == R.id.newDriverTextView) {
            Intent intent = new Intent(LoginActivity.this, DriverRegisterActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        }
    }
}
