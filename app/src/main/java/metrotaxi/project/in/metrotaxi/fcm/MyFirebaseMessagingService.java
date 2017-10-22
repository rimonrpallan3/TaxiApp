package metrotaxi.project.in.metrotaxi.fcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import metrotaxi.project.in.metrotaxi.R;
import metrotaxi.project.in.metrotaxi.activities.DriverAlertActivity;
import metrotaxi.project.in.metrotaxi.activities.UserRequestStatusActivity;
import metrotaxi.project.in.metrotaxi.activities.UserTripEndActivity;
import metrotaxi.project.in.metrotaxi.utils.AppConstants;
import retrofit2.http.Streaming;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public MyFirebaseMessagingService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if(remoteMessage.getData().size() > 0) {
            Log.e("fcm: ", remoteMessage.getData().get("message"));
            try {
                JSONObject jsonObject = new JSONObject(remoteMessage.getData().get("message"));
                String to = jsonObject.getString("to");
                if (to.equals("user")) {
                    SharedPreferences sharedPreferences = getSharedPreferences(AppConstants.SHARED_PREF, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("trip_user", jsonObject.toString());
                    editor.apply();
                    Intent intent1 = new Intent(getApplicationContext(), UserRequestStatusActivity.class);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent1);
                } else if (to.equals("driver")) {
                    String reqId = jsonObject.get("id").toString();
                    Intent intent = new Intent(getApplicationContext(), DriverAlertActivity.class);
                    intent.putExtra("id", reqId);
                    intent.putExtra("json", jsonObject.toString());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else if (to.equals("user_notice")) {
                    //String reqId = jsonObject.get("id").toString();
                    Intent intent = new Intent(getApplicationContext(), UserTripEndActivity.class);
                    intent.putExtra("json", jsonObject.toString());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
         }
    }
}
