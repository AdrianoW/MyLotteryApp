package br.com.awa.mylottery.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import br.com.awa.mylottery.data.LotteryContract;

/**
 * Created by adriano on 22/01/16.
 */
public class LotterySyncService extends Service {
    private static final String TAG = LotteryContract.class.getSimpleName();
    private static final Object sSyncAdapterLock = new Object();
    private static LotterySyncAdapter sSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null) {
                sSyncAdapter = new LotterySyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sSyncAdapter.getSyncAdapterBinder();
    }
}
