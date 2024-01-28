package dummydata.android.sqlUserGameData;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import dummydata.android.model.AllGameInOne;
import dummydata.android.userData.UserContext;

import java.util.HashMap;
import java.util.Map;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "dummydata.db";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "free_ad_game_table";
    private static final String USER_ID_COL = "user_id";
    private static final String GAME_ID_COL = "game_id";
    private static final String CHANCE_LEFT_COL = "chance_left";
    private static final String COINS_COL = "coins";
    private static final String CASH_COL = "total_cash";

    public DBHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String freeAdGameTable = "create Table " + TABLE_NAME + "("
                + USER_ID_COL + " TEXT " + " NOT NULL,"
                + GAME_ID_COL + " TEXT " + " NOT NULL, "
                + CHANCE_LEFT_COL + " TEXT " + " NOT NULL, "
                + COINS_COL + " TEXT " + " NOT NULL, "
                + CASH_COL + " TEXT " + " NOT NULL DEFAULT 0, "
                + " CONSTRAINT " + " unique_user_game UNIQUE ( " + USER_ID_COL + " , " + GAME_ID_COL + " ))";
        db.execSQL(freeAdGameTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        int upgradeTo = oldVersion + 1;
        switch (upgradeTo) {
            case 1:
                // Contain ALTER COMMAND FOR VERSION 1
                // version 1 SQL Table is deleted.
                Log.e("DB New Verion 1 called", "Successful");
            case 2:
                // Contain ALTER COMMAND FOR VERSION 2
                // version 2 is current version...move to next version
                Log.e("DB New Verion 2 called", "Successful");
            case 3:
                // Contain ALTER COMMAND FOR VERSION 3
                Log.d("DB New Verion 3 called", "Successful");

                String addCashColumn = "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + CASH_COL
                        + " TEXT " + " NOT NULL DEFAULT 0";
                db.execSQL(addCashColumn);
                break;
        }
    }

    /**
     * To insert new row
     *
     * @param allGameInOne contains information regarding each game within the App.
     */
    public void insertFreeAdGameData(AllGameInOne allGameInOne) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues addValues = new ContentValues();

        addValues.put(USER_ID_COL, allGameInOne.getUserId());
        addValues.put(GAME_ID_COL, allGameInOne.getGameId());
        addValues.put(CHANCE_LEFT_COL, allGameInOne.getChanceLeft());
        addValues.put(COINS_COL, allGameInOne.getCoins());
        addValues.put(CASH_COL, allGameInOne.getCash());

        db.insert(TABLE_NAME, null, addValues);
        db.close();
    }

    /**
     * To read User all game data by userId and gameId
     *
     * @param userId
     * @param gameId
     * @return object of AllGameInOne.
     */
    public AllGameInOne getFreeAdGameDataByUserIdAndGameId(String userId, String gameId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursorData = db.rawQuery("Select * from " + TABLE_NAME + " where " + USER_ID_COL + " = ? and " + GAME_ID_COL + " = ?",
                new String[]{userId, gameId});

        AllGameInOne allGameInOne = null;
        if (cursorData.moveToFirst()) {
            do {
                String chanceLeft = cursorData.getString(cursorData.getColumnIndex(CHANCE_LEFT_COL));
                String coins = cursorData.getString(cursorData.getColumnIndex(COINS_COL));
                String cash = cursorData.getString(cursorData.getColumnIndex(CASH_COL));
                allGameInOne = new AllGameInOne(userId, gameId, chanceLeft, coins, cash);
            } while (cursorData.moveToNext());
        }
        cursorData.close();
        return allGameInOne;
    }

    /**
     * To get all game data of User by userId
     *
     * @param userId
     * @return map of gameId and corresponding AllGameInOne obj.
     */
    public Map<String, AllGameInOne> getUserAllGameData(String userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursorData = db.rawQuery("Select * from " + TABLE_NAME + " where " + USER_ID_COL + " = ?",
                new String[]{userId});

        Map<String, AllGameInOne> allGameInOne = new HashMap<>();
        if (cursorData.moveToFirst()) {
            do {
                String gameId = cursorData.getString(cursorData.getColumnIndex(GAME_ID_COL));
                String chanceLeft = cursorData.getString(cursorData.getColumnIndex(CHANCE_LEFT_COL));
                String coins = cursorData.getString(cursorData.getColumnIndex(COINS_COL));
                String cash = cursorData.getString(cursorData.getColumnIndex(CASH_COL));

                allGameInOne.put(gameId, new AllGameInOne(userId, gameId, chanceLeft, coins, cash));
            } while (cursorData.moveToNext());
        }
        cursorData.close();
        return allGameInOne;
    }

    /**
     * To updating existing row data by userId and its gameId
     *
     * @param allGameInOne
     */
    public void updateFreeAdGameChanceAndCoinsData(AllGameInOne allGameInOne) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues updateValues = new ContentValues();
        if (!TextUtils.isEmpty(allGameInOne.getChanceLeft())) {
            updateValues.put(CHANCE_LEFT_COL, allGameInOne.getChanceLeft());
        }
        if (!TextUtils.isEmpty(allGameInOne.getCoins())) {
            updateValues.put(COINS_COL, allGameInOne.getCoins());
        }
        if (!TextUtils.isEmpty(allGameInOne.getCash())) {
            updateValues.put(CASH_COL, allGameInOne.getCash());
        }
        /** Update DB **/
        db.update(TABLE_NAME, updateValues, "user_id=? and game_id=?",
                new String[]{allGameInOne.getUserId(), allGameInOne.getGameId()});
        db.close();
    }

    public void resetAllGameDataByValue(String userId, String resetValue) {
        SQLiteDatabase db = this.getWritableDatabase();

        Map<String, AllGameInOne> allGameDataMap = new HashMap<>();

        ContentValues updateValues = new ContentValues();
        updateValues.put(COINS_COL, resetValue);
        updateValues.put(CASH_COL, resetValue);
        /** Update DB **/
        db.update(TABLE_NAME, updateValues, "user_id=?", new String[]{userId});
        db.close();
    }

    public void resetAllGameCashByValue(String userId, String resetValue) {
        SQLiteDatabase db = this.getWritableDatabase();

        Map<String, AllGameInOne> allGameDataMap = new HashMap<>();

        ContentValues updateValues = new ContentValues();
        updateValues.put(CASH_COL, resetValue);
        /** Update DB **/
        db.update(TABLE_NAME, updateValues, "user_id=?", new String[]{userId});
        db.close();
    }

}

