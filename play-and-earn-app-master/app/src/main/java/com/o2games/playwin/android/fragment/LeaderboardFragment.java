package dummydata.android.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import dummydata.android.Constants;
import dummydata.android.FirebaseDataService;
import dummydata.android.Game;
import dummydata.android.R;
import dummydata.android.Utils;
import dummydata.android.activity.MainActivity;
import dummydata.android.adapter.LeaderboardAdapter;
import dummydata.android.databinding.FragmentLeaderboardBinding;
import dummydata.android.model.LeaderboardModel;
import dummydata.android.userData.UserContext;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class LeaderboardFragment extends Fragment {

    private static LeaderboardFragment instance;
    private static final String TAG = LeaderboardFragment.class.getName();
    private MainActivity mainActivity;
    private FirebaseDataService firebaseDataService;
    private static boolean isDailyWinner;
    private static boolean isWeeklyWinner;
    public static final String sqlTotal_CashCoinsCOL = Game.TOTAL_CASH_COINS.getId();
    FragmentLeaderboardBinding binding;
    private SwipeRefreshLayout refreshLayout;

    private DatabaseReference databaseRef;
    private FirebaseAuth firebaseAuth;
    private List<LeaderboardModel> leaderboard = new ArrayList<>();
    long numberOfChildren_in_leaderboard;
    Context context;
    Animation rotate_clockwise;
    Animation blinking;
    private CountDownTimer countDownTimer;

    double decimal_wallet_bal;
    String sqlTotal_Coins;
    long userTotal_COIN;
    int numbers = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static LeaderboardFragment GetInstance() {
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLeaderboardBinding.inflate(inflater, container, false);
        View rootView = inflater.inflate(R.layout.fragment_leaderboard, container, false);

        instance = this;
        context = getActivity();
        mainActivity = MainActivity.GetInstance();
        firebaseDataService = new FirebaseDataService(getActivity());
        rotate_clockwise = AnimationUtils.loadAnimation(context, R.anim.anim_rotate_clockwise);
        blinking = AnimationUtils.loadAnimation(context, R.anim.anim_blinking_repeat);

        databaseRef = FirebaseDatabase.getInstance().getReference();
        firebaseAuth =FirebaseAuth.getInstance();
        isDailyWinner = UserContext.getIsDailyWinnerLeaderboard();
        isWeeklyWinner = UserContext.getIsWeeklyWinnerLeaderboard();

        getWalletBalance();
        startLeaderboardResultTimer();

        LeaderboardAdapter adapter = new LeaderboardAdapter(context, leaderboard, numberOfChildren_in_leaderboard, binding.leaderboardUserRanking);
        databaseRef
                .child(Constants.LEADERBOARD_TABLE)
                .orderByChild(Constants.LEADERBOARD_ORDERING_PARENT)   //userWalletBalance
                .limitToLast(100)  // limiting not giving rank for far players
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        numberOfChildren_in_leaderboard = snapshot.getChildrenCount();
                        leaderboard.clear();
                        for (DataSnapshot firebaseData : snapshot.getChildren()) {
                            LeaderboardModel modelData = firebaseData.getValue(LeaderboardModel.class);
                            leaderboard.add(modelData);
                        }
                        Collections.reverse(leaderboard);
                        adapter.notifyDataSetChanged();

                        if (UserContext.getAutoCleanLeaderboard()) {
                            if (numberOfChildren_in_leaderboard >= 10) {
                                binding.leaderboardRv.setVisibility(View.VISIBLE);
                                binding.leaderboardCleaningRefreshingMsg.setVisibility(View.GONE);
                            } else {
                                binding.leaderboardRv.setVisibility(View.GONE);
                                binding.leaderboardCleaningRefreshingMsg.setVisibility(View.VISIBLE);
                            }
                        } else {
                            binding.leaderboardRv.setVisibility(View.VISIBLE);
                            binding.leaderboardCleaningRefreshingMsg.setVisibility(View.GONE);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

        binding.leaderboardRv.setLayoutManager(new GridLayoutManager(getContext(), 1));
        binding.leaderboardRv.setAdapter(adapter);

        binding.refreshLeaderboardButtonCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.refreshLeaderboardButtonCV.startAnimation(rotate_clockwise);
                refreshLeaderboardData();
            }
        });

        /** Setting User Self data, Apart from LeaderBoard Data **/
        RequestOptions loadImage = new RequestOptions()
                .centerCrop()
                .circleCrop()  //to crop image in circle view
                .placeholder(R.drawable.user_color)
                .error(R.drawable.user_color);

        Glide.with(getActivity())
                .load(UserContext.getLoggedInUser().getUserPhotoUrl())
                .apply(loadImage)
                .into(binding.leaderboardUserPhoto);

        binding.leaderboardUserName.setText(UserContext.getLoggedInUser().getUserName());


        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        refreshLayout = view.findViewById(R.id.leaderboard_swipe_refresh_layout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLeaderboardData();
                refreshLayout.setRefreshing(false);
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }

    private void getWalletBalance() {
        sqlTotal_Coins = firebaseDataService.getCoinBalance();
        binding.leaderboardUserTotalCoins.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_bg_dollar_coin_stack_32px, 0, 0, 0);
        binding.leaderboardUserTotalCoins.setText(sqlTotal_Coins);

        userTotal_COIN = Long.parseLong(sqlTotal_Coins);

        if (UserContext.getCheckFraudUser()) {
            if (userTotal_COIN > UserContext.getUserCoinBalance()) {
                databaseRef
                        .child(Constants.BLOCKED_USER_TABLE)
                        .child(firebaseAuth.getCurrentUser().getUid())
                        .setValue(true)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(@NonNull @NotNull Void unused) {
                                signOut();
                                showAccountBlockedDialog();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull @NotNull Exception e) {
                                signOut();
                                showAccountBlockedDialog();
                            }
                        });
            }
        }
    }

    private void refreshLeaderboardData() {
        getWalletBalance();

        String userId = UserContext.getLoggedInUser().getId();
        String userName = UserContext.getLoggedInUser().getUserName();
        String userPhotoUrl = UserContext.getLoggedInUser().getUserPhotoUrl();

        String authUid = UserContext.getLoggedInUser().getAuthUid();
        LeaderboardModel modelData = new LeaderboardModel(userId, authUid, userName, userPhotoUrl, userTotal_COIN);
        databaseRef
                .child(Constants.LEADERBOARD_TABLE)
                .child(UserContext.getLoggedInUser().getAuthUid())
                .setValue(modelData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(@NonNull @NotNull Void unused) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                    }
                });

    }

    private void signOut() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        mGoogleSignInClient.signOut()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        firebaseAuth.signOut();
                    }
                });
    }

    private void showAccountBlockedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View getLayout_rootView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_dialog_blocked_user,
                (ConstraintLayout) getActivity().findViewById(R.id.constraint_dialog_blocked_user));
        builder.setView(getLayout_rootView);
        builder.setCancelable(false);

        AlertDialog userBlockedDialog = builder.create();
        userBlockedDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        userBlockedDialog.show();

        FrameLayout closeBtn = userBlockedDialog.findViewById(R.id.dialog_blocked_user_btn_frame);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finishAffinity();
                userBlockedDialog.dismiss();
            }
        });
    }


    @Override
    public void onResume() {
        getWalletBalance();
        startLeaderboardResultTimer();
        binding.leaderNoticeWeeklyContestSoonText.startAnimation(blinking);
        super.onResume();
    }
}