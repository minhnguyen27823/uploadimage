package com.example.uploadimage;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.net.Socket;

import api.ApiService;
import api.Const;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    Bitmap bitmap;
    public static final int STATUS_CODE = 0;

    public static final String TAG = MainActivity.class.getName();
    private static final int MY_REQUEST_CODE = 10;
    private static final int REQUEST_CODE = 10;

    private EditText editUsername, editPassword;
    private ImageView imgFromGallery, imgFromApi;
    private Button btnSelectImage, button, btnpicture;
    private TextView tvUsername, tvPassword,tvPredict;
    private Uri mUri;
    private ProgressDialog mProgressDialog;


    private ActivityResultLauncher<Intent> mActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.e(TAG, "onActivityResult");
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data == null) {
                            return;
                        }
                        Uri uri = data.getData();
                        mUri = uri;
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                            imgFromGallery.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
    );


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUi();

        //newwww/*

        mProgressDialog=new ProgressDialog(this);
        mProgressDialog.setMessage("Please wait....");
        /////end
        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickRequestPermission();
            }
        });

      /* button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ByteArrayOutputStream byteArrayOutputStream;
                byteArrayOutputStream = new ByteArrayOutputStream();
                if (bitmap != null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                    byte[] bytes = byteArrayOutputStream.toByteArray();
                    final String base64Image = Base64.encodeToString(bytes, Base64.DEFAULT);
                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                    String url = "http://192.168.1.3/upload-image-android/upload.php";

                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                            new com.android.volley.Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    if (response.equals("success")) {
                                        Toast.makeText(getApplicationContext(), "Image uploaded", Toast.LENGTH_SHORT).show();
                                    } else
                                        Toast.makeText(getApplicationContext(), "Failed 1 to upload image", Toast.LENGTH_LONG).show();
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    ) {
                        protected Map<String, String> getParams() {
                            Map<String, String> paramV = new HashMap<>();
                            paramV.put("image", base64Image);
                            return paramV;
                        }
                    };
                    queue.add(stringRequest);


                }
            }
        });*/
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mUri != null){
                    callApiRegisterAccount();
                }
            }

            private void callApiRegisterAccount() {
                mProgressDialog.show();

                String strRealPath = RealPathUtil.getRealPath(getApplicationContext(), mUri);
                Log.e("Minh",strRealPath);

                File file = new File(strRealPath);
                RequestBody requestBodyImage = RequestBody.create(MediaType.parse("multipart/form-data"),file);
                MultipartBody.Part MultipartBodyImage = MultipartBody.Part.createFormData(Const.KEY_IMAGE,file.getName(),requestBodyImage);
                ApiService.apiService.registerAccount(MultipartBodyImage).enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        mProgressDialog.dismiss();
                        User user = response.body();
                        if(user != null){
                            Toast.makeText(MainActivity.this,"Call Api success",Toast.LENGTH_SHORT).show();
                            tvPredict.setText(user.getProbability().getG());
                        }

                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        mProgressDialog.dismiss();
                        Toast.makeText(MainActivity.this,"Call Api failed",Toast.LENGTH_SHORT).show();

                    }
                });

            }
        });


//////////////////////////////////////////////////
        btnpicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, REQUEST_CODE);
            }
        });
    }

    private void initUi() {
        imgFromGallery = findViewById(R.id.img_from_gallery);
        btnSelectImage = findViewById(R.id.btn_select_image);
        button = findViewById(R.id.btn_upload_image);
        btnpicture = findViewById(R.id.btncamera_id);
        tvPredict= findViewById(R.id.tv_predict);

    }

    private void onClickRequestPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            openGallery();
            return;
        }
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        } else {
            String[] permission = {Manifest.permission.READ_EXTERNAL_STORAGE};
            requestPermissions(permission, MY_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            }
        }

    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        mActivityResultLauncher.launch(Intent.createChooser(intent, "Select Picture"));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            mUri= uri;
            bitmap = (Bitmap) data.getExtras().get("data");
            imgFromGallery.setImageBitmap(bitmap);

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }


    }
}