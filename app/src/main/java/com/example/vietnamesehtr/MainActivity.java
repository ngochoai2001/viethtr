package com.example.vietnamesehtr;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.example.vietnamesehtr.databinding.ActivityMainBinding;
import com.example.vietnamesehtr.retrofit.RetrofitInstance;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private final static int CAMERA_REQUEST = 1;
    private final static int GALLERY_REQUEST = 0;
    private boolean image_selected = false;
    private ActivityMainBinding activityMainBinding;
    private  RetrofitInstance retrofitInstance = RetrofitInstance.getRetrofit();
    private String path;
    private Uri uri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());
        initView();
    }

    private void initView() {
        activityMainBinding.userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED){
                    selectedImage();
                }else{
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }

            }


        });
        activityMainBinding.textRecognitionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!image_selected){
                    Toast.makeText(MainActivity.this, "Bạn chưa chọn hình ảnh", Toast.LENGTH_LONG).show();
                }else{
                    //send request;
                    ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                    progressDialog.setTitle("Nhận diện ...");
                    progressDialog.setMessage("Đang nhận diện chữ ...");
                    progressDialog.show();
                    System.out.println("Send request to API");
                    File file = new File(path);
                    RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                    MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

                    Context context = MainActivity.this;
                    path = RealPathUtil.getRealPath(context, uri);

                    retrofitInstance.getApiInterface().sendImg(body)
                            .enqueue(new Callback<ImageRes>() {
                                @Override
                                public void onResponse(Call<ImageRes> call, Response<ImageRes> response) {
                                    if(response.isSuccessful()){
                                        progressDialog.dismiss();
                                        Toast.makeText(MainActivity.this, "Nhận diện thành công", Toast.LENGTH_LONG).show();
                                        ImageRes imageRes = response.body();
                                        activityMainBinding.textRecognitionRes.setText(imageRes.toString());
                                    }
                                }

                                @Override
                                public void onFailure(Call<ImageRes> call, Throwable t) {
                                    t.printStackTrace();
                                    progressDialog.dismiss();
                                    System.out.println("Failed"+ t.getMessage());
                                    Toast.makeText(MainActivity.this, "Nhận diện thất bại", Toast.LENGTH_LONG).show();
                                }
                            });
                }
            }
        });
    }


    public void selectedImage() {
        final CharSequence[] items = {"Take Photo", "Choose From Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Add Photo");
        builder.setIcon(R.drawable.cam_64);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (items[which].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, CAMERA_REQUEST);
                } else if (items[which].equals("Choose From Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, GALLERY_REQUEST);
                } else if (items[which].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            Context context = MainActivity.this;
            uri = data.getData();
            path = RealPathUtil.getRealPath(context, uri);

            if(requestCode == CAMERA_REQUEST){
                this.image_selected = true;

                Bitmap bitmap =  (Bitmap) data.getExtras().get("data");
                this.activityMainBinding.userImage.setImageBitmap(bitmap);

            }else if(requestCode == GALLERY_REQUEST){
                this.image_selected = true;
                uri = data.getData();
                this.activityMainBinding.userImage.setImageURI(uri);

            }
        }
    }
}