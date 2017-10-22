package metrotaxi.project.in.metrotaxi.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RatingBar;
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

public class UserTripEndActivity extends AppCompatActivity {
    String userRating = "";
    String id = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_trip_end);
        TextView messageTextView = (TextView) findViewById(R.id.messageTextView);
        SharedPreferences sharedPreferences = getSharedPreferences(AppConstants.SHARED_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("trip_user", "");
        editor.apply();
        Intent intent = getIntent();
        if (intent != null) {
            String json = intent.getStringExtra("json");
            if (json != null) {
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    if (jsonObject != null) {
                        id = jsonObject.getString("id");
                        messageTextView.setText(jsonObject.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
        RatingBar ratingBar = (RatingBar) findViewById(R.id.userRateBar);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                userRating = String.valueOf(rating);
            }
        });
        findViewById(R.id.feedbackSubmitButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(UserTripEndActivity.this);
                progressDialog.setTitle("Wait");
                progressDialog.show();
                Retrofit retrofit = RetrofitClient.getRetrofitClient();
                WebServices webServices = retrofit.create(WebServices.class);
                webServices.userFeedBack(id, userRating).enqueue(new Callback<UserModel>() {
                    @Override
                    public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                        progressDialog.dismiss();
                        Toast.makeText(UserTripEndActivity.this, response.body().getResponse(), Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onFailure(Call<UserModel> call, Throwable t) {
                        progressDialog.dismiss();
                        Toast.makeText(UserTripEndActivity.this, "Error in feedback posting", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }
}
