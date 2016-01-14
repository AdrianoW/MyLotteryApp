package br.com.awa.mylottery;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import br.com.awa.mylottery.backends.MyLotteryBackend;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AccountAuthenticatorActivity {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    public static final String ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE";
    public static final String ARG_AUTH_TYPE = "AUTH_TYPE";
    public static final String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";
    public final static String PARAM_USER_PASS = "USER_PASS";
    private static final int REQ_SIGNUP = 1;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private View mRegisterView;

    public static final String TAG = MyLotteryBackend.class.getSimpleName();
    private AccountManager mAccountManager;
    private String mAuthTokenType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // get the Account manager
        mAccountManager = AccountManager.get(getBaseContext());
        mAuthTokenType = getIntent().getStringExtra(ARG_AUTH_TYPE);

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        // register button
        Button mRegisterButton = (Button) findViewById(R.id.register_button);
        mRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // Since there can only be one AuthenticatorActivity, we call the sign up activity, get his results,
                // and return them in setAccountAuthenticatorResult(). See finishLogin().
                Intent signup = new Intent(getBaseContext(), RegisterActivity.class);
                signup.putExtras(getIntent().getExtras());
                startActivityForResult(signup, REQ_SIGNUP);
            }
        });

        // register context to the Backend
        MyLotteryBackend.getInstance().setContext(getApplicationContext());
    }

    /**
     * This function will be called when the activity called for results comes back
     * @param requestCode: random code of this activity caller
     * @param resultCode: what was the result of the activity
     * @param data: data from the register activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // The sign up activity returned that the user has successfully created an account
        if (requestCode == REQ_SIGNUP && resultCode == RESULT_OK) {
            finishLogin(data);
        } else
            super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        final String email = mEmailView.getText().toString();
        final String password = mPasswordView.getText().toString();
        final String accountType = getIntent().getStringExtra(ARG_ACCOUNT_TYPE);

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);


            //mAuthTask = new UserLoginTask(email, password);
            //mAuthTask.execute((Void) null);
            MyLotteryBackend
                    .getInstance()
                    .login(email,
                            password,
                            new MyLotteryBackend.VolleyCallback() {
                                @Override
                                public void onResponse(JSONObject result) {
                                    Log.d(TAG, result.toString());

                                    Bundle data = new Bundle();
                                    try {
                                        // Parsing json object response
                                        // response will be a json object
                                        showProgress(false);

                                        // get the returned token
                                        String token = result.getString("key");

                                        // create a bundle to to be used on account creation
                                        data.putString(AccountManager.KEY_ACCOUNT_NAME, email);
                                        data.putString(AccountManager.KEY_ACCOUNT_TYPE, accountType);
                                        data.putString(AccountManager.KEY_AUTHTOKEN, token);
                                        data.putString(PARAM_USER_PASS, password);

                                        final Intent res = new Intent();
                                        res.putExtras(data);
                                        finishLogin(res);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Toast.makeText(getApplicationContext(),
                                                "Error: " + e.getMessage(),
                                                Toast.LENGTH_LONG).show();
                                    }

                                }

                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    showProgress(false);

                                    String errorMessage = new String(error.networkResponse.data);

                                    Log.d(TAG, "Error: " + error.networkResponse.statusCode + errorMessage);
                                    Toast.makeText(getApplicationContext(),
                                            errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            });
        }
    }

    /**
     * Simple email validation before posting to the server
     * @param email: must contain at least a @ symbol
     * @return
     */
    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    /**
     * The password must be at least 6 characters long
     * @param password
     * @return
     */
    private boolean isPasswordValid(String password) {
        return password.length() >= 6;
    }

    /**
     * Saves the information from the created account and calls the home activity.
     * @param intent: the data data came from the login or register activity
     */
    private void finishLogin(Intent intent) {
        Log.d(TAG, TAG + "> finishLogin");

        // get the account name, password to create and account
        String accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        String accountPassword = intent.getStringExtra(PARAM_USER_PASS);
        final Account account = new Account(accountName, intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));

        // if there is the need to create the account
        if (getIntent().getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, false)) {
            // we came from the register screen
            Log.d(TAG, TAG + "> finishLogin > addAccountExplicitly");
            String authtoken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);
            String authtokenType = mAuthTokenType;

            // Creating the account on the device and setting the auth token we got
            // (Not setting the auth token will cause another call to the server to authenticate the user)
            mAccountManager.addAccountExplicitly(account, accountPassword, null);
            mAccountManager.setAuthToken(account, authtokenType, authtoken);
        } else {
            // just a login
            Log.d(TAG, TAG + "> finishLogin > setPassword");
            mAccountManager.setPassword(account, accountPassword);
        }

        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}

