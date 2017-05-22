package com.elliott.a18350.mylearning;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_STORAGE_CODE = 1;
    TessBaseAPI mTess;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTessBaseData();
        setContentView(R.layout.activity_main);
        String datapath = Environment.getExternalStorageDirectory() + "/tesseract/";
        EditText myedittext=(EditText)this.findViewById(R.id.editText);
        myedittext.setText(datapath);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            myedittext.setText("access get");
            // 已经赋予权限，直接调用拨打电话的代码
            //callPhone();
        } else {
            // 没有赋予权限，那就去申请权限
            myedittext.setText("access get");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_STORAGE_CODE);
        }
        View detectButton = findViewById(R.id.button2);
        detectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onChooseClicked();
            }
        });
    }

    private void initTessBaseData() {

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





    public void onClick(View v) {



        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.textimage);

        mTess.setImage(bitmap);

        String result = mTess.getUTF8Text();

        EditText myedittext=(EditText)this.findViewById(R.id.editText);

        myedittext.setText(result);

        ImageView imgView = (ImageView)this.findViewById(R.id.imageView);

        imgView.setImageBitmap(bitmap);

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_STORAGE_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 权限请求成功
                    //callPhone();
                    EditText myedittext=(EditText)this.findViewById(R.id.editText);
                    myedittext.setText("access get");
                } else {
                    // 用户拒绝了
                    showTipDialog();
                }
                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();
            Log.e("uri", uri.toString());
            ContentResolver cr = this.getContentResolver();
            try {
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
        super.onActivityResult(requestCode, resultCode, data);
    }
}



