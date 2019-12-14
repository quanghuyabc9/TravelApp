package com.ygaps.travelapp.view;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ygaps.travelapp.R;
import com.ygaps.travelapp.manager.Constants;
import com.ygaps.travelapp.utils.EditTool;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.Objects;

public class EditProfileActivity extends AppCompatActivity {

    //Ui references
    EditText fullName;
    EditText email;
    EditText phone;
    TextView dob;
    EditText address;
    RadioButton genderMale;
    RadioButton genderFemale;
    Button update;
    LinearLayout updateForm;

    //User information
    String strAuthorization = null;
    String strFullName = null;
    String strEmail = null;
    String strPhone = null;
    String strAddress = null;
    String strDob = null;
    int intGender = -1;

    DatePickerDialog.OnDateSetListener onDataSetListener;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        EditTool.CustomizeActionBar("Edit Profile", EditProfileActivity.this);

        fullName = findViewById(R.id.edittext_editprofile_fullname);
        email = findViewById(R.id.edittext_editprofile_email);
        phone = findViewById(R.id.edittext_editprofile_phone);
        address = findViewById(R.id.edittext_editprofile_address);
        dob = findViewById(R.id.textview_editprofile_dob);
        genderMale =findViewById(R.id.radiobutton_editprofile_male);
        genderFemale = findViewById(R.id.radiobutton_editprofile_female);
        update = findViewById(R.id.button_editprofile_update);
        updateForm = findViewById(R.id.linearlayout_editprofile_updateform);

        SharedPreferences  sharedPref = getSharedPreferences(getString(R.string.shared_pref_name), Context.MODE_PRIVATE);
        strAuthorization = sharedPref.getString(getString(R.string.saved_access_token), null);
        getUserInformation();

        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH),
                        month = cal.get(Calendar.MONTH),
                        year = cal.get(Calendar.YEAR);
                String[] strDobSplit = dob.getText().toString().split("/");
                if(strDobSplit.length >= 3) {
                    dayOfMonth = Integer.parseInt(strDobSplit[0]);
                    month = Integer.parseInt(strDobSplit[1]) - 1;
                    year = Integer.parseInt(strDobSplit[2]);
                }
                DatePickerDialog dialog = new DatePickerDialog
                        (EditProfileActivity.this,
                                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                                onDataSetListener,
                                year, month, dayOfMonth);
                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        onDataSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                int month_tmp = month + 1;
                String date = dayOfMonth + "/" + month_tmp + "/" + year;
                dob.setText(date);
            }
        };
        genderMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRadioButtonClicked(v);
            }
        });
        genderFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRadioButtonClicked(v);
            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptUpdate();
            }
        });
        updateForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditTool.HideSoftKeyboard(EditProfileActivity.this);
            }
        });
    }

    private void getUserInformation() {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(Constants.APIEndpoint + "/user/info")
                .addHeader("Authorization", strAuthorization)
                .get()
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                    Toast.makeText(EditProfileActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if(response.code() == 200) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        strFullName = jsonObject.getString("fullName");
                        strEmail = jsonObject.getString("email");
                        strPhone = jsonObject.getString("phone");
                        strAddress = jsonObject.getString("address");
                        strDob = jsonObject.getString("dob");
                        String intGender_tmp = jsonObject.getString("gender");
                        if(intGender_tmp != "null")
                            intGender = jsonObject.getInt("gender");
                        if(strDob != "null") {
                            String[] strDobSplit = strDob.split("[-T]");
                            strDob =  strDobSplit[2] + '/' + strDobSplit[1] + '/' + strDobSplit[0];
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(strFullName != "null")
                                    fullName.setText(strFullName);
                                if(strEmail != "null")
                                    email.setText(strEmail);
                                if(strPhone != "null")
                                    phone.setText(strPhone);
                                if(strAddress != "null")
                                    address.setText(strAddress);
                                if (strDob != "null")
                                    dob.setText(strDob);
                                if(intGender == 0) genderMale.setChecked(true);
                                if(intGender == 1) genderFemale.setChecked(true);
                            }
                        });
                    }
                    catch (JSONException e) {
                        Toast.makeText(EditProfileActivity.this, e.toString(),Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radiobutton_editprofile_male:
                if (checked)
                    intGender = 0;
                    break;
            case R.id.radiobutton_editprofile_female:
                if (checked)
                    intGender = 1;
                    break;
        }
    }

    private void attemptUpdate() {
        OkHttpClient okHttpClient = new OkHttpClient();
        FormEncodingBuilder formEncodingBuilder = new FormEncodingBuilder();
        strFullName = fullName.getText().toString();
        formEncodingBuilder.add("fullName", strFullName);
        strEmail = email.getText().toString();
        formEncodingBuilder.add("email", strEmail);
        strPhone = phone.getText().toString();
        formEncodingBuilder.add("phone", strPhone);
        if(intGender != -1) {
            formEncodingBuilder.add("gender", intGender == 0 ? "0" : "1");
        }
        String[] tmp = dob.getText().toString().split("/");
        if (tmp.length >= 3) {
            String dd = tmp[0];
            String mm = tmp[1];
            String yyyy = tmp[2];
            formEncodingBuilder.add("dob", yyyy + '-' + mm + '-' + dd);
        }
        RequestBody requestBody = formEncodingBuilder.build();
        Request request = new Request.Builder()
                .url(Constants.APIEndpoint + "/user/edit-info")
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", strAuthorization)
                .post(requestBody)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(EditProfileActivity.this,getString(R.string.error_check_network_connection), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onResponse(Response response) throws IOException {
                if (response.code() == 200) {
                    Intent intent = new Intent(EditProfileActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    EditProfileActivity.this.finish();
                } else {
                    Toast.makeText(EditProfileActivity.this, getString(R.string.error_unknown), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
