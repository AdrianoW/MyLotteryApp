package br.com.awa.mylottery;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


public class PreloadActivity extends AppCompatActivity {

    private MediaPlayer mPlayer;
    private static String LOG_TAG = PreloadActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preload);

        // play the sound and wait a little to show info
        new LoadViewTask().execute();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {

        super.onPostCreate(savedInstanceState);
    }


    /*
    * Async task to play the sound and wait a little
    *
     */
    private class LoadViewTask extends AsyncTask<Void, Integer, Void> {

        //Before running code in separate thread
        @Override
        protected void onPreExecute() {

        }

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
                    Log.v(LOG_TAG, "Comecou");
                    mPlayer.start();
                    this.wait(200);
                    while (mPlayer.isPlaying()){}

                    // clean things
                    Log.v(LOG_TAG, "Tocou o som");
                    mPlayer.release();
                    mPlayer = null;
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
            Intent intent = getNextActivityIntent();
            startActivity(intent);
            finish();
        }

        /*
        *  Decide where to go based on being logged or not
        * */
        private Intent getNextActivityIntent()
        {
            boolean logged = Utility.getPreferredLocation(getApplicationContext());

            // check if the client is logged
            if (logged == true) {
                return new Intent(getApplicationContext(), HomeActivity.class);
            } else {
                // client is logged, go for the main screen
                return new Intent(getApplicationContext(), LoginActivity.class);
            }
        }
    }

}
