package br.com.awa.mylottery;

import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import br.com.awa.mylottery.backends.MyLotteryBackend;

/**
* Activity responsible for showing the splash screen and getting the token for communication
*/
public class PreloadActivity extends AppCompatActivity {

    private static String LOG_TAG = PreloadActivity.class.getSimpleName();
    private AccountManager mAccountManager;
    private String mAccountType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preload);

        // get the account manager and the account type
        mAccountManager = AccountManager.get(this);
        mAccountType = getString(R.string.authenticator_account_type);

        // play the sound in another thread and wait a little to show info
        new LoadViewTask().execute();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    /**
    * Async task to play the sound and wait a little
    **/
    private class LoadViewTask extends AsyncTask<Void, Integer, Void> {

        //The code to be executed in a background thread.
        @Override
        protected Void doInBackground(Void... params) {
            /* This is just a code that delays the thread execution
             * during 200 milliseconds */
            try {
                //Get the current thread's token
                synchronized (this) {

                    // play the sound
                    MediaPlayer mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.preload2);
                    Log.d(LOG_TAG, "Sound started");
                    mPlayer.start();
                    this.wait(200);
                    while (mPlayer.isPlaying()) {}

                    // clean things
                    Log.d(LOG_TAG, "Sound Finished");
                    mPlayer.release();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;

        }

        //after executing the code in the thread
        @Override
        protected void onPostExecute(Void result) {
            // go for the next activity
            goToNextActivity();
        }

        /**
        *  Go to the main activity if the user is logged. Else it will call the login screen
        * */
        private void goToNextActivity()
        {
            // check if there is an account created
            final AccountManagerFuture<Bundle> future = mAccountManager
                    .getAuthTokenByFeatures(mAccountType,
                            MyLotteryBackend.ACCOUNT_ACESS,
                            null,
                            PreloadActivity.this,
                            null,
                            null,
                            new AccountManagerCallback<Bundle>() {
                                @Override
                                public void run(AccountManagerFuture<Bundle> future) {
                                    Bundle bnd;
                                    try {
                                        // get the token. If it does not exists, will call the login activity
                                        bnd = future.getResult();
                                        MyLotteryBackend.getInstance().setToken(bnd.getString(AccountManager.KEY_AUTHTOKEN));
                                        Log.d(LOG_TAG, "GetTokenForAccount Bundle is " + bnd);

                                        // starts the app
                                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));

                                    } catch (Exception e) {
                                        // an error occurred. quit with a toast
                                        e.printStackTrace();
                                        finish();
                                        Toast.makeText(getApplicationContext(),
                                                "User Cancelled Operation",
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            },
                            null);
        }
    }
}
