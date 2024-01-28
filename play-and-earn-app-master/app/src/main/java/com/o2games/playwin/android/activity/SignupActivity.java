package dummydata.android.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.browser.customtabs.CustomTabColorSchemeParams;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.applovin.sdk.AppLovinPrivacySettings;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;
import com.applovin.sdk.AppLovinSdkSettings;
import com.appodeal.ads.Appodeal;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import dummydata.android.Constants;
import dummydata.android.FirebaseDataService;
import dummydata.android.Game;
import dummydata.android.LanguageManager;
import dummydata.android.R;
import dummydata.android.databinding.ActivitySignupBinding;
import dummydata.android.model.AllGameInOne;
import dummydata.android.model.LeaderboardModel;
import dummydata.android.model.User;
import dummydata.android.model.UserWalletDataModel;
import dummydata.android.spinWheel.WheelItem;
import dummydata.android.sqlUserGameData.DBHelper;
import dummydata.android.userData.UserContext;
import com.tapjoy.TJConnectListener;
import com.tapjoy.Tapjoy;
import com.tapjoy.TapjoyConnectFlag;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class SignupActivity extends AppCompat {

    private static final String TAG = SignupActivity.class.getName();
    private final FirebaseDataService firebaseDataService = new FirebaseDataService(this);
    private static final String sqlTotal_CashCoinsCOL = Game.TOTAL_CASH_COINS.getId();
    String googleAdverId;
    boolean connectedToAdNetwork;
    boolean doubleBackToExit = false;

    private static int RC_SIGN_IN = 100;
    private static int SPIN_WHEEL_ROUND_COUNT = 2;
    private User loggedInUser;

    GoogleSignInClient mGoogleSignInClient;
    FirebaseRemoteConfig remoteConfig;

    ActivitySignupBinding binding;
    ConnectivityManager connectivityManager;
    DBHelper dbHelper;
    DatabaseReference databaseRef;
    FirebaseAuth firebaseAuth;
    Handler handler;

    List<WheelItem> data;
    Animation scaleDown;
    AlertDialog progressDialog;
    String telegram_channel_link;

    private LanguageManager languageManager;
    private Spinner lang_spinner;
    private ArrayAdapter<CharSequence> adapter;

    private SharedPreferences writeSPref;
    private SharedPreferences.Editor editorSPref;
    private SharedPreferences readSPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        remoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings remoteConfigSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(0)
                .build();
        remoteConfig.setConfigSettingsAsync(remoteConfigSettings);

        gettingAdvertisingId();

        dbHelper = new DBHelper(SignupActivity.this);
        databaseRef = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        handler = new Handler(Looper.getMainLooper());
        scaleDown = AnimationUtils.loadAnimation(this, R.anim.anim_scale_down);

        writeSPref = getSharedPreferences(Constants.SHARED_PREF_COMMON, Context.MODE_PRIVATE);
        editorSPref = writeSPref.edit();
        readSPref = getSharedPreferences(Constants.SHARED_PREF_COMMON, Context.MODE_PRIVATE);

        languageManager = new LanguageManager(this);
        adapter = ArrayAdapter.createFromResource(this, R.array.spinner_language, R.layout.layout_spinner_custom);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.signupLanguageSpinner.setAdapter(adapter);
        Intent intent = getIntent();
        binding.signupLanguageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedLanguage = parent.getItemAtPosition(position).toString();
                if (selectedLanguage.equals("English")) {
                    languageManager.updateLanguage("en");
                    finish();
                    startActivity(intent);
                }
                if (selectedLanguage.equals("Español")) {
                    languageManager.updateLanguage("es");
                    finish();
                    startActivity(intent);
                }
                if (selectedLanguage.equals("Português")) {
                    languageManager.updateLanguage("pt");
                    finish();
                    startActivity(intent);
                }
                if (selectedLanguage.equals("Deutsch")) {
                    showCustomToast(getString(R.string.lang_spinner_toast_german_Deutsch_msg));
                }
                if (selectedLanguage.equals("Français")) {
                    showCustomToast(getString(R.string.lang_spinner_toast_french_Français_msg));
                }
                if (selectedLanguage.equals("Italiano")) {
                    showCustomToast(getString(R.string.lang_spinner_toast_italian_Italiano_msg));
                }
                if (selectedLanguage.equals("Indonesia")) {
                    showCustomToast(getString(R.string.lang_spinner_toast_indonesian_Indonesia_msg));
                }
                if (selectedLanguage.equals("日本")) {
                    showCustomToast(getString(R.string.lang_spinner_toast_japanese_日本_msg));
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        binding.signupCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
                overridePendingTransition(R.anim.anim_enter_from_left, R.anim.anim_exit_to_left);
                System.exit(0);
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account == null) {
            FrameLayout signInButton = findViewById(R.id.sign_in_button);
            signInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (internetConnected(SignupActivity.this)) {
                            signIn();
                    } else {
                        showNoInternetDialog();
                    }
                }
            });
        } else {
            registerUserAndNavigate(account);
        }

        binding.privacyPolicyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTermsPrivacy_bsDialog();
            }
        });


    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            showCustomProgressDialog("Welcome to " + getString(R.string.app_name) + " by O2 Games Inc.", false);

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        } else {
            progressDialog.dismiss();
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
            if (account != null) {
                    registerUserAndNavigate(account);
            } else {
                String message = getString(R.string.signup_registration_cancelled);
                showCustomToast(message);
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            progressDialog.dismiss();
            showFirebaseDBCustomToast();

        }
    }

    public void registerUserAndNavigate(GoogleSignInAccount account) {
        String userId = account.getEmail().replaceAll("[.#$\\[\\]]", "");
        String userName = account.getDisplayName();
        String userEmail = account.getEmail();
        String userGoogleId = account.getId();
        String userPhotoUrl = account.getPhotoUrl() != null ? account.getPhotoUrl().toString() : null;

        loggedInUser = new User(userId, userName, userEmail, userGoogleId, userPhotoUrl, googleAdverId);

//        UserContext.setDbHelper(dbHelper);
        createSQLDatabaseTable(loggedInUser);

        if (firebaseAuth.getCurrentUser() == null) {
            UserContext.setLoggedInUser(loggedInUser);
            firebaseAuthWithGoogle(account, loggedInUser);
        } else {
            String authUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            loggedInUser.setAuthUid(authUid);
            UserContext.setLoggedInUser(loggedInUser);
            setUserData_sPref(loggedInUser);
            initializeFetchRemoteConfig();
        }
    }

    /**
     * When using Google Sign-In with firebase
     **/
    private void firebaseAuthWithGoogle(GoogleSignInAccount account, User loggedInUser) {

        String idToken = account.getIdToken();

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth
                .signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            boolean isNewUser = task.getResult().getAdditionalUserInfo().isNewUser();

                            String authUid = firebaseAuth.getCurrentUser().getUid();
                            loggedInUser.setAuthUid(authUid);
                            UserContext.setLoggedInUser(loggedInUser);

                            setUserData_sPref(loggedInUser);
                            registerNewUserToFirebase(isNewUser, loggedInUser);
                        } else {
                            progressDialog.dismiss();
                            showFirebaseDBCustomToast();
                        }
                    }
                });
    }

    /**
     * When using Google Sign-In with firebase
     **/
    private void registerNewUserToFirebase(boolean isNewUser, User loggedInUser) {
        databaseRef
                .child(Constants.USER_TABLE)
                .child(loggedInUser.getAuthUid())
                .setValue(loggedInUser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        if (isNewUser) {
                            LeaderboardModel leaderboardModel = new LeaderboardModel(loggedInUser.getId(), loggedInUser.getAuthUid(),
                                    loggedInUser.getUserName(), loggedInUser.getUserPhotoUrl(),
                                    0);
                            databaseRef
                                    .child(Constants.LEADERBOARD_TABLE)
                                    .child(loggedInUser.getAuthUid())
                                    .setValue(leaderboardModel);

                            UserWalletDataModel walletDataModel = new UserWalletDataModel(
                                    loggedInUser.getId(), loggedInUser.getAuthUid(), 0);
                            databaseRef
                                    .child(Constants.USER_WALLET)
                                    .child(loggedInUser.getAuthUid())
                                    .setValue(walletDataModel);
                        }

                        checkAndClearDeletedAccountTableForThisUser();
                        initializeFetchRemoteConfig();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        databaseRef.child(Constants.BLOCKED_USER_TABLE).child(loggedInUser.getAuthUid()).setValue(true);
                        signOut();
                        showAccountBlockedDialog();
                    }
                });
    }

    private void checkAndClearDeletedAccountTableForThisUser() {
        databaseRef
                .child(Constants.DELETED_ACCOUNT)
                .child(UserContext.getLoggedInUser().getId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        if (snapshot.getValue() == null) {

                        } else {
                            databaseRef
                                    .child(Constants.DELETED_ACCOUNT)
                                    .child(UserContext.getLoggedInUser().getId())
                                    .removeValue();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
    }

    private void setUserData_sPref(User loggedInUser) {
        editorSPref.putString(Constants.SP_USER_ID, loggedInUser.getId());
        editorSPref.putString(Constants.SP_USER_NAME, loggedInUser.getUserName());
        editorSPref.putString(Constants.SP_USER_EMAIL, loggedInUser.getUserEmail());
        editorSPref.putString(Constants.SP_USER_GOOGLE_ID, loggedInUser.getUserGoogleAccountId());
        editorSPref.putString(Constants.SP_USER_PHOTO_URL, loggedInUser.getUserPhotoUrl());
        editorSPref.putString(Constants.SP_USER_ADVER_ID, googleAdverId);
        editorSPref.putString(Constants.SP_USER_AUTH_UID, loggedInUser.getAuthUid());
        editorSPref.commit();
    }

    private void createSQLDatabaseTable(User user) {
        Map<String, AllGameInOne> allGameMap = dbHelper.getUserAllGameData(user.getId());
        AllGameInOne gameData = null;
        for (Game game : Game.values()) {
            if (allGameMap == null || !allGameMap.containsKey(game.getId())) {
                gameData = new AllGameInOne(user.getId(), game.getId(),
                        Constants.DEFAULT_CHANCE_LEFT, Constants.DEFAULT_COINS, Constants.DEFAULT_CASH);
                dbHelper.insertFreeAdGameData(gameData);
            }
            if(gameData != null) {
                /** Set UserContext for new row inserted in the table **/
                FirebaseDataService.setAllGameInOneMapByGameId(game.getId(), gameData);
            } else {
                /** Set UserContext for existing game in the table **/
                FirebaseDataService.setAllGameInOneMapByGameId(game.getId(), allGameMap.get(game.getId()));
            }
        }
    }

    private void showWelcomeCustomToast(String accountDisplayName) {
        LayoutInflater inflater = getLayoutInflater();
        View rootView1 = inflater.inflate(R.layout.layout_toast_custom,
                (ConstraintLayout) findViewById(R.id.custom_toast_constraint));

        TextView toastText = rootView1.findViewById(R.id.custom_toast_text);
        toastText.setText(new StringBuffer().append("Welcome back ").append(accountDisplayName).toString());
        Toast toast = new Toast(this);
        toast.setGravity(Gravity.BOTTOM, 0, 100);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(rootView1);
        toast.show();
    }

    private void showTermsPrivacy_bsDialog() {
        BottomSheetDialog default_bsDialog = new BottomSheetDialog(this, R.style.bottomSheetDialog);
        default_bsDialog.setContentView(R.layout.layout_bsdialog_default);
        default_bsDialog.getDismissWithAnimation();
        default_bsDialog.setCancelable(true);
        default_bsDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        default_bsDialog.show();

        TextView terms_of_service = default_bsDialog.findViewById(R.id.default_bsdialog_textView1);
        TextView privacy_policy = default_bsDialog.findViewById(R.id.default_bsdialog_textView2);

        terms_of_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String termUrl = getString(R.string.app_terms_of_service_url);
                openURL_in_CCT(termUrl);
            }
        });

        privacy_policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String privacyUrl = getString(R.string.app_privacy_policy_url);
                openURL_in_CCT(privacyUrl);
            }
        });
    }

    private void openURL_in_CCT(String cctUrl) {
        int cctToolbarColor = getResources().getColor(R.color.darker_blue_app_theme);

        CustomTabColorSchemeParams setCCTBarColors = new CustomTabColorSchemeParams.Builder()
                .setToolbarColor(cctToolbarColor)
                .build();

        CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder()
                .setDefaultColorSchemeParams(setCCTBarColors)
                .build();
        customTabsIntent.launchUrl(this, Uri.parse(cctUrl));
    }

    private void showFirebaseDBCustomToast() {
        LayoutInflater inflater = getLayoutInflater();
        View rootView1 = inflater.inflate(R.layout.layout_toast_custom,
                (ConstraintLayout) findViewById(R.id.custom_toast_constraint));

        TextView toastText = rootView1.findViewById(R.id.custom_toast_text);
        toastText.setText(getString(R.string.signup_firebase_error_msg));
        Toast toast = new Toast(this);
        toast.setGravity(Gravity.BOTTOM, 0, 100);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(rootView1);
        toast.show();
    }

    /**
     * Need to use this method for fraud account
     **/
    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        firebaseAuth.signOut();
                    }
                });
    }

    private void showAccountBlockedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View getLayout_rootView = LayoutInflater.from(this).inflate(R.layout.layout_dialog_blocked_user,
                (ConstraintLayout) findViewById(R.id.constraint_dialog_blocked_user));
        builder.setView(getLayout_rootView);
        builder.setCancelable(false);

        AlertDialog userBlockedDialog = builder.create();
        userBlockedDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        userBlockedDialog.show();

        FrameLayout closeBtn = userBlockedDialog.findViewById(R.id.dialog_blocked_user_btn_frame);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
                userBlockedDialog.dismiss();
            }
        });
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

    private void showNoInternetDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SignupActivity.this);
        View view = LayoutInflater.from(SignupActivity.this).inflate(R.layout.layout_dialog_no_internet,
                (ConstraintLayout) findViewById(R.id.constraint_dialog_no_internet));
        builder.setView(view);
        builder.setCancelable(false);
        android.app.AlertDialog alertDialog = builder.create();

        Button reloadActivityButton = view.findViewById(R.id.reload_activity_button);
        Button exitActivityButton = view.findViewById(R.id.exit_button);
        reloadActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(getIntent());
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

    /**
     * Checking Internet Connectivity
     **/
    private boolean internetConnected(SignupActivity signupActivity) {
        connectivityManager = (ConnectivityManager) signupActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiConnection = connectivityManager.getNetworkInfo(connectivityManager.TYPE_WIFI);
        NetworkInfo mobileDataConnection = connectivityManager.getNetworkInfo(connectivityManager.TYPE_MOBILE);

        if ((wifiConnection != null && wifiConnection.isConnected()
                || (mobileDataConnection != null && mobileDataConnection.isConnected()))) {
            return true;
        }
        return false;
    }
    /** Checking Internet Connectivity **/

    private void initializeFetchRemoteConfig() {
        showCustomProgressDialog(getString(R.string.signup_verifying_account), false);

        remoteConfig.fetchAndActivate().addOnCompleteListener(new OnCompleteListener<Boolean>() {
            @Override
            public void onComplete(@NonNull Task<Boolean> task) {
                remoteConfig.fetchAndActivate().addOnCompleteListener(new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        if (task.isSuccessful()) {
                            boolean googleForceUpdate = remoteConfig.getBoolean(Constants.GOOGLE_FORCE_UPDATE);
                            UserContext.setGoogleForceUpdate(googleForceUpdate);

                            boolean autoClean_leaderboard = remoteConfig.getBoolean(Constants.AUTO_CLEAN_LEADERBOARD);
                            UserContext.setAutoCleanLeaderboard(autoClean_leaderboard);
                            if (autoClean_leaderboard) {
                                long cleanLeaderboard_interval = remoteConfig.getLong(Constants.CLEAN_LEADERBOARD_INTERVAL);
                                UserContext.setClean_leaderboard_interval(cleanLeaderboard_interval);
                            }

                            boolean checkFraudUser = remoteConfig.getBoolean(Constants.CHECK_FRAUD_USER);
                            UserContext.setCheckFraudUser(checkFraudUser);
                            if (checkFraudUser) {
                                long userCoinBalance = remoteConfig.getLong(Constants.USER_COIN_BALANCE);
                                UserContext.setUserCoinBalance(userCoinBalance);
                            }

                            boolean isPaypalEnabled = remoteConfig.getBoolean(Constants.IS_PAYPAL_PAYMENT_ENABLED);
                            UserContext.setIsPaypalPaymentEnabled(isPaypalEnabled);
                            boolean isAmazonEnabled = remoteConfig.getBoolean(Constants.IS_AMAZON_PAYMENT_ENABLED);
                            UserContext.setIsAmazonPaymentEnabled(isAmazonEnabled);
                            boolean isUSD_1_Enabled = remoteConfig.getBoolean(Constants.USD_1_PAYMENT_ENABLED);
                            UserContext.setIsUSD_1_Disable(isUSD_1_Enabled);

                            long maxInGameCoins = remoteConfig.getLong(Constants.MAX_IN_GAME_COINS);
                            UserContext.setMaxInGameCoins((int)maxInGameCoins);
                            long minInGameCoins = remoteConfig.getLong(Constants.MIN_IN_GAME_COINS);
                            UserContext.setMinInGameCoins((int)minInGameCoins);
                            long maxGameQuizCoins = remoteConfig.getLong(Constants.MAX_GAME_QUIZ_COINS);
                            UserContext.setMaxGameQuizCoins((int)maxGameQuizCoins);
                            long minGameQuizCoins = remoteConfig.getLong(Constants.MIN_GAME_QUIZ_COINS);
                            UserContext.setMinGameQuizCoins((int)minGameQuizCoins);

                            boolean isTapjoyEnabled = remoteConfig.getBoolean(Constants.IS_TAPJOY_ENABLED);
                            UserContext.setIsTapjoy_enabled(isTapjoyEnabled);

                            boolean isAppLovinEnabled = remoteConfig.getBoolean(Constants.IS_APPLOVIN_ENABLED);
                            UserContext.setIsAppLovin_enabled(isAppLovinEnabled);

                            boolean isAppodealEnabled = remoteConfig.getBoolean(Constants.IS_APPODEAL_ENABLED);
                            UserContext.setIsAppodeal_enabled(isAppodealEnabled);
                            if (isTapjoyEnabled || isAppLovinEnabled || isAppodealEnabled) {

                                if (isTapjoyEnabled) {
                                    boolean isTapjoy_offerwall = remoteConfig.getBoolean(Constants.IS_TAPJOY_OFFERWALL);
                                    UserContext.setIsTapjoy_offerwall(isTapjoy_offerwall);

                                    Hashtable<String, Object> connectFlags = new Hashtable<String, Object>();
                                    connectFlags.put(TapjoyConnectFlag.USER_ID, UserContext.getLoggedInUser().getId());

                                    Tapjoy.connect(getApplicationContext(), getString(R.string.tJ_sdk_key),
                                            connectFlags, new TJConnectListener() {
                                                @Override
                                                public void onConnectSuccess() {
                                                    connectedToAdNetwork = true;
                                                    Tapjoy.setUserID(UserContext.getLoggedInUser().getId());
                                                    Tapjoy.setGcmSender(getString(R.string.firebase_push_sender_id));

                                                    Tapjoy.onActivityStart(SignupActivity.this);
//                                            Log.e(TAG, "Tapjoy SDK initialized successfully");
                                                }

                                                @Override
                                                public void onConnectFailure() {
                                                    connectedToAdNetwork = false;
//                                            Log.e(TAG, "Tapjoy SDK FAILED initialize");
                                                }
                                            });
                                }

                                if (isAppLovinEnabled) {
                                    boolean isAppLovinBanner = remoteConfig.getBoolean(Constants.IS_APPLOVIN_BANNER);
                                    boolean isAppLovinInterstitial = remoteConfig.getBoolean(Constants.IS_APPLOVIN_INTERSTITIAL);
                                    boolean isAppLovinRewarded = remoteConfig.getBoolean(Constants.IS_APPLOVIN_REWARDED);
                                    boolean isAppLovin_mediumNative = remoteConfig.getBoolean(Constants.IS_APPLOVIN_MEDIUM_NATIVE);

                                    UserContext.setIsAppLovin_banner(isAppLovinBanner);
                                    UserContext.setIsAppLovin_interstitial(isAppLovinInterstitial);
                                    UserContext.setIsAppLovin_rewarded(isAppLovinRewarded);
                                    UserContext.setIsAppLovin_mediumNative(isAppLovin_mediumNative);

                                    AppLovinSdkSettings appLovin_adUnit_settings = new AppLovinSdkSettings(SignupActivity.this);
                                    List<String> adUnitIds = new ArrayList<>();
                                    adUnitIds.add(getString(R.string.aL_banner_default)); // Banner Ad Unit ID
                                    adUnitIds.add(getString(R.string.aL_interstitial_default)); // Interstitial Ad Unit ID
                                    adUnitIds.add(getString(R.string.aL_rewarded_default)); // Rewarded Ad Unit ID
                                    adUnitIds.add(getString(R.string.aL_native_default_medium)); // Rewarded Ad Unit ID
                                    appLovin_adUnit_settings.setInitializationAdUnitIds(adUnitIds);

                                    AppLovinSdk appLovinSdk = AppLovinSdk.getInstance(SignupActivity.this);
                                    appLovinSdk.setMediationProvider("max");
                                    appLovinSdk.initializeSdk(SignupActivity.this, new AppLovinSdk.SdkInitializationListener() {
                                        @Override
                                        public void onSdkInitialized(final AppLovinSdkConfiguration configuration) {
                                            connectedToAdNetwork = true;
                                            AppLovinPrivacySettings.setIsAgeRestrictedUser(false, SignupActivity.this);
                                            AppLovinPrivacySettings.setDoNotSell(true, SignupActivity.this);
                                        }
                                    });
                                }

                                if (isAppodealEnabled) {
                                    boolean isAppodealBanner = remoteConfig.getBoolean(Constants.IS_APPODEAL_BANNER);
                                    boolean isAppodealInterstitial = remoteConfig.getBoolean(Constants.IS_APPODEAL_INTERSTITIAL);
                                    boolean isAppodealRewarded = remoteConfig.getBoolean(Constants.IS_APPODEAL_REWARDED);
                                    UserContext.setIsAppodeal_banner(isAppodealBanner);
                                    UserContext.setIsAppodeal_interstitial(isAppodealInterstitial);
                                    UserContext.setIsAppodeal_rewarded(isAppodealRewarded);

                                    Appodeal.initialize(SignupActivity.this, getString(R.string.apd_app_key),
                                            Appodeal.BANNER | Appodeal.INTERSTITIAL | Appodeal.REWARDED_VIDEO,
                                            true);
                                    Appodeal.setAutoCache(Appodeal.REWARDED_VIDEO, false);
                                }
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        startActivity();
                                    }
                                }, 3000);
                            } else {
                                connectedToAdNetwork = false;
                                startActivity();
                            }
                        } else {
                        }
                    }
                });
            }
        });
    }


    private void startActivity() {
        boolean showLanguageSelection = readSPref.getBoolean(Constants.SP_SHOW_LANGUAGE_ACTIVITY, true);

        if (connectedToAdNetwork) {
            showCustomToast(new StringBuffer()
                    .append(getString(R.string.welcome_text)).append(" ")
                    .append(firebaseAuth.getCurrentUser().getDisplayName()).toString());
            progressDialog.dismiss();
            if (showLanguageSelection) {
                startActivity(new Intent(SignupActivity.this, LanguageSelectionActivity.class));
            } else {
                startActivity(new Intent(SignupActivity.this, MainActivity.class));
            }
        } else {
            showCustomToast(new StringBuffer()
                    .append(getString(R.string.welcome_text)).append(" ")
                    .append(firebaseAuth.getCurrentUser().getDisplayName()).toString());
            progressDialog.dismiss();
            if (showLanguageSelection) {
                startActivity(new Intent(SignupActivity.this, LanguageSelectionActivity.class));
            } else {
                startActivity(new Intent(SignupActivity.this, MainActivity.class));
            }
        }
    }



    private void gettingAdvertisingId() {
        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                AdvertisingIdClient.Info idInfo = null;
                try {
                    idInfo = AdvertisingIdClient.getAdvertisingIdInfo(getApplicationContext());
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                String advertId = null;
                try {
                    advertId = idInfo.getId();
                    googleAdverId = idInfo.getId();
                    UserContext.setUserAdverId(googleAdverId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return advertId;
            }

            @Override
            protected void onPostExecute(String advertId) {
            }
        };
        task.execute();
    }

    private void showCustomProgressDialog(String dialog_msg, boolean showCloseBtn) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View getLayout_rootView = LayoutInflater.from(this).inflate(R.layout.layout_dialog_progress_bar_white_long,
                (ConstraintLayout) findViewById(R.id.constraint_dialog_progress_bar_black));
        builder.setView(getLayout_rootView);
        builder.setCancelable(false);

        progressDialog = builder.create();
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressDialog.show();

        TextView progressDialog_msg = getLayout_rootView.findViewById(R.id.dialog_progressBar_black_msgText);
        progressDialog_msg.setText(dialog_msg);

        ProgressBar progressDialog_progressCircle = getLayout_rootView.findViewById(R.id.dialog_progressBar_black_progressCircle);
        ImageView progressDialog_closeBtn = getLayout_rootView.findViewById(R.id.dialog_progressBar_black_closeBtn);
        if (showCloseBtn) {
            progressDialog_closeBtn.setVisibility(View.VISIBLE);
            progressDialog_progressCircle.setVisibility(View.GONE);
        } else {
            progressDialog_closeBtn.setVisibility(View.GONE);
            progressDialog_progressCircle.setVisibility(View.VISIBLE);
        }

        progressDialog_closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExit) {
            super.onBackPressed();
            finishAffinity();
            overridePendingTransition(R.anim.anim_enter_from_left, R.anim.anim_exit_to_left);
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

    @Override
    protected void onStop() {
        Tapjoy.onActivityStop(SignupActivity.this);
        super.onStop();
    }

}