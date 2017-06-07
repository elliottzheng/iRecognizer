package com.elliott.a18350.irecognizer;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

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
        Log.d(TAG, "onCreate: nothirng happen");
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
        Log.d(TAG, "getNumber: "+result);
        return result;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
        RecognizeActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    private void recognize(Uri image_uri){
        try {
            Log.e("uri", image_uri.toString());
            ContentResolver cr = this.getContentResolver();
            Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(image_uri));
            if(bitmap==null)
                Log.i(TAG, "bitmap又是空的");
            Intent intent = new Intent(this, MainActivity.class);
            intent.setData(image_uri);
            intent.putExtra("num",getNumber(bitmap));
            setResult(RESULT_OK,intent);
        }
        catch (FileNotFoundException e) {
            Log.e("Exception", e.getMessage(),e);
        }

    }

}
