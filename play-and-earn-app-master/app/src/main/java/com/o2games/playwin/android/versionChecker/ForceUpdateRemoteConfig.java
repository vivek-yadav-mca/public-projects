package dummydata.android.versionChecker;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.HashMap;
import java.util.Map;

public class ForceUpdateRemoteConfig extends Application {

    private static final String TAG = ForceUpdateRemoteConfig.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();

        final FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        // set in-app defaults
        Map<String, Object> remoteConfigDefaults_hashMap = new HashMap();
        remoteConfigDefaults_hashMap.put(ForceUpdateChecker.KEY_UPDATE_REQUIRED, false);
        remoteConfigDefaults_hashMap.put(ForceUpdateChecker.KEY_NEW_VERSION, "1.0.0");
        remoteConfigDefaults_hashMap.put(ForceUpdateChecker.KEY_UPDATE_URL,
                "///play-store-link///");

        firebaseRemoteConfig.setDefaultsAsync(remoteConfigDefaults_hashMap);


        firebaseRemoteConfig.fetch(0)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "remote config is fetched.");
                            firebaseRemoteConfig.fetchAndActivate();
                        }
                    }
                });
    }
}
