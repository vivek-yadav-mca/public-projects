package dummydata.android.activity;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdFormat;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.MaxReward;
import com.applovin.mediation.MaxRewardedAdListener;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.applovin.mediation.ads.MaxRewardedAd;
import com.applovin.mediation.nativeAds.MaxNativeAdLoader;
import com.applovin.sdk.AppLovinSdkUtils;
import com.appodeal.ads.Appodeal;
import com.appodeal.ads.RewardedVideoCallbacks;
import com.google.android.gms.ads.AdSize;
import com.ironsource.mediationsdk.IronSource;
import dummydata.android.AlarmService.AllGameAlarmReciever;
import dummydata.android.App;
import dummydata.android.Constants;
import dummydata.android.FirebaseDataService;
import dummydata.android.Game;
import dummydata.android.R;
import dummydata.android.TapdaqNativeLargeLayout;
import dummydata.android.databinding.ActivityFlipBinding;
import dummydata.android.flipView.FlipView;
import dummydata.android.services.JobNotificationBackground;
import dummydata.android.sqlUserGameData.DBHelper;
import dummydata.android.userData.UserContext;
import com.tapjoy.Tapjoy;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class FlipActivity extends AppCompat {

    ConnectivityManager connectivityManager;
    private static final String TAG = "alti_palti_game";

    private final FirebaseDataService firebaseDataService = new FirebaseDataService(this);
    private static final String flipGameId_enum = Game.NORMAL_FLIP.getId();
    private static final String sqlTotal_CashCoinsCOL = Game.TOTAL_CASH_COINS.getId();
    private static int MAX_AMOUNT_OF_COIN = 25;  // 111
    private static int MIN_AMOUNT_OF_COIN = 5;  // 5

    private NotificationManagerCompat notificationManager;
    int numberGamePLayed = 0;

    private boolean isAppLovinBannerEnabled;
    private boolean isAppLovinInterstitialEnabled;
    private boolean isAppLovinRewardedEnabled;
    private boolean isAppLovinNativeEnabled;
    private static String APPLOVIN_BANNER_ID;
    private static String APPLOVIN_INTERSTITIAL_ID;
    private static String APPLOVIN_REWARDED_ID;
    private static String APPLOVIN_MEDIUM_NATIVE_ID;
    private MaxAdView applovinBanner;
    private MaxInterstitialAd applovinInterstitial;
    private MaxRewardedAd applovinRewarded;
    private MaxNativeAdLoader applovinNativeAdLoader;
    private MaxAd applovinNativeAd;
    private View maxNativeAdView_received;

    private boolean isAppodealBannerEnabled;
    private boolean isAppodealInterstitialEnabled;
    private boolean isAppodealRewardedEnabled;
    private boolean isAppodealNativeEnabled;

    ActivityFlipBinding binding;
    DBHelper dbHelper;
    String flipChanceFromSQL;

    FlipView flipView;
    Random random;
    private long generateCoinsEarned;
    boolean giveDoubleReward = false;
    int randomForDoubleReward;
    View coins_flipView_rootView;
    TextView flip_generated_randon_number;

    Animation scale_up;
    Animation scale_down;
    Handler handler;
    AlertDialog progressDialog;
    private SharedPreferences writeSPref;
    private SharedPreferences readSPref;
    private SharedPreferences.Editor editorSPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flip);
        binding = ActivityFlipBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        notificationManager = NotificationManagerCompat.from(this);

        writeSPref = getSharedPreferences("o2_" + UserContext.getLoggedInUser().getId() + Constants.USER_SPECIFIC, Context.MODE_PRIVATE);
        editorSPref = writeSPref.edit();
        readSPref = getSharedPreferences("o2_" + UserContext.getLoggedInUser().getId() + Constants.USER_SPECIFIC, Context.MODE_PRIVATE);

        dbHelper = new DBHelper(this);
        handler = new Handler(Looper.getMainLooper());
        scale_up = AnimationUtils.loadAnimation(this, R.anim.anim_scale_up);
        scale_down = AnimationUtils.loadAnimation(this, R.anim.anim_scale_down);

        numberGamePLayed = getSPrefGamePlayed();
        checkAndStart_earnBtnTimer();
        chanceChecker();
        updateUIChance();
        updateUICoins();

        MAX_AMOUNT_OF_COIN = UserContext.getMaxInGameCoins();
        MIN_AMOUNT_OF_COIN = UserContext.getMinInGameCoins();

        isAppLovinBannerEnabled = UserContext.getIsAppLovin_banner();
        isAppLovinInterstitialEnabled = UserContext.getIsAppLovin_interstitial();
        isAppLovinRewardedEnabled = UserContext.getIsAppLovin_rewarded();
        isAppLovinNativeEnabled = UserContext.getIsAppLovin_mediumNative();
        isAppodealBannerEnabled = UserContext.getIsAppodeal_banner();
        isAppodealInterstitialEnabled = UserContext.getIsAppodeal_interstitial();
        isAppodealRewardedEnabled = UserContext.getIsAppodeal_rewarded();
        isAppodealNativeEnabled = UserContext.getIsAppodeal_native();

        APPLOVIN_BANNER_ID = getString(R.string.aL_banner_default);
        APPLOVIN_INTERSTITIAL_ID = getString(R.string.aL_interstitial_default);
        applovinInterstitial = new MaxInterstitialAd(APPLOVIN_INTERSTITIAL_ID, this);
        APPLOVIN_REWARDED_ID = getString(R.string.aL_rewarded_default);
        applovinRewarded = MaxRewardedAd.getInstance(APPLOVIN_REWARDED_ID, this);
        applovinRewarded.setListener(new AppLovinRewardedAdListener());
        APPLOVIN_MEDIUM_NATIVE_ID = getString(R.string.aL_native_default_medium);
        applovinNativeAdLoader = new MaxNativeAdLoader(APPLOVIN_MEDIUM_NATIVE_ID, this);

        showBannerAdFrom();
        loadInterstitialFrom();
        loadRewardedAdFrom();
        loadNativeAdFrom();

        coins_flipView_rootView = LayoutInflater.from(FlipActivity.this).inflate(R.layout.layout_flip_view_back_cardview,
                (ConstraintLayout) findViewById(R.id.constraint_flipView_back_cardView));
        flip_generated_randon_number = coins_flipView_rootView.findViewById(R.id.flip_generated_random_number);
        random = new Random();
        generateCoinsEarned = generateRandomCoins();
        flip_generated_randon_number.setText(new StringBuilder().append("  + ").append(generateCoinsEarned).toString());
        flip_generated_randon_number.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_coin_dollar, 0, 0, 0);

        flipView = findViewById(R.id.flipview_flip_win);
        flipView.setOnFlipListener(new FlipView.OnFlipAnimationListener() {
            @Override
            public void onViewFlipCompleted(FlipView flipView, FlipView.FlipState mCurrentSide) {
                chanceChecker();
                if (FlipView.FlipState.BACK_SIDE.equals(mCurrentSide)) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            numberGamePLayed++;
                            updateSharedPrefGamePlayed();
                            randomForDoubleReward = generateRandom();
                            showDialogAfterInterstitial(false, true, generateCoinsEarned);
                        }
                    }, 200);
                }
                flipView.setAutoFlipBack(true);
                flipView.setAutoFlipBackTime(500);
            }
        });

        binding.earnChance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Long.parseLong(flipChanceFromSQL) <= 0) {
                    numberGamePLayed = 0;
                    updateSharedPrefGamePlayed();
                    showRewardedFrom(true, false, 0);
                } else {
                    showCustomToast(getString(R.string.game_you_have_enough_chance));
                }
            }
        });
    }

    private void updateSharedPrefGamePlayed() {
        editorSPref.putInt(Constants.SP_NORMAL_FLIP_AUTO_INTERSTITIAL, numberGamePLayed);
        editorSPref.commit();
    }

    private int getSPrefGamePlayed() {
        int gamePlayedFromSPref = readSPref.getInt(Constants.SP_NORMAL_FLIP_AUTO_INTERSTITIAL, 0);
        return gamePlayedFromSPref;
    }

    private long getSPrefBtnTime() {
        long earnBtnTime = readSPref.getLong(Constants.SP_NORMAL_FLIP_EARN_BTN_TIME, 0);
        return earnBtnTime;
    }

    private long generateRandomCoins() {
        return random.nextInt(MAX_AMOUNT_OF_COIN - MIN_AMOUNT_OF_COIN) + MIN_AMOUNT_OF_COIN;
    }

    private int generateRandom() {
        int randomNo = random.nextInt(11 - 1) + 1;

        if (randomNo <= 4) {
            // less than or equal to 4
            giveDoubleReward = true;
        } else {
            // more than 4
            giveDoubleReward = false;
        }
        return randomNo;
    }

    private void updateUIChance() {
        binding.flipChanceLeft.setText(firebaseDataService.getChanceAvailable(flipGameId_enum));
    }

    private void updateUICoins() {
        binding.flipTotalCoins.setText(firebaseDataService.getCoinBalance());
    }


    private void chanceChecker() {
        flipChanceFromSQL = firebaseDataService.getChanceAvailable(flipGameId_enum);

        if (Integer.parseInt(flipChanceFromSQL) <= 0) {
            binding.flipviewFlipWin.setVisibility(View.INVISIBLE);
            binding.chanceChecker.setVisibility(View.VISIBLE);

            binding.chanceChecker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showCustomToast(getString(R.string.game_watch_ad_to_get_chance));
                    binding.earnChance.startAnimation(scale_up);
                }
            });
        } else {
            binding.flipviewFlipWin.setVisibility(View.VISIBLE);
            binding.chanceChecker.setVisibility(View.GONE);
        }
        if (!internetConnected(this)) {
            showNoInternetDialog();
        }
    }

    private void showCustomToast(String toastMessage) {
        LayoutInflater inflater = getLayoutInflater();
        View rootView1 = inflater.inflate(R.layout.layout_toast_custom,
                (ConstraintLayout) findViewById(R.id.custom_toast_constraint));

        TextView toastText = rootView1.findViewById(R.id.custom_toast_text);
        toastText.setText(toastMessage);
        Toast toast = new Toast(this);
        toast.setGravity(Gravity.BOTTOM, 0, 100);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(rootView1);
        toast.show();
    }

    private void updateChanceLeftAndCoins(boolean updateChance, boolean addChance, boolean updateCoins, long coinsEarned) {
        if (updateChance) {
            firebaseDataService.updateUserChance(flipGameId_enum, addChance);
            chanceChecker();
            updateUIChance();
        }
        if (updateCoins) {
            firebaseDataService.updateUserCoin(true, binding.flipTotalCoins, coinsEarned);
            updateUICoins();
        }
    }

    private void showDialogAfterInterstitial(boolean chanceMsgToBeShown, boolean coinsMsgToBeShown, long coinsEarned) {
        AlertDialog.Builder builder = new AlertDialog.Builder(FlipActivity.this);
        View dialog_rootLayout = LayoutInflater.from(FlipActivity.this).inflate(R.layout.layout_dialog_add_reward_spin_flip_scratch,
                (ConstraintLayout) findViewById(R.id.constraint_dialog_after_interstitial));
        builder.setView(dialog_rootLayout);
        builder.setCancelable(false);

        AlertDialog dialogAfterInterstitial = builder.create();
        dialogAfterInterstitial.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialogAfterInterstitial.show();

        TapdaqNativeLargeLayout tapdaqNativeLayout = dialog_rootLayout.findViewById(R.id.native_ad_container);
        tapdaqNativeLayout.setVisibility(View.GONE);
        FrameLayout applovinNativeFrameLayout = dialog_rootLayout.findViewById(R.id.native_ad_frameLayout);
        applovinNativeFrameLayout.setVisibility(View.GONE);

        TextView dialogTimerText = dialog_rootLayout.findViewById(R.id.dialog_after_interstitial_timer_text);
        TextView dialogEarnMessage = dialog_rootLayout.findViewById(R.id.dialog_after_interstitial_message);
        Button dialogDoubleRewardBtn = dialog_rootLayout.findViewById(R.id.dialog_after_interstitial_double_reward_btn);
        FrameLayout dialogClaimRewardBtn = dialog_rootLayout.findViewById(R.id.dialog_after_interstitial_claim_reward_btn);
        dialogDoubleRewardBtn.setTextColor(getResources().getColor(R.color.white));

        if (chanceMsgToBeShown) {
            dialogTimerText.setVisibility(View.GONE);
            dialogEarnMessage.setText(getString(R.string.dialog_after_interstitial_16_chances));
            dialogDoubleRewardBtn.setText(getString(R.string.dialog_after_interstitial_chance_add_button));

            dialogDoubleRewardBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateChanceLeftAndCoins(true, true, false, 0);
                    dialogAfterInterstitial.dismiss();
                }
            });
        }

        if (coinsMsgToBeShown) {
            try {
                showNativeAdFrom(tapdaqNativeLayout, applovinNativeFrameLayout);
            } catch (Exception e) {
                e.printStackTrace();
            }

            dialogEarnMessage.setText(new StringBuilder().append("  +").append(coinsEarned).toString());
            dialogEarnMessage.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_coin_dollar, 0, 0, 0);

            if (giveDoubleReward) {
                /** Give Double Reward **/
                dialogTimerText.setVisibility(View.GONE);
                dialogDoubleRewardBtn.setEnabled(false);
                new CountDownTimer(5000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        dialogDoubleRewardBtn.setText(new StringBuilder()
                                .append(getString(R.string.dialog_after_interstitial_getting_coins_ready))
                                .append(" ")
                                .append(millisUntilFinished / 1000).append("s").toString());
                    }

                    @Override
                    public void onFinish() {
                        dialogDoubleRewardBtn.setEnabled(true);
                        dialogClaimRewardBtn.setVisibility(View.VISIBLE);
                        dialogDoubleRewardBtn.setText(getString(R.string.dialog_after_interstitial_double_reward_btn_text));
                    }
                }.start();

            } else {
                /** NO Double Reward **/
                dialogDoubleRewardBtn.setVisibility(View.GONE);
                new CountDownTimer(5000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        dialogTimerText.setText(new StringBuilder().append(millisUntilFinished / 1000).toString());
                    }
                    @Override
                    public void onFinish() {
                        dialogTimerText.setVisibility(View.GONE);
                        dialogClaimRewardBtn.setVisibility(View.VISIBLE);
                    }
                }.start();


            }

            dialogClaimRewardBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateChanceLeftAndCoins(true, false, true, coinsEarned);
                    FlipActivity.this.generateCoinsEarned = generateRandomCoins();
                    flip_generated_randon_number.setText(new StringBuilder().append("  + ").append(generateCoinsEarned).toString());
                    flip_generated_randon_number.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_coin_dollar, 0, 0, 0);

                    dialogAfterInterstitial.dismiss();

                    if (getSPrefGamePlayed() >= 1) {
                        if (isAppLovinNativeEnabled || isAppodealNativeEnabled) {
                            loadNativeAdFrom();
                        }
                    }
                    if (getSPrefGamePlayed() >= 2) {
                        numberGamePLayed = 0; // shifted to showInterstitial
                        updateSharedPrefGamePlayed();
                        showInterstitialFrom();
                    }
                }
            });

            dialogDoubleRewardBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showRewardedFrom(false, true, coinsEarned);

                    FlipActivity.this.generateCoinsEarned = generateRandomCoins();
                    flip_generated_randon_number.setText(new StringBuilder().append("  + ").append(generateCoinsEarned).toString());
                    flip_generated_randon_number.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_coin_dollar, 0, 0, 0);

                    dialogAfterInterstitial.dismiss();
                }
            });
        }
    }

    private void showDialogAfterDoubleReward(long coinsEarned) {
        AlertDialog.Builder builder = new AlertDialog.Builder(FlipActivity.this);
        View dialog_rootLayout = LayoutInflater.from(FlipActivity.this).inflate(R.layout.layout_dialog_add_double_reward_spin_flip_scratch,
                (ConstraintLayout) findViewById(R.id.constraint_dialog_add_double_reward));
        builder.setView(dialog_rootLayout);
        builder.setCancelable(false);

        AlertDialog dialogAfterDoubleReward = builder.create();
        dialogAfterDoubleReward.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialogAfterDoubleReward.show();

        TextView dialogMsg = dialogAfterDoubleReward.findViewById(R.id.dialog_add_double_reward_message);
        FrameLayout dialogClaimRewardBtn = dialogAfterDoubleReward.findViewById(R.id.dialog_add_double_reward_claim_reward_btn);

        dialogMsg.setText(new StringBuilder().append("  +").append(coinsEarned * 2).toString());
        dialogMsg.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_coin_dollar, 0, 0, 0);

        dialogClaimRewardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateChanceLeftAndCoins(true, false, true, coinsEarned*2);
                dialogAfterDoubleReward.dismiss();
            }
        });
    }


    private void disableEarnChanceBtn() {
        Date dateTime = new Date(System.currentTimeMillis());
        long btnCurrentTime = dateTime.getTime();

        Calendar calFutureTime = Calendar.getInstance();
        calFutureTime.setTimeInMillis(btnCurrentTime);
        calFutureTime.add(Calendar.HOUR_OF_DAY, 3);
        calFutureTime.set(Calendar.SECOND, 0);
        long btnEnable_futureTime = calFutureTime.getTimeInMillis();

        editorSPref.putLong(Constants.SP_NORMAL_FLIP_EARN_BTN_TIME, btnEnable_futureTime);
        editorSPref.commit();

        setNotifAlarm(calFutureTime);
        checkAndStart_earnBtnTimer();
    }

    private void checkAndStart_earnBtnTimer() {
        Date dateTime = new Date(System.currentTimeMillis());
        long currentTime = dateTime.getTime();

        long btnEnablingTime = getSPrefBtnTime();

        if (currentTime <= btnEnablingTime) {
            binding.earnChance.setEnabled(false);
            binding.earnChanceImageView.setVisibility(View.GONE);
            binding.earnChance.setCardBackgroundColor(getResources().getColor(R.color.darker_blue_app_theme));

            long timeLeft = btnEnablingTime - currentTime;
            new CountDownTimer(timeLeft, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    long secondsInMilli = 1000;
                    long minutesInMilli = secondsInMilli * 60;
                    long hoursInMilli = minutesInMilli * 60;

                    long elapsedHours = millisUntilFinished / hoursInMilli;
                    millisUntilFinished = millisUntilFinished % hoursInMilli;

                    long elapsedMinutes = millisUntilFinished / minutesInMilli;
                    millisUntilFinished = millisUntilFinished % minutesInMilli;

                    long elapsedSeconds = millisUntilFinished / secondsInMilli;

                    binding.earnChanceTextView.setText(new StringBuilder()
                            .append(elapsedHours).append("h : ")
                            .append(elapsedMinutes).append("m : ")
                            .append(elapsedSeconds).append("s").toString());
                }

                @Override
                public void onFinish() {
                    binding.earnChance.setEnabled(true);
                    binding.earnChanceImageView.setVisibility(View.VISIBLE);
                    binding.earnChanceTextView.setText(getString(R.string.flip_get_free_flip));
                    binding.earnChance.setCardBackgroundColor(getResources().getColor(R.color.blue_app_theme));

                    sendFreeChanceNotif();
                }
            }.start();
        } else {
            binding.earnChance.setEnabled(true);
            binding.earnChanceImageView.setVisibility(View.VISIBLE);
            binding.earnChanceTextView.setText(getString(R.string.flip_get_free_flip));
            binding.earnChance.setCardBackgroundColor(getResources().getColor(R.color.blue_app_theme));
        }
    }



    /**
     * * Banner Ad Code
     * **/
    private void showBannerAdFrom() {
        if (isAppLovinBannerEnabled || isAppodealBannerEnabled) {

            if (isAppLovinBannerEnabled) {
                applovinBanner = new MaxAdView(APPLOVIN_BANNER_ID, FlipActivity.this);

                int width = ViewGroup.LayoutParams.MATCH_PARENT;
                int heightDp = MaxAdFormat.BANNER.getAdaptiveSize(FlipActivity.this).getHeight();
                int heightPx = AppLovinSdkUtils.dpToPx(FlipActivity.this, heightDp);
                applovinBanner.setLayoutParams(new FrameLayout.LayoutParams(width, heightPx));
                applovinBanner.setExtraParameter("adaptive_banner", "true");
                applovinBanner.setBackgroundColor(getResources().getColor(R.color.darker_blue_app_theme));
                binding.flipAdmobAdView.addView(applovinBanner);
                applovinBanner.loadAd();
            }

            if (isAppodealBannerEnabled) {
                binding.flipAdmobAdView.setVisibility(View.GONE);
                Appodeal.setBannerViewId(R.id.flip_appodealBannerView);
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

    private void showProgressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(FlipActivity.this);
        View rootView1 = LayoutInflater.from(FlipActivity.this).inflate(R.layout.layout_dialog_progress_bar_white_short,
                (ConstraintLayout) findViewById(R.id.constraint_dialog_progress));
        builder.setView(rootView1);
        builder.setCancelable(false);
        progressDialog = builder.create();
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressDialog.show();
    }

    /**
     * * * Rewarded Ad Code
     **/
    private void loadRewardedAdFrom() {
        if (isAppLovinRewardedEnabled || isAppodealRewardedEnabled) {
            if (isAppLovinRewardedReady() || Appodeal.isLoaded(Appodeal.REWARDED_VIDEO)) {
            } else {
                if (isAppLovinRewardedEnabled) {
                    loadAppLovinRewarded();
                }
                if (isAppodealRewardedEnabled) {
                    loadAppodealRewarded();
                }
            }
        }
    }

    private void showRewardedFrom(boolean rewardForChance, boolean giveDoubleReward, long coinsEarned) {
        if (isAppLovinRewardedEnabled || isAppodealRewardedEnabled) {
            if (isAppLovinRewardedReady() || Appodeal.isLoaded(Appodeal.REWARDED_VIDEO)) {
                numberGamePLayed = 0;
                updateSharedPrefGamePlayed();

                if (isAppLovinRewardedReady()) {
                    show_AppLovinRewarded(rewardForChance, giveDoubleReward, coinsEarned);
                }
                if (Appodeal.isLoaded(Appodeal.REWARDED_VIDEO)) {
                    showAppodealRewarded(rewardForChance, giveDoubleReward, coinsEarned);
                }
            } else {
                showCustomToast(getString(R.string.game_no_ad_available));
                if (rewardForChance) {
                    // Don't give free chance
                }
                if (giveDoubleReward) {
                    // only give previous earned coins
                    updateChanceLeftAndCoins(true, false, true, coinsEarned);
                }
            }
        } else {
            // None of the above ad network is enabled
            if (rewardForChance) {
                disableEarnChanceBtn();
                giveChancesForFree();
            }
            if (giveDoubleReward) {
                updateChanceLeftAndCoins(true, false, true, coinsEarned);
            }
        }
    }

    private void giveChancesForFree() {
        showProgressDialog();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                numberGamePLayed = 0;
                updateSharedPrefGamePlayed();
                showCustomToast(getString(R.string.game_no_ad_available));
                progressDialog.dismiss();
//                disableEarnChanceBtn();
                showDialogAfterInterstitial(true, false, 0);
            }
        }, 3000);
    }




    /**
     * * * * * Interstitial
     ***/
    private void loadInterstitialFrom() {
        if (isAppLovinInterstitialEnabled || isAppodealInterstitialEnabled) {
            if (isAppLovinIntertitialReady() || Appodeal.isLoaded(Appodeal.INTERSTITIAL)) {
            } else {
                if (isAppLovinInterstitialEnabled) {
                    loadAppLovinInterstitial();
                }
                if (isAppodealInterstitialEnabled) {
                    // Auto cached
                }
            }
        }
    }

    public void showInterstitialFrom() {
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





    public void sendFreeChanceNotif() {
        String title = getString(R.string.notif_free_chance_title);
        String message = getString(R.string.notif_free_chance_message);

        Intent notificationIntent = new Intent(this, FlipActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, App.CHANNEL_FREE_CHANCE)
                .setSmallIcon(R.drawable.ic_app_notif_icon_cartoon_g1_svg)
                .setColor(getResources().getColor(R.color.system_accent3_700))
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setVibrate(new long[]{1000, 1000})
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

        notificationManager.notify(1, notification);
    }

    private void setNotifAlarm(Calendar calFutureTime) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AllGameAlarmReciever.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, Constants.FREE_CHANCE_NOR_FLIP_REQUEST_CODE, intent, 0);

//        if (calFutureTime.before(Calendar.getInstance())) {
//            calFutureTime.add(Calendar.DATE, 1);
//        }
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calFutureTime.getTimeInMillis(), pendingIntent);
    }

    public void scheduleJob() {
        ComponentName componentName = new ComponentName(this, JobNotificationBackground.class);
        JobInfo info = new JobInfo.Builder(123, componentName)
//                .setRequiresCharging(true)
//                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .setPersisted(true)
                .setPeriodic(15 * 60 * 1000)
                .build();

        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        int resultCode = scheduler.schedule(info);

        if (resultCode == JobScheduler.RESULT_SUCCESS) {
//            Log.d(TAG, "Job scheduled");
        } else {
//            Log.d(TAG, "Job scheduling failed");
        }
    }

    public void cancelJob() {
        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        scheduler.cancel(123);
//        Log.d(TAG, "Job cancelled");
    }


    /*****Checking Internet Connectivity****/
    private boolean internetConnected(Context context) {
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
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
    private void showNoInternetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialog_rootView = LayoutInflater.from(this).inflate(R.layout.layout_dialog_no_internet,
                (ConstraintLayout) findViewById(R.id.constraint_dialog_no_internet));
        builder.setView(dialog_rootView);
        builder.setCancelable(false);

        AlertDialog alertDialog = builder.create();

        Button reloadActivityButton = dialog_rootView.findViewById(R.id.reload_activity_button);
        Button exitActivityButton = dialog_rootView.findViewById(R.id.exit_button);

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
                finishAffinity();
                System.exit(0);
            }
        });
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.show();
    }


    @Override
    protected void onPause() {
        super.onPause();
        IronSource.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Appodeal.show(this, Appodeal.BANNER_VIEW);
//        IronSource.onResume(this);
    }

    @Override
    protected void onDestroy() {
//        if (admobInterstitial != null) {
//            admobInterstitial = null;
//        }
//        if (tapdaqBanner != null) {
//            tapdaqBanner.destroy(this);
//        }
        super.onDestroy();
    }

    @Override
    protected void onStart() {
//        Log.e("Flip Activity - ", "onStart()");
        Tapjoy.onActivityStart(FlipActivity.this);
        super.onStart();
    }

    @Override
    protected void onStop() {
//        Log.e("Flip Activity - ", "onStop()");
        Tapjoy.onActivityStop(FlipActivity.this);
        super.onStop();
    }

}