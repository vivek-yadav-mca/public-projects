package dummydata.android.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import dummydata.android.Constants;
import dummydata.android.R;
import dummydata.android.databinding.ActivityFirstScreenBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FirstScreenActivity extends AppCompat {

    public static final String APP_STORE_LINK = Constants.APP_PLAYSTORE_LINK;

    ActivityFirstScreenBinding binding;
    boolean isRemoteConfig_DownloadFromPlayStore = false;
    FirebaseRemoteConfig remoteConfig;
    Animation scaleDown;
    Animation fadeIn;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_screen);
        binding = ActivityFirstScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        remoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings remoteConfigSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(0)
                .build();
        remoteConfig.setConfigSettingsAsync(remoteConfigSettings);
        remoteConfig.fetchAndActivate().addOnCompleteListener(new OnCompleteListener<Boolean>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Boolean> task) {
                if (task.isSuccessful()) {
                    isRemoteConfig_DownloadFromPlayStore = remoteConfig.getBoolean(Constants.CHECK_DOWNLOAD_FROM_PLAYSTORE);
                } else {
                    showNoInternetDialog();

                }
            }
        });

        handler = new Handler(Looper.getMainLooper());
        scaleDown = AnimationUtils.loadAnimation(this, R.anim.anim_large_scale_down);
        fadeIn = AnimationUtils.loadAnimation(this, R.anim.anim_fade_in);

        int adb = 0;

        binding.firstScreenCartoonGame101Icon.startAnimation(fadeIn);

        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (adb == 0) {
                            if (isRemoteConfig_DownloadFromPlayStore) {
                                if (isDownloadedFromPlayStore(FirstScreenActivity.this)) {
                                    startActivity();
                                } else {
                                    showAppNotDownloadedFromPlayStoreDialog();
                                }
                            } else {
                                startActivity();
                            }
                        }
                        if (adb == 1) {
                            showDisableDeveloperOptionDialog();
                        }
                    }
                }, 2000);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

    }

    private void startActivity() {
        SharedPreferences readSPref = getSharedPreferences(Constants.SHARED_PREF_COMMON, MODE_PRIVATE);
        boolean showLanguageSelection = readSPref.getBoolean(Constants.SP_SHOW_LANGUAGE_ACTIVITY, true);

        if (showLanguageSelection) {
            startActivity(new Intent(FirstScreenActivity.this, LanguageSelectionActivity.class));
        } else {
            startActivity(new Intent(FirstScreenActivity.this, SplashScreenActivity.class));
        }
    }

    private void showDisableDeveloperOptionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(FirstScreenActivity.this);
        View rootView1 = LayoutInflater.from(FirstScreenActivity.this).inflate(R.layout.layout_dialog_disable_developer_option,
                (ConstraintLayout) findViewById(R.id.constraint_dialog_disable_developer_option));
        builder.setView(rootView1);
        builder.setCancelable(false);

        AlertDialog dialogAfterInterstitial = builder.create();
        dialogAfterInterstitial.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialogAfterInterstitial.show();

        Button closeBtn = rootView1.findViewById(R.id.developer_dialog_close_btn);

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
                System.exit(0);
            }
        });

    }

    boolean isDownloadedFromPlayStore(Context context) {
        List<String> validInstallers = new ArrayList<>(Arrays.asList("com.android.vending", "com.google.android.feedback"));
        final String installer = context.getPackageManager().getInstallerPackageName(context.getPackageName());
        return installer != null && validInstallers.contains(installer);
    }

    private void showAppNotDownloadedFromPlayStoreDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(FirstScreenActivity.this);
        View rootView1 = LayoutInflater.from(FirstScreenActivity.this).inflate(R.layout.layout_dialog_app_not_downloaded_from_playstore,
                (ConstraintLayout) findViewById(R.id.constraint_dialog_app_not_downloaded_from_playstore));
        builder.setView(rootView1);
        builder.setCancelable(false);
        AlertDialog alertDialog = builder.create();

        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.show();

        FrameLayout exitBtn = rootView1.findViewById(R.id.dialog_app_not_playstore_exit_btn_fl);
        FrameLayout downloadBtn = rootView1.findViewById(R.id.dialog_app_not_playstore_download_btn_fl);

        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitBtn.startAnimation(scaleDown);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finishAffinity();
                        System.exit(1);
                    }
                }, 100);
            }
        });

        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadBtn.startAnimation(scaleDown);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(APP_STORE_LINK)));
                    }
                }, 100);
            }
        });
    }

    private void showNoInternetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View rootView1 = LayoutInflater.from(this).inflate(R.layout.layout_dialog_no_internet,
                (ConstraintLayout) findViewById(R.id.constraint_dialog_no_internet));
        builder.setView(rootView1);
        builder.setCancelable(false);

        AlertDialog alertDialog = builder.create();

        Button reloadActivityButton = rootView1.findViewById(R.id.reload_activity_button);
        Button exitActivityButton = rootView1.findViewById(R.id.exit_button);

        reloadActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recreate();
                alertDialog.dismiss();
            }
        });

        exitActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                System.exit(0);
            }
        });
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.show();
    }

}