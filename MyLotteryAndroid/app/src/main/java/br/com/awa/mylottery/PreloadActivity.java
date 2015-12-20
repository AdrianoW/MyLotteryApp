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

        new LoadViewTask().execute();

        // create a media player to play later the sound
        //mPlayer = mPlayer.create(getApplicationContext(), R.raw.preload2);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {

        // play the sound
        /*Log.v(LOG_TAG, "Comecou");
        mPlayer.start();
        while (mPlayer.isPlaying()){}

        Log.v(LOG_TAG, "Tocou o som");
        mPlayer.release();
        mPlayer = null;

        // go for the next activity
        Intent intent = getNextActivityIntent();
        startActivity(intent);*/
        super.onPostCreate(savedInstanceState);
    }

/*    @Override
    protected void() {
        // wait the screen to appear TODO: put in async task
        //SystemClock.sleep(100);

        // play the sound
        Log.v(LOG_TAG, "Comecou");
        mPlayer.start();
        while (mPlayer.isPlaying()){}

        Log.v(LOG_TAG, "Tocou o som");
        mPlayer.release();
        mPlayer = null;

        // go for the next activity
        Intent intent = getNextActivityIntent();
        startActivity(intent);

        super.onResume();
    }*/

    private class LoadViewTask extends AsyncTask<Void, Integer, Void> {

        //Before running code in separate thread
        @Override
        protected void onPreExecute() {

        }

        //The code to be executed in a background thread.
        @Override
        protected Void doInBackground(Void... params) {
            /* This is just a code that delays the thread execution 4 times,
             * during 850 milliseconds and updates the current progress. This
             * is where the code that is going to be executed on a background
             * thread must be placed.
             */
            try {
                //Get the current thread's token
                synchronized (this) {
                    this.wait(200);

                    // play the sound
                    MediaPlayer mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.preload2);
                    Log.v(LOG_TAG, "Comecou");
                    mPlayer.start();
                    while (mPlayer.isPlaying()){}

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

        private Intent getNextActivityIntent()
        {
            boolean logged = true;

            // check if the client is logged
            if (logged == true) {
                return new Intent(getApplicationContext(), HomeActivity.class);
            } else {
                // client is logged, go for the main screen
                return new Intent(getApplicationContext(), HomeActivity.class);
            }
        }
    }

}
