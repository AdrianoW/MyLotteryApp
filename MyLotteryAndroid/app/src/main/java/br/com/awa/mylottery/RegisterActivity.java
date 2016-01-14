package br.com.awa.mylottery;

import android.accounts.AccountManager;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
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

import static br.com.awa.mylottery.LoginActivity.ARG_ACCOUNT_TYPE;
import static br.com.awa.mylottery.LoginActivity.PARAM_USER_PASS;

/**
 * A Register screen that offers login via email/password.
 */
public class RegisterActivity extends AppCompatActivity{

    private static final String TAG = RegisterActivity.class.getSimpleName();

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private EditText mPasswordConfirmView;
    private View mProgressView;
    private View mLoginFormView;
    private EditText mName;
    private TextView mTerms;
    private String mAccountType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.register_email);
        mPasswordView = (EditText) findViewById(R.id.register_password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptRegister();
                    return true;
                }
                return false;
            }
        });
        mPasswordConfirmView = (EditText) findViewById(R.id.register_password_confirm);
        mTerms = (TextView) findViewById(R.id.register_terms);
        mTerms.setMovementMethod(LinkMovementMethod.getInstance());
        mTerms.setText(Html.fromHtml(getString(R.string.register_terms)));

        // save the name view
        mName = (EditText) findViewById(R.id.register_name);

        // Register 
        Button mRegisterButton = (Button) findViewById(R.id.register_button);
        mRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

        mLoginFormView = findViewById(R.id.register_form);
        mProgressView = findViewById(R.id.register_progress);

        mAccountType = getIntent().getStringExtra(ARG_ACCOUNT_TYPE);
    }

    /**
     * Attempts to register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptRegister() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        final String email = mEmailView.getText().toString();
        final String password = mPasswordView.getText().toString();
        String passwordConfirm = mPasswordConfirmView.getText().toString();
        String userName = mName.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // check if confirmation is ok
        if (!passwordConfirm.equals(password)) {
            mPasswordConfirmView.setError(getString(R.string.error_password_dont_match));
            focusView = mPasswordConfirmView;
            cancel = true;
        }

        // check if a name was set
        if (TextUtils.isEmpty(userName)) {
            mName.setError(getString(R.string.error_name));
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

            // make the call
            MyLotteryBackend
                    .getInstance()
                    .register(email,
                            password,
                            new MyLotteryBackend.VolleyCallback() {
                                @Override
                                public void onResponse(JSONObject result) {
                                    Log.d(TAG, result.toString());

                                    try {
                                        // Parsing json object response
                                        // response will be a json object
                                        showProgress(false);

                                        // get the returned token
                                        String token = result.getString("key");

                                        // create a bundle to to be used on account creation
                                        Bundle data = new Bundle();
                                        data.putString(AccountManager.KEY_ACCOUNT_NAME, email);
                                        data.putString(AccountManager.KEY_ACCOUNT_TYPE, mAccountType);
                                        data.putString(AccountManager.KEY_AUTHTOKEN, token);
                                        data.putString(PARAM_USER_PASS, password);

                                        final Intent res = new Intent();
                                        res.putExtras(data);
                                        setResult(RESULT_OK, res);
                                        finish();

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
     * As this is called with result, tell the parent that the user canceled the action
     */
    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
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

