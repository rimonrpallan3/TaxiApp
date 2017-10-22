package metrotaxi.project.in.metrotaxi.fcm;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import metrotaxi.project.in.metrotaxi.models.UserModel;
import metrotaxi.project.in.metrotaxi.utils.AppConstants;
import metrotaxi.project.in.metrotaxi.webservices.RetrofitClient;
import metrotaxi.project.in.metrotaxi.webservices.WebServices;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MyInstanceIdService extends FirebaseInstanceIdService {
    public MyInstanceIdService() {
    }

    @Override
    public void onTokenRefresh() {
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.e("fcm", token);
        sendTockenToServer(token);
    }
    private void sendTockenToServer(String token) {
        UserModel userModel = new UserModel();
        SharedPreferences sharedPreferences = getSharedPreferences(AppConstants.SHARED_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(AppConstants.FCM_ID, token);
        editor.apply();
        userModel.setUsername(sharedPreferences.getString(AppConstants.USER_NAME, ""));
        userModel.setFcm(token);

        Retrofit retrofit = new RetrofitClient().getRetrofitClient();
        WebServices webServices = retrofit.create(WebServices.class);

        Call<UserModel> call = webServices.updateFCMId(userModel);

        call.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                Log.e("FCM Update Response: ", response.body().getResponse());
            }
            @Override
            public void onFailure(Call<UserModel> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
