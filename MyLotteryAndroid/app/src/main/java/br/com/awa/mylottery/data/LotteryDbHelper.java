package br.com.awa.mylottery.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by adriano on 22/01/16.
 */
public class LotteryDbHelper extends SQLiteOpenHelper {

    // current version
    private static final int DATABASE_VERSION = 2;

    // database file name
    static final String DATABASE_NAME = "mylottery.db";

    public LotteryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // command to create the tables
        final String SQL_CREATE_AVAILABLE_TABLE = "CREATE TABLE" + LotteryContract.Available.TABLE_NAME +
                "(" +
                LotteryContract.Available._ID + " INTEGER PRIMARY KEY" +
                LotteryContract.Available.COLUMN_NAME + " TEXT UNIQUE NOT NULL" +
                LotteryContract.Available.COLUMN_PRIZE_A + " TEXT NOT NULL" +
                LotteryContract.Available.COLUMN_PRIZE_B + " TEXT" +
                LotteryContract.Available.COLUMN_PRIZE_C + " TEXT" +
                LotteryContract.Available.COLUMN_STATUS + " TEXT" +
                ")";

        // create the tables
        db.execSQL(SQL_CREATE_AVAILABLE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // when upgrading, delete all information and start over
        db.execSQL("DROP TABLE IF EXISTS " + LotteryContract.Available.TABLE_NAME);
        onCreate(db);
    }
}
