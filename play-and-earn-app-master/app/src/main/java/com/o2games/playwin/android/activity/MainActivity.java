package dummydata.android.activity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdFormat;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.applovin.mediation.ads.MaxRewardedAd;
import com.applovin.sdk.AppLovinSdkUtils;
import com.appodeal.ads.Appodeal;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.review.model.ReviewErrorCode;
import com.google.android.play.core.review.testing.FakeReviewManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.ironsource.mediationsdk.IronSource;
import dummydata.android.Constants;
import dummydata.android.FirebaseDataService;
import dummydata.android.Game;
import dummydata.android.R;
import dummydata.android.adapter.MainFragmentAdapter;
import dummydata.android.databinding.ActivityMainBinding;
import dummydata.android.fragment.HomeFragment;
import dummydata.android.model.LeaderboardModel;
import dummydata.android.model.User;
import dummydata.android.model.UserWalletDataModel;
import dummydata.android.model.WinnerDataModel;
import dummydata.android.userData.UserContext;
import dummydata.android.versionChecker.ForceUpdateChecker;
import com.tapjoy.TJActionRequest;
import com.tapjoy.TJError;
import com.tapjoy.TJPlacement;
import com.tapjoy.TJPlacementListener;
import com.tapjoy.Tapjoy;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompat {

    public static final int UPDATE_REQUEST_CODE = 100;
    private AppUpdateManager appUpdateManager;
    private static MainActivity instance;
    private ConnectivityManager connectivityManager;
    private BottomSheetDialog pastWinner_bsDialog;
    private WinnerDataModel winnerDataModel;
    private int winnerDataPosition;
    boolean doubleBackToExit = false;
    private static final String TAG = "papa";
    private final FirebaseDataService firebaseDataService = new FirebaseDataService(this);
    public static final String sql_Total_CashCoinsCOL = Game.TOTAL_CASH_COINS.getId();
    private FirebaseAuth firebaseUser;
    DatabaseReference databaseRef;
    boolean autoCleanLeaderboard;
    public static long LEADERBOARD_DELETION_FIREBASE_FUTURE_TIME;


    private boolean isAppLovinBannerEnabled;
    private boolean isAppLovinInterstitialEnabled;
    private boolean isAppLovinRewardedEnabled;
    private static String APPLOVIN_BANNER_ID;
    private static String APPLOVIN_INTERSTITIAL_ID;
    private static String APPLOVIN_REWARDED_ID;
    private MaxAdView applovinBanner;
    private MaxInterstitialAd applovinInterstitial;
    private MaxRewardedAd applovinRewarded;

    private boolean isAppodealBannerEnabled;
    private boolean isAppodealInterstitialEnabled;

    private boolean isTapjoyOfferwallEnabled;
    private TJPlacement tapjoyOfferwall;

    ActivityMainBinding binding;
    FirebaseRemoteConfig remoteConfig;
    ViewPager2 viewPager_main;
    BottomNavigationView bottomMenu;

    private static final boolean isDailyWinner = UserContext.getIsDailyWinnerLeaderboard();
    private static final boolean isWeeklyWinner = UserContext.getIsWeeklyWinnerLeaderboard();

    Handler handler;
    SharedPreferences writeSPref;
    SharedPreferences.Editor editorSPref;
    SharedPreferences readSPref;

    public static MainActivity GetInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        appUpdateManager = AppUpdateManagerFactory.create(this);
        instance = this;


        firebaseUser = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference();

        remoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings remoteConfigSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(0)
                .build();
        remoteConfig.setConfigSettingsAsync(remoteConfigSettings);

        handler = new Handler(Looper.getMainLooper());
        autoCleanLeaderboard = UserContext.getAutoCleanLeaderboard();

        writeSPref = getSharedPreferences(Constants.SHARED_PREF_COMMON, Context.MODE_PRIVATE);
        editorSPref = writeSPref.edit();
        readSPref = getSharedPreferences(Constants.SHARED_PREF_COMMON, Context.MODE_PRIVATE);
        isAppLovinBannerEnabled = UserContext.getIsAppLovin_banner();
        isAppLovinInterstitialEnabled = UserContext.getIsAppLovin_interstitial();
        isAppLovinRewardedEnabled = UserContext.getIsAppLovin_rewarded();
        isTapjoyOfferwallEnabled = UserContext.getIsTapjoy_offerwall();
        isAppodealBannerEnabled = UserContext.getIsAppodeal_banner();
        isAppodealInterstitialEnabled = UserContext.getIsAppodeal_interstitial();
        APPLOVIN_BANNER_ID = getString(R.string.aL_banner_default);
        APPLOVIN_INTERSTITIAL_ID = getString(R.string.aL_interstitial_default);
        applovinInterstitial = new MaxInterstitialAd(APPLOVIN_INTERSTITIAL_ID, this);
        APPLOVIN_REWARDED_ID = getString(R.string.aL_rewarded_default);
        tapjoyOfferwall = Tapjoy.getPlacement(getString(R.string.tJ_offerwall_default), new TJOfferwallListener());


        showBannerFrom();
        loadOfferwallFrom();
        loadMainAct_interstitialFrom();
        continuousListenerForFirebaseData();

        Date dateTime = new Date(System.currentTimeMillis());
        long currentTime = dateTime.getTime();

        /**
         * Handle leaderboard deletion **/
        handleAutoCleanLeaderboard(currentTime);
        /** Handle leaderboard deletion
         **/

        if (currentTime >= getRatingBsDialogFutureTime()) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showRatingBsDialog();
                }
            }, 2500);
        }

        viewPager_main = findViewById(R.id.viewPager_main_activity);
        bottomMenu = findViewById(R.id.bottom_menu);

        FragmentManager fragmentManager = getSupportFragmentManager();
        MainFragmentAdapter mainFragmentAdapter = new MainFragmentAdapter(fragmentManager, getLifecycle());
        viewPager_main.setAdapter(mainFragmentAdapter);

        bottomMenu.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.home:
                        viewPager_main.setCurrentItem(0);
                        break;
                    case R.id.leaderboard:
                        viewPager_main.setCurrentItem(1);
                        break;
                    case R.id.offerwall_is:
                        viewPager_main.setCurrentItem(2);
                        break;
                    case R.id.wallet:
                        viewPager_main.setCurrentItem(3);
                        break;
                    case R.id.user_profile:
                        viewPager_main.setCurrentItem(4);
                }
                return true;
            }
        });

        viewPager_main.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        bottomMenu.getMenu().findItem(R.id.home).setChecked(true);
                        break;
                    case 1:
                        bottomMenu.getMenu().findItem(R.id.leaderboard).setChecked(true);
                        break;
                    case 2:
                        bottomMenu.getMenu().findItem(R.id.offerwall_is).setChecked(true);
                        break;
                    case 3:
                        bottomMenu.getMenu().findItem(R.id.wallet).setChecked(true);
                        break;
                    case 4:
                        bottomMenu.getMenu().findItem(R.id.user_profile).setChecked(true);
                        break;
                }
            }
        });

        remoteConfig.fetchAndActivate().addOnCompleteListener(new OnCompleteListener<Boolean>() {
            @Override
            public void onComplete(@NonNull Task<Boolean> task) {
                if (task.isSuccessful()) {
                    boolean showImpNotice = remoteConfig.getBoolean(Constants.SHOW_HOME_IMP_NOTICE_DIALOG);

                    if (showImpNotice) {
                        String dialog_title = remoteConfig.getString(Constants.GET_HOME_IMP_NOTICE_TITLE);
                        String dialog_msg_eng = remoteConfig.getString(Constants.GET_HOME_IMP_NOTICE_TITLE_ENG);
                        String dialog_msg_hindi = remoteConfig.getString(Constants.GET_HOME_IMP_NOTICE_TITLE_HI);

                        showImportantNoticeDialog(dialog_title, dialog_msg_eng, dialog_msg_hindi);
                    }
                } else {
                }
            }
        });


        checkUpdate();
    }

    private void checkUpdate() {
        ForceUpdateChecker.with(this).onUpdateNeeded(this::onUpdateNeeded).check();

        if (UserContext.getGoogleForceUpdate()) {
            // Returns an intent object that you use to check for an update.
            com.google.android.play.core.tasks.Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
            appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                        && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                    try {
                        appUpdateManager.startUpdateFlowForResult(
                                appUpdateInfo, AppUpdateType.IMMEDIATE, this, UPDATE_REQUEST_CODE);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UPDATE_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                checkUpdate();
            }
        }
    }

    private void handleImmediateUpdate() {
        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                try {
                    appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo, AppUpdateType.IMMEDIATE, this, UPDATE_REQUEST_CODE);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void onUpdateNeeded(final String updateUrl) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View rootView1 = LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_dialog_force_update,
                (ConstraintLayout) findViewById(R.id.constraint_dialog_force_update));
        builder.setView(rootView1);

        builder.setCancelable(false);
        AlertDialog alertDialog = builder.create();

        View updateButton = rootView1.findViewById(R.id.forceUpdate_dialog_button);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectStore(updateUrl);
            }
        });

        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.show();
    }

    private void redirectStore(String updateUrl) {
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    private void showRatingBsDialog() {
        BottomSheetDialog ratingBsDialog = new BottomSheetDialog(this, R.style.bottomSheetDialog);
        ratingBsDialog.setContentView(R.layout.layout_bsdialog_rating_playstore);
        ratingBsDialog.getDismissWithAnimation();
        ratingBsDialog.setCancelable(false);
        ratingBsDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        ratingBsDialog.show();

        RatingBar ratingBar = ratingBsDialog.findViewById(R.id.rating_dialog_ratingBar);
        CheckBox dontShowCheckBox = ratingBsDialog.findViewById(R.id.rating_dialog_checkBox);
        FrameLayout closeBtn = ratingBsDialog.findViewById(R.id.upstox_dialog_ac_open_btn);

        ratingBar.setRating(1);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.APP_PLAYSTORE_LINK)));
                        ratingBsDialog.dismiss();
                    }
                }, 500);
            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dontShowCheckBox.isChecked()) {
                    disableRatingBsFor(168);
                } else {
                    disableRatingBsFor(24);
                }
                ratingBsDialog.dismiss();
            }
        });
    }

    private long getRatingBsDialogFutureTime() {
        long ratingBsDialogFutureTime = readSPref.getLong(Constants.SP_RATING_BSDIALOG_FUTURE_TIME, 0);
        return ratingBsDialogFutureTime;
    }

    private void disableRatingBsFor(int daysDisabled) {
        Date dateTime = new Date(System.currentTimeMillis());
        long btnCurrentTime = dateTime.getTime();

        Calendar calFutureTime = Calendar.getInstance();
        calFutureTime.setTimeInMillis(btnCurrentTime);
        calFutureTime.add(Calendar.DAY_OF_YEAR, daysDisabled);
        calFutureTime.set(Calendar.SECOND, 0);
        long btnEnable_futureTime = calFutureTime.getTimeInMillis();

        editorSPref.putLong(Constants.SP_RATING_BSDIALOG_FUTURE_TIME, btnEnable_futureTime);
        editorSPref.commit();
    }

    private void showImportantNoticeDialog(String dialog_title, String dialog_msg_eng, String dialog_msg_hindi) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View rootView1 = LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_dialog_important_notice,
                (ConstraintLayout) findViewById(R.id.constraint_dialog_important_notice));
        builder.setView(rootView1);
        builder.setCancelable(false);

        AlertDialog impNoticeDialog = builder.create();

        TextView title = rootView1.findViewById(R.id.dialog_imp_notice_title);
        TextView eng_msg = rootView1.findViewById(R.id.dialog_imp_notice_message_eng);
        TextView hindi_msg = rootView1.findViewById(R.id.dialog_imp_notice_message_hindi);
        Button closeBtn = rootView1.findViewById(R.id.dialog_imp_notice_close_btn);

        title.setText(dialog_title);
        eng_msg.setText(dialog_msg_eng);
        hindi_msg.setText(dialog_msg_hindi);

        if (TextUtils.isEmpty(dialog_msg_hindi)) {
            hindi_msg.setVisibility(View.GONE);
        }

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                impNoticeDialog.dismiss();
            }
        });

        impNoticeDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        impNoticeDialog.show();
    }

    private void showBannerFrom() {
        if (isAppLovinBannerEnabled || isAppodealBannerEnabled) {

            if (isAppLovinBannerEnabled) {
                binding.mainAdmobAdView.setVisibility(View.VISIBLE);
                applovinBanner = new MaxAdView(APPLOVIN_BANNER_ID, MainActivity.this);
                int width = ViewGroup.LayoutParams.MATCH_PARENT;
                int heightDp = MaxAdFormat.BANNER.getAdaptiveSize(MainActivity.this).getHeight();
                int heightPx = AppLovinSdkUtils.dpToPx(MainActivity.this, heightDp);
                applovinBanner.setLayoutParams(new FrameLayout.LayoutParams(width, heightPx));
                applovinBanner.setExtraParameter("adaptive_banner", "true");
                binding.mainAdmobAdView.addView(applovinBanner);
                applovinBanner.loadAd();
            }

            if (isAppodealBannerEnabled) {
                binding.mainAppodealBannerView.setVisibility(View.VISIBLE);
                Appodeal.setBannerViewId(R.id.main_appodealBannerView);
                Appodeal.show(this, Appodeal.BANNER_VIEW);
            }
        }
    }

    private AdSize getAdSize() {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
    }

    private long getRefreshLeaderboardTime() {
        long refreshLeaderboardTime = readSPref.getLong(Constants.SP_REFRESH_LEADERBOARD_TIME, 0);
        return refreshLeaderboardTime;
    }

    private void refreshLeaderboardAfter30() {
        Date dateTime = new Date(System.currentTimeMillis());
        long btnCurrentTime = dateTime.getTime();

        Calendar calFutureTime = Calendar.getInstance();
        calFutureTime.setTimeInMillis(btnCurrentTime);
        calFutureTime.add(Calendar.MINUTE, 30);
        calFutureTime.set(Calendar.SECOND, 0);
        long refreshFutureTime = calFutureTime.getTimeInMillis();

        editorSPref.putLong(Constants.SP_REFRESH_LEADERBOARD_TIME, refreshFutureTime);
        editorSPref.commit();
    }

    /**
     * Leaderboard Auto Deletion Starts
     **/
    private void handleAutoCleanLeaderboard(long currentTime) {
    }

    private void cleanLeaderboard() {
        if (autoCleanLeaderboard) {
            databaseRef
                    .child(Constants.LEADERBOARD_TABLE)
                    .removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(@NonNull @NotNull Void unused) {
                            Date dateTime = new Date(System.currentTimeMillis());
                            long userTime_leaderboardDeletion = dateTime.getTime();

                            int clean_leaderboard_interval = (int) UserContext.getClean_leaderboard_interval();

                            Calendar calFutureTime = Calendar.getInstance();
                            calFutureTime.setTimeInMillis(userTime_leaderboardDeletion);
                            calFutureTime.add(Calendar.HOUR, clean_leaderboard_interval);

                            long dataDeletion_futureTime = calFutureTime.getTimeInMillis();

                            databaseRef
                                    .child(Constants.LEADERBOARD_DELETION_FUTURE_TIME)
                                    .setValue(dataDeletion_futureTime);

                            UserContext.setLeaderboardDeletion_futureTime(dataDeletion_futureTime);
                            setLeaderboardDeletion_sPrefFutureTime(dataDeletion_futureTime);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {

                        }
                    });
        }
    }

    private void getLeaderboardDeletion_firebaseFutureTime() {
        if (autoCleanLeaderboard) {
            databaseRef
                    .child(Constants.LEADERBOARD_DELETION_FUTURE_TIME)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                            if (snapshot.getValue() == null) {
                                Date dateTime = new Date(System.currentTimeMillis());
                                long currentTime = dateTime.getTime();

                                Calendar calFutureTime = Calendar.getInstance();
                                calFutureTime.setTimeInMillis(currentTime);
                                calFutureTime.add(Calendar.HOUR, 1);

                                long self_future_time = calFutureTime.getTimeInMillis();

                                UserContext.setLeaderboardDeletion_futureTime(self_future_time);
                                setLeaderboardDeletion_sPrefFutureTime(self_future_time);
                            } else {
                                long dataDeletion_futureTime = Long.parseLong(String.valueOf(snapshot.getValue()));

                                LEADERBOARD_DELETION_FIREBASE_FUTURE_TIME = dataDeletion_futureTime;
                                UserContext.setLeaderboardDeletion_futureTime(dataDeletion_futureTime);
                                setLeaderboardDeletion_sPrefFutureTime(dataDeletion_futureTime);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                        }
                    });
        }
    }

    private long getLeaderboardDeletion_sPrefFutureTime() {
        return readSPref.getLong(Constants.LEADERBOARD_DELETION_FUTURE_TIME, 0);
    }

    private void setLeaderboardDeletion_sPrefFutureTime(long dataDeletion_futureTime) {
        editorSPref.putLong(Constants.LEADERBOARD_DELETION_FUTURE_TIME, dataDeletion_futureTime);
        editorSPref.commit();
    }
    private void loadMainAct_interstitialFrom() {
        if (isAppLovinInterstitialEnabled || isAppodealInterstitialEnabled) {
            if (isAppLovinIntertitialReady() || Appodeal.isLoaded(Appodeal.INTERSTITIAL)) {
            } else {
                if (isAppLovinInterstitialEnabled) {
                    loadAppLovinInterstitial();
                }
                if (isAppodealInterstitialEnabled) {
                }
            }
        }
    }

    public void showMainAct_interstitialFrom() {
        if (isAppLovinInterstitialEnabled || isAppodealInterstitialEnabled) {
            if (isAppLovinIntertitialReady() || Appodeal.isLoaded(Appodeal.INTERSTITIAL)) {
                if (isAppLovinIntertitialReady()) {
                    showAppLovinInterstitial();
                }
                if (Appodeal.isLoaded(Appodeal.INTERSTITIAL)) {
                    Appodeal.show(this, Appodeal.INTERSTITIAL);
                }
            } else {
                showCustomToast(getString(R.string.game_no_ad_available));
            }
        }
    }


    /**
     * AppLovin Intertitial
     **/
    private void loadAppLovinInterstitial() {
        applovinInterstitial.setListener(new AppLovinInterstitialAdListener());
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
        }
        @Override
        public void onAdClicked(MaxAd ad) {
        }
        @Override
        public void onAdLoadFailed(String adUnitId, MaxError error) {
        }
        @Override
        public void onAdDisplayFailed(MaxAd ad, MaxError error) {
        }
    }


    private void showCustomToast(String message) {
        LayoutInflater inflater = getLayoutInflater();
        View rootView1 = inflater.inflate(R.layout.layout_toast_custom,
                (ConstraintLayout) findViewById(R.id.custom_toast_constraint));
        TextView toastText = rootView1.findViewById(R.id.custom_toast_text);
        toastText.setText(message);
        Toast toast = new Toast(this);
        toast.setGravity(Gravity.BOTTOM, 0, 100);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(rootView1);
        toast.show();
    }

    private void continuousListenerForFirebaseData() {
        getNextWinnerAnnouncementTime();
        databaseRef
                .child(Constants.LEADERBOARD_TABLE)
                .child(firebaseUser.getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        LeaderboardModel userLeaderDataModel = snapshot.getValue(LeaderboardModel.class);
                        User loggedInUser = UserContext.getLoggedInUser();

                        if (userLeaderDataModel != null) {
                            firebaseDataService.updateSqlCoinData(userLeaderDataModel.getUserTotalCOIN());
                        } else {
                            if (Long.parseLong(firebaseDataService.getCoinBalance()) <= 0) {
                                LeaderboardModel leaderboardDataModel = new LeaderboardModel(loggedInUser.getId(), loggedInUser.getAuthUid(),
                                        loggedInUser.getUserName(), loggedInUser.getUserPhotoUrl(),
                                        0);
                                databaseRef
                                        .child(Constants.LEADERBOARD_TABLE)
                                        .child(loggedInUser.getAuthUid())
                                        .setValue(leaderboardDataModel);
                            } else {
                                long userSQLCoins = Long.parseLong(firebaseDataService.getCoinBalance());

                                LeaderboardModel leaderboardDataModel = new LeaderboardModel(loggedInUser.getId(), loggedInUser.getAuthUid(),
                                        loggedInUser.getUserName(), loggedInUser.getUserPhotoUrl(),
                                        userSQLCoins);
                                databaseRef
                                        .child(Constants.LEADERBOARD_TABLE)
                                        .child(loggedInUser.getAuthUid())
                                        .setValue(leaderboardDataModel);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                    }
                });

        databaseRef
                .child(Constants.USER_WALLET)
                .child(firebaseUser.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        UserWalletDataModel userWalletDataModel = snapshot.getValue(UserWalletDataModel.class);
                        User loggedInUser = UserContext.getLoggedInUser();

                        if (userWalletDataModel != null) {
                            firebaseDataService.updateSqlCashData(userWalletDataModel.getWalletAmount());
                        } else {
                            UserWalletDataModel walletDataModel = new UserWalletDataModel(
                                    loggedInUser.getId(), loggedInUser.getAuthUid(), 0);
                            databaseRef
                                    .child(Constants.USER_WALLET)
                                    .child(loggedInUser.getAuthUid())
                                    .setValue(walletDataModel);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                    }
                });
    }

    private void getNextWinnerAnnouncementTime() {
        long getNextWinnerAnnouncementTime = readSPref.getLong(Constants.NEXT_WINNER_ANNOUNCEMENT_TIME, 0);
        UserContext.setNextWinnerAnnouncementTime(getNextWinnerAnnouncementTime);

        Date dateTime = new Date(System.currentTimeMillis());
        long currentTime = dateTime.getTime();

        if (isDailyWinner) {
            if (currentTime >= getNextWinnerAnnouncementTime) {
                databaseRef
                        .child(Constants.WINNER_LIST)
                        .child(Constants.WINNER_LIST_DAILY_LEADERBOARD)
                        .child(Constants.NEXT_WINNER_ANNOUNCEMENT_TIME)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                if (snapshot.getValue() == null) {

                                } else {
                                    long nextWinnerAnnouncementTime = Long.parseLong(String.valueOf(snapshot.getValue()));

                                    editorSPref.putLong(Constants.NEXT_WINNER_ANNOUNCEMENT_TIME, nextWinnerAnnouncementTime);
                                    editorSPref.commit();
                                    UserContext.setNextWinnerAnnouncementTime(nextWinnerAnnouncementTime);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                            }
                        });
            } else {
                UserContext.setNextWinnerAnnouncementTime(getNextWinnerAnnouncementTime);
            }
        }

        if (isWeeklyWinner) {
            if (currentTime >= getNextWinnerAnnouncementTime) {
                //** Current time is more than next winner time **/
                databaseRef
                        .child(Constants.WINNER_LIST)
                        .child(Constants.WINNER_LIST_WEEKLY_LEADERBOARD)
                        .child(Constants.NEXT_WINNER_ANNOUNCEMENT_TIME)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                if (snapshot.getValue() == null) {

                                } else {
                                    long nextWinnerAnnouncementTime = Long.parseLong(String.valueOf(snapshot.getValue()));

                                    editorSPref.putLong(Constants.NEXT_WINNER_ANNOUNCEMENT_TIME, nextWinnerAnnouncementTime);
                                    editorSPref.commit();
                                    UserContext.setNextWinnerAnnouncementTime(nextWinnerAnnouncementTime);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                            }
                        });
            } else {
                UserContext.setNextWinnerAnnouncementTime(getNextWinnerAnnouncementTime);
            }
        }
    }

    /**
     * Offerwall
     * **/

    public void loadOfferwallFrom() {
        if (isTapjoyOfferwallEnabled) {
            if (isTapjoyOfferwallEnabled) {
                if (Tapjoy.isConnected()) {
                    loadTapjoyOfferwall();
                }
            }
        }
    }
    public void showOfferwallFrom() {
        if (isTapjoyOfferwallEnabled) {
            if (tapjoyOfferwall.isContentAvailable() || IronSource.isOfferwallAvailable()) {

                if (tapjoyOfferwall.isContentReady()) {
                    showTapjoyOfferwall();
                } else {
                    showCustomToast(getString(R.string.main_act_please_wait_loading_offer)); }

            } else {
                showCustomToast(getString(R.string.main_act_no_offer_available));
            }
        } else {
            showCustomToast(getString(R.string.main_act_no_offer_available));
        }
    }

    private void loadTapjoyOfferwall() {
        tapjoyOfferwall.requestContent();
    }
    private void showTapjoyOfferwall() {
        tapjoyOfferwall.showContent();
    }

    public class TJOfferwallListener implements TJPlacementListener {
        @Override
        public void onRequestSuccess(TJPlacement tjPlacement) {
        }
        @Override
        public void onRequestFailure(TJPlacement tjPlacement, TJError tjError) {
        }
        @Override
        public void onContentReady(TJPlacement tjPlacement) {
        }
        @Override
        public void onContentShow(TJPlacement tjPlacement) {
        }
        @Override
        public void onContentDismiss(TJPlacement tjPlacement) {
//            Log.e("TJ Offerwall", " Closed");
            loadTapjoyOfferwall();
        }
        @Override
        public void onPurchaseRequest(TJPlacement tjPlacement, TJActionRequest tjActionRequest, String s) {
        }
        @Override
        public void onRewardRequest(TJPlacement tjPlacement, TJActionRequest tjActionRequest, String s, int i) {
//            firebaseDataService.updateUserCoin(false, binding.dummyTextView, Long.parseLong(String.valueOf(i)));
        }
        @Override
        public void onClick(TJPlacement tjPlacement) {
        }
    }


/*****Checking Internet Connectivity****/
    public boolean internetConnected() {
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiConnection = connectivityManager.getNetworkInfo(connectivityManager.TYPE_WIFI);
        NetworkInfo mobileDataConnection = connectivityManager.getNetworkInfo(connectivityManager.TYPE_MOBILE);

        if ((wifiConnection != null && wifiConnection.isConnected()
                || (mobileDataConnection != null && mobileDataConnection.isConnected()))) {
            return true;
        } else {
            return false;
        }
    }
/*****Checking Internet Connectivity****/

    public void showNoInternetDialog(View rootView1) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        rootView1 = LayoutInflater.from(this).inflate(R.layout.layout_dialog_no_internet,
                (ConstraintLayout) rootView1.findViewById(R.id.constraint_dialog_no_internet));
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

    @Override
    public void onBackPressed() {
        if (viewPager_main.getCurrentItem() != 0) {
            viewPager_main.setCurrentItem(0);
        } else {
            if (doubleBackToExit) {
                super.onBackPressed();
                overridePendingTransition(R.anim.anim_enter_from_left, R.anim.anim_exit_to_left);
                finishAffinity();
                System.exit(0);
            }

            doubleBackToExit = true;
            showCustomToast(getString(R.string.main_act_press_back_to_exit) + " " + getString(R.string.app_name));

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExit = false;
                }
            }, 2000);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        IronSource.onPause(this);
    }

    @Override
    protected void onResume() {
        if (UserContext.getGoogleForceUpdate()) {
            handleImmediateUpdate();
        }

        Date dateTime = new Date(System.currentTimeMillis());
        long currentTime = dateTime.getTime();
//        continuousListenerForFirebaseData();
        getNextWinnerAnnouncementTime();

//        if (currentTime >= getRefreshLeaderboardTime()) {
////            refreshLeaderboardData();
//        }

        Appodeal.show(this, Appodeal.BANNER_VIEW);
        IronSource.onResume(this);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
//        if (tapdaqBanner != null) {
//            tapdaqBanner.destroy(this);
//        }
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        Tapjoy.onActivityStart(MainActivity.this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        Tapjoy.onActivityStop(MainActivity.this);
        super.onStop();
    }
}