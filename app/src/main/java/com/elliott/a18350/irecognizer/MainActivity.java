package com.elliott.a18350.irecognizer;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;

import java.io.File;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;
/**
 * Using Ucrop in my project --Elliott 20175.28
 * Copyright 2017, Yalantis

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */


@RuntimePermissions
public class MainActivity extends BaseColorActivity {

    private static final String TAG = "MainActivity";
    private long exitTime;
    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_PICK = 2;
    private static final int REQUEST_RECOGNIZE = 3;

    private Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent formal_intent = getIntent();
        String action = formal_intent.getAction();
        String type = formal_intent.getType();

        if (formal_intent.getStringExtra("num") == null && formal_intent.getData() != null) {
            Log.d(TAG, "onCreate: on opening image");
            before_crop(formal_intent);
        } else if (action != null) {
            Log.d(TAG, "onCreate: on sending image");
            Log.d(TAG, "onCreate: " + type);
            if (action.equals(Intent.ACTION_SEND)) {
                Uri uri = formal_intent.getParcelableExtra(Intent.EXTRA_STREAM);
                formal_intent.setData(uri);
                before_crop(formal_intent);
            }
        } else {
            Log.d(TAG, "onCreate: on normal open");
            exitTime = System.currentTimeMillis();
        }
        com.elliott.a18350.irecognizer.MainActivityPermissionsDispatcher.init_CroperWithCheck(this);


    }

    private void ShowResult(String str, Uri uri) {
        Intent show_intent = new Intent(this, ResultActivity.class);
        show_intent.setData(uri);
        show_intent.putExtra("num", str);
        startActivity(show_intent);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.main;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {

            if ((System.currentTimeMillis() - exitTime) > 2000)  //System.currentTimeMillis()无论何时调用，肯定大于2000
            {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
        com.elliott.a18350.irecognizer.MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }


    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void Album_click() {
        Intent choosePicIntent = new Intent(Intent.ACTION_PICK, null);
        choosePicIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(choosePicIntent, REQUEST_PICK);
    }


    @NeedsPermission(Manifest.permission.CAMERA)
    public void Camera_click() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            imageUri = FileProvider.getUriForFile(this, "com.elliott.a18350.irecognizer.fileprovider", new File(getCacheDir(), "image.jpg"));
        } else {
            imageUri = Uri.fromFile(new File(getCacheDir(), "image.jpg"));
        }
        Log.d("sdad", "Camera_click: should be some thing");
        Log.d("uri", "Camera_click: " + imageUri.toString());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, REQUEST_CAMERA);
    }


    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void init_Croper() {
        ImageButton button = (ImageButton) findViewById(R.id.Picker);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Camera_or_Album();
            }
        });//相册选取

    }

    protected void Camera_or_Album() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("选择");
        builder.setMessage("请选择图片导入方式");

        builder.setPositiveButton("相机拍摄", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                com.elliott.a18350.irecognizer.MainActivityPermissionsDispatcher.Camera_clickWithCheck(MainActivity.this);
            }
        });

        builder.setNegativeButton("相册导入", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                com.elliott.a18350.irecognizer.MainActivityPermissionsDispatcher.Album_clickWithCheck(MainActivity.this);
            }
        });

        builder.show();
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(resultCode) {
            case RESULT_CANCELED:
                Log.d(TAG, "onActivityResult= "+requestCode );
                if(requestCode==UCrop.REQUEST_CROP)
                    MainActivityPermissionsDispatcher.Album_clickWithCheck(MainActivity.this);
                else
                    return;
        }
        switch (requestCode){
            case REQUEST_CAMERA:
                try{
                    Uri camera_uri=startCropImage();
                    startUcrop(camera_uri);
                }catch (Exception e){
                    e.printStackTrace();
                }
                //调用相机了，要调用图片裁剪的方法
                break;
            case REQUEST_PICK :
                before_crop(data);
                break;
            case UCrop.REQUEST_CROP:
                Uri croppedFileUri = UCrop.getOutput(data);
                Intent recognize_intent=new Intent(this,RecognizeActivity.class);
                recognize_intent.setData(croppedFileUri);
                startActivityForResult(recognize_intent,REQUEST_RECOGNIZE);
                break;
            case REQUEST_RECOGNIZE://识别完后将结果输出到ResultActivity
                ShowResult(data.getStringExtra("num"),data.getData());
                break;
        }

    }


    private void before_crop(Intent data)
    {
        try {
            String path;
            if(data != null){
                Uri uri = data.getData();
                Log.d(TAG, "before_crop1: "+uri.toString());
                if(!TextUtils.isEmpty(uri.getAuthority())){
                    Cursor cursor = this.getContentResolver().query(uri,new String[]{MediaStore.Images.Media.DATA},null,null,null);
                    if(null == cursor){
                        return;
                    }
                    cursor.moveToFirst();
                    //拿到了照片的path
                    path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    cursor.close();
                    path = "file://"+path;

                }
                else
                    path=uri.toString();
                Uri uri_crop = Uri.parse(path);
                Log.d(TAG, "before_crop: "+uri_crop.toString());
                startUcrop(uri_crop);
                //启动裁剪界面，配置裁剪参数
            }
        } catch (Exception e){
            e.printStackTrace();
            Log.d(TAG, "before_crop: "+"exception");
        }
    }



    private void startUcrop(Uri uri_crop) {
        //裁剪后保存到文件中
        Uri destinationUri = Uri.fromFile(new File(getCacheDir(), "SampleCropImage.png"));
        UCrop uCrop = UCrop.of(uri_crop, destinationUri);
        UCrop.Options options = new UCrop.Options();
        //设置标题
        options.setToolbarTitle("请将号码放在扫描框中，减少干扰");
        //设置裁剪图片可操作的手势
        options.setAllowedGestures(UCropActivity.SCALE, UCropActivity.ROTATE, UCropActivity.ALL);
        //设置隐藏底部容器，默认显示,设置为false就会显示了
        options.setHideBottomControls(false);
        //设置toolbar颜色
        options.setToolbarColor(ActivityCompat.getColor(this, R.color.colorPrimary));
        //设置状态栏颜色
        options.setStatusBarColor(ActivityCompat.getColor(this, R.color.colorPrimary));
        //是否能调整裁剪框
        options.setFreeStyleCropEnabled(true);
        uCrop.useSourceImageAspectRatio();
        uCrop.withOptions(options);
        try {
            uCrop.start(this);
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    /**e
     * 调用相机后对图片进行裁剪的方法
     * @param
     */

    private Uri startCropImage() {
        //显示图片
        Bitmap bmp = BitmapFactory.decodeFile(imageUri.toString());// 解析返回的图片成bitmap
        ImageView imageView=(ImageView)findViewById(R.id.imageView);
        imageView.setImageBitmap(bmp);
        return imageUri;
    }
}










