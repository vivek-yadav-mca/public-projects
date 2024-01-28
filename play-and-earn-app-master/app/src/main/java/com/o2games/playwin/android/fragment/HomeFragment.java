package dummydata.android.fragment;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.browser.customtabs.CustomTabColorSchemeParams;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.MaxReward;
import com.applovin.mediation.MaxRewardedAdListener;
import com.applovin.mediation.ads.MaxRewardedAd;
import com.appodeal.ads.Appodeal;
import com.appodeal.ads.RewardedVideoCallbacks;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import dummydata.android.AlarmService.DailyRewardAlarmReciever;
import dummydata.android.Constants;
import dummydata.android.FirebaseDataService;
import dummydata.android.Game;
import dummydata.android.R;
import dummydata.android.TapdaqNativeLargeLayout;
import dummydata.android.activity.FlipActivity;
import dummydata.android.activity.MainActivity;
import dummydata.android.activity.ScratchActivity;
import dummydata.android.activity.SpinningActivity;
import dummydata.android.databinding.FragmentHomeBinding;
import dummydata.android.model.User;
import dummydata.android.sqlUserGameData.DBHelper;
import dummydata.android.userData.UserContext;
import com.tapjoy.TJGetCurrencyBalanceListener;
import com.tapjoy.TJSpendCurrencyListener;
import com.tapjoy.Tapjoy;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HomeFragment extends Fragment {

    View rootView;
    private static HomeFragment instance;
    private MainActivity mainActivity;
    private static final String TAG = HomeFragment.class.getName();
    private FirebaseDataService firebaseDataService;
    private static final String sqlTotalCashCoinCOL = Game.TOTAL_CASH_COINS.getId();
    private String sql_totalCash;
    private String sql_totalCoins;
    public static int TAPJOY_CURRENCY_BALANCE;
    private User loggedInUser;

    private Context context;
    FragmentHomeBinding binding;
    private ConnectivityManager connectivityManager;
    private FirebaseRemoteConfig remoteConfig;
    private DatabaseReference databaseRef;
    private DBHelper dbHelper;
    private Handler handler;
    private SharedPreferences writeSPref;
    private SharedPreferences readSPref;
    private SharedPreferences.Editor editorSPref;
    private CountDownTimer timer;
    private AlertDialog underMaintenanceDialog;
    private AlertDialog progressDialog;
    private Animation scaleDown;
    private Animation rotate_clockwise;
    private Animation blinking;
    private Random random;

//    public static String UPSTOX_REFERRAL_LINK;
    public static String GZOP_MGL_DEDICATED_URL; 
    public static String QUREKA_PREDCHAMP_DEDICATED_URL; 
    int cctToolbarColor;

    public static long QUIZ_PRED_COUNTDOWN_TIME_IN_MILLIS;
    public static final int MAX_QUIZ_PRED_COUNTDOWN_TIME = 6; // exclusive 6 will not be counted
    public static final int MIN_QUIZ_PRED_COUNTDOWN_TIME = 3;
    public static long QUIZ_PRED_AMOUNT_OF_REWARD;
    //    public static final String QUIZ_AMOUNT_OF_REWARD_TEXT = "+ 75";
    public static String QUIZ_PRED_AMOUNT_OF_REWARD_TEXT; // Also change bsDialog text

    private boolean gamezopClicked = false;
    private int MAX_GAME_QUIZ_REWARD_AMOUNT = 76;
    private int MIN_GAME_QUIZ_REWARD_AMOUNT = 50;

    public static  long GAME_COUNTDOWN_TIME_IN_MILLIS;
    public static final int MAX_GAME_COUNTDOWN_TIME = 7;
    public static final int MIN_GAME_COUNTDOWN_TIME = 4;
    public static long GAME_AMOUNT_OF_REWARD;
    //    public static final String GAME_AMOUNT_OF_REWARD_TEXT = "+ 100";
    public static String GAME_AMOUNT_OF_REWARD_TEXT; // Also change bsDialog text

    public static int COUNTDOWN_TIME_IN_MINUTES;
    private long mTimeLeft_in_millis;
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private boolean updateScoreAutomatically_OnResume = true;

    private boolean isAppLovinRewardedEnabled;
    private static String APPLOVIN_REWARDED_ID;
    private MaxRewardedAd applovinRewarded;

    private boolean isAppodealRewardedEnabled;

    public HomeFragment() {
    }

    public static HomeFragment GetInstance() {
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        instance = this;
        loggedInUser = UserContext.getLoggedInUser();
        mainActivity = MainActivity.GetInstance();
        remoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings remoteConfigSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(0)
                .build();
        remoteConfig.setConfigSettingsAsync(remoteConfigSettings);

        context = getActivity();
        ViewPager2 viewPager_main_activity = getActivity().findViewById(R.id.viewPager_main_activity);

        firebaseDataService = new FirebaseDataService(getActivity());
        dbHelper = new DBHelper(getContext());
        handler = new Handler(Looper.getMainLooper());
        databaseRef = FirebaseDatabase.getInstance().getReference();
        scaleDown = AnimationUtils.loadAnimation(context, R.anim.anim_scale_down);
        rotate_clockwise = AnimationUtils.loadAnimation(context, R.anim.anim_rotate_clockwise);
        blinking = AnimationUtils.loadAnimation(context, R.anim.anim_blinking_repeat);
        random = new Random();

        updateHomeUI_CashCoinWallet();

        MAX_GAME_QUIZ_REWARD_AMOUNT = UserContext.getMaxGameQuizCoins();
        MIN_GAME_QUIZ_REWARD_AMOUNT = UserContext.getMinGameQuizCoins();

        isAppLovinRewardedEnabled = UserContext.getIsAppLovin_rewarded();
        isAppodealRewardedEnabled = UserContext.getIsAppodeal_rewarded();

        APPLOVIN_REWARDED_ID = getString(R.string.aL_rewarded_default);
        applovinRewarded = MaxRewardedAd.getInstance(APPLOVIN_REWARDED_ID, getActivity());
        isAppodealRewardedEnabled = UserContext.getIsAppodeal_rewarded();

        if (!internetConnected(context)) {
            showNoInternetDialog(rootView);
        }

        try {
            String spUserId = loggedInUser.getId() + "_";
            writeSPref = getActivity().getSharedPreferences("o2_" + loggedInUser.getId() + Constants.USER_SPECIFIC, Context.MODE_PRIVATE);
            editorSPref = writeSPref.edit();
            readSPref = getActivity().getSharedPreferences("o2_" + loggedInUser.getId() + Constants.USER_SPECIFIC, Context.MODE_PRIVATE);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

/**** Home Daily Reward Timer Setting ****/
        updateUIrewardTimer();
/**** Home Daily Reward Timer Setting ****/


        binding.refreshHomeScoreButtonCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.refreshHomeScoreButtonCV.startAnimation(rotate_clockwise);
                updateHomeUI_CashCoinWallet();
            }
        });

        if (!UserContext.getIsTapjoy_offerwall()) {
            binding.homeFragOfferwallCardView.setVisibility(View.GONE);
        }
        binding.homeFragOfferwallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.showOfferwallFrom();
            }
        });

        RequestOptions loadImageConfig = new RequestOptions()
                .centerCrop()
                .circleCrop()  //to crop image in circle view
                .placeholder(R.drawable.user_color)
                .error(R.drawable.user_color);

        Glide.with(getActivity())
                .load(loggedInUser.getUserPhotoUrl())
                .apply(loadImageConfig)
                .into(binding.homeUserPhotoBox);

        binding.homeUserPhotoBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager_main_activity.setCurrentItem(4);
            }
        });

        binding.homeFragCashBalanceFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager_main_activity.setCurrentItem(3);
            }
        });
        binding.homeFragCoinBalanceFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager_main_activity.setCurrentItem(3);
            }
        });

        binding.dailyRewardCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!internetConnected(context)) {
                    showNoInternetDialog(rootView);
                } else {
                    binding.dailyRewardCardView.startAnimation(scaleDown);
                    showDailyRewardDialog(rootView);
                }
            }
        });

        binding.spinWheelCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!internetConnected(context)) {
                    showNoInternetDialog(rootView);
                } else {
                    binding.spinCvFrame.startAnimation(scaleDown);
                    startActivity(new Intent(getActivity(), SpinningActivity.class));
                }
            }
        });

        binding.scratchCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!internetConnected(context)) {
                    showNoInternetDialog(rootView);
                } else {
                    binding.scratchCvFrame.startAnimation(scaleDown);
                    startActivity(new Intent(getActivity(), ScratchActivity.class));
                }
            }
        });

        binding.flipWinCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!internetConnected(context)) {
                    showNoInternetDialog(rootView);
                } else {
                    binding.flipCvFrame.startAnimation(scaleDown);
                    startActivity(new Intent(getActivity(), FlipActivity.class));
                }
            }
        });



        /**
         * * Dedicated Gamezop URL
         * **/
        binding.homeGamezopBottleShoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.homeGamezopBottleShoot.startAnimation(scaleDown);
                gamezopClicked = true;
                sendGzopMglData_to_timerDialog(R.string.gamezop_bottle_shoot_url, R.string.gamezop_bottle_shoot,
                        R.drawable.gamezop_action_bottle_shoot_webp, R.color.gamezop_bottle_shoot);
            }
        });
        binding.homeGamezopSolitaireGold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.homeGamezopSolitaireGold.startAnimation(scaleDown);
                gamezopClicked = true;
                sendGzopMglData_to_timerDialog(R.string.gamezop_solitaire_gold_url, R.string.gamezop_solitaire_gold,
                        R.drawable.gamezop_strategy_solitaire_gold_webp, R.color.gamezop_solitaire_gold);
            }
        });
        binding.homeGamezopCityCricket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.homeGamezopCityCricket.startAnimation(scaleDown);
                gamezopClicked = true;
                sendGzopMglData_to_timerDialog(R.string.gamezop_city_cricket_url, R.string.gamezop_city_cricket,
                        R.drawable.gamezop_sports_racing_city_cricket_webp, R.color.gamezop_city_cricket);
            }
        });

        binding.homeGamezopBubbleWipeout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.homeGamezopBubbleWipeout.startAnimation(scaleDown);
                gamezopClicked = true;
                sendGzopMglData_to_timerDialog(R.string.gamezop_bubble_wipeout_url, R.string.gamezop_bubble_wipeout,
                        R.drawable.gamezop_strategy_bubble_wipeout_webp, R.color.gamezop_bubble_wipeout);
            }
        });
        binding.homeGamezopStickyGoo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.homeGamezopStickyGoo.startAnimation(scaleDown);
                gamezopClicked = true;
                sendGzopMglData_to_timerDialog(R.string.gamezop_sticky_goo_url, R.string.gamezop_sticky_goo,
                        R.drawable.gamezop_adventure_sticky_goo_webp, R.color.gamezop_sticky_goo);
            }
        });
        binding.homeGamezopSaloonRobbery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.homeGamezopSaloonRobbery.startAnimation(scaleDown);
                gamezopClicked = true;
                sendGzopMglData_to_timerDialog(R.string.gamezop_saloon_robbery_url, R.string.gamezop_saloon_robbery,
                        R.drawable.gamezop_action_saloon_robbery_webp, R.color.gamezop_saloon_robbery);
            }
        });

        binding.homeGamezopJellySlice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.homeGamezopJellySlice.startAnimation(scaleDown);
                gamezopClicked = true;
                sendGzopMglData_to_timerDialog(R.string.gamezop_jelly_slice_url, R.string.gamezop_jelly_slice,
                        R.drawable.gamezop_puzzle_logic_jelly_slice_webp, R.color.gamezop_jelly_slice);
            }
        });
        binding.homeGamezopBouncy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.homeGamezopBouncy.startAnimation(scaleDown);
                gamezopClicked = true;
                sendGzopMglData_to_timerDialog(R.string.gamezop_bouncy_url, R.string.gamezop_bouncy,
                        R.drawable.gamezop_arcade_bouncy_webp, R.color.gamezop_bouncy);
            }
        });
        binding.homeGamezopJimboJump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.homeGamezopJimboJump.startAnimation(scaleDown);
                gamezopClicked = true;
                sendGzopMglData_to_timerDialog(R.string.gamezop_jimbo_jump_url, R.string.gamezop_jimbo_jump,
                        R.drawable.gamezop_adventure_jimbo_jump_webp, R.color.gamezop_jimbo_jump);
            }
        });

        return binding.getRoot();
    }


    private void showCustomToast(View rootView1, String toastMessage) {
        LayoutInflater inflater = getLayoutInflater();
        rootView1 = inflater.inflate(R.layout.layout_toast_custom,
                (ConstraintLayout) rootView1.findViewById(R.id.custom_toast_constraint));

        TextView toastText = rootView1.findViewById(R.id.custom_toast_text);
        toastText.setText(toastMessage);

        Toast toast = new Toast(getActivity());
        toast.setGravity(Gravity.BOTTOM, 0, 100);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(rootView1);
        toast.show();
    }

    private void updateUIrewardTimer() {
        long homePrevious10Time = readSPref.getLong(Constants.SP_10_BUTTON_TIME, 0);
        Date homeDateTime = new Date(System.currentTimeMillis());
        long homeCurrentTime = homeDateTime.getTime();
        if (homePrevious10Time == 0) {
            binding.homeDailyRewardTimer.setText(context.getString(R.string.home_frag_cV_reward_available));
        }
        homeTimer1Hours(homePrevious10Time, homeCurrentTime);
    }

    public void updateHomeUI_CashCoinWallet() {
        sql_totalCash = dbHelper.getFreeAdGameDataByUserIdAndGameId(loggedInUser.getId(), sqlTotalCashCoinCOL).getCash();
        binding.homeUserTotalCashBalance.setText(new StringBuilder().append("\u0024 ").append(sql_totalCash).toString());

        sql_totalCoins = dbHelper.getFreeAdGameDataByUserIdAndGameId(loggedInUser.getId(), sqlTotalCashCoinCOL).getCoins();
        binding.homeUserTotalCoinBalance.setText(sql_totalCoins);

        updateScoreAutomatically_OnResume = true;
    }

    private long getRandomQuizPredict_CountdownTime() {
        int randomQuizCountdownTime = random.nextInt(MAX_QUIZ_PRED_COUNTDOWN_TIME - MIN_QUIZ_PRED_COUNTDOWN_TIME) + MIN_QUIZ_PRED_COUNTDOWN_TIME;
        long convertRandomTime_to_millis = (randomQuizCountdownTime * 1000) * 60;
        return convertRandomTime_to_millis;
    }
    private long getRandomQuizPredict_RewardAmount() {
        int randomQuizReward = random.nextInt(MAX_GAME_QUIZ_REWARD_AMOUNT - MIN_GAME_QUIZ_REWARD_AMOUNT) + MIN_GAME_QUIZ_REWARD_AMOUNT;
        return randomQuizReward;
    }

    private long getRandomGame_CountdownTime() {
        int randomGameCountdownTime = random.nextInt(MAX_GAME_COUNTDOWN_TIME - MIN_GAME_COUNTDOWN_TIME) + MIN_GAME_COUNTDOWN_TIME;
        long convertRandomTime_to_millis = (randomGameCountdownTime * 1000) * 60;
        return convertRandomTime_to_millis;
    }
    private long getRandomGame_RewardAmount() {
        int randomGameReward = random.nextInt(MAX_GAME_QUIZ_REWARD_AMOUNT - MIN_GAME_QUIZ_REWARD_AMOUNT) + MIN_GAME_QUIZ_REWARD_AMOUNT;
        return randomGameReward;
    }

    private void sendQuizPredData_to_timerDialog(int stringURL, int stringName,
                                                 int timerDialog_drawableIcon, int timerDialog_colorSpecific) {
        QUIZ_PRED_COUNTDOWN_TIME_IN_MILLIS = getRandomQuizPredict_CountdownTime();
        COUNTDOWN_TIME_IN_MINUTES = (int) (QUIZ_PRED_COUNTDOWN_TIME_IN_MILLIS / 1000) / 60 ;
        QUIZ_PRED_AMOUNT_OF_REWARD = getRandomQuizPredict_RewardAmount();
        QUIZ_PRED_AMOUNT_OF_REWARD_TEXT = "+ " + QUIZ_PRED_AMOUNT_OF_REWARD;
        QUREKA_PREDCHAMP_DEDICATED_URL = context.getString(stringURL);

        String bsDialogTitle = new StringBuilder()
                .append(context.getString(R.string.dedicated_timer_bsdialog_qureka_title_play))
                .append(" ").append(context.getString(stringName))
                .append(" ").append(context.getString(R.string.dedicated_timer_bsdialog_qureka_title_for))
                .append(" ").append(COUNTDOWN_TIME_IN_MINUTES)
                .append(" ").append(context.getString(R.string.dedicated_timer_bsdialog_qureka_title_X_min_and_get))
                .append(" ").append(QUIZ_PRED_AMOUNT_OF_REWARD)
                .append(" ").append(context.getString(R.string.dedicated_timer_bsdialog_qureka_title_coins)).toString();

        showTimerBsDialog(1, timerDialog_drawableIcon,
                bsDialogTitle, getResources().getColor(R.color.white));
    }

    private void sendGzopMglData_to_timerDialog(int stringURL, int stringName,
                                                int timerDialog_drawableIcon, int timerDialog_colorSpecific) {
        GAME_COUNTDOWN_TIME_IN_MILLIS = getRandomGame_CountdownTime();
        COUNTDOWN_TIME_IN_MINUTES = (int) (GAME_COUNTDOWN_TIME_IN_MILLIS / 1000) / 60 ;
        GAME_AMOUNT_OF_REWARD = getRandomGame_RewardAmount();
        GAME_AMOUNT_OF_REWARD_TEXT = new StringBuilder().append("+ ").append(GAME_AMOUNT_OF_REWARD).toString();
        if (gamezopClicked) {
            String userSpecificGameUrl = new StringBuffer()
                    .append(context.getString(stringURL))
                    .append("&sub=")
                    .append(loggedInUser.getAuthUid()).toString();
            GZOP_MGL_DEDICATED_URL = userSpecificGameUrl;
        } else {
            GZOP_MGL_DEDICATED_URL = context.getString(stringURL);
        }

        String bsDialogTitle = new StringBuilder()
                .append(context.getString(R.string.dedicated_timer_bsdialog_game_title_play))
                .append(" ").append(context.getString(stringName))
                .append(" ").append(context.getString(R.string.dedicated_timer_bsdialog_game_title_for))
                .append(" ").append(COUNTDOWN_TIME_IN_MINUTES)
                .append(" ").append(context.getString(R.string.dedicated_timer_bsdialog_game_title_X_min_and_get))
                .append(" ").append(GAME_AMOUNT_OF_REWARD)
                .append(" ").append(context.getString(R.string.dedicated_timer_bsdialog_game_title_coins)).toString();

        showTimerBsDialog(3, timerDialog_drawableIcon,
                bsDialogTitle, getResources().getColor(R.color.white));
    }

    private void openCCT_URL(int QU1_PC1_GZ3_MGL3) {
        String cct_URL = null;
        if (QU1_PC1_GZ3_MGL3 == 1) {
            cct_URL = QUREKA_PREDCHAMP_DEDICATED_URL;
            cctToolbarColor = context.getResources().getColor(R.color.darker_blue_app_theme);
        }

        if (QU1_PC1_GZ3_MGL3 == 3) {
            cct_URL = GZOP_MGL_DEDICATED_URL;
            cctToolbarColor = context.getResources().getColor(R.color.darker_blue_app_theme);
        }

        CustomTabColorSchemeParams setCCTBarColors = new CustomTabColorSchemeParams.Builder()
                .setToolbarColor(cctToolbarColor)
                .build();

        CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder()
                .setDefaultColorSchemeParams(setCCTBarColors)
                .build();
        customTabsIntent.launchUrl(context, Uri.parse(cct_URL));
    }

    private void showTimerBsDialog(int QU1_PC1_GZ3_MGL3, int timerDialog_drawable_int,
                                   String timerDialog_dedi_title, int timerDialog_dedi_title_color) {
        BottomSheetDialog timerBsDialog = new BottomSheetDialog(getActivity(), R.style.bottomSheetDialog);
        timerBsDialog.setContentView(R.layout.layout_bsdialog_game_quiz_url_timer);
        timerBsDialog.getDismissWithAnimation();
        timerBsDialog.setCancelable(false);
        timerBsDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        timerBsDialog.show();

        FrameLayout dedURL_design_frame = timerBsDialog.findViewById(R.id.dedicated_url_design_frame);

        FrameLayout timer_stop_btn = timerBsDialog.findViewById(R.id.common_url_timer_stop_btn);
        TextView timer_stop_text = timerBsDialog.findViewById(R.id.common_url_timer_stop_text);
        FrameLayout timer_continue_btn = timerBsDialog.findViewById(R.id.common_url_timer_continue_btn);
        TextView timer_continue_text = timerBsDialog.findViewById(R.id.common_url_timer_continue_text);

        ImageView timer_bdDialog_icon = timerBsDialog.findViewById(R.id.timer_bsDialog_icon);
        TextView timer_bdDialog_title = timerBsDialog.findViewById(R.id.timer_bsDialog_title);
        TextView timer_text = timerBsDialog.findViewById(R.id.dedicated_url_timer_tv);

        /** Common for all **/
        timer_text.startAnimation(blinking);
        timer_text.setText(new StringBuffer()
                .append(COUNTDOWN_TIME_IN_MINUTES)
                .append(" ")
                .append(context.getString(R.string.common_timer_bsdialog_timer_min_text))
                .append(" 00 ")
                .append(context.getString(R.string.common_timer_bsdialog_timer_sec_text)).toString());

        if (QU1_PC1_GZ3_MGL3 == 1) {
            mTimeLeft_in_millis = QUIZ_PRED_COUNTDOWN_TIME_IN_MILLIS;
            timer_bdDialog_icon.setImageResource(timerDialog_drawable_int);
            timer_bdDialog_title.setText(timerDialog_dedi_title);
            timer_bdDialog_title.setTextColor(timerDialog_dedi_title_color);
            timer_text.setTextColor(timerDialog_dedi_title_color);
        }

        if (QU1_PC1_GZ3_MGL3 == 3) {
            mTimeLeft_in_millis = GAME_COUNTDOWN_TIME_IN_MILLIS;
            timer_bdDialog_icon.setImageResource(timerDialog_drawable_int);
            timer_bdDialog_title.setText(timerDialog_dedi_title);
            timer_bdDialog_title.setTextColor(timerDialog_dedi_title_color);
            timer_text.setTextColor(timerDialog_dedi_title_color);
        }

        timer_stop_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer_stop_text.startAnimation(scaleDown);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        timerBsDialog.dismiss();
                        resetCommonTimer(QU1_PC1_GZ3_MGL3);
                    }
                }, 100);
            }
        });

        timer_continue_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTimerRunning = true;
                timer_continue_text.startAnimation(scaleDown);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        openCCT_URL(QU1_PC1_GZ3_MGL3);

                        startCommonTimer(v, QU1_PC1_GZ3_MGL3, timerBsDialog, timer_text);
                    }
                }, 100);
            }
        });
    }

    private void startCommonTimer(View v, int comQU1_comGZ2_dedGZ3, BottomSheetDialog gamezopBsDialog, TextView gz_timer_text) {
        mCountDownTimer = new CountDownTimer(mTimeLeft_in_millis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeft_in_millis = millisUntilFinished;
                int minutes = (int) (mTimeLeft_in_millis / 1000) / 60;
                int seconds = (int) (mTimeLeft_in_millis / 1000) % 60;

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        gz_timer_text.setText(new StringBuffer()
                                .append(minutes).append(" ")
                                .append(context.getString(R.string.common_timer_bsdialog_timer_min_text))
                                .append(" ")
                                .append(seconds).append(" ")
                                .append(context.getString(R.string.common_timer_bsdialog_timer_sec_text)).toString());
                    }
                }, 2000);
            }

            @Override
            public void onFinish() {
                mTimerRunning = false;
                resetCommonTimer(comQU1_comGZ2_dedGZ3);
                gamezopBsDialog.dismiss();
                updateScoreAutomatically_OnResume = false;
                showAddRewardDialog(v, comQU1_comGZ2_dedGZ3);
            }
        }.start();
    }

    private void pauseCommonTimer() {
        if (mTimerRunning) {
            mCountDownTimer.cancel();
            mTimerRunning = false;
        }
    }

    private void resetCommonTimer(int QU1_PC1_GZ3_MGL3) {
        if (QU1_PC1_GZ3_MGL3 == 1) {
            mTimeLeft_in_millis = QUIZ_PRED_COUNTDOWN_TIME_IN_MILLIS;
        }
        if (QU1_PC1_GZ3_MGL3 == 3) {
            mTimeLeft_in_millis = GAME_COUNTDOWN_TIME_IN_MILLIS;
        }
    }

    private void showAddRewardDialog(View rootView1, int QU1_PC1_GZ3_MGL3) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        rootView1 = LayoutInflater.from(getActivity()).inflate(R.layout.layout_dialog_add_reward_game_home,
                (ConstraintLayout) getActivity().findViewById(R.id.constraint_dialog_add_reward_game_home));
        builder.setView(rootView1);
        builder.setCancelable(false);

        AlertDialog dialogAddReward = builder.create();
        dialogAddReward.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialogAddReward.show();

        ImageView dialogRewardIcon = rootView1.findViewById(R.id.dialog_add_reward_icon);
        TextView dialogRewardMsg = rootView1.findViewById(R.id.dialog_add_reward_msg);
        ImageView dialogCloseBtnIV = rootView1.findViewById(R.id.dialog_add_reward_close_btn);
        dialogRewardIcon.setImageResource(R.drawable.ic_coin_dollar);
        if (QU1_PC1_GZ3_MGL3 == 1) {
            dialogRewardMsg.setText(QUIZ_PRED_AMOUNT_OF_REWARD_TEXT);
        }
        if (QU1_PC1_GZ3_MGL3 == 3) {
            dialogRewardMsg.setText(GAME_AMOUNT_OF_REWARD_TEXT);
        }
        dialogCloseBtnIV.setVisibility(View.VISIBLE);

        dialogCloseBtnIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTokenInSQL(QU1_PC1_GZ3_MGL3);
                ((MainActivity)getActivity()).showMainAct_interstitialFrom();
                dialogAddReward.dismiss();
            }
        });
    }

    private void addTokenInSQL(int QU1_PC1_GZ3_MGL3) {
        if (QU1_PC1_GZ3_MGL3 == 1) {
            firebaseDataService.updateUserCoin(true, binding.homeUserTotalCoinBalance, QUIZ_PRED_AMOUNT_OF_REWARD);
        }
        if (QU1_PC1_GZ3_MGL3 == 3) {
            firebaseDataService.updateUserCoin(true, binding.homeUserTotalCoinBalance, GAME_AMOUNT_OF_REWARD);
        }

    }

    private void showDailyRewardDialog(View rootView1) {
        BottomSheetDialog rewardDialog = new BottomSheetDialog(getActivity(), R.style.bottomSheetDialog);
        rewardDialog.setContentView(R.layout.layout_dialog_daily_rewards);
        rewardDialog.getDismissWithAnimation();
        rewardDialog.setCancelable(true);
        rewardDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        rewardDialog.show();

        loadRewardedAdFrom(rootView1);

        TextView timer_reward_10 = rewardDialog.findViewById(R.id.timer_reward_10);
        TextView timer_reward_25 = rewardDialog.findViewById(R.id.timer_reward_25);
        TextView timer_reward_50 = rewardDialog.findViewById(R.id.timer_reward_50);
        TextView timer_reward_75 = rewardDialog.findViewById(R.id.timer_reward_75);
        TextView timer_reward_100 = rewardDialog.findViewById(R.id.timer_reward_100);

        FrameLayout free_10_coin_button = rewardDialog.findViewById(R.id.free_10_coin_button);
        FrameLayout free_25_coin_button = rewardDialog.findViewById(R.id.free_25_coin_button);
        FrameLayout free_50_coin_button = rewardDialog.findViewById(R.id.free_50_coin_button);
        FrameLayout free_75_coin_button = rewardDialog.findViewById(R.id.free_75_coin_button);
        FrameLayout free_100_coin_button = rewardDialog.findViewById(R.id.free_100_coin_button);


        Date dateTime = new Date(System.currentTimeMillis());
        long currentTime = dateTime.getTime();

        long previous10Time = readSPref.getLong(Constants.SP_10_BUTTON_TIME, 0);
        long previous25Time = readSPref.getLong(Constants.SP_25_BUTTON_TIME, 0);
        long previous50Time = readSPref.getLong(Constants.SP_50_BUTTON_TIME, 0);
        long previous75Time = readSPref.getLong(Constants.SP_75_BUTTON_TIME, 0);
        long previous100Time = readSPref.getLong(Constants.SP_100_BUTTON_TIME, 0);

/***** All Buttons timer Pre-Check *****/
        if (previous10Time != 0) {
            timerRewardButton(1, free_10_coin_button, timer_reward_10, previous10Time, currentTime);
        } else {
            timer_reward_10.setText(context.getString(R.string.home_frag_cV_reward_available));
        }

        if (previous25Time != 0) {
            timerRewardButton(2, free_25_coin_button, timer_reward_25, previous25Time, currentTime);
        } else {
            timer_reward_25.setText(context.getString(R.string.home_frag_cV_reward_available));
        }

        if (previous50Time != 0) {
            timerRewardButton(4, free_50_coin_button, timer_reward_50, previous50Time, currentTime);
        } else {
            timer_reward_50.setText(context.getString(R.string.home_frag_cV_reward_available));
        }

        if (previous75Time != 0) {
            timerRewardButton(8, free_75_coin_button, timer_reward_75, previous75Time, currentTime);
        } else {
            timer_reward_75.setText(context.getString(R.string.home_frag_cV_reward_available));
        }

        if (previous100Time != 0) {
            timerRewardButton(12, free_100_coin_button, timer_reward_100, previous100Time, currentTime);
        } else {
            timer_reward_100.setText(context.getString(R.string.home_frag_cV_reward_available));
        }

/***** 10 Coins Button *****/
        long diffTimeLess1Hours = currentTime - previous10Time;
        if (diffTimeLess1Hours >= 3600000) {
            free_10_coin_button.setEnabled(true);
        } else {
            free_10_coin_button.setEnabled(false);
        }

        free_10_coin_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date dateTime = new Date(System.currentTimeMillis());
                long savedReward10dateTime = dateTime.getTime();
                editorSPref.putLong(Constants.SP_10_BUTTON_TIME, savedReward10dateTime);
                editorSPref.commit();
                free_10_coin_button.setEnabled(false);

                long freeRewardAmount = 10;  // 25
                freeRewardUpdateToSQL(freeRewardAmount);
//                updateUICoinsWallet();
//                updateHomeUI_CashCoinWallet();

                updateUIrewardTimer();

                timerRewardButton(1, free_10_coin_button, timer_reward_10, savedReward10dateTime, currentTime);

            }
        });

/***** 25 Coins Button *****/
        long diffTimeLess2Hours = currentTime - previous25Time;
        if (diffTimeLess2Hours >= 7200000) {
            free_25_coin_button.setEnabled(true);
        } else {
            free_25_coin_button.setEnabled(false);
        }

        free_25_coin_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sPrefName = Constants.SP_25_BUTTON_TIME;
                long freeRewardAmount = 25;  //125

                showRewardedAdFrom(free_25_coin_button, sPrefName, freeRewardAmount, v, timer_reward_25, currentTime);

            }
        });

    }


    /***** TIMER - 10 Coins Button *****/
    private void timerRewardButton(int increaseInTime, FrameLayout free_coin_button, TextView timer_reward_tv, long savedRewardTime, long currentTime) {
        Calendar calFutureTime = Calendar.getInstance();
        calFutureTime.setTimeInMillis(savedRewardTime);
        calFutureTime.add(Calendar.HOUR_OF_DAY, increaseInTime);
        calFutureTime.set(Calendar.SECOND, 0);

        long futureTime = calFutureTime.getTimeInMillis();
        long leftTime = (futureTime - currentTime);

        if (currentTime <= futureTime) {
            timer = new CountDownTimer(leftTime, 1000) {
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

                    timer_reward_tv.setText(new StringBuilder()
                            .append(elapsedHours).append("h : ")
                            .append(elapsedMinutes).append("m : ")
                            .append(elapsedSeconds).append("s").toString());
                }

                @Override
                public void onFinish() {
                    timer_reward_tv.setText(context.getString(R.string.home_frag_cV_reward_available));
                    free_coin_button.setEnabled(true);
                }
            }.start();
        } else {
            timer_reward_tv.setText(context.getString(R.string.home_frag_cV_reward_available));
            free_coin_button.setEnabled(true);
        }
    }


    /***** HOME TIMER - 10 Coins Button *****/
    private void homeTimer1Hours(long homePrevious10Time, long homeCurrentTime) {
        Calendar futureTime = Calendar.getInstance();
        futureTime.setTimeInMillis(homePrevious10Time);
        futureTime.add(Calendar.HOUR_OF_DAY, 1);
        futureTime.set(Calendar.SECOND, 0);
        long leftTime = (futureTime.getTimeInMillis() - homeCurrentTime);

        timer = new CountDownTimer(leftTime, 1000) {
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

                binding.homeDailyRewardTimer.setText(new StringBuilder()
                        .append(elapsedHours).append("h : ")
                        .append(elapsedMinutes).append("m : ")
                        .append(elapsedSeconds).append("s").toString());
            }

            @Override
            public void onFinish() {
                binding.homeDailyRewardTimer.setText(context.getString(R.string.home_frag_cV_reward_available));
            }
        }.start();
    }


    /**** FREE Reward update to SQL ****/
    private void freeRewardUpdateToSQL(long freeRewardAmount) {
        firebaseDataService.updateUserCoin(true, binding.homeUserTotalCoinBalance, freeRewardAmount);
    }
    /**** FREE Reward update to SQL ****/


    private void showProgressDialog(View rootView1) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        rootView1 = LayoutInflater.from(getActivity()).inflate(R.layout.layout_dialog_progress_bar_white_short,
                (ConstraintLayout) rootView1.findViewById(R.id.constraint_dialog_progress));
        builder.setView(rootView1);
        builder.setCancelable(false);

        progressDialog = builder.create();

        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressDialog.show();
    }

    private void loadRewardedAdFrom(View rootView1) {
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


    private void ifRewardedAdNotEnabled(FrameLayout free_coin_button, String sPrefName, long dailyRewardAmount, View v, TextView timer_reward_tv, long currentTime) {
        Date dateTime = new Date(System.currentTimeMillis());
        long savingReward_time = dateTime.getTime();
        editorSPref.putLong(sPrefName, savingReward_time);
        editorSPref.commit();
        free_coin_button.setEnabled(false);

        freeRewardUpdateToSQL(dailyRewardAmount);

        show_free_addCoinsDialog(v, dailyRewardAmount);

        if (dailyRewardAmount == 25) {
            timerRewardButton(2, free_coin_button, timer_reward_tv, savingReward_time, currentTime);
        }
        if (dailyRewardAmount == 50) {
            timerRewardButton(4, free_coin_button, timer_reward_tv, savingReward_time, currentTime);
        }
        if (dailyRewardAmount == 75) {
            timerRewardButton(8, free_coin_button, timer_reward_tv, savingReward_time, currentTime);
        }
        if (dailyRewardAmount == 100) {
            timerRewardButton(12, free_coin_button, timer_reward_tv, savingReward_time, currentTime);
        }
    }

    private void showNoInternetDialog(View rootView1) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        rootView1 = LayoutInflater.from(getContext()).inflate(R.layout.layout_dialog_no_internet,
                (ConstraintLayout) rootView1.findViewById(R.id.constraint_dialog_no_internet));
        builder.setView(rootView1);
        builder.setCancelable(false);

        AlertDialog alertDialog = builder.create();

        Button reloadActivityButton = rootView1.findViewById(R.id.reload_activity_button);
        Button exitActivityButton = rootView1.findViewById(R.id.exit_button);

        reloadActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().recreate();
                alertDialog.dismiss();
            }
        });

        exitActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
                System.exit(0);
            }
        });
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.show();
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

    private void setNotifAlarm(long savedReward_time) {
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getActivity(), DailyRewardAlarmReciever.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), Constants.DAILY_REWARD_DEFAULT_REQUEST_CODE, intent, 0);

        Calendar calFutureTime = Calendar.getInstance();
        calFutureTime.setTimeInMillis(savedReward_time);
        calFutureTime.add(Calendar.HOUR_OF_DAY, 2);
        calFutureTime.set(Calendar.SECOND, 0);

        long notifAlarmTime = calFutureTime.getTimeInMillis();

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, notifAlarmTime, pendingIntent);
    }


    private void getAndSetChanceNotif() {
        int normalSpinChance = Integer.parseInt(firebaseDataService.getAllGameInOneMapByGameId(Game.NORMAL_SPIN.getId()).getChanceLeft());
        int normalScratchChance = Integer.parseInt(firebaseDataService.getAllGameInOneMapByGameId(Game.NORMAL_SCRATCH.getId()).getChanceLeft());
        int normalFlipChance = Integer.parseInt(firebaseDataService.getAllGameInOneMapByGameId(Game.NORMAL_FLIP.getId()).getChanceLeft());
        binding.normalSpinNotifTv.setText(String.valueOf(normalSpinChance));
        binding.normalScratchNotifTv.setText(String.valueOf(normalScratchChance));
        binding.normalFlipNotifTv.setText(String.valueOf(normalFlipChance));

        if (normalSpinChance >= 11) {
            binding.normalSpinNotifTv.setTextColor(getResources().getColor(R.color.white));
            binding.normalSpinNotifFl.setBackground(getResources().getDrawable(R.drawable.bg_home_notif_game_chance_green));
        } else if (normalSpinChance <= 10 && normalSpinChance >= 4) {
            binding.normalSpinNotifTv.setTextColor(getResources().getColor(R.color.black));
            binding.normalSpinNotifFl.setBackground(getResources().getDrawable(R.drawable.bg_home_notif_game_chance_yellow));
        } else if (normalSpinChance <= 3) {
            binding.normalSpinNotifTv.setTextColor(getResources().getColor(R.color.white));
            binding.normalSpinNotifFl.setBackground(getResources().getDrawable(R.drawable.bg_home_notif_game_chance_red));
        } else if (normalSpinChance == 0) {

        }
    }

    private void getAddClearTapjoyCurrencyBalance() {
        showProgressDialog(rootView);

        if (Tapjoy.isConnected()) {
            Tapjoy.getCurrencyBalance(new TJGetCurrencyBalanceListener() {
                @Override
                public void onGetCurrencyBalanceResponse(String currencyName, int tapjoyBalance) {
                    TAPJOY_CURRENCY_BALANCE = tapjoyBalance;
                }
                @Override
                public void onGetCurrencyBalanceResponseFailure(String error) {
                }
            });

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                    if (TAPJOY_CURRENCY_BALANCE > 0) {
                        addOfferwallCoinDialog(rootView, TAPJOY_CURRENCY_BALANCE);
                    }
                }
            }, 3500);
        }
    }

    private void addOfferwallCoinDialog(View rootView1, long coinToBeAdded) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        rootView1 = LayoutInflater.from(getContext()).inflate(R.layout.layout_dialog_add_reward_spin_flip_scratch,
                (ConstraintLayout) rootView1.findViewById(R.id.constraint_dialog_add_reward_game_home));
        builder.setView(rootView1);
        builder.setCancelable(false);

        AlertDialog addCoinsDialog = builder.create();
        addCoinsDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        addCoinsDialog.show();

        TextView youWonMessage = rootView1.findViewById(R.id.dialog_after_interstitial_message);
        TextView timer = rootView1.findViewById(R.id.dialog_after_interstitial_timer_text);
        Button doubleCoinBtn = rootView1.findViewById(R.id.dialog_after_interstitial_double_reward_btn);
        FrameLayout claimCoins = rootView1.findViewById(R.id.dialog_after_interstitial_claim_reward_btn);
        TapdaqNativeLargeLayout nativeLargeLayout = rootView1.findViewById(R.id.native_ad_container);
        FrameLayout nativeAdFrameLayout = rootView1.findViewById(R.id.native_ad_frameLayout);

        youWonMessage.setText(new StringBuilder().append(" + ").append(coinToBeAdded).toString());
        youWonMessage.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_coin_dollar, 0, 0, 0);
        timer.setVisibility(View.GONE);
        doubleCoinBtn.setVisibility(View.GONE);
        claimCoins.setVisibility(View.VISIBLE);
        nativeLargeLayout.setVisibility(View.GONE);
        nativeAdFrameLayout.setVisibility(View.GONE);

        claimCoins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseDataService.updateUserCoin(true, binding.homeUserTotalCoinBalance, coinToBeAdded);
                Tapjoy.spendCurrency(TAPJOY_CURRENCY_BALANCE, new TJSpendCurrencyListener() {
                    @Override
                    public void onSpendCurrencyResponse(String currencyName, int balance) {
//                        Log.e(TAG, "getCurrencyBalance deducted " + currencyName + " : " + balance);
                    }
                    @Override
                    public void onSpendCurrencyResponseFailure(String error) {
                    }
                });
                addCoinsDialog.dismiss();
            }
        });
    }

    private void runShinningEffect() {
        /** For Shining Effect on EARN CHANCE button **/
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        startShineEffect();
                    }
                });
            }
        }, 2, 4, TimeUnit.SECONDS);
        /** For Shining Effect on EARN CHANCE button **/
    }

    private void startShineEffect() {
        Animation shiningAnimation = new TranslateAnimation(
                0,
                binding.homeFragOfferwallBtn.getWidth() + binding.homeFragShinningDrawImageView.getWidth(),
                0, 0);

        shiningAnimation.setDuration(800);
        shiningAnimation.setFillAfter(false);
        shiningAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        binding.homeFragShinningDrawImageView.startAnimation(shiningAnimation);
    }




    private void showVirtualCurrencyEarnedDialog() {
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        pauseCommonTimer();
        if (updateScoreAutomatically_OnResume) {
            updateHomeUI_CashCoinWallet();
        } else {
        }
        getAndSetChanceNotif();
        getAddClearTapjoyCurrencyBalance();
        if (UserContext.getIsTapjoy_offerwall()) {
            runShinningEffect();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}