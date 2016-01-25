package br.com.awa.mylottery.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Vector;

import br.com.awa.mylottery.R;
import br.com.awa.mylottery.backends.MyLotteryBackend;
import br.com.awa.mylottery.data.LotteryContract;

/**
 * Created by adriano on 22/01/16.
 */
public class LotterySyncAdapter extends AbstractThreadedSyncAdapter {

    private static int SYNC_ALL = 0;
    public static final int SYNC_MYCOUPONS = 1;
    public static final int SYNC_AVAILABLE = 2;
    private static final String SYNC_FLAG = "SYNC_FLAG";
    private final String LOG_TAG = LotterySyncAdapter.class.getSimpleName();

    // Interval at which to sync with the weather, in seconds.
    // 60 seconds (1 minute) * 180 = 3 hours
    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;
    private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
    private final Context mContext;

    public LotterySyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);

        // save the context that is calling this
        mContext = context;
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "onPerformSync Starting");

        // call the server to get available tickets.
        // TODO: do not get all tickets, maybe just the new
        MyLotteryBackend.getInstance().setContext(mContext);

        int sync = extras.getInt(SYNC_FLAG);
        if (sync==SYNC_ALL || sync==SYNC_AVAILABLE) {
            // sync available tickets
            syncAvailableTickets();
        }
        if (sync==SYNC_ALL || sync==SYNC_MYCOUPONS) {
            // sync my coupons
            syncMyCoupons();
        }

    }

    private void syncAvailableTickets(){
        MyLotteryBackend.getInstance().getAvailableTickets(new MyLotteryBackend.VolleyArrayCallback() {
            @Override
            public void onResponse(JSONArray result) {
                Log.d(LOG_TAG, result.toString());

                // create content array
                Vector<ContentValues> cVVector = new Vector<ContentValues>(result.length());

                // for each campaign, create a content and insert into the vector
                ContentValues availableItem;
                for (int i = 0; i < result.length(); i++) {
                    try {
                        // get the content item from json
                        availableItem = createAvailableFromCampaign(result.getJSONObject(i));

                        // add to the vector
                        if (null != availableItem) {
                            cVVector.add(availableItem);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                // delete all information
                getContext().getContentResolver().delete(LotteryContract.Available.CONTENT_URI, null, null);

                // bulk insert information
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                getContext().getContentResolver().bulkInsert(LotteryContract.Available.CONTENT_URI, cvArray);

                Log.d(LOG_TAG, "Sync Complete. " + cVVector.size() + " Inserted");
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                // check if it is an error that came from the server
                String errorMessage;
                if (error.networkResponse != null) {
                    errorMessage = error.networkResponse.statusCode + new String(error.networkResponse.data);
                } else {
                    errorMessage = error.getMessage();
                }

                // show the message to the user
                Log.d(LOG_TAG, "Error: " + errorMessage);
                Toast.makeText(mContext,
                        errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private ContentValues createAvailableFromCampaign(JSONObject jsonObj) {

        ContentValues cv = new ContentValues();
        try{
            cv.put(LotteryContract.Available.COLUMN_NAME, jsonObj.getString("name"));
            cv.put(LotteryContract.Available.COLUMN_PRIZE_A, jsonObj.getString("prize_a"));
            cv.put(LotteryContract.Available.COLUMN_PRIZE_B, jsonObj.getString("prize_b"));
            cv.put(LotteryContract.Available.COLUMN_PRIZE_C, jsonObj.getString("prize_c"));
            cv.put(LotteryContract.Available.COLUMN_STATUS, jsonObj.getInt("status"));
            cv.put(LotteryContract.Available._ID, jsonObj.getInt("id"));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return cv;
    }

    private void syncMyCoupons() {
        MyLotteryBackend.getInstance().getMyTickets(new MyLotteryBackend.VolleyArrayCallback() {
            @Override
            public void onResponse(JSONArray result) {
                Log.d(LOG_TAG, result.toString());

                // create content array
                Vector<ContentValues> cVVector = new Vector<ContentValues>(result.length());

                // for each purchase, create a content and insert into the vector
                ContentValues myTickets;
                for (int i = 0; i < result.length(); i++) {
                    try {
                        // get the content item from json
                        myTickets = createMyCouponsFromPurchases(result.getJSONObject(i));

                        // add to the vector
                        if (null != myTickets) {
                            cVVector.add(myTickets);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                // delete all information
                getContext().getContentResolver().delete(LotteryContract.MyCoupons.CONTENT_URI, null, null);

                // bulk insert information
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                int total = getContext().getContentResolver().bulkInsert(LotteryContract.MyCoupons.CONTENT_URI, cvArray);

                Log.d(LOG_TAG, "My Coupons Sync Complete. " + total + " Inserted");
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                // check if it is an error that came from the server
                String errorMessage;
                if (error.networkResponse != null) {
                    errorMessage = error.networkResponse.statusCode + new String(error.networkResponse.data);
                } else {
                    errorMessage = error.getMessage();
                }

                // show the message to the user
                Log.d(LOG_TAG, "Error: " + errorMessage);
                Toast.makeText(mContext,
                        errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private ContentValues createMyCouponsFromPurchases(JSONObject jsonObj) {


        ContentValues cv = new ContentValues();
        try{
            JSONObject ticket = jsonObj.getJSONObject("ticket");
            cv.put(LotteryContract.MyCoupons.COLUMN_CAMPAIGN, ticket.getInt("campaign"));
            cv.put(LotteryContract.MyCoupons.COLUMN_TICKET_ID, ticket.getInt("id"));
            cv.put(LotteryContract.MyCoupons.COLUMN_TICKET_STATUS, ticket.getInt("status"));
            cv.put(LotteryContract.MyCoupons.COLUMN_TICKET_TYPE, ticket.getInt("type"));
            cv.put(LotteryContract.MyCoupons.COLUMN_DATE, LotteryContract.normalizeDate(jsonObj.getString("date")));
            cv.put(LotteryContract.MyCoupons.COLUMN_METHOD, jsonObj.getInt("method"));
            cv.put(LotteryContract.MyCoupons.COLUMN_TOKEN, jsonObj.getString("token"));
            cv.put(LotteryContract.MyCoupons.COLUMN_STATUS, jsonObj.getInt("status"));
            cv.put(LotteryContract.MyCoupons._ID, jsonObj.getInt("id"));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return cv;
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.authenticator_account_type);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context, int flagSync) {
        
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);

        if (flagSync==SYNC_ALL) {
            bundle.putInt(SYNC_FLAG, flagSync);
        }
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.authenticator_account_type), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {

        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account[] accounts = accountManager.getAccountsByType(
                context.getString(R.string.authenticator_account_type));

        /*// If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        *//*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         *//*
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            *//*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             *//*

            onAccountCreated(newAccount, context);
        }*/

        //onAccountCreated(accounts[0], context);
        return accounts[0];
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        LotterySyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.authenticator_account_type), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context, SYNC_ALL);
    }

    public static void initializeSyncAdapter(Context context) {
        syncImmediately(context, SYNC_ALL);
    }
}
