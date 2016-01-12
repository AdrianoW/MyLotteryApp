package br.com.awa.mylottery.backends;

import android.content.Context;
import android.widget.Toast;

import java.io.IOException;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import retrofit.http.Body;
import retrofit.http.POST;

/**
 * Created by adrianowalmeida on 12/01/16.
 * references:
 *      http://www.iayon.com/consuming-rest-api-with-retrofit-2-0-in-android/
 *      https://github.com/codepath/android_guides/wiki/Consuming-APIs-with-Retrofit
 *      http://inthecheesefactory.com/blog/retrofit-2.0/en
 */
public class MyLotteryBackendServer {

    private MyLotteryService mService;
    public static final String BASE_URL = "http://10.0.2.2:8000/api";

    /**
     * Class to handle requests to the server. The functions should do all the sync in the
     * background
     */
    public MyLotteryBackendServer() {

        // create the service
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                //.client(okClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mService = retrofit.create(MyLotteryService.class);
    }

    /**
     * Create the interface of service
     */
    private interface MyLotteryService {
        @POST("sign_up/")
        Call<UserCreation> register(@Body String name, @Body String pass);

    }

    /**
     * User signup response class
     */
    public class UserCreation {

        String username;
        String password;

        @Override
        public String toString() {
            return(username);
        }
    }

    /**
     * User Token response class
     */
    public class UserToken {

        String token;

        @Override
        public String toString() {
            return(token);
        }
    }

    /**
     * SingIn to the server, receiving a token back. This should be used to communicate and
     * get information
     */
    public String singIn(String username, String pass) {

        return "";
    }

    /**
     * Register the user in the system. Must call signin later
     */
    public UserCreation register(Context context, String username, String pass, String email) {
        // create the call and call sync
        Call<UserCreation> call = mService.register(username, pass);

        // effectively make the call
        UserCreation user = null;
        try {
            // get the user back
            Response<UserCreation> response = call.execute();
            user = response.body();
        } catch (IOException e ){
            // return null as there was an error
            Toast.makeText(context, "Error:" + e.toString(), Toast.LENGTH_LONG);
            return null;
        }

        return user;
    }

    /**
     * Get all opened coupons that can be bought
     */
    public void getOpenedCoupons() {

    }

    /**
     * Get all user's coupons
     */
    public void getMyCoupons() {

    }

    /**
     * Buy the coupon
     */
    public void buyCoupon() {

    }


}
