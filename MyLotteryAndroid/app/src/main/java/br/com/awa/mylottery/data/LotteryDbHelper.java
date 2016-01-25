package br.com.awa.mylottery.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by adriano on 22/01/16.
 */
public class LotteryDbHelper extends SQLiteOpenHelper {

    // current version
    private static final int DATABASE_VERSION = 4;

    // database file name
    static final String DATABASE_NAME = "mylottery.db";

    public LotteryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // command to create the tables
        final String SQL_CREATE_AVAILABLE_TABLE = "CREATE TABLE " + LotteryContract.Available.TABLE_NAME +
                " (" +
                LotteryContract.Available._ID + " INTEGER PRIMARY KEY, " +
                LotteryContract.Available.COLUMN_NAME + " TEXT UNIQUE NOT NULL, " +
                LotteryContract.Available.COLUMN_PRIZE_A + " TEXT NOT NULL, " +
                LotteryContract.Available.COLUMN_PRIZE_B + " TEXT, " +
                LotteryContract.Available.COLUMN_PRIZE_C + " TEXT, " +
                LotteryContract.Available.COLUMN_STATUS + " TEXT " +
                " )";

        final String SQL_CREATE_MYCOUPONS_TABLE = "CREATE TABLE " + LotteryContract.MyCoupons.TABLE_NAME +
                " (" +
                LotteryContract.MyCoupons._ID + " INTEGER PRIMARY KEY, " +
                LotteryContract.MyCoupons.COLUMN_CAMPAIGN + " INTEGER NOT NULL, " +
                LotteryContract.MyCoupons.COLUMN_TICKET_ID + " INTEGER NOT NULL, " +
                LotteryContract.MyCoupons.COLUMN_TICKET_STATUS + " INTEGER NOT NULL, " +
                LotteryContract.MyCoupons.COLUMN_TICKET_TYPE+ " INTEGER NOT NULL, " +
                LotteryContract.MyCoupons.COLUMN_DATE + " INTEGER NOT NULL, " +
                LotteryContract.MyCoupons.COLUMN_METHOD + " INTEGER NOT NULL, " +
                LotteryContract.MyCoupons.COLUMN_TOKEN + " STRING, " +
                LotteryContract.MyCoupons.COLUMN_STATUS+ " INTEGER NOT NULL, " +

                // Set up the location column as a foreign key to location table.
                " FOREIGN KEY (" + LotteryContract.MyCoupons.COLUMN_CAMPAIGN  + ") REFERENCES " +
                LotteryContract.Available.TABLE_NAME + " (" + LotteryContract.Available._ID + ") " +

                " )";

        // create the tables
        db.execSQL(SQL_CREATE_AVAILABLE_TABLE);
        db.execSQL(SQL_CREATE_MYCOUPONS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // when upgrading, delete all information and start over
        db.execSQL("DROP TABLE IF EXISTS " + LotteryContract.Available.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + LotteryContract.MyCoupons.TABLE_NAME);
        onCreate(db);
    }
}
