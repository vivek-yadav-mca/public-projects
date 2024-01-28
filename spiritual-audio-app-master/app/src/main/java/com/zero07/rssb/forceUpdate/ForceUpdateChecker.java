package dummydata.forceUpdate;

import android.content.Context;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ForceUpdateChecker {

    private static final String TAG = ForceUpdateChecker.class.getSimpleName();

    public static final String KEY_UPDATE_REQUIRED = "dummydata";
    public static final String KEY_NEW_VERSION = "dummydata";
    public static final String KEY_UPDATE_URL = "dummydata";
    public static final String KEY_UPDATE_MESSAGE_HI = "dummydata";
    public static final String KEY_UPDATE_MESSAGE_EN = "dummydata";

    private OnUpdateNeededListener onUpdateNeededListener;
    private Context context;

    public interface OnUpdateNeededListener {
        void onUpdateNeeded(String updateUrl, String remote_message_hi, String remote_message_en);
    }

    public static Builder with(@NonNull Context context) {
        return new Builder(context);
    }

    public ForceUpdateChecker(@NonNull Context context,
                              OnUpdateNeededListener onUpdateNeededListener) {
        this.context = context;
        this.onUpdateNeededListener = onUpdateNeededListener;
    }

    public void check() {
        FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();

        FirebaseRemoteConfigSettings remoteConfigSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(0)
                .build();
        remoteConfig.setConfigSettingsAsync(remoteConfigSettings);

        remoteConfig.fetchAndActivate().addOnCompleteListener(new OnCompleteListener<Boolean>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Boolean> task) {
                if (task.isSuccessful()) {
                    if (remoteConfig.getBoolean(KEY_UPDATE_REQUIRED)) {
                        String currentVersion = remoteConfig.getString(KEY_NEW_VERSION);
                        String appVersion = getAppVersion(context);
                        String updateUrl = remoteConfig.getString(KEY_UPDATE_URL);

                        String remote_message_hi = remoteConfig.getString(KEY_UPDATE_MESSAGE_HI);
                        String remote_message_en = remoteConfig.getString(KEY_UPDATE_MESSAGE_EN);

                        if (!TextUtils.equals(currentVersion, appVersion)
                                && onUpdateNeededListener != null) {
                            onUpdateNeededListener.onUpdateNeeded(updateUrl, remote_message_hi, remote_message_en);
                        }
                    }
                }
            }
        });
    }

    private String getAppVersion(Context context) {
        String userAppVersion = "";
        try {
            userAppVersion = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0)
                    .versionName;
            userAppVersion = userAppVersion.replaceAll("[a-zA-Z]|-", "");
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }

        return userAppVersion;
    }

    public static class Builder {

        private Context context;
        private OnUpdateNeededListener onUpdateNeededListener;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder onUpdateNeeded(OnUpdateNeededListener onUpdateNeededListener) {
            this.onUpdateNeededListener = onUpdateNeededListener;
            return this;
        }

        public ForceUpdateChecker build() {
            return new ForceUpdateChecker(context, onUpdateNeededListener);
        }

        public ForceUpdateChecker check() {
            ForceUpdateChecker forceUpdateChecker = build();
            forceUpdateChecker.check();

            return forceUpdateChecker;
        }
    }
}