package com.ygaps.travelapp.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ygaps.travelapp.R;
import com.ygaps.travelapp.manager.Constants;
import com.ygaps.travelapp.manager.MyApplication;
import com.ygaps.travelapp.model.LoginRequest;
import com.ygaps.travelapp.model.LoginResponse;
import com.ygaps.travelapp.network.MyAPIClient;
import com.ygaps.travelapp.network.UserService;
import com.ygaps.travelapp.utils.EditTool;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    // Constants
    private static final int RC_SIGN_IN = 0;
    public static String TAG  = "LoginActivity";
    private static final String EMAIL = "email";

    // UI references.
    private AutoCompleteTextView emailPhoneView;
    private EditText passwordView;
    private UserService userService;
    private RelativeLayout relLayout_SignInFormWithAppName, relLayout_SignUpForgotPwBtn;
    private ProgressDialog progressDialog;
    private GoogleSignInClient mGoogleSignInClient;
    private Button  button_forgotPassword;
    //private SignInButton signInButton_Google;
    private ImageButton signInButton_Google;
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            relLayout_SignInFormWithAppName.setVisibility(View.VISIBLE);
            relLayout_SignUpForgotPwBtn.setVisibility(View.VISIBLE);
        }
    };
    private CallbackManager callbackManager;
    //private LoginButton signInButton_Facebook;
    private LoginButton signInButton_Facebook;
    private ImageButton signInButton_Facebook_Fake;
    private Button btn_signUp;
    private RelativeLayout relLayout_LoginActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        relLayout_LoginActivity = findViewById(R.id.relLayout_LoginActivity);
        relLayout_LoginActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditTool.HideSoftKeyboard(LoginActivity.this);
            }
        });

        //Forgot password
        button_forgotPassword = findViewById(R.id.btn_ForgotPassword);
        button_forgotPassword.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity_Step1.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }
        );


        //Sign up
        btn_signUp=(Button)findViewById(R.id.btn_SignUp);
        btn_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });


        //printHashKey(this);

        getSupportActionBar().hide();

        /* Sign in with Google*/
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.DRIVE_APPFOLDER))
                .requestServerAuthCode(Constants.Google_ClientID)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signInButton_Google = findViewById(R.id.signInButton_Google);
        //signInButton_Google.setSize(SignInButton.SIZE_WIDE);
        signInButton_Google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.signInButton_Google:
                        signIn();
                        break;
                    // ...
                }
            }
        });
        /* --------------------------------------------------------------------------------------------- */

        /*Sign in with Facebook*/
        callbackManager = CallbackManager.Factory.create();
        //LoginManager.getInstance().logOut();
        // Callback registration
        signInButton_Facebook = findViewById(R.id.signInButton_Facebook);
        signInButton_Facebook.setPermissions(Arrays.asList(EMAIL));
        signInButton_Facebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                Toast.makeText(LoginActivity.this, "Sign in with Facebook success", Toast.LENGTH_LONG).show();
                Log.i(TAG, "access_token_facebook: " + loginResult.getAccessToken().getToken());
                SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.shared_pref_name),0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(getString(R.string.saved_access_token_facebook), loginResult.getAccessToken().getToken());
                editor.apply();
                // Build request to get access token of Google account
                OkHttpClient client = new OkHttpClient();
                RequestBody requestBody = new FormEncodingBuilder()
                        .add("accessToken", loginResult.getAccessToken().getToken())
                        .build();
                final Request request = new Request.Builder()
                        .url(Constants.APIEndpoint + "/user/login/by-facebook")
                        .post(requestBody)
                        .build();
                client.newCall(request)
                        .enqueue(new com.squareup.okhttp.Callback() {
                            @Override
                            public void onFailure(Request request, IOException e) {
                                Log.e(TAG, e.toString());
                            }

                            @Override
                            public void onResponse(com.squareup.okhttp.Response response) throws IOException {
                                try {
                                    JSONObject jsonObject = new JSONObject(response.body().string());
                                    String accessToken = jsonObject.getString("token");
                                    SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.shared_pref_name), 0);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString(getString(R.string.saved_access_token), accessToken);
                                    editor.apply();
                                    Log.i(TAG, accessToken);
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                );
            }

            @Override
            public void onCancel() {
                // App code
                Toast.makeText(LoginActivity.this, "Sign in with Facebook cancelled", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Toast.makeText(LoginActivity.this, "Sign in with Facebook error", Toast.LENGTH_LONG).show();
                Log.i(TAG, exception.getMessage());
            }
        });

        signInButton_Facebook_Fake = findViewById(R.id.signInButton_Facebook_Fake);
        signInButton_Facebook_Fake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInButton_Facebook.performClick();
            }
        });

        /* Animation */
        relLayout_SignInFormWithAppName = findViewById(R.id.relLayout_SignInFromWithAppName);
        relLayout_SignUpForgotPwBtn = findViewById(R.id.relLayout_SignInForgotPwBtn);

        handler.postDelayed(runnable, 2000);


        /* Sign in with emailPhone, password registered */
        userService = MyAPIClient.getInstance().getAdapter().create(UserService.class);

        // Set up the login form.
        emailPhoneView = findViewById(R.id.emailPhone);
        emailPhoneView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    passwordView.requestFocus();
                    handled = true;
                }
                return handled;
            }
        });

        passwordView = findViewById(R.id.password);
        passwordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if(actionId == EditorInfo.IME_ACTION_DONE) {
                    EditTool.HideSoftKeyboard(LoginActivity.this);
                    handled = true;
                }
                return handled;
            }
        });

        Button mEmailSignInButton = findViewById(R.id.signInButton);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }

    // Google
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) // Google
        {
            // The Task returned from this call is always completed, no need to attach a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
        else // Facebook
        {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    // Google
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            //String test = account.getServerAuthCode();
            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        // GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        // updateUI(account);
    }

    // Get access token of Google account
    private void updateUI(GoogleSignInAccount account) {
        //Change UI according to user data.
        if (account != null) {
            Toast.makeText(this, "Sign in with Google success", Toast.LENGTH_LONG).show();

            // Build request to get access token of Google account
            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = new FormEncodingBuilder()
                    .add("grant_type", "authorization_code")
                    .add("client_id", Constants.Google_ClientID)
                    .add("client_secret", Constants.Google_ClientSecret)
                    .add("redirect_uri","")
                    .add("code", account.getServerAuthCode())
                    .build();
            final Request request = new Request.Builder()
                    .url("https://www.googleapis.com/oauth2/v4/token")
                    .post(requestBody)
                    .build();
            client.newCall(request).enqueue(new com.squareup.okhttp.Callback() {
                        @Override
                        public void onFailure(Request request, IOException e) {
                            Log.e(TAG, e.toString());
                        }

                        @Override
                        public void onResponse(com.squareup.okhttp.Response response) throws IOException {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().string());
                                final String message = jsonObject.toString(5);
                                String accessToken = jsonObject.getString("access_token");
                                // This is access token of Google account
                                Log.i(TAG, accessToken);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
            );
        } else {
            Toast.makeText(this, "Sign in with Google error", Toast.LENGTH_LONG).show();
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // Reset errors.
        emailPhoneView.setError(null);
        passwordView.setError(null);

        // Store values at the time of the login attempt.
        String email = emailPhoneView.getText().toString();
        String password = passwordView.getText().toString();

        boolean cancel = false;
//        View focusView = null;

        // Check for a valid password
        if (TextUtils.isEmpty((password))) {
            passwordView.setError(getString(R.string.error_field_required));
            cancel =true;
        }
        else if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            passwordView.setError(getString(R.string.error_invalid_password));
            //focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            emailPhoneView.setError(getString(R.string.error_field_required));
            //focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            emailPhoneView.setError(getString(R.string.error_invalid_email));
            //focusView = mEmailView;
            cancel = true;
        }

        if(cancel == true){

        }else{
            progressDialog= new ProgressDialog(LoginActivity.this);
            progressDialog.setMessage("Please wait...");
            //To show the dialog
            progressDialog.show();

            //To dismiss the dialog
            //progressDialog.dismiss();

            final LoginRequest request = new LoginRequest();
            request.setUsername(email);
            request.setPassword(password);
            Call<LoginResponse> call = userService.login(request);

            call.enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    //Login successfully
                    if(response.code() == 200) {
                        // Save login info
                        MyAPIClient.getInstance().setAccessToken(response.body().getToken());
                        long time = (new Date()).getTime() / 1000;
                        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(getString(R.string.shared_pref_name), 0);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString(getString(R.string.saved_access_token), response.body().getToken());
                        editor.putLong(getString(R.string.saved_access_token_time), time);
                        editor.commit();

                        MyApplication app = (MyApplication) LoginActivity.this.getApplication();
                        app.setTokenInfo((response.body()));

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        LoginActivity.this.finish();
                        progressDialog.dismiss();
                    }
                    else {
                        Toast.makeText(LoginActivity.this,"Sign in failed", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    Log.d(TAG, t.getMessage());
                    Toast.makeText(LoginActivity.this,"Sign in failed", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            });
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
//        return email.contains("@");
        return true;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    public static void printHashKey(Context pContext) {
        try {
            PackageInfo info = pContext.getPackageManager().getPackageInfo(pContext.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.i(TAG, "printHashKey() Hash Key: " + hashKey);
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "printHashKey()", e);
        } catch (Exception e) {
            Log.e(TAG, "printHashKey()", e);
        }
    }
}
