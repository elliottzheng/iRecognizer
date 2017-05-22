package com.elliott.a18350.mylearning;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;


public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_STORAGE_CODE = 1;
    TessBaseAPI mTess;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String datapath = Environment.getExternalStorageDirectory() + "/tesseract/";
        TextView txtView = (TextView)this.findViewById(R.id.idCard_textVie);
        txtView.setText(datapath);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            txtView.setText("access get");
            // 已经赋予权限，直接调用拨打电话的代码
            //callPhone();
        } else {
            // 没有赋予权限，那就去申请权限
            txtView.setText("access get");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_STORAGE_CODE);
        }
    }

    private void initTessBaseData() {

        mTess = new TessBaseAPI();
        mTess.setDebug(true);
        // 使用默认语言初始化BaseApi
        String datapath = Environment.getExternalStorageDirectory() + "/tesseract/";

        // String language = "num";

        String language = "eng";

        File dir = new File(datapath + "tessdata/");

        if (!dir.exists())
            dir.mkdirs();

        mTess.init(datapath, language);
    }

    public void onClick(View v) {


        initTessBaseData();
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.textimage);

        mTess.setImage(bitmap);

        String result = mTess.getUTF8Text();

        TextView txtView = (TextView)this.findViewById(R.id.idCard_textVie);

        txtView.setText("结果为:" + result);

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
                    TextView txtView = (TextView)this.findViewById(R.id.idCard_textVie);
                    txtView.setText("access get");
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

}



