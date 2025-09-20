package app.nepaliapp.mblfree.common;

import android.content.Context;
import android.content.SharedPreferences;

public class StorageClass {
    private static final String USER_DETAILS = "j";
    private static final String USER_CHOICE = "c";
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


}
