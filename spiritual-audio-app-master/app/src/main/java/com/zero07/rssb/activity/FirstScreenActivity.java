package dummydata.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import dummydata.R;
import dummydata.databinding.ActivityFirstScreenBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FirstScreenActivity extends AppCompatActivity {

    public static final String APP_STORE_LINK = "https://dummydata/store/apps/details?id=dummydata";
    ActivityFirstScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_screen);
        binding = ActivityFirstScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings remoteConfigSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(0)
                .build();
        remoteConfig.setConfigSettingsAsync(remoteConfigSettings);

        remoteConfig.fetchAndActivate().addOnCompleteListener(new OnCompleteListener<Boolean>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Boolean> task) {
                if (task.isSuccessful()) {
                    boolean checkAppDownloadOrigin = remoteConfig.getBoolean("checkAppDownloadOrigin");

                    if (checkAppDownloadOrigin) {
                        // Check app origin
                        if (isDownloadedFromPlayStore(FirstScreenActivity.this)) {
                            // App Downloaded from PlayStore
                            startApp();
                        } else {
                            // App not downloaded from playstore
                            showAppOriginDialog();
                        }
                    } else {
                        // Don't check app origin
                        startApp();
                    }
                } else {
                    Toast.makeText(FirstScreenActivity.this, "Slow internet connection !!!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    boolean isDownloadedFromPlayStore(Context context) {
        // A list with valid installers package name
        List<String> validInstallers = new ArrayList<>(Arrays.asList("com.android.vending", "com.google.android.feedback"));
        // The package name of the app that has installed your app
        final String installer = context.getPackageManager().getInstallerPackageName(context.getPackageName());
        // true if your app has been downloaded from Play Store
        return installer != null && validInstallers.contains(installer);
//        return true;
    }

    private void startApp() {
        startActivity(new Intent(FirstScreenActivity.this, SplashScreenActivity.class));
    }

    private void showAppOriginDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(FirstScreenActivity.this);
        View view = LayoutInflater.from(FirstScreenActivity.this).inflate(R.layout.layout_dialog_show_app_origin,
                (ConstraintLayout) findViewById(R.id.layout_constraint_dialog_show_app_origin));
        builder.setView(view);
        builder.setCancelable(false);

        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.show();

        Button downloadButton = view.findViewById(R.id.download_playstore_btn);
        Button exitButton = view.findViewById(R.id.exit_close_btn);

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(APP_STORE_LINK));
                startActivity(intent);
            }
        });

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                finish();
                finishAffinity();
                System.exit(0);
            }
        });
    }
}