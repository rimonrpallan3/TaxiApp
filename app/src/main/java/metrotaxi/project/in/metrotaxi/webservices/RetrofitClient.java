package metrotaxi.project.in.metrotaxi.webservices;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit = null;
    private static Retrofit pathRetrofit = null;
    private static OkHttpClient client = new OkHttpClient.Builder().connectTimeout(300, TimeUnit.SECONDS).readTimeout(300, TimeUnit.SECONDS).build();
    public static Retrofit getRetrofitClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder().baseUrl("http://192.168.1.36:8080/MetroTaxi/")
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create()).build();
        }
        return retrofit;
    }

    public static Retrofit getRetrofitClientPath() {
        if (pathRetrofit == null) {
            pathRetrofit = new Retrofit.Builder().baseUrl("https://maps.googleapis.com/maps/api/directions/").addConverterFactory(GsonConverterFactory.create()).build();
        }
        return pathRetrofit;
    }
}
