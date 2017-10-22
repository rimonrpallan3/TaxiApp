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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import metrotaxi.project.in.metrotaxi.R;
import metrotaxi.project.in.metrotaxi.models.RechargeModel;
import metrotaxi.project.in.metrotaxi.models.UserModel;
import metrotaxi.project.in.metrotaxi.utils.AppConstants;
import metrotaxi.project.in.metrotaxi.webservices.RetrofitClient;
import metrotaxi.project.in.metrotaxi.webservices.WebServices;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UserRechargeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private EditText nameOnCard;
    private EditText numberOnCard;
    private EditText rechargeAmount;
    private Spinner monthSpinner;
    private Spinner yearSpinner;
    private EditText cvvNumberEditText;
    private String userName;
    String[] months = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    String[] years = new String[]{"2017", "2018", "2019", "2020", "2021"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_recharge);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        nameOnCard = (EditText) findViewById(R.id.cardNameEditText);
        numberOnCard = (EditText) findViewById(R.id.cardNumberEditText);
        rechargeAmount = (EditText) findViewById(R.id.amountEditText);
        monthSpinner = (Spinner) findViewById(R.id.monthSpinner);
        yearSpinner = (Spinner) findViewById(R.id.yearSpinner);
        cvvNumberEditText = (EditText) findViewById(R.id.cvvEditText);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, months);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(adapter);
        ArrayAdapter adapter2 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, years);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(adapter2);
        SharedPreferences sharedPreferences = getSharedPreferences(AppConstants.SHARED_PREF, MODE_PRIVATE);
        userName = sharedPreferences.getString(AppConstants.USER_NAME, "");
        findViewById(R.id.rechargeButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean flag = true;
                if (nameOnCard.getText().toString().equals("")) {
                    nameOnCard.setError("Enter name on card");
                    flag = false;
                }
                if (numberOnCard.getText().toString().equals("")) {
                    numberOnCard.setError("Enter card number");
                    flag = false;
                }
                if (rechargeAmount.getText().toString().equals("")) {
                    rechargeAmount.setError("Enter recharge amount");
                    flag = false;
                }
                if (cvvNumberEditText.getText().toString().equals("")) {
                    cvvNumberEditText.setError("Enter cvv number");
                    flag = false;
                }
                if (flag) {
                        RechargeModel rechargeModel = new RechargeModel();
                        rechargeModel.setUsername(userName);
                        rechargeModel.setCardName(nameOnCard.getText().toString());
                        rechargeModel.setCardNumber(numberOnCard.getText().toString());
                        rechargeModel.setRechargeAmount(Integer.parseInt(rechargeAmount.getText().toString()));
                        rechargeModel.setCvv(cvvNumberEditText.getText().toString());
                        rechargeModel.setExpiryDate(monthSpinner.getSelectedItem().toString() + "/" + yearSpinner.getSelectedItem().toString());
                    Retrofit retrofit = new RetrofitClient().getRetrofitClient();
                    final WebServices webServiveAPIs = retrofit.create(WebServices.class);
                    final ProgressDialog progressDialog = new ProgressDialog(UserRechargeActivity.this);
                    progressDialog.setTitle("wait");
                    progressDialog.show();
                    webServiveAPIs.userWalletRecharge(rechargeModel).enqueue(new Callback<RechargeModel>() {
                        @Override
                        public void onResponse(Call<RechargeModel> call, Response<RechargeModel> response) {
                            progressDialog.dismiss();
                            RechargeModel rechargeModel1 = response.body();
                            if (rechargeModel1 != null) {
                                Toast.makeText(UserRechargeActivity.this, rechargeModel1.getStatus(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<RechargeModel> call, Throwable t) {
                            progressDialog.dismiss();
                            t.printStackTrace();
                            Toast.makeText(UserRechargeActivity.this, "RECHARGE NOT SUCCESSFULL", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_request_status) {
            Intent intent = new Intent(UserRechargeActivity.this, UserRequestStatusActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_new_trip) {
            Intent intent = new Intent(UserRechargeActivity.this, TripActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_history) {
            Intent intent = new Intent(UserRechargeActivity.this, TripHistoryActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(UserRechargeActivity.this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            SharedPreferences sharedPreferences = getSharedPreferences(AppConstants.SHARED_PREF, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(AppConstants.USER_NAME, "");
            editor.putString(AppConstants.USER_TYPE, "");
            editor.apply();
            Intent intent = new Intent(UserRechargeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_recharge) {
            Intent intent = new Intent(UserRechargeActivity.this, UserRechargeActivity.class);
            startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
