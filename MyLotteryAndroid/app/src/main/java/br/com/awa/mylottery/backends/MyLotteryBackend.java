package br.com.awa.mylottery.backends;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by adriano on 12/01/16.
 * This class will be responsible for communication with the server backend
 * use the library volley to communicate with the server
 * reference:
 *      http://www.androidhive.info/2014/09/android-json-parsing-using-volley/
 *      http://code.tutsplus.com/tutorials/an-introduction-to-volley--cms-23800
 */
public class MyLotteryBackend {
    private static MyLotteryBackend ourInstance = new MyLotteryBackend();
    private RequestQueue mRequestQueue;

    public static final String TAG = MyLotteryBackend.class.getSimpleName();
    private Context mContext;

    public static MyLotteryBackend getInstance() {
        return ourInstance;
    }

    private static String URL_BASE = "http://10.0.2.2:8000/api/";

    private MyLotteryBackend() {
    }

    public void setContext(Context context) {
        mContext = context;
    }

    private RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext);
        }

        return mRequestQueue;
    }

    private <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    private <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    private void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public interface VolleyCallback{
        void onResponse(JSONObject result);
        void onErrorResponse(VolleyError error);
    }

    /*
    * Login method
    * @ params:
    * @ username: user name to login
    * @ password: password
    * @ callback: implements interface VolleyCallback
    */
    public void login(String username, String password, final VolleyCallback callback) {
        // define the endpoint url and create the json body
        String url = URL_BASE + "api-token-auth/";
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("username", username);
            jsonBody.put("password", password);
        }catch (JSONException e) {
            e.printStackTrace();
        }

        // create volley json req
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, jsonBody, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                callback.onResponse(response);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onErrorResponse(error);
            }

        });

        // Adding request to request queue
        Log.d(TAG, "Rodando Queue");
        addToRequestQueue(jsonObjReq);
    }

}
