package metrotaxi.project.in.metrotaxi.services;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import metrotaxi.project.in.metrotaxi.models.UserModel;
import metrotaxi.project.in.metrotaxi.utils.AppConstants;
import metrotaxi.project.in.metrotaxi.webservices.RetrofitClient;
import metrotaxi.project.in.metrotaxi.webservices.WebServices;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LocationService extends Service implements LocationListener {
    LocationManager locationManager;
    AppCompatActivity appCompatActivity;
    Retrofit retrofit;
    WebServices webServices;
    String username;

    public LocationService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        retrofit = RetrofitClient.getRetrofitClient();
        webServices = retrofit.create(WebServices.class);
        username = getSharedPreferences(AppConstants.SHARED_PREF, MODE_PRIVATE).getString(AppConstants.USER_NAME, "");
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 12000, 3, LocationService.this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 12000, 3, LocationService.this);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            UserModel userModel = new UserModel();
            userModel.setUsername(username);
            userModel.setLat(location.getLatitude());
            userModel.setLongi(location.getLongitude());
            Toast.makeText(getApplicationContext(), location.getLatitude() + ","  + location.getLongitude(), Toast.LENGTH_SHORT).show();
            Log.e("Driver long: ", location.getLongitude() + "");
            webServices.driverLocationUpdate(userModel).enqueue(new Callback<UserModel>() {
                @Override
                public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                    UserModel model = response.body();
                }

                @Override
                public void onFailure(Call<UserModel> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onDestroy() {
        locationManager.removeUpdates(LocationService.this);
        super.onDestroy();
    }
}
