package br.com.awa.mylottery.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class LotteryProvider extends ContentProvider {

    static final int AVAILABLE_CODE = 100;
    static final int AVAILABLE_ITEM_CODE = 101;
    static final int MYCOUPONS_CODE = 200;
    static final int MYCOUPONS_ITEM_CODE = 201;
    private LotteryDbHelper mOpenHelper;
    private static final UriMatcher sUriMatcher = buildUriMatcher();


    public LotteryProvider() {
    }

    // join campaign and tickets table to bring campaign name to screen
    private static final SQLiteQueryBuilder sMyTicketCampaignQueryBuilder;
    static{
        sMyTicketCampaignQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //weather INNER JOIN location ON weather.location_id = location._id
        sMyTicketCampaignQueryBuilder.setTables(
                LotteryContract.MyCoupons.TABLE_NAME + " INNER JOIN " +
                        LotteryContract.Available.TABLE_NAME +
                        " ON " + LotteryContract.MyCoupons.TABLE_NAME +
                        "." + LotteryContract.MyCoupons.COLUMN_CAMPAIGN +
                        " = " + LotteryContract.Available.TABLE_NAME +
                        "." + LotteryContract.Available._ID);
    }

    static UriMatcher buildUriMatcher() {
        // get the passed uri and maps to a valid operation

        // no match is the base
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = LotteryContract.CONTENT_AUTHORITY;

        // for each type of uri, give a different type
        matcher.addURI(authority, LotteryContract.PATH_AVAILABLE, AVAILABLE_CODE);
        matcher.addURI(authority, LotteryContract.PATH_AVAILABLE + "/#", AVAILABLE_ITEM_CODE);
        matcher.addURI(authority, LotteryContract.PATH_MYCOUPONS, MYCOUPONS_CODE);
        matcher.addURI(authority, LotteryContract.PATH_MYCOUPONS + "/#", MYCOUPONS_ITEM_CODE);

        return matcher;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // get the database, match the uri code and insert
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";

        // delete according to the type of data
        switch (match) {
            case AVAILABLE_CODE: {
                rowsDeleted = db.delete(
                        LotteryContract.Available.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case MYCOUPONS_CODE: {
                rowsDeleted = db.delete(
                        LotteryContract.MyCoupons.TABLE_NAME, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            // Student: Uncomment and fill out these two cases
            case AVAILABLE_ITEM_CODE:
                return LotteryContract.Available.CONTENT_ITEM_TYPE;
            case AVAILABLE_CODE:
                return LotteryContract.Available.CONTENT_TYPE;
            case MYCOUPONS_ITEM_CODE:
                return LotteryContract.MyCoupons.CONTENT_ITEM_TYPE;
            case MYCOUPONS_CODE:
                return LotteryContract.MyCoupons.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // get the database, match the uri code and insert
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        // insert according to the type of data
        switch (match) {
            case AVAILABLE_CODE: {
                long _id = db.insert(LotteryContract.Available.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = LotteryContract.Available.buildAvailableCouponUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case MYCOUPONS_CODE: {
                long _id = db.insert(LotteryContract.MyCoupons.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = LotteryContract.MyCoupons.buildMyCouponUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // notify of changes
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public boolean onCreate() {
        // get the db helper
        mOpenHelper = new LotteryDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case AVAILABLE_CODE: {
                // return all available codes
                retCursor = mOpenHelper.getReadableDatabase().query(
                        LotteryContract.Available.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case AVAILABLE_ITEM_CODE: {
                // return a single item.
                // get the id from the Uri
                String id = uri.getPathSegments().get(1);
                retCursor = mOpenHelper.getReadableDatabase().query(
                        LotteryContract.Available.TABLE_NAME,
                        projection,
                        LotteryContract.Available.sWhereID,
                        new String[]{id},
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case MYCOUPONS_CODE: {
                // return all available codes
                retCursor = sMyTicketCampaignQueryBuilder.query(
                        mOpenHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case MYCOUPONS_ITEM_CODE: {
                // return a single item.
                // get the id from the Uri
                String id = uri.getPathSegments().get(1);
                retCursor = sMyTicketCampaignQueryBuilder.query(
                        mOpenHelper.getReadableDatabase(),
                        projection,
                        LotteryContract.MyCoupons.sWhereID,
                        new String[]{id},
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case AVAILABLE_CODE:
                rowsUpdated = db.update(LotteryContract.Available.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case MYCOUPONS_CODE:
                rowsUpdated = db.update(LotteryContract.MyCoupons.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int returnCount = 0;
        switch (match) {
            case AVAILABLE_CODE:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(LotteryContract.Available.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case MYCOUPONS_CODE:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(LotteryContract.MyCoupons.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }
}
