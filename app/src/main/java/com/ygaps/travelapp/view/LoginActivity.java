package com.ygaps.travelapp.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
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

import com.facebook.AccessToken;
import com.ygaps.travelapp.R;
import com.ygaps.travelapp.manager.Constants;
import com.ygaps.travelapp.manager.MyApplication;
import com.ygaps.travelapp.model.LoginRequest;
import com.ygaps.travelapp.model.LoginResponse;
import com.ygaps.travelapp.network.MyAPIClient;
import com.ygaps.travelapp.network.UserService;
import com.ygaps.travelapp.utils.CheckTool;
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
import java.util.Arrays;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    // Constants
    private static final int RC_SIGN_IN_WITH_GOOGLE = 0;
    public static String TAG  = "LoginActivity";

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

    private LoginButton signInButton_Facebook;
    private ImageButton signInButton_Facebook_Fake;
    private Button btn_signUp;
    private RelativeLayout relLayout_LoginActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if(getSupportActionBar() != null)
            getSupportActionBar().hide();
        relLayout_LoginActivity = findViewById(R.id.relLayout_LoginActivity);
        relLayout_LoginActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditTool.HideSoftKeyboard(LoginActivity.this);
            }
        });
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
        btn_signUp = findViewById(R.id.btn_SignUp);
        btn_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        /* --------------------------------------------------------------------------------------------- */

        /* Sign in with Google*/
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.DRIVE_APPFOLDER))
                .requestServerAuthCode(getString(R.string.Google_ClientID))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        signInButton_Google = findViewById(R.id.signInButton_Google);
        signInButton_Google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.signInButton_Google:
                        signInWithGoogle();
                        break;
                }
            }
        });

        /* --------------------------------------------------------------------------------------------- */

        /*Sign in with Facebook*/
        callbackManager = CallbackManager.Factory.create();
        signInButton_Facebook = findViewById(R.id.signInButton_Facebook);
        signInButton_Facebook.setPermissions(Arrays.asList("email"));
        signInButton_Facebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                Toast.makeText(LoginActivity.this, "Sign in with Facebook successfully", Toast.LENGTH_SHORT).show();
                String accessToken = loginResult.getAccessToken().getToken();
                signInWithFacebook_API(accessToken);
            }

            @Override
            public void onCancel() {
                // App code
                Toast.makeText(LoginActivity.this, "Sign in with Facebook cancelled", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Toast.makeText(LoginActivity.this, exception.toString(), Toast.LENGTH_LONG).show();
            }
        });

        signInButton_Facebook_Fake = findViewById(R.id.signInButton_Facebook_Fake);
        signInButton_Facebook_Fake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInButton_Facebook.performClick();
            }
        });

        /* --------------------------------------------------------------------------------------------- */

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
                signInWithAccount_API();
            }
        });
    }


    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN_WITH_GOOGLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN_WITH_GOOGLE) // Google
        {
            // The Task returned from this call is always completed, no need to attach a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInWithGoogleResult(task);
        }
        else // Facebook
            callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleSignInWithGoogleResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            updateUI_SignInWithGoogle(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.

            Toast.makeText(LoginActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            updateUI_SignInWithGoogle(null);
        }
    }

    private void updateUI_SignInWithGoogle(GoogleSignInAccount account) {
        //Change UI according to user data.
        if (account != null && account.getServerAuthCode() != null) {
            Toast.makeText(this, "Sign in with Google successful", Toast.LENGTH_LONG).show();
            // Build request to get access token of Google account
            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = new FormEncodingBuilder()
                    .add("grant_type", "authorization_code")
                    .add("client_id", getString(R.string.Google_ClientID))
                    .add("client_secret", getString(R.string.Google_ClientSecret))
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
                            final IOException fe = e;
                            LoginActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(LoginActivity.this, fe.toString(), Toast.LENGTH_SHORT).show();
                                }
                            });

                        }

                        @Override
                        public void onResponse(com.squareup.okhttp.Response response) throws IOException {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().string());
                                String accessToken = jsonObject.getString("access_token");
                                signInWithGoogle_API(accessToken);
                            } catch (JSONException e) {
                                final JSONException fe = e;
                                LoginActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(LoginActivity.this, fe.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }
            );
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void signInWithAccount_API() {
        // Reset errors.
        emailPhoneView.setError(null);
        passwordView.setError(null);
        // Store values at the time of the login attempt.
        String email = emailPhoneView.getText().toString();
        String password = passwordView.getText().toString();

        boolean cancel = false;

        // Check for a valid password
        if (TextUtils.isEmpty((password))) {
            passwordView.setError(getString(R.string.error_field_required));
            cancel =true;
        }
        else if (!TextUtils.isEmpty(password) && !CheckTool.isValidPassword(password)) {
            passwordView.setError(getString(R.string.error_invalid_password));
            cancel = true;
        }
        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            emailPhoneView.setError(getString(R.string.error_field_required));
            cancel = true;
        } else if (!CheckTool.isValidEmail(email)) {
            emailPhoneView.setError(getString(R.string.error_invalid_email));
            cancel = true;
        }
        if(!cancel){
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
                        editor.apply();

                        MyApplication app = (MyApplication) LoginActivity.this.getApplication();
                        app.setTokenInfo((response.body()));

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        LoginActivity.this.finish();
                        progressDialog.dismiss();
                    }
                    else if (response.code() == 404) {
                        Toast.makeText(LoginActivity.this,"Wrong email/phone or password", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                    else {
                        Toast.makeText(LoginActivity.this, getString(R.string.error_unknown), Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    Toast.makeText(LoginActivity.this,"Sign in failed", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            });
        } else
        {
            Toast.makeText(LoginActivity.this, "Sign in canceled", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
    }

    private void signInWithFacebook_API(String accessToken) {
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormEncodingBuilder()
                .add("accessToken", accessToken)
                .build();
        final Request request = new Request.Builder()
                .url(Constants.APIEndpoint + "/user/login/by-facebook")
                .post(requestBody)
                .build();
        client.newCall(request)
                .enqueue(new com.squareup.okhttp.Callback() {
                             @Override
                             public void onFailure(Request request, IOException e) {
                                 final IOException fe = e;
                                 LoginActivity.this.runOnUiThread(new Runnable() {
                                     @Override
                                     public void run() {
                                         Toast.makeText(LoginActivity.this, fe.toString() , Toast.LENGTH_SHORT).show();
                                     }
                                 });
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

                                     Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                     intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                     startActivity(intent);
                                     finish();
                                 } catch (JSONException e) {
                                     final JSONException fe = e;
                                     LoginActivity.this.runOnUiThread(new Runnable() {
                                         @Override
                                         public void run() {
                                             Toast.makeText(LoginActivity.this, fe.toString(), Toast.LENGTH_SHORT).show();
                                         }
                                     });
                                 }
                             }
                         }
                );
    }

    private void signInWithGoogle_API(String accessToken) {
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormEncodingBuilder()
                .add("accessToken", accessToken)
                .build();
        Request request = new Request.Builder()
                .url(Constants.APIEndpoint + "/user/login/by-google")
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(new com.squareup.okhttp.Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                final IOException fe = e;
                LoginActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginActivity.this, fe.toString(),Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(com.squareup.okhttp.Response response) throws IOException {
                if (response.code() == 200) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        String accessToken = jsonObject.getString("token");
                        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.shared_pref_name), 0);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(getString(R.string.saved_access_token), accessToken);
                        editor.apply();
                        LoginActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, "Sign in with google successful",Toast.LENGTH_SHORT).show();
                            }
                        });
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        LoginActivity.this.finish();
                    }
                    catch (JSONException e) {
                        final JSONException fe = e;
                        LoginActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, fe.toString(),Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                else if (response.code() == 400 || response.code() == 500) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        final String message = jsonObject.getString("message");
                        LoginActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, message,Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    catch (JSONException e) {
                        final JSONException fe = e;
                        LoginActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, fe.toString(),Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        });
    }
}
