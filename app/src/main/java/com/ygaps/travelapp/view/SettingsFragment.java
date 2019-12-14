package com.ygaps.travelapp.view;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.ygaps.travelapp.R;
import com.ygaps.travelapp.manager.Constants;
import com.ygaps.travelapp.utils.ImageFilePath;
import com.facebook.login.LoginManager;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.RequestBody;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

//import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;

public class SettingsFragment extends Fragment {

    private final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

    private Button signOutButton;
    private Spinner spinner_SettingLanguage;
    private TextView textView_UserName;
    private Button button_EditProfile;
//    private CircleImageView imageView_AccountProfile;

    private static int RESULT_LOAD_IMAGE = 1;
    private String accessToken = null;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_settings, container, false);
        textView_UserName = view.findViewById(R.id.textView_UserName);
        signOutButton = view.findViewById(R.id.signOutButton);
        button_EditProfile = view.findViewById(R.id.button_EditProfile);
//        imageView_AccountProfile = view.findViewById(R.id.imageView_AccountProfile);


        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sign out facebook
                LoginManager.getInstance().logOut();

                SharedPreferences sharedPref = v.getContext().getApplicationContext().getSharedPreferences(getString(R.string.shared_pref_name), 0);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(getString(R.string.saved_access_token),"");
                editor.apply();

                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                ((Activity)v.getContext()).finish();
            }
        });

        button_EditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

//        imageView_AccountProfile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
//                photoPickerIntent.setType("image/*");
//                startActivityForResult(photoPickerIntent, RESULT_LOAD_IMAGE);
//            }
//        });

        spinner_SettingLanguage = view.findViewById(R.id.spinner_SettingLanguage);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(view.getContext(),
                android.R.layout.simple_spinner_item);
        arrayAdapter.add("English");
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_SettingLanguage.setAdapter(arrayAdapter);

        SharedPreferences sharedPref = view.getContext().getApplicationContext().getSharedPreferences(getString(R.string.shared_pref_name), 0);
        accessToken = sharedPref.getString(getString(R.string.saved_access_token), null);
        if(accessToken!= null) {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(Constants.APIEndpoint + "/user/info")
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .header("Authorization", accessToken)
                    .build();


            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    //Log.i("Error: ",e.getMessage());
                    ((Activity)view.getContext()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(view.getContext(), getString(R.string.error_check_network_connection), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        final String fullName = jsonObject.getString("fullName");
                        final String avatar = jsonObject.getString("avatar");
                        ((Activity)view.getContext()).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(fullName != "null")
                                    textView_UserName.setText(fullName);
//                                if(avatar != "null")
//                                    new DownloadImageTask((imageView_AccountProfile))
//                                            .execute(avatar);
                            }
                        });
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        ((Activity)view.getContext()).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(view.getContext(), getString(R.string.error_unknown), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                }
            });
        }
        return view;
    }

    String filePath = null;
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            filePath = ImageFilePath.getPath(getActivity(), data.getData());
            if(ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                }else {
                    ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
            }
            else {
                // Permission has already been granted
                sendFileToAPI(filePath);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (filePath != null)
                        sendFileToAPI(filePath);
                } else {

                }
            }
        }
    }

    private void sendFileToAPI (String filePath) {
        if(filePath != null && accessToken != null) {
            com.squareup.okhttp.OkHttpClient client = new com.squareup.okhttp.OkHttpClient();
            com.squareup.okhttp.MediaType MEDIA_TYPE_IMAGE = MediaType.parse("image/*");

            RequestBody requestBody = new MultipartBuilder()
                    .type(MultipartBuilder.FORM)
                    .addPart(
                            Headers.of("Content-Disposition", "form-data; name=\"file\"; filename=\"" + filePath + "\""),
                            RequestBody.create(MEDIA_TYPE_IMAGE, new File(filePath))
                    )
                    .build();

            com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder()
                    .url(Constants.APIEndpoint + "/user/update-avatar")
                    .header("Authorization", accessToken)
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new com.squareup.okhttp.Callback() {
                @Override
                public void onFailure(com.squareup.okhttp.Request request, IOException e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), getString(R.string.error_unknown), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onResponse(com.squareup.okhttp.Response response) throws IOException {
                    if(response.code() == 200 || response.code() == 500) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            final String message = jsonObject.getString("message");
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }catch (JSONException e) {
                            e.printStackTrace();
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getContext(), getString(R.string.error_unknown), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }
            });
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
//        CircleImageView bmImage;

//        public DownloadImageTask(CircleImageView bmImage) {
//            this.bmImage = bmImage;
//        }

        protected Bitmap doInBackground(String... urls) {
            String urlDisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urlDisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                //Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

//        protected void onPostExecute(Bitmap result) {
//            bmImage.setImageBitmap(result);
//        }
    }


}
