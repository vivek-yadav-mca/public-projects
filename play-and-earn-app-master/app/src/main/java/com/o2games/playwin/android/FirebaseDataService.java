package dummydata.android;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import dummydata.android.model.AllGameInOne;
import dummydata.android.model.LeaderboardModel;
import dummydata.android.model.User;
import dummydata.android.model.UserWalletDataModel;
import dummydata.android.sqlUserGameData.DBHelper;
import dummydata.android.userData.UserContext;
import com.tapjoy.TJGetCurrencyBalanceListener;
import com.tapjoy.TJSpendCurrencyListener;
import com.tapjoy.Tapjoy;

import java.util.HashMap;
import java.util.Map;

public class FirebaseDataService {

    private static Map<String, AllGameInOne> allGameInOneMap = new HashMap<>();

    private Context context;
    private DBHelper dbHelper;
    private final String sqlTotal_CashCoinsCOL = Game.TOTAL_CASH_COINS.getId();

    private final DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
    private final User loggedInUser = UserContext.getLoggedInUser();

    public FirebaseDataService(Context context) {
        this.context = context;
        this.dbHelper = new DBHelper(context);
    }

    public String getChanceAvailable(String gameEnum) {
        return getAllGameInOneMapByGameId(gameEnum).getChanceLeft();
    }

    public void updateUserChance(String gameEnum, boolean addChance) {
        AllGameInOne flipDataToSQL = getAllGameInOneMapByGameId(gameEnum);

        String finalChanceAfterFlip = null;
        int chanceBefore = Integer.parseInt(flipDataToSQL.getChanceLeft());
        int finalTotalChance;
        if (addChance) {
            // add chance after reward
            finalTotalChance = chanceBefore + 16;
        } else {
            // minus chance after use
            finalTotalChance = chanceBefore - 1;
        }

        finalChanceAfterFlip = String.valueOf(finalTotalChance);
        flipDataToSQL.setChanceLeft(finalChanceAfterFlip != null ? finalChanceAfterFlip : flipDataToSQL.getChanceLeft());
        dbHelper.updateFreeAdGameChanceAndCoinsData(flipDataToSQL);
    }

    public String getCoinBalance() {
        return getAllGameInOneMapByGameId(sqlTotal_CashCoinsCOL).getCoins();
    }

    public void updateUserCoin(boolean animateText, TextView textView, long coinsEarned) {
        AllGameInOne allGameInOneModel = getAllGameInOneMapByGameId(sqlTotal_CashCoinsCOL);
        long existingTotalCoins = Long.parseLong(allGameInOneModel.getCoins());
        long finalTotalCoins = existingTotalCoins + coinsEarned;

        LeaderboardModel leaderboardModel = new LeaderboardModel(
                loggedInUser.getId(), loggedInUser.getAuthUid(),
                loggedInUser.getUserName(), loggedInUser.getUserPhotoUrl(), finalTotalCoins);
        databaseRef
                .child(Constants.LEADERBOARD_TABLE)
                .child(loggedInUser.getAuthUid())
                .setValue(leaderboardModel);

        allGameInOneModel.setCoins(String.valueOf(finalTotalCoins));
        dbHelper.updateFreeAdGameChanceAndCoinsData(allGameInOneModel);

        if (animateText) {
            animateCoinBalance((int) existingTotalCoins, (int) finalTotalCoins, textView);
        }
    }

    public void minusUserCoin(boolean animateText, TextView textView, long coinsDeduct) {
        AllGameInOne allGameInOneModel = getAllGameInOneMapByGameId(sqlTotal_CashCoinsCOL);
        long existingTotalCoins = Long.parseLong(allGameInOneModel.getCoins());
        long finalTotalCoins = existingTotalCoins - coinsDeduct;

        LeaderboardModel leaderboardModel = new LeaderboardModel(
                loggedInUser.getId(), loggedInUser.getAuthUid(),
                loggedInUser.getUserName(), loggedInUser.getUserPhotoUrl(), finalTotalCoins);
        databaseRef
                .child(Constants.LEADERBOARD_TABLE)
                .child(loggedInUser.getAuthUid())
                .setValue(leaderboardModel);

        allGameInOneModel.setCoins(String.valueOf(finalTotalCoins));
        dbHelper.updateFreeAdGameChanceAndCoinsData(allGameInOneModel);

        if (animateText) {
            animateCoinBalance((int) existingTotalCoins, (int) finalTotalCoins, textView);
        }
    }

    private void animateCoinBalance(int fromCoins, int toCoins, TextView textView) {
        Animation scaleUp = new ScaleAnimation(1f, 1.2f, 1f, 1.2f,
                Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 1f);
        scaleUp.setFillAfter(true);
        scaleUp.setDuration(300);
        textView.startAnimation(scaleUp);

        ValueAnimator animator = ValueAnimator.ofInt(fromCoins, toCoins); //0 is min number, 600 is max number
        animator.setDuration(2000); //Duration is in milliseconds
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                textView.setText(animation.getAnimatedValue().toString());
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }
            @Override
            public void onAnimationEnd(Animator animator) {
                Animation scaleDown = new ScaleAnimation(1.2f, 1f, 1.2f, 1f,
                        Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 1f);
                scaleDown.setFillAfter(true);
                scaleDown.setDuration(300);
                textView.startAnimation(scaleDown);
            }
            @Override
            public void onAnimationCancel(Animator animator) {
            }
            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        animator.start();
    }

    public String getUserWalletBalance() {
        return getAllGameInOneMapByGameId(sqlTotal_CashCoinsCOL).getCash();
    }

    public void updateUserWalletAmount(boolean animateText, TextView textView, int userWalletAmt) {
        AllGameInOne allGameInOneModel = getAllGameInOneMapByGameId(sqlTotal_CashCoinsCOL);
        int existingTotalCash = Integer.parseInt(allGameInOneModel.getCash());
        int finalTotalCash = existingTotalCash + userWalletAmt;

        UserWalletDataModel userWalletDataModel = new UserWalletDataModel(
                loggedInUser.getId(), loggedInUser.getAuthUid(), finalTotalCash);
        databaseRef
                .child(Constants.USER_WALLET)
                .child(loggedInUser.getAuthUid())
                .setValue(userWalletDataModel);

        allGameInOneModel.setCash(String.valueOf(finalTotalCash));
        dbHelper.updateFreeAdGameChanceAndCoinsData(allGameInOneModel);

        if (animateText) {
            animateCoinBalance(existingTotalCash, finalTotalCash, textView);
        }
    }

    public void minusUserWalletAmount(boolean animateText, TextView textView, int cashDeduct) {
        AllGameInOne allGameInOneModel = getAllGameInOneMapByGameId(sqlTotal_CashCoinsCOL);
        int existingTotalCash = Integer.parseInt(allGameInOneModel.getCash());
        int finalTotalCash = existingTotalCash - cashDeduct;

        UserWalletDataModel userWalletDataModel = new UserWalletDataModel(
                loggedInUser.getId(), loggedInUser.getAuthUid(), finalTotalCash);
        databaseRef
                .child(Constants.USER_WALLET)
                .child(loggedInUser.getAuthUid())
                .setValue(userWalletDataModel);

        allGameInOneModel.setCash(String.valueOf(finalTotalCash));
        dbHelper.updateFreeAdGameChanceAndCoinsData(allGameInOneModel);

        if (animateText) {
            animateCoinBalance(existingTotalCash, finalTotalCash, textView);
        }
    }

    /**
     *
     * **/

    public AllGameInOne getAllGameInOneMapByGameId(String gameId) {
        dbHelper = new DBHelper(context);

        if(allGameInOneMap != null && allGameInOneMap.containsKey(gameId)) {
            return allGameInOneMap.get(gameId);
        } else {
            return dbHelper.getFreeAdGameDataByUserIdAndGameId(loggedInUser.getId(), gameId);
        }
    }

    public static void setAllGameInOneMapByGameId(String gameId, AllGameInOne allGameInOne) {
        if (allGameInOneMap != null) {
            allGameInOneMap.put(gameId, allGameInOne);
        }
    }

    public static Map<String, AllGameInOne> getAllGameInOneMap() {
        return allGameInOneMap;
    }

    public static void setAllGameInOneMap(Map<String, AllGameInOne> allGameInOneMap) {
        FirebaseDataService.allGameInOneMap = allGameInOneMap;
    }

    public void updateSqlCoinData(long coinAmount) {
        AllGameInOne sqlUserData = this.getAllGameInOneMapByGameId(sqlTotal_CashCoinsCOL);

        sqlUserData.setCoins(String.valueOf(coinAmount));
        dbHelper.updateFreeAdGameChanceAndCoinsData(sqlUserData);
    }

    public void updateSqlCashData(long cashAmount) {
        AllGameInOne sqlUserData = this.getAllGameInOneMapByGameId(sqlTotal_CashCoinsCOL);

        sqlUserData.setCash(String.valueOf(cashAmount));
        dbHelper.updateFreeAdGameChanceAndCoinsData(sqlUserData);
    }

    public void clearTapjoyCurrencyBalance() {
        Tapjoy.getCurrencyBalance(new TJGetCurrencyBalanceListener(){
            @Override
            public void onGetCurrencyBalanceResponse(String currencyName, int tapjoyBalance) {
                Tapjoy.spendCurrency(tapjoyBalance, new TJSpendCurrencyListener() {
                    @Override
                    public void onSpendCurrencyResponse(String currencyName, int balance) {
                    }
                    @Override
                    public void onSpendCurrencyResponseFailure(String error) {
                    }
                });
            }
            @Override
            public void onGetCurrencyBalanceResponseFailure(String error) {
            }
        });
    }

}
