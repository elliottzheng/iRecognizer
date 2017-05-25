package com.elliott.a18350.mylearning;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
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
import android.support.v7.app.AppCompatActivity;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

import static android.widget.Toast.makeText;

@RuntimePermissions
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_PICK = 2;
    private Uri imageUri;
    //String FileName ;
    TessBaseAPI mTess;
    private EditText tvMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        MainActivityPermissionsDispatcher.initTessBaseDataWithCheck(this);
        //FileName=getCacheDir()+"image.jpg";

        MainActivityPermissionsDispatcher.init_CroperWithCheck(this);

        tvMsg = (EditText) findViewById(R.id.editText);
        Button button3 = (Button) findViewById(R.id.phone);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+tvMsg.getText()));
                startActivity(intent);
            }
        });//打电话

        Button  button1= (Button) findViewById(R.id.message);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("smsto:"+tvMsg.getText()));
                intent.putExtra("sms_body","");
                startActivity(intent);
            }
        });//发短信

        Button button2 = (Button) findViewById(R.id.copy);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 从API11开始android推荐使用android.content.ClipboardManager
                // 为了兼容低版本我们这里使用旧版的android.text.ClipboardManager，虽然提示deprecated，但不影响使用。
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                // 将文本内容放到系统剪贴板里。
                cm.setText(tvMsg.getText());
                Toast toast= makeText(getApplicationContext(), "号码已复制成功", Toast.LENGTH_LONG);
                toast.show();

            }
        });//复制到剪贴板

        Button  button4= (Button) findViewById(R.id.info);
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, InfoActivity.class));
            }
        });//帮助

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }


    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void Album_click()
    {
        Intent choosePicIntent = new Intent(Intent.ACTION_PICK, null);
        choosePicIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(choosePicIntent, REQUEST_PICK);
    }


    @NeedsPermission(Manifest.permission.CAMERA)
    public void Camera_click() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            imageUri= FileProvider.getUriForFile(this,"com.elliott.a18350.mylearning.fileprovider", new File(getCacheDir(),"image.jpg"));
        } else {
            imageUri = Uri.fromFile(new File(getCacheDir(),"image.jpg"));
        }
        Log.d("sdad", "Camera_click: should be some thing");
        Log.d("uri", "Camera_click: "+imageUri.toString());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    public void initTessBaseData() {

        mTess = new TessBaseAPI();
        mTess.setDebug(true);
        // 使用默认语言初始化BaseApi

        // String language = "num";

        String language = "write";
        String path = getFilesDir().getAbsolutePath();
        File file = new File(path+"/tessdata");

        if (!file.exists()) {
            file.mkdirs();
            try {
                InputStream is = getClass().getClassLoader().getResourceAsStream("assets/write.traineddata");
                File file2 = new File(file.getAbsoluteFile() + "/write.traineddata");
                FileOutputStream out = new FileOutputStream(file2);
                int len = 0;
                byte[] buffer = new byte[1024];
                while ((len = is.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                    out.flush();
                }
                is.close();
                out.close();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mTess.init(path, language);
    }



    private void showTipDialog() {
        new AlertDialog.Builder(this)
                .setMessage("该程序需要读取外部存储权限，否则无法正常运行")
                .setPositiveButton(android.R.string.ok, null)
                .create()
                .show();
    }

    public void onChooseClicked() {
        Intent intent = new Intent();
                /* 开启Pictures画面Type设定为image */
        intent.setType("image/*");
                /* 使用Intent.ACTION_GET_CONTENT这个Action */
        intent.setAction(Intent.ACTION_GET_CONTENT);
                /* 取得相片后返回本画面 */
        startActivityForResult(intent, 1);
        Log.d(TAG, "onChooseClicked: ");
    }

    protected String getNumber(Bitmap bitmap)
    {

        mTess.setImage(bitmap);
        String result = mTess.getUTF8Text();
        return result;
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void init_Croper() {
        Button  button1= (Button) findViewById(R.id.album);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivityPermissionsDispatcher.Album_clickWithCheck(MainActivity.this);
            }
        });//相册选取


        Button  button2= (Button) findViewById(R.id.camera);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivityPermissionsDispatcher.Camera_clickWithCheck(MainActivity.this);
            }
        });//拍照获得
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
                try {
                    if(data != null){
                        Uri uri = data.getData();
                        if(!TextUtils.isEmpty(uri.getAuthority())){
                            Cursor cursor = this.getContentResolver().query(uri,new String[]{MediaStore.Images.Media.DATA},null,null,null);
                            if(null == cursor){
                                return;
                            }
                            cursor.moveToFirst();
                            //拿到了照片的path
                            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                            cursor.close();
                            path = "file://"+path;
                            Uri uri_crop = Uri.parse(path);
                            startUcrop(uri_crop);
                            //启动裁剪界面，配置裁剪参数
                        }
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case UCrop.REQUEST_CROP:
                Uri croppedFileUri = UCrop.getOutput(data);
                recognize(croppedFileUri);
                break;
        }
    }

    private void startUcrop(Uri uri_crop) {
        //裁剪后保存到文件中
        Uri destinationUri = Uri.fromFile(new File(getCacheDir(), "SampleCropImage.png"));
        UCrop uCrop = UCrop.of(uri_crop, destinationUri);
        UCrop.Options options = new UCrop.Options();
        //设置标题
        options.setToolbarTitle("请将号码放在扫描框中，尽量减少干扰");
        //设置裁剪图片可操作的手势
        //options.setAllowedGestures(UCropActivity.SCALE, UCropActivity.ROTATE, UCropActivity.ALL);
        //设置隐藏底部容器，默认显示
        options.setHideBottomControls(true);
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



    private void recognize(Uri uri){
        try {
            Log.e("uri", uri.toString());
            ContentResolver cr = this.getContentResolver();
            Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
            if(bitmap==null)
                Log.i(TAG, "bitmap又是空的");
            ImageView imageview = (ImageView) findViewById(R.id.imageView);
            if(imageview==null)
                Log.i(TAG, "又是空的");
                /* 将Bitmap设定到ImageView */
            imageview.setImageBitmap(bitmap);
            //将结果输出到textedit
            EditText myedittext=(EditText)this.findViewById(R.id.editText);
            myedittext.setText(getNumber(bitmap));
        }
        catch (FileNotFoundException e) {
            Log.e("Exception", e.getMessage(),e);
        }

    }
}






