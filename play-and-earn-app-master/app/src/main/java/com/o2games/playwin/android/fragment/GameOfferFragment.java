package dummydata.android.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.transition.AutoTransition;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.browser.customtabs.CustomTabColorSchemeParams;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import dummydata.android.FirebaseDataService;
import dummydata.android.Game;
import dummydata.android.R;
import dummydata.android.activity.MainActivity;
import dummydata.android.databinding.FragmentGameOfferBinding;
import dummydata.android.model.User;
import dummydata.android.sqlUserGameData.DBHelper;
import dummydata.android.userData.UserContext;

import java.util.Random;

public class GameOfferFragment extends Fragment {

    private static final String TAG = "saare_games_ka_fra";
    private FirebaseDataService firebaseDataService;
    public static final String sqlTotalCoinsCOL = Game.TOTAL_CASH_COINS.getId();
    private User loggedInUser;

    View rootView;
    Context context;
    FragmentGameOfferBinding binding;
    DBHelper dbHelper;
    Handler handler;
    Animation scaleDown;
    Animation largeScaleUp;
    Animation blinking;
    Animation rotate_clockwise;
    private AlertDialog progressDialog;

    int cctToolbarColor;
    public static String GAMEZOP_COMMON_URL;
    public static String GZOP_MGL_DEDICATED_URL;
    public static String QUREKA_COMMON_URL;
    public static String QUREKA_PREDCHAMP_DEDICATED_URL;

    boolean isCPALeadOfferwallEnabled;
    public static String UNIQUE_CPA_LEAD_OFFERWALL_LINK;

    Random random;
    public static int COUNTDOWN_TIME_IN_MINUTES;

    public static long QUIZ_PRED_COUNTDOWN_TIME_IN_MILLIS;
    public static final int MAX_QUIZ_PRED_COUNTDOWN_TIME = 6; // 4 exclusive 6 will not be counted
    public static final int MIN_QUIZ_PRED_COUNTDOWN_TIME = 3; // 2
    public static long QUIZ_PRED_AMOUNT_OF_REWARD;
    //    public static final String QUIZ_AMOUNT_OF_REWARD_TEXT = "+ 75";
    public static String QUIZ_PRED_AMOUNT_OF_REWARD_TEXT; // Also change bsDialog text

    private boolean gamezopClicked = false;
    private int MAX_GAME_QUIZ_REWARD_AMOUNT = 76;  // 151
    private int MIN_GAME_QUIZ_REWARD_AMOUNT = 50;  // 75

    public static  long GAME_COUNTDOWN_TIME_IN_MILLIS;
    public static final int MAX_GAME_COUNTDOWN_TIME = 6;  // 4 6
    public static final int MIN_GAME_COUNTDOWN_TIME = 4;  // 2 3
    public static long GAME_AMOUNT_OF_REWARD;
    //    public static final String GAME_AMOUNT_OF_REWARD_TEXT = "+ 100";
    public static String GAME_AMOUNT_OF_REWARD_TEXT; // Also change bsDialog text

    private long mTimeLeft_in_millis;
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;

    private long gameEntryTime;
    private long gameExitTime;

    public WebView mWebView;

    public GameOfferFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_game_offer, container, false);
        binding = FragmentGameOfferBinding.inflate(inflater, container, false);

        context = getActivity();
        loggedInUser = UserContext.getLoggedInUser();
        firebaseDataService = new FirebaseDataService(getActivity());
        random = new Random();
        dbHelper = new DBHelper(context);
        handler = new Handler(Looper.getMainLooper());
        scaleDown = AnimationUtils.loadAnimation(context, R.anim.anim_scale_down);
        largeScaleUp = AnimationUtils.loadAnimation(context, R.anim.anim_large_scale_up);
        blinking = AnimationUtils.loadAnimation(context, R.anim.anim_blinking_repeat);
        rotate_clockwise = AnimationUtils.loadAnimation(context, R.anim.anim_rotate_clockwise);

        MAX_GAME_QUIZ_REWARD_AMOUNT = UserContext.getMaxGameQuizCoins();
        MIN_GAME_QUIZ_REWARD_AMOUNT = UserContext.getMinGameQuizCoins();

        updateUICoinsData();

        binding.gameOfferRefreshCoinImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.gameOfferRefreshCoinImage.startAnimation(rotate_clockwise);
                updateUICoinsData();
            }
        });

        binding.lottieAnimationView7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.lottieAnimationView7.startAnimation(scaleDown);
                if (isCPALeadOfferwallEnabled) {
                    binding.lottieAnimationView7.startAnimation(largeScaleUp);
                    largeScaleUp.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            openCpaLeadOfferwallURL();
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                }
            }
        });


        /**
         * * Action
         * **/
//        binding.gamezopBlazingBladesCvName.setSelected(true);
        binding.gamezopBlazingBlades.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // binding.gamezopBlazingBlades.startAnimation(scaleDown);
                gamezopClicked = true;
                sendGzopMglData_to_timerDialog(R.string.gamezop_blazing_blades_url, R.string.gamezop_blazing_blades,
                        R.drawable.gamezop_action_blazing_blades_webp, R.color.gamezop_blazing_blades);
            }
        });
//        binding.gamezopBottleShootCvName.setSelected(true);
        binding.gamezopBottleShoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                binding.gamezopBottleShoot.startAnimation(scaleDown);
                gamezopClicked = true;
                sendGzopMglData_to_timerDialog(R.string.gamezop_bottle_shoot_url, R.string.gamezop_bottle_shoot,
                        R.drawable.gamezop_action_bottle_shoot_webp, R.color.gamezop_bottle_shoot);
            }
        });
//        binding.gamezopBoulderBlastCvName.setSelected(true);
        binding.gamezopBoulderBlast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                binding.gamezopBoulderBlast.startAnimation(scaleDown);
                gamezopClicked = true;
                sendGzopMglData_to_timerDialog(R.string.gamezop_boulder_blast_url, R.string.gamezop_boulder_blast,
                        R.drawable.gamezop_action_boulder_blast_webp, R.color.gamezop_boulder_blast);
            }
        });
//        binding.gamezopSaloonRobberyCvName.setSelected(true);
        binding.gamezopSaloonRobbery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                binding.gamezopSaloonRobbery.startAnimation(scaleDown);
                gamezopClicked = true;
                sendGzopMglData_to_timerDialog(R.string.gamezop_saloon_robbery_url, R.string.gamezop_saloon_robbery,
                        R.drawable.gamezop_action_saloon_robbery_webp, R.color.gamezop_saloon_robbery);
            }
        });


        return binding.getRoot();
    }

    public void showHideActionGames() {
        if (binding.gameOfferActionGamesExpandableLayout.getVisibility() == View.GONE) {
            TransitionManager.beginDelayedTransition(binding.gameOfferActionGamesCardView, new AutoTransition());
            binding.gameOfferActionShowMoreText.setText(context.getString(R.string.gameOffer_frag_show_less_game));
            binding.gameOfferActionShowMoreIcon.setRotation(180);
            binding.gameOfferActionGamesExpandableLayout.setVisibility(View.VISIBLE);
        } else {
            TransitionManager.beginDelayedTransition(binding.gameOfferActionGamesCardView, new Slide());
            binding.gameOfferActionShowMoreText.setText(context.getString(R.string.gameOffer_frag_show_more_game));
            binding.gameOfferActionShowMoreIcon.setRotation(0);
            binding.gameOfferActionGamesExpandableLayout.setVisibility(View.GONE);
        }
    }

    public void showHideAdventureGames() {
        if (binding.gameOfferAdventureGamesExpandableLayout.getVisibility() == View.GONE) {
            TransitionManager.beginDelayedTransition(binding.gameOfferAdventureGamesCardView, new AutoTransition());
            binding.gameOfferAdventureShowMoreText.setText(context.getString(R.string.gameOffer_frag_show_less_game));
            binding.gameOfferAdventureShowMoreIcon.setRotation(180);
            binding.gameOfferAdventureGamesExpandableLayout.setVisibility(View.VISIBLE);
        } else {
            TransitionManager.beginDelayedTransition(binding.gameOfferAdventureGamesCardView, new Slide());
            binding.gameOfferAdventureShowMoreText.setText(context.getString(R.string.gameOffer_frag_show_more_game));
            binding.gameOfferAdventureShowMoreIcon.setRotation(0);
            binding.gameOfferAdventureGamesExpandableLayout.setVisibility(View.GONE);
        }
    }

    private void openCpaLeadOfferwallURL() {
        CustomTabColorSchemeParams setCCTBarColors = new CustomTabColorSchemeParams.Builder()
                .setToolbarColor(cctToolbarColor)
                .build();

        CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder()
                .setDefaultColorSchemeParams(setCCTBarColors)
                .build();
        customTabsIntent.launchUrl(context, Uri.parse(UNIQUE_CPA_LEAD_OFFERWALL_LINK));
    }


    private void updateUICoinsData() {
        String sql_totalCoins = firebaseDataService.getCoinBalance();
        binding.gameOfferUserTotalCoins.setText(sql_totalCoins);
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

        String bsDialogTitle = context.getString(R.string.dedicated_timer_bsdialog_qureka_title_play) +
                " " + context.getString(stringName) +
                " " + context.getString(R.string.dedicated_timer_bsdialog_qureka_title_for) +
                " " + COUNTDOWN_TIME_IN_MINUTES +
                " " + context.getString(R.string.dedicated_timer_bsdialog_qureka_title_X_min_and_get) +
                " " + QUIZ_PRED_AMOUNT_OF_REWARD +
                " " + context.getString(R.string.dedicated_timer_bsdialog_qureka_title_coins);

        showTimerBsDialog(1, timerDialog_drawableIcon,
                bsDialogTitle, getResources().getColor(R.color.white));
    }

    private void sendGzopMglData_to_timerDialog(int stringURL, int stringName,
                                                int timerDialog_drawableIcon, int timerDialog_colorSpecific) {
        GAME_COUNTDOWN_TIME_IN_MILLIS = getRandomGame_CountdownTime();
        COUNTDOWN_TIME_IN_MINUTES = (int) (GAME_COUNTDOWN_TIME_IN_MILLIS / 1000) / 60 ;
        GAME_AMOUNT_OF_REWARD = getRandomGame_RewardAmount();
        GAME_AMOUNT_OF_REWARD_TEXT = "+ " + GAME_AMOUNT_OF_REWARD;
        if (gamezopClicked) {
            String userSpecificGameUrl = new StringBuffer()
                    .append(context.getString(stringURL))
                    .append("&sub=")
                    .append(loggedInUser.getAuthUid()).toString();
            GZOP_MGL_DEDICATED_URL = userSpecificGameUrl;
        } else {
            GZOP_MGL_DEDICATED_URL = context.getString(stringURL);
        }

        String bsDialogTitle = new StringBuffer()
                .append(context.getString(R.string.dedicated_timer_bsdialog_game_title_play))
                .append(" ").append(context.getString(stringName))
                .append(" ").append(context.getString(R.string.dedicated_timer_bsdialog_game_title_for))
                .append(" ").append(COUNTDOWN_TIME_IN_MINUTES)
                .append(" ")
                .append(context.getString(R.string.dedicated_timer_bsdialog_game_title_X_min_and_get))
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

    private void startCommonTimer(View v, int QU1_PC1_GZ3_MGL3, BottomSheetDialog gamezopBsDialog, TextView gz_timer_text) {
        mCountDownTimer = new CountDownTimer(mTimeLeft_in_millis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeft_in_millis = millisUntilFinished;
                int seconds = (int) (mTimeLeft_in_millis / 1000) % 60;
                int minutes = (int) (mTimeLeft_in_millis / 1000) / 60;

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
                resetCommonTimer(QU1_PC1_GZ3_MGL3);
                gamezopBsDialog.dismiss();
                showAddRewardDialog(v, QU1_PC1_GZ3_MGL3);
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

        addTokenInSQL(QU1_PC1_GZ3_MGL3);

        dialogCloseBtnIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUICoinsData();
                ((MainActivity)getActivity()).showMainAct_interstitialFrom();
                dialogAddReward.dismiss();
            }
        });
    }

    private void addTokenInSQL(int QU1_PC1_GZ3_MGL3) {
        if (QU1_PC1_GZ3_MGL3 == 1) {
            firebaseDataService.updateUserCoin(true, binding.gameOfferUserTotalCoins, QUIZ_PRED_AMOUNT_OF_REWARD);
        }
        if (QU1_PC1_GZ3_MGL3 == 3) {
            firebaseDataService.updateUserCoin(true, binding.gameOfferUserTotalCoins, GAME_AMOUNT_OF_REWARD);
        }

    }


    private void showProgressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View rootView1 = LayoutInflater.from(getActivity()).inflate(R.layout.layout_dialog_progress_bar_white_short,
                (ConstraintLayout) getActivity().findViewById(R.id.constraint_dialog_progress));
        builder.setView(rootView1);
        builder.setCancelable(false);
        progressDialog = builder.create();
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressDialog.show();
    }



    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUICoinsData();
        pauseCommonTimer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}