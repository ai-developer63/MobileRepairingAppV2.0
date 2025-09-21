package app.nepaliapp.mblfree.common;

import android.content.Context;
import android.content.SharedPreferences;

public class StorageClass {
    private static final String USER_DETAILS = "j";
    private static final String USER_CHOICE = "c";
    private static final String PREFERENCES_DEVICE_TOKEN = "d";
    Context context;
    public StorageClass(Context context) {
        this.context = context;
    }

    public void userUpdateDecision(String decision) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(USER_CHOICE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("updateChoice", decision);
        editor.apply();
    }
    public String getUserUpdateDecision() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(USER_CHOICE, Context.MODE_PRIVATE);
        return sharedPreferences.getString("updateChoice", "unselected");
    }
    public void UpdateJwtToken(String JwtToken){
        SharedPreferences sharedPreferences = context.getSharedPreferences(USER_DETAILS,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("jwtToken",JwtToken);
        editor.apply();
    }

    public String getJwtToken(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(USER_DETAILS,Context.MODE_PRIVATE);
        return sharedPreferences.getString("jwtToken","Jwt_kali_xa");
    }
    public void UpdateDeviceUniqueID(String deviceToken){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_DEVICE_TOKEN,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("DeviceToken",deviceToken);
        editor.apply();
    }
    public String getDeviceUniqueID(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_DEVICE_TOKEN,Context.MODE_PRIVATE);
        return sharedPreferences.getString("DeviceToken","token_kali_xa");
    }


}
