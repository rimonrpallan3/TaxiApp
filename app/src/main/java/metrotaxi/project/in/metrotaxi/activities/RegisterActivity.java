package metrotaxi.project.in.metrotaxi.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import metrotaxi.project.in.metrotaxi.R;
import metrotaxi.project.in.metrotaxi.models.UserModel;
import metrotaxi.project.in.metrotaxi.webservices.RetrofitClient;
import metrotaxi.project.in.metrotaxi.webservices.WebServices;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    EditText nameEditText;
    EditText emailEditText;
    EditText phoneNumberEditText;
    EditText usernameEditText;
    EditText passwordEditText;
    EditText addressEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        nameEditText = (EditText) findViewById(R.id.nameEditText);
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        phoneNumberEditText = (EditText) findViewById(R.id.phoneEditText);
        usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        addressEditText = (EditText) findViewById(R.id.addressEditText);
        findViewById(R.id.registerButton).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Retrofit retrofit = new RetrofitClient().getRetrofitClient();
        WebServices webServices = retrofit.create(WebServices.class);
        UserModel userModel = new UserModel();
        userModel.setUsername(usernameEditText.getText().toString());
        userModel.setPassword(passwordEditText.getText().toString());
        userModel.setName(nameEditText.getText().toString());
        userModel.setEmail(emailEditText.getText().toString());
        userModel.setPhoneNumber(phoneNumberEditText.getText().toString());
        userModel.setAddress(addressEditText.getText().toString());
        Call<UserModel> call = webServices.registerUser(userModel);
        final ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this);
        progressDialog.setTitle("wait");
        progressDialog.show();
        call.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                String res = response.body().getResponse();
                progressDialog.dismiss();
                if (res.trim().equals("success")) {
                    Toast.makeText(RegisterActivity.this, "Successfully registered", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, res, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserModel> call, Throwable t) {
                progressDialog.dismiss();
                t.printStackTrace();
                Toast.makeText(RegisterActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
