package dummydata.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdFormat;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.applovin.sdk.AppLovinSdkUtils;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.ironsource.mediationsdk.ISBannerSize;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.IronSourceBannerLayout;
import com.tapdaq.sdk.TMBannerAdView;
import com.tapdaq.sdk.Tapdaq;
import com.tapdaq.sdk.common.TMAdError;
import com.tapdaq.sdk.common.TMBannerAdSizes;
import com.tapdaq.sdk.listeners.TMAdListener;
import com.unity3d.services.banners.BannerView;
import com.unity3d.services.banners.UnityBannerSize;
import dummydata.Constants;
import dummydata.IronsourceBannerListener;
import dummydata.R;
import dummydata.adapters.HomeItemAdapter;
import dummydata.databinding.ActivityMainBinding;
import dummydata.forceUpdate.ForceUpdateChecker;
import dummydata.models.HomeItemModel;
import dummydata.userModels.UserContext;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    int s0s1s2p3b4s5;

    private boolean isAdmobInterstitialEnabled;
    AdView admobBanner;
    InterstitialAd admobInterstitial;

    private boolean isTapdaqInterstitialEnabled;
    TMBannerAdView tapdaqBanner;

    private boolean isAppLovinInterstitialEnabled;
    MaxAdView applovinBanner;
    MaxInterstitialAd applovinInterstitial;

    BannerView unityBanner;
    String unityAdUnitId = "Banner_Android";

    IronSourceBannerLayout ironSourceBanner;

    ActivityMainBinding binding;
    FirebaseRemoteConfig remoteConfig;
    public static String UPSTOX_REFERRAL_LINK;
    boolean upstoxReferralToBeShown;

    Handler handler;
    AlertDialog progressDialog;
    SharedPreferences writeSPref;
    SharedPreferences readSPref;
    SharedPreferences.Editor editorSPref;
    ConnectivityManager connectivityManager;

    private Toolbar toolbar;
    int retryCount = 1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        writeSPref = getSharedPreferences(Constants.COMMON_SPREF_RSSB, Context.MODE_PRIVATE);
        editorSPref = writeSPref.edit();
        readSPref = getSharedPreferences(Constants.COMMON_SPREF_RSSB, Context.MODE_PRIVATE);

        remoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings remoteConfigSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(0)
                .build();
        remoteConfig.setConfigSettingsAsync(remoteConfigSettings);

        isAdmobInterstitialEnabled = UserContext.getIsAdmobInterstitialEnabled();
        isTapdaqInterstitialEnabled = UserContext.getIsTapdaqInterstitialEnabled();
        isAppLovinInterstitialEnabled = UserContext.getIsApplovinInterstitial();
        applovinInterstitial = new MaxInterstitialAd(getString(R.string.appl_interstitial_default), this);
        applovinInterstitial.setListener(new AppLovinInterstitialAdListener());

        binding.upstoxReferCard.setVisibility(View.GONE);
        remoteConfig.fetchAndActivate().addOnCompleteListener(new OnCompleteListener<Boolean>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Boolean> task) {
                if (task.isSuccessful()) {
                    if (upstoxReferralToBeShown) {
                        binding.upstoxReferCard.setVisibility(View.VISIBLE);
                    } else {
                        binding.upstoxReferCard.setVisibility(View.GONE);
                    }

                } else {
                    binding.upstoxReferCard.setVisibility(View.GONE);
                }
            }
        });

        handler = new Handler(Looper.getMainLooper());

        checkAppUpdate();
        loadBannerAdFrom();

        Date dateTime = new Date(System.currentTimeMillis());
        long currentTime = dateTime.getTime();

        if (!getDontShowDialogBoolean()) {
            if (currentTime >= getRatingDialogTime()) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showPlayStoreRatingBSdialog();
                    }
                }, 2500);
            }
        }

        if (!internetConnected(MainActivity.this)) {
            showNoInternetDialog();
        }

        ArrayList<HomeItemModel> homeitems = new ArrayList<>();
        HomeItemAdapter adapter = new HomeItemAdapter(MainActivity.this, homeitems);
        binding.homeItemList.setLayoutManager(new GridLayoutManager(this, 3));
        binding.homeItemList.setAdapter(adapter);

        binding.topCardView.setBackgroundResource(R.drawable.bg_red_gradient);


        binding.upstoxReferCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUpstoxOfferBsDialog();
            }
        });


/**** For Shining Effect on Google Play Rating button ****/
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        shineEffectStart();
                    }
                });
            }
        }, 1, 2, TimeUnit.SECONDS);
/**** For Shining Effect on Google Play Rating button ****/


        binding.googlePlayButn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tapdaq.getInstance().startTestActivity(MainActivity.this);


            }
        });

        binding.shareAppButn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name");
                    shareMessage = shareMessage + "dummydata?id=dummydata" + "\n\n";
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, "choose one"));
                } catch (Exception e) {
                    //e.toString();
                }
            }
        });

    }


    private void showUpstoxOfferBsDialog() {
        BottomSheetDialog upstoxBsDialog = new BottomSheetDialog(this, R.style.bottomSheetDialog);
        upstoxBsDialog.setContentView(R.layout.layout_bsdialog_upstox_referral);
        upstoxBsDialog.getDismissWithAnimation();
        upstoxBsDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        upstoxBsDialog.show();

        FrameLayout upstox_dialog_ac_open_btn = upstoxBsDialog.findViewById(R.id.upstox_dialog_ac_open_btn);

        upstox_dialog_ac_open_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(UPSTOX_REFERRAL_LINK));
                startActivity(browserIntent);
            }
        });
    }

    private long getRatingDialogTime() {
        long futureRatingDialogTime = readSPref.getLong(Constants.COMMON_SPREF_RATING_DIALOG_TIME, 0);
        return futureRatingDialogTime;
    }

    private boolean getDontShowDialogBoolean() {
        boolean dontShowDialogBoolean = readSPref.getBoolean(Constants.COMMON_SPREF_RATING_DIALOG_DONT_SHOW_BOOLEAN, false);
        return dontShowDialogBoolean;
    }

    private void showPlayStoreRatingBSdialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MainActivity.this);
        bottomSheetDialog.setContentView(R.layout.layout_dialog_rate_on_playstore);
        bottomSheetDialog.getDismissWithAnimation();
        bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.show();

        RatingBar ratingBar = bottomSheetDialog.findViewById(R.id.rating_dialog_ratingBar);
        CheckBox ratingCheckBox = bottomSheetDialog.findViewById(R.id.rating_dialog_checkBox);
        FrameLayout closeBtn = bottomSheetDialog.findViewById(R.id.rating_dialog_close_btn);

        ratingBar.setRating(1);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("dummydata?id=dummydata")));
                    }
                }, 500);
            }
        });

        ratingCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date dateTime = new Date(System.currentTimeMillis());
                long currentTime = dateTime.getTime();

                Calendar calFutureTime = Calendar.getInstance();
                calFutureTime.setTimeInMillis(currentTime);
                calFutureTime.add(Calendar.HOUR_OF_DAY, 24);
                calFutureTime.set(Calendar.SECOND, 0);
                long futureTime = calFutureTime.getTimeInMillis();

                editorSPref.putLong(Constants.COMMON_SPREF_RATING_DIALOG_TIME, futureTime);
                editorSPref.commit();

                if (ratingCheckBox.isChecked()) {
                    editorSPref.putBoolean(Constants.COMMON_SPREF_RATING_DIALOG_DONT_SHOW_BOOLEAN, true);
                    editorSPref.commit();
                }

                bottomSheetDialog.dismiss();
            }
        });
    }


    private void shineEffectStart() {
        Animation shiningAnimation = new TranslateAnimation(
                0,
                binding.ratingButtonLayout.getWidth() + binding.shiningEffectImage.getWidth(),
                0,
                0);

        shiningAnimation.setDuration(2000);
        shiningAnimation.setFillAfter(false);
        shiningAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        binding.shiningEffectImage.startAnimation(shiningAnimation);
    }


    public void loadBannerAdFrom() {
        if (UserContext.getIsAdmobEnabled()) {
            loadAdmobBanner();
        }
        if (UserContext.getIsTapdaqEnabled()) {
            loadTapdaqBanner();
        }
        if (UserContext.getIsApplovinEnabled()) {
            loadApplovinBanner();
        }
        if (UserContext.getIsUnityEnabled()) {
            loadUnityBanner();
        }
        if (UserContext.getIsIronsource_banner()) {
            loadIronsourceBanner();
        }
    }

    private void loadUnityBanner() {
/**** Unity Ads ****/
        unityBanner = new BannerView(this, unityAdUnitId, new UnityBannerSize(320, 50));
        binding.mainAdContainer.addView(unityBanner);
        unityBanner.load();
    }

    public void loadAdmobBanner() {
        admobBanner = new AdView(this);
        binding.mainAdContainer.addView(admobBanner);
        admobBanner.setAdUnitId(getString(R.string.admob_default_banner));
        AdSize adaptiveSize = getAdSize();
        admobBanner.setAdSize(adaptiveSize);
        AdRequest adRequest = new AdRequest.Builder().build();
        admobBanner.loadAd(adRequest);
    }

    private void loadTapdaqBanner() {
        tapdaqBanner = new TMBannerAdView(this);
        binding.mainAdContainer.addView(tapdaqBanner);
        tapdaqBanner.load(this, getString(R.string.tapdaq_banner_main), TMBannerAdSizes.STANDARD, new TMAdListener());
    }

    private void loadApplovinBanner() {
            applovinBanner = new MaxAdView(getString(R.string.appl_banner_default), MainActivity.this);

            // Stretch to the width of the screen for banners to be fully functional
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            // Get the adaptive banner height.
            int heightDp = MaxAdFormat.BANNER.getAdaptiveSize(MainActivity.this).getHeight();
            int heightPx = AppLovinSdkUtils.dpToPx(MainActivity.this, heightDp);
            applovinBanner.setLayoutParams(new FrameLayout.LayoutParams(width, heightPx));
            applovinBanner.setExtraParameter("adaptive_banner", "true");
            // Set background or background color for banners to be fully functional
            // Load the ad
            binding.mainAdContainer.addView(applovinBanner);
            applovinBanner.loadAd();
    }

    private void loadIronsourceBanner() {
//        IronSource.init(this, getString(R.string.ironSource_app_id), IronSource.AD_UNIT.BANNER);
        ironSourceBanner = IronSource.createBanner(MainActivity.this, ISBannerSize.BANNER);
        binding.mainAdContainer.addView(ironSourceBanner);

        binding.mainAdContainer.setVisibility(View.VISIBLE);
        ironSourceBanner.setBannerListener(new IronsourceBannerListener(this));
        IronSource.loadBanner(ironSourceBanner);
    }

    private AdSize getAdSize() {
        // Step 2 - Determine the screen width (less decorations) to use for the ad width.
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);

        // Step 3 - Get adaptive ad size and return for setting on the ad view.
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
    }

    /**
     * * * * * Interstitial
     ***/
    private void loadMainAct_interstitialFrom() {
        if (isAdmobInterstitialEnabled || isTapdaqInterstitialEnabled || isAppLovinInterstitialEnabled) {
            if (admobInterstitial != null || isTapdaqIntersVideoReady() || isAppLovinIntertitialReady()) {
                // Do Nothing
                // Don't load ad again
            } else {
                if (UserContext.getIsAdmobInterstitialEnabled()) {
                    loadAdmobInterstitial();
                }
                if (UserContext.getIsTapdaqInterstitialEnabled()) {
                    loadTapdaqIntersVideo();
                }
                if (UserContext.getIsApplovinInterstitial()) {
                    loadAppLovinInterstitial();
                }
            }
        }
//        else {
//            openNextActivity();
//        }
    }

    public void showMainAct_interstitialFrom() {
        if (isAdmobInterstitialEnabled || isTapdaqInterstitialEnabled || isAppLovinInterstitialEnabled) {
            if (admobInterstitial != null || isTapdaqIntersVideoReady() || isAppLovinIntertitialReady()) {
                if (isTapdaqIntersVideoReady()) {
                    showTapdaqIntersVideo();
                }
                if (admobInterstitial != null) {
                    showAdmobInterstitial();
                }
                if (isAppLovinIntertitialReady()) {
                    showAppLovinInterstitial();
                }
            } else {
                openNextActivity();
            }
        } else {
            openNextActivity();
        }
    }

    /**
     * Tapdaq Intertitial
     **/
    private void loadTapdaqIntersVideo() {
//        showProgressDialog();
        Tapdaq.getInstance().loadVideo(this, getString(R.string.tapdaq_interstitial_default), new TapdaqInterstitialVideoListener());
    }

    private boolean isTapdaqIntersVideoReady() {
        boolean isReady = Tapdaq.getInstance().isVideoReady(this, getString(R.string.tapdaq_interstitial_default));
        return isReady;
    }

    private void showTapdaqIntersVideo() {
        Tapdaq.getInstance().showVideo(this, getString(R.string.tapdaq_interstitial_default), new TapdaqInterstitialVideoListener());
    }

    private class TapdaqInterstitialVideoListener extends TMAdListener {
        @Override
        public void didLoad() {
            super.didLoad();
        }

        @Override
        public void didFailToLoad(TMAdError error) {
            super.didFailToLoad(error);
            openNextActivity();
        }

        @Override
        public void didClose() {
            loadTapdaqIntersVideo();
            openNextActivity();
            super.didClose();
        }
    }

    /**
     * Admob Interstitial
     **/
    private void loadAdmobInterstitial() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this, getString(R.string.admob_default_interstitial), adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                admobInterstitial = interstitialAd;
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                admobInterstitial = null;
                openNextActivity();
            }
        });
    }

    private void showAdmobInterstitial() {
        if (admobInterstitial != null) {
            admobInterstitial.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    admobInterstitial = null;
                }

                @Override
                public void onAdShowedFullScreenContent() {
                }

                @Override
                public void onAdDismissedFullScreenContent() {
                    admobInterstitial = null;
                    loadAdmobInterstitial();
                    openNextActivity();
                }
            });
            admobInterstitial.show(this);
        }
    }

    /**
     * AppLovin Intertitial
     **/
    private void loadAppLovinInterstitial() {
        applovinInterstitial.loadAd();
    }

    private void showAppLovinInterstitial() {
        applovinInterstitial.showAd();
    }

    private boolean isAppLovinIntertitialReady() {
        return applovinInterstitial.isReady();
    }

    private class AppLovinInterstitialAdListener implements MaxAdListener {
        @Override
        public void onAdLoaded(MaxAd ad) {
        }
        @Override
        public void onAdDisplayed(MaxAd ad) {
        }
        @Override
        public void onAdHidden(MaxAd ad) {
            loadAppLovinInterstitial();
            openNextActivity();
        }
        @Override
        public void onAdClicked(MaxAd ad) {
        }
        @Override
        public void onAdLoadFailed(String adUnitId, MaxError error) {
            openNextActivity();
        }
        @Override
        public void onAdDisplayFailed(MaxAd ad, MaxError error) {
        }
    }

    private void openNextActivity() {
        if (s0s1s2p3b4s5 == 0) {
            Intent intent = new Intent(MainActivity.this, ShabadActivity.class);
            Toast.makeText(this, "Please wait. Loading....", Toast.LENGTH_LONG).show();
            startActivity(intent);
        }
        if (s0s1s2p3b4s5 == 1) {
            Intent intent = new Intent(MainActivity.this, SatsangActivity.class);
            Toast.makeText(this, "Please wait. Loading....", Toast.LENGTH_LONG).show();
            startActivity(intent);
        }
        if (s0s1s2p3b4s5 == 2) {
            Intent intent = new Intent(MainActivity.this, StoryActivity.class);
            Toast.makeText(this, "Please wait. Loading....", Toast.LENGTH_LONG).show();
            startActivity(intent);
        }
        if (s0s1s2p3b4s5 == 3) {
            Intent intent = new Intent(MainActivity.this, PhotoActivity.class);
            Toast.makeText(this, "Please wait. Loading....", Toast.LENGTH_LONG).show();
            startActivity(intent);
        }
        if (s0s1s2p3b4s5 == 4) {
            Intent intent = new Intent(MainActivity.this, BookActivity.class);
            Toast.makeText(this, "Please wait. Loading....", Toast.LENGTH_LONG).show();
            startActivity(intent);
        }
        if (s0s1s2p3b4s5 == 5) {
            showToastComingSoon();
        }
    }

    private void showToastComingSoon() {
        View rootView1 = LayoutInflater.from(this).inflate(R.layout.layout_toast_custom,
                (ConstraintLayout) findViewById(R.id.custom_toast_constraint));

        CardView toastCardView = rootView1.findViewById(R.id.toast_custom_card_view);
        TextView toastText = rootView1.findViewById(R.id.toast_custom_text);
        ImageView toastIcon = rootView1.findViewById(R.id.toast_custom_image);

        toastCardView.setBackgroundResource(R.drawable.bg_custom_toast);
        toastText.setText("COMING SOON");
        toastIcon.setImageResource(R.drawable.coming_soon);

        Toast toast = new Toast(this);
        toast.setGravity(Gravity.CENTER, 0, 500);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(rootView1);

        toast.show();
    }

    @Override
    protected void onPause() {
        if (admobBanner != null) {
            admobBanner.pause();
        }

        IronSource.onPause(this);
        IronSource.destroyBanner(ironSourceBanner);
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (admobBanner != null) {
            admobBanner.resume();
        }

        IronSource.onResume(this);
        loadBannerAdFrom();

//        loadAdmobInterstitial();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (admobBanner != null) {
            admobBanner.destroy();
        }
        if (unityBanner != null) {
            unityBanner.destroy();
        }
        if (tapdaqBanner != null) {
            tapdaqBanner.destroy(this);
        }

        IronSource.destroyBanner(ironSourceBanner);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        customExitDialog();
    }

    private void customExitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_dialog_exit,
                (ConstraintLayout) findViewById(R.id.layout_exit_dialog_container));
        builder.setView(view);
        builder.setCancelable(false);

        AlertDialog alertDialog = builder.create();

        Button exitCancelButton = view.findViewById(R.id.exit_cancel_button);
        Button exitButton = view.findViewById(R.id.exit_button);

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                finish();
                finishAffinity();
                System.exit(0);
            }
        });

        exitCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.show();
    }


    private void showNoInternetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_dialog_network,
                (ConstraintLayout) findViewById(R.id.layout_network_dialog_container));
        builder.setView(view);

        builder.setCancelable(false);
        AlertDialog alertDialog = builder.create();

        Button networkExit = view.findViewById(R.id.network_exit_butn);
        Button networkConnect = view.findViewById(R.id.network_connect_butn);

        networkConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.ACTION_NETWORK_OPERATOR_SETTINGS));
            }
        });

        networkExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                System.exit(0);
            }
        });
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.show();
    }

    private void checkAppUpdate() {
        ForceUpdateChecker.with(this).onUpdateNeeded(this::onUpdateNeeded).check();
    }

    //    @Override
    public void onUpdateNeeded(final String updateUrl, String remote_message_hi, String remote_message_en) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View rootView1 = LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_dialog_force_update,
                (ConstraintLayout) findViewById(R.id.constraint_dialog_force_update));
        builder.setView(rootView1);

        builder.setCancelable(false);
        AlertDialog alertDialog = builder.create();

        TextView dialog_message_hi = rootView1.findViewById(R.id.force_update_dialog_message_hi);
        TextView dialog_message_en = rootView1.findViewById(R.id.force_update_dialog_message_en);
        View updateButton = rootView1.findViewById(R.id.forceUpdate_dialog_button);

        dialog_message_hi.setText(remote_message_hi);
        dialog_message_en.setText(remote_message_en);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.show();
    }

    /*****Checking Internet Connectivity****/
    private boolean internetConnected(MainActivity mainActivity) {

        connectivityManager = (ConnectivityManager) mainActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiConnection = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileDataConnection = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if ((wifiConnection != null && wifiConnection.isConnected()
                || (mobileDataConnection != null && mobileDataConnection.isConnected()))) {
            return true;
        } else {
            return false;
        }
    }

    /*****Checking Internet Connectivity****/

    private void showProgressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View rootView1 = LayoutInflater.from(this).inflate(R.layout.layout_dialog_progress,
                (ConstraintLayout) findViewById(R.id.dialog_progress_constraint));
        builder.setView(rootView1);
        builder.setCancelable(false);

        progressDialog = builder.create();

        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressDialog.show();
    }

}
