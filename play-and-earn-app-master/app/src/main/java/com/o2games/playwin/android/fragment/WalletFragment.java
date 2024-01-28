package dummydata.android.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import dummydata.android.Constants;
import dummydata.android.FirebaseDataService;
import dummydata.android.Game;
import dummydata.android.R;
import dummydata.android.activity.MainActivity;
import dummydata.android.databinding.FragmentWalletBinding;
import dummydata.android.model.WalletGiftCardWithdrawModel;
import dummydata.android.sqlUserGameData.DBHelper;
import dummydata.android.userData.UserContext;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import java.util.UUID;

public class WalletFragment extends Fragment {

    View rootView;
    private MainActivity mainActivity;
    private FirebaseDataService firebaseDataService;
    private static final String TAG = WalletFragment.class.getName();
    public static final String sqlTotalCashCoinCOL = Game.TOTAL_CASH_COINS.getId();
    public static final int PAYTM_MINIMUM_WITHDRAW_LIMIT = 37500;
    private static String WITHDRAW_MODE;
    private int paypal0_aPay1_gPlay2;
    private static long COIN_DEDUCTION_AMOUNT;
    private static long CASH_ADDITION_AMOUNT;
    private static long CASH_DEDUCTION_AMOUNT;
    private static long MINIMUM_CASH_WITHDRAW_REQUIRED;
    private static long MINIMUM_COIN_REDEEM_REQUIRED;

    String sql_totalCash;
    String sql_totalCoins;
    String withdraw_Date;
    SimpleDateFormat formatUTCTime;

    Context context;
    FragmentWalletBinding binding;
    DBHelper dbHelper;
    Handler handler;
    Animation scaleDown;
    Animation scaleUp;
    Animation blinking;
    private AlertDialog progressDialog;

    public WalletFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_wallet, container, false);
        binding = FragmentWalletBinding.inflate(inflater, container, false);

        context = getActivity();
        mainActivity = MainActivity.GetInstance();
        firebaseDataService = new FirebaseDataService(getActivity());
        dbHelper = new DBHelper(getActivity());
        handler = new Handler(Looper.getMainLooper());
        scaleDown = AnimationUtils.loadAnimation(context, R.anim.anim_scale_down);
        scaleUp = AnimationUtils.loadAnimation(context, R.anim.anim_scale_up);
        blinking = AnimationUtils.loadAnimation(context, R.anim.anim_blinking_repeat);

        updateUI_CashCoinWallet();

        ViewPager2 mainViewPager = getActivity().findViewById(R.id.viewPager_main_activity);
        binding.withdrawCashAmazonPayScrollLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mainViewPager.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        binding.withdrawCashPaypalScrollLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mainViewPager.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DateTimeFormatter customFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            ZonedDateTime zonedDateTime = ZonedDateTime.now();
            withdraw_Date = customFormatter.format(zonedDateTime);
        } else {
            SimpleDateFormat formatUTCDate = new SimpleDateFormat("dd_MM_yyyy");  //for more format check at the end
            formatUTCDate.setTimeZone(TimeZone.getTimeZone("UTC"));
            withdraw_Date = formatUTCDate.format(new Date());
        }

        formatUTCTime = new SimpleDateFormat("EEE, d MMM yyyy - HH:mm:ss");  //for more format check at the end
        formatUTCTime.setTimeZone(TimeZone.getTimeZone("UTC"));
        String withdraw_UTC_Timestamp = formatUTCTime.format(new Date());

        SimpleDateFormat formatUserDevice = new SimpleDateFormat("EEE, d MMM yyyy - HH:mm:ss", Locale.getDefault());
        String userDeviceTimestamp = formatUserDevice.format(new Date());

        binding.withdrawHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomProgressDialog(context.getString(R.string.wall_frag_transaction_history_custom_toast_text));
            }
        });

        if (UserContext.getIsPaypalPaymentEnabled()) {
            binding.withdrawCashPaypalLayout1.setVisibility(View.VISIBLE);
        } else {
            binding.withdrawCashPaypalLayout1.setVisibility(View.GONE);
        }
        if (UserContext.getIsAmazonPaymentEnabled()) {
            binding.withdrawCashAmazonPayLayout1.setVisibility(View.VISIBLE);
        } else {
            binding.withdrawCashAmazonPayLayout1.setVisibility(View.GONE);
        }
        if (UserContext.getIsUSD_1_Disable()) {
            binding.withdrawCash1usdAmazonPayDisableFrame.setVisibility(View.VISIBLE);
            binding.withdrawCash1usdAmazonPayBtn.setEnabled(false);
            binding.withdrawCash1usdPaypalDisableFrame.setVisibility(View.VISIBLE);
            binding.withdrawCash1usdPaypalBtn.setEnabled(false);
        }

        binding.walletFragWithdrawBtnFL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.walletFragWithdrawCashScrollLayout.setVisibility(View.VISIBLE);
                binding.withdrawCash1usdPaypalTicket.requestFocus();

                binding.walletFragRedeemCoinScrollLayout.setVisibility(View.GONE);
            }
        });
        binding.walletFragRedeemBtnFL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.walletFragRedeemCoinScrollLayout.setVisibility(View.VISIBLE);
                binding.redeemCoinRs1Ticket.requestFocus();

                binding.walletFragWithdrawCashScrollLayout.setVisibility(View.GONE);
            }
        });

        binding.withdrawCash1usdPaypalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.withdrawCash1usdPaypalBtn.startAnimation(scaleDown);

                WITHDRAW_MODE = context.getString(R.string.wall_frag_withCash_paypal);
                paypal0_aPay1_gPlay2 = 0;
                sendCashData_to_withdrawRedeemBsDialog(1,1, getString(R.string.wallet_withdrawCash_offer_1usd_paypal_text));
            }
        });


        binding.withdrawCash1usdAmazonPayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.withdrawCash1usdAmazonPayBtn.startAnimation(scaleDown);

                WITHDRAW_MODE = context.getString(R.string.wall_frag_withCash_amazonPay);
                paypal0_aPay1_gPlay2 = 1;
                sendCashData_to_withdrawRedeemBsDialog(1, 1, getString(R.string.wallet_withdrawCash_offer_1usd_amazonPay_text));
            }
        });

        binding.redeemCoinRs1Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.redeemCoinRs1Btn.startAnimation(scaleDown);
                sendCashData_to_withdrawRedeemBsDialog(2, 1, getString(R.string.wallet_redeemCoin_offer_1rs_text));
            }
        });


        return binding.getRoot();
    }

    private void showCustomProgressDialog(String toast_msg) {
        LayoutInflater inflater = getLayoutInflater();
        View getLayout_rootView = inflater.inflate(R.layout.layout_dialog_progress_bar_white_long,
                (ConstraintLayout) getActivity().findViewById(R.id.constraint_dialog_progress_bar_black));

        ProgressBar progressDialog_progressCircle = getLayout_rootView.findViewById(R.id.dialog_progressBar_black_progressCircle);
        ImageView progressDialog_closeBtn = getLayout_rootView.findViewById(R.id.dialog_progressBar_black_closeBtn);
        progressDialog_closeBtn.setVisibility(View.GONE);
        progressDialog_progressCircle.setVisibility(View.GONE);

        TextView toastText = getLayout_rootView.findViewById(R.id.dialog_progressBar_black_msgText);
        toastText.setText(toast_msg);

        Toast toast = new Toast(getContext());
        toast.setGravity(Gravity.BOTTOM, 0, 100);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(getLayout_rootView);

        toast.show();
    }

    private void showProgressDialog(String dialog_msg, boolean showCloseBtn) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View getLayout_rootView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_dialog_progress_bar_white_long,
                (ConstraintLayout) getActivity().findViewById(R.id.constraint_dialog_progress_bar_black));
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
                ((MainActivity) getActivity()).showMainAct_interstitialFrom();
                progressDialog.dismiss();
            }
        });
    }

    private void sendCashData_to_withdrawRedeemBsDialog(int withCash1_redCoin2, int usd1_2_5_10, String title_BsDialog) {
        if (withCash1_redCoin2 == 1) {
            if (usd1_2_5_10 == 1) {
                MINIMUM_CASH_WITHDRAW_REQUIRED = 1;
                CASH_DEDUCTION_AMOUNT = 1;
            }
            if (usd1_2_5_10 == 2) {
                MINIMUM_CASH_WITHDRAW_REQUIRED = 2;
                CASH_DEDUCTION_AMOUNT = 2;
            }
            if (usd1_2_5_10 == 5) {
                MINIMUM_CASH_WITHDRAW_REQUIRED = 5;
                CASH_DEDUCTION_AMOUNT = 5;
            }
            if (usd1_2_5_10 == 10) {
                MINIMUM_CASH_WITHDRAW_REQUIRED = 10;
                CASH_DEDUCTION_AMOUNT = 10;
            }
        }

        if (withCash1_redCoin2 == 2) {
            if (usd1_2_5_10 == 1) {
                MINIMUM_COIN_REDEEM_REQUIRED = 20000;
                COIN_DEDUCTION_AMOUNT = 20000;
                CASH_ADDITION_AMOUNT = 1;
            }
            if (usd1_2_5_10 == 2) {
                MINIMUM_COIN_REDEEM_REQUIRED = 38000;
                COIN_DEDUCTION_AMOUNT = 38000;
                CASH_ADDITION_AMOUNT = 2;
            }
            if (usd1_2_5_10 == 5) {
                MINIMUM_COIN_REDEEM_REQUIRED = 92500;
                COIN_DEDUCTION_AMOUNT = 92500;
                CASH_ADDITION_AMOUNT = 5;
            }
            if (usd1_2_5_10 == 10) {
                MINIMUM_COIN_REDEEM_REQUIRED = 180000;
                COIN_DEDUCTION_AMOUNT = 180000;
                CASH_ADDITION_AMOUNT = 10;
            }
        }

        showWithdrawRedeemBsdialog(withCash1_redCoin2, title_BsDialog);
    }

    private void showWithdrawRedeemBsdialog(int withCash1_redCoin2, String withRed_bsD_title) {
        BottomSheetDialog bsDialog = new BottomSheetDialog(getActivity(), R.style.bottomSheetDialog);
        bsDialog.setContentView(R.layout.layout_bsdialog_withdraw_cash);
        bsDialog.getDismissWithAnimation();
        bsDialog.setCancelable(true);
        bsDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        bsDialog.show();

        TextView withdrawBsdialogTitle = bsDialog.findViewById(R.id.bsdialog_withdraw_cash_title);
        TextView withdrawBsdialogMessage = bsDialog.findViewById(R.id.bsdialog_withdraw_cash_message);
        FrameLayout inputDetailFrame = bsDialog.findViewById(R.id.withWinningCash_bsDialog_inputDetail_frameL);
        FrameLayout withdrawRedeemBtn = bsDialog.findViewById(R.id.bsDialog_withdraw_cash_btn_frame);
        TextView withdrawRedeemBtnText = bsDialog.findViewById(R.id.withWinningCash_bsDialog_btn_text);

        TextInputEditText withRed_bsDialog_inputName_editText = bsDialog.findViewById(R.id.withCash_bsDialog_inputName_editText);
        TextInputEditText withRed_bsDialog_inputRegEmail_editText = bsDialog.findViewById(R.id.withCash_bsDialog_inputEmail_editText);
        TextInputEditText withRed_bsDialog_inputMobileNo_editText = bsDialog.findViewById(R.id.withCash_bsDialog_inputMobile_editText);

        if (withCash1_redCoin2 == 1) {
            withdrawBsdialogTitle.setText(new StringBuilder()
                    .append(context.getString(R.string.withdraw_cash_bsdialog_title))
                    .append(" ").append(WITHDRAW_MODE).toString());

            withdrawBsdialogMessage.setText(context.getString(R.string.withdraw_cash_bsdialog_note));
            inputDetailFrame.setVisibility(View.VISIBLE);
            withdrawRedeemBtnText.setText(context.getString(R.string.wallet_withdrawCash_btnFrame_text));

        }
        if (withCash1_redCoin2 == 2) {
            withdrawBsdialogTitle.setText(withRed_bsD_title);
            withdrawBsdialogMessage.setText(new StringBuilder()
                    .append(context.getString(R.string.redeem_bsdialog_message_part1))
                    .append(" ").append(COIN_DEDUCTION_AMOUNT)
                    .append(" ").append(context.getString(R.string.redeem_bsdialog_message_part2))
                    .append(" USD \u0024").append(CASH_ADDITION_AMOUNT)
                    .append(" ").append(context.getString(R.string.redeem_bsdialog_message_part3)).toString());

            inputDetailFrame.setVisibility(View.GONE);
            withdrawRedeemBtnText.setText(context.getString(R.string.wallet_redeemCoin_btnFrame_text));
        }

    private void updateUI_CashCoinWallet() {
        sql_totalCash = firebaseDataService.getUserWalletBalance();
        binding.walletFragUserAvailableCashBalance.setText("\u0024 " + sql_totalCash);

        sql_totalCoins = firebaseDataService.getCoinBalance();
        binding.walletFragUserCoinBalance.setText(sql_totalCoins);
    }

    private long getUser_cashBalance() {
        sql_totalCash = firebaseDataService.getUserWalletBalance();
        long sql_cash = Long.parseLong(sql_totalCash);
        return sql_cash;
    }
    private long getUser_coinBalance() {
        sql_totalCoins = firebaseDataService.getCoinBalance();
        long sql_coin = Long.parseLong(sql_totalCoins);
        return sql_coin;
    }

    private void convertCoin_to_Cash() {
        firebaseDataService.minusUserCoin(true, binding.walletFragUserCoinBalance, (int) COIN_DEDUCTION_AMOUNT);
        firebaseDataService.updateUserWalletAmount(true, binding.walletFragUserAvailableCashBalance, (int) CASH_ADDITION_AMOUNT);
    }


    private void uploadCouponRequest_to_Db(TextInputEditText inputName_editText, TextInputEditText inputRegEmail_editText, TextInputEditText inputMobile_editText) {
        String userName = Objects.requireNonNull(inputName_editText.getText()).toString();
        String userEmailID = UserContext.getLoggedInUser().getUserEmail();
        String userMobileNumber = Objects.requireNonNull(inputMobile_editText.getText()).toString();

        String paymentMode = null;
        if (paypal0_aPay1_gPlay2 == 0) {
            paymentMode = "PayPal";
        }
        if (paypal0_aPay1_gPlay2 == 1) {
            paymentMode = "Amazon Gift Card";
        }
        if (paypal0_aPay1_gPlay2 == 2) {
            paymentMode = "Google Play Gift Card";
        }
        String paymentRegEmail = null;
        if (TextUtils.isEmpty(inputRegEmail_editText.getText())) {
            paymentRegEmail = context.getString(R.string.null_string_text);
        } else {
            paymentRegEmail = Objects.requireNonNull(inputRegEmail_editText.getText()).toString();
        }
        String paymentAmount = String.valueOf(CASH_DEDUCTION_AMOUNT);
        String paymentTime = formatUTCTime.format(new Date());
        String transactionId = UUID.randomUUID().toString();

        String cashBal_BeforeDed = String.valueOf(getUser_cashBalance());
        String cashBal_AfterDed = String.valueOf(getUser_cashBalance() - CASH_DEDUCTION_AMOUNT);
        String coinBal_At_Ded = String.valueOf(getUser_coinBalance());

        WalletGiftCardWithdrawModel giftCardWithdrawModel = new WalletGiftCardWithdrawModel(
                userName, userEmailID, userMobileNumber,
                paymentMode, paymentRegEmail, paymentAmount, paymentTime, transactionId,
                cashBal_BeforeDed, cashBal_AfterDed, coinBal_At_Ded);

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        databaseRef
                .child(Constants.CASH_WITHDRAWN)
                .child(withdraw_Date)
                .child(UserContext.getLoggedInUser().getId())
                .child(paymentMode)
                .child(transactionId)
                .setValue(giftCardWithdrawModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(@NonNull @NotNull Void unused) {
                        progressDialog.dismiss();

                        deductCash_to_GiftCard();
                        showCashWithdraw_ConfirmationDialog(giftCardWithdrawModel);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        showCustomProgressDialog(context.getString(R.string.wall_frag_withdraw_sending_request_failed));
                    }
                });
    }

    private void deductCash_to_GiftCard() {
        firebaseDataService.minusUserWalletAmount(true, binding.walletFragUserAvailableCashBalance, (int) CASH_DEDUCTION_AMOUNT);
    }

    private void showCashWithdraw_ConfirmationDialog(WalletGiftCardWithdrawModel giftCardWithdrawModel) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View getLayout_rootView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_dialog_withdraw_confirmation,
                (ConstraintLayout) getActivity().findViewById(R.id.withdraw_dialog_constraint));
        builder.setView(getLayout_rootView);
        builder.setCancelable(false);

        AlertDialog payment_confirmationDialog = builder.create();
        payment_confirmationDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        payment_confirmationDialog.show();

        TextView withdrawName = getLayout_rootView.findViewById(R.id.withdraw_dialog_name);
        TextView withdrawEmail = getLayout_rootView.findViewById(R.id.withdraw_dialog_email);
        TextView withdrawMobileNo = getLayout_rootView.findViewById(R.id.withdraw_dialog_mobileNumber);
        TextView withdrawMode = getLayout_rootView.findViewById(R.id.withdraw_dialog_paymentMode);
        TextView withdrawRegEmail = getLayout_rootView.findViewById(R.id.withdraw_dialog_regEmail);
        TextView withdrawAmount = getLayout_rootView.findViewById(R.id.withdraw_dialog_paymentAmount);
        TextView withdrawTime = getLayout_rootView.findViewById(R.id.withdraw_dialog_paymentTime);
        TextView withdrawTransactionId = getLayout_rootView.findViewById(R.id.withdraw_dialog_transactionID);
        Button closeBtn = getLayout_rootView.findViewById(R.id.withdraw_dialog_close_butn);

        withdrawName.setText(giftCardWithdrawModel.getUserName());
        withdrawEmail.setText(giftCardWithdrawModel.getUserEmailID());
        withdrawMobileNo.setText(giftCardWithdrawModel.getUserMobileNumber());
        withdrawMode.setText(giftCardWithdrawModel.getPaymentMode());
        if (giftCardWithdrawModel.getPaymentRegEmail().equals(context.getString(R.string.null_string_text))) {
            withdrawRegEmail.setText(context.getString(R.string.na_string_text));
        } else {
            withdrawRegEmail.setText(giftCardWithdrawModel.getPaymentRegEmail());
        }
        withdrawAmount.setText( "\u0024 " + giftCardWithdrawModel.getPaymentAmount());
        withdrawTime.setText(giftCardWithdrawModel.getPaymentTime());
        withdrawTransactionId.setText(giftCardWithdrawModel.getTransactionID());

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((MainActivity) getActivity()).internetConnected()) {
                    ((MainActivity) getActivity()).showMainAct_interstitialFrom();
                    showRatingBsDialog(giftCardWithdrawModel);
                    payment_confirmationDialog.dismiss();
                } else {
                    showCustomProgressDialog(context.getString(R.string.no_internet));
                }
            }
        });

    }

    private void showRatingBsDialog(WalletGiftCardWithdrawModel giftCardWithdrawModel) {
        BottomSheetDialog ratingBsDialog = new BottomSheetDialog(getActivity(), R.style.bottomSheetDialog);
        ratingBsDialog.setContentView(R.layout.layout_bsdialog_rating_playstore);
        ratingBsDialog.getDismissWithAnimation();
        ratingBsDialog.setCancelable(false);
        ratingBsDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        ratingBsDialog.show();

        TextView ratingBsTitle = ratingBsDialog.findViewById(R.id.rating_bs_dialog_title);
        RatingBar ratingBar = ratingBsDialog.findViewById(R.id.rating_dialog_ratingBar);
        CheckBox ratingCheckBox = ratingBsDialog.findViewById(R.id.rating_dialog_checkBox);
        FrameLayout closeBtn = ratingBsDialog.findViewById(R.id.upstox_dialog_ac_open_btn);

        ratingBsTitle.setText(new StringBuilder()
                .append(getString(R.string.wall_frag_rating_bs_withdraw_note_11)).append(" ")
                .append(giftCardWithdrawModel.getPaymentMode()).append(" ")
                .append(getString(R.string.wall_frag_rating_bs_withdraw_note_12))
                .append("\n ")
                .append("\n ")
                .append(getString(R.string.wall_frag_rating_bs_title)).toString());
        ratingCheckBox.setVisibility(View.GONE);
        closeBtn.setVisibility(View.GONE);

        ratingBar.setRating(1);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.APP_PLAYSTORE_LINK)));
                        ratingBsDialog.dismiss();
                    }
                }, 500);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI_CashCoinWallet();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}