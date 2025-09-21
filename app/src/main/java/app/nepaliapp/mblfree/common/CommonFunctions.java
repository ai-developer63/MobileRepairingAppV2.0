package app.nepaliapp.mblfree.common;

import android.content.Context;

import java.util.Objects;
import java.util.UUID;

public class CommonFunctions {


    public static String getDeviceId(Context context) {
        StorageClass storageClass = new StorageClass(context);
        String deviceId = storageClass.getDeviceUniqueID();

        if (Objects.equals(deviceId, "token_kali_xa") || deviceId == null) {
            deviceId = UUID.randomUUID().toString();
            storageClass.UpdateDeviceUniqueID(deviceId);
        }

        return deviceId;
    }
}
