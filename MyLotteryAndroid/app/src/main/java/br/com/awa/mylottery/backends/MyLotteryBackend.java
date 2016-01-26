package br.com.awa.mylottery.backends;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

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
    private String mToken;

    public static MyLotteryBackend getInstance() {
        return ourInstance;
    }

    private static String URL_BASE = "http://localhost:8000/api/";
    public static String TOKEN_ACCESS = "User Access";
    private static String PATH_AVAILABLE = "campaigns/";
    private static String PATH_PURCHASES = "purchases/";

    private MyLotteryBackend() {
    }

    public void setContext(Context context) {
        mContext = context;
    }
    public void setToken(String token) {
        mToken = token;
    }
    public String getToken() {
        return mToken;
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

    public interface VolleyArrayCallback{
        void onResponse(JSONArray result);
        void onErrorResponse(VolleyError error);
    }
    /*
    * Login method
    * @ params:
    * @ username: user name to login
    * @ password: password
    * @ callback: implements interface VolleyCallback
    */
    public void login(String email, String password, final VolleyCallback callback) {
        // define the endpoint url and create the json body
        String url = URL_BASE + "rest-auth/login/";
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("email", email);
            jsonBody.put("password", password);
        }catch (JSONException e) {
            e.printStackTrace();
        }

        // make non authenticated request for an object
        makeObjectRequest(Request.Method.POST, url, jsonBody, callback);
    }

    /*
    * Register method
    * @ params:
    * @ email: user name to login
    * @ password1: password first time
    * @ password2: password second time
    * @ callback: implements interface VolleyCallback
    */
    public void register(String email, String password, final VolleyCallback callback) {
        // define the endpoint url and create the json body
        String url = URL_BASE + "rest-auth/registration/";
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("email", email);
            jsonBody.put("password1", password);
            jsonBody.put("password2", password);
        }catch (JSONException e) {
            e.printStackTrace();
        }

        // make non authenticated request for an object
        makeObjectRequest(Request.Method.POST, url, jsonBody, callback);
    }

    public void getAvailableTickets(final VolleyArrayCallback callback) {
        // define the endpoint url and create the json body
        String url = URL_BASE + PATH_AVAILABLE;

        // make authenticated, array request
        makeAuthenticatedArrayRequest(Request.Method.GET, url, null, callback);
    }

    public void buyTicket(int campaignId, final VolleyCallback callback) {
        // define the endpoint url and create the json body
        String url = URL_BASE + PATH_AVAILABLE + campaignId + "/buy/";

        // make the request, post, authenticated
        makeAuthenticatedObjectRequest(Request.Method.POST, url, null, callback);
    }

    public void getMyTickets(final VolleyArrayCallback callback) {
        // define the endpoint url and create the json body
        String url = URL_BASE + PATH_PURCHASES;

        // make authenticated, array request
        makeAuthenticatedArrayRequest(Request.Method.GET, url, null, callback);
    }

    public void saveGCMToken(String registration_id, final VolleyCallback callback) {
        // define the endpoint url and create the json body
        String url = URL_BASE + "gcm/";
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("registration_id", registration_id);
        }catch (JSONException e) {
            e.printStackTrace();
        }

        // save the token in the server for later communication
        makeAuthenticatedObjectRequest(Request.Method.POST, url, jsonBody, callback);
    }

    private void makeObjectRequest(int verb, String url,
                                   JSONObject jsonBody, final VolleyCallback callback) {

        // create volley json req
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(verb,
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
        Log.d(TAG, "Making non-authenticated request " + url);
        addToRequestQueue(jsonObjReq);

    }

    private void makeAuthenticatedObjectRequest(int verb, String url,
                                                 JSONObject jsonBody, final VolleyCallback callback) {

        // create volley json req
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(verb,
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

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Token " + mToken);
                return headers;
            }
        };

        // Adding request to request queue
        Log.d(TAG, "Making authenticated request " + url);
        addToRequestQueue(jsonObjReq);

    }

    private void makeAuthenticatedArrayRequest(int verb, String url,
                                               JSONArray jsonBody, final VolleyArrayCallback callback) {
        // create volley json req
        JsonArrayRequest arrayRequest = new JsonArrayRequest(verb,
                url, jsonBody, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                callback.onResponse(response);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onErrorResponse(error);
            }

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Token " + mToken);
                return headers;
            }
        };

        // Adding request to request queue
        Log.d(TAG, "Making authenticated array request " + url);
        addToRequestQueue(arrayRequest);

    }



}
