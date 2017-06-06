package com.elliott.a18350.irecognizer;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

/**
 * Created by 18350 on 2017/6/6 0006.
 */
@RuntimePermissions
public class RecognizeActivity extends Activity {
    private TessBaseAPI mTess;
    private static final String TAG = "RecognizeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RecognizeActivityPermissionsDispatcher.initTessBaseDataWithCheck(RecognizeActivity.this);
        Uri image_uri=getIntent().getData();
        recognize(image_uri);
        finish();
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

    protected String getNumber(Bitmap bitmap)
    {

        mTess.setImage(bitmap);
        String result = mTess.getUTF8Text();
        return result;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
        RecognizeActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
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
