package br.com.awa.mylottery.auth;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by adriano on 13/01/16.
 * from
 *  https://github.com/Udinic/AccountAuthenticator/blob/master/src/com/udinic/accounts_authenticator_example/authentication/UdinicAuthenticatorService.java
 */
public class MyLotteryAuthenticatorService extends Service {
    @Override
    public IBinder onBind(Intent intent) {

        MyLotteryAuthenticator authenticator = new MyLotteryAuthenticator(this);
        return authenticator.getIBinder();
    }
}
