package com.elliott.a18350.irecognizer;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;

import static android.widget.Toast.makeText;

/**
 * Created by 18350 on 2017/6/19 0019.
 */

public class ResultActivity extends BaseColorActivity {
    private com.elliott.a18350.irecognizer.ClearEditText tvMsg;
    private static final String TAG = "ResultActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setKeyBoardFragment();//初始化键盘

        Intent formal_intent=getIntent();
        if(formal_intent.getStringExtra("num")!=null&&formal_intent.getData()!=null)
        {
            Log.d(TAG, "onCreate: get a Result");
            place(formal_intent.getStringExtra("num"),formal_intent.getData());
        }

        tvMsg = (com.elliott.a18350.irecognizer.ClearEditText) findViewById(R.id.editText);
        ImageButton button3 = (ImageButton) findViewById(R.id.phone);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + tvMsg.getText()));
                startActivity(intent);
            }
        });//打电话


        ImageButton button1 = (ImageButton) findViewById(R.id.message);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("smsto:" + tvMsg.getText()));
                intent.putExtra("sms_body", "");
                startActivity(intent);
            }
        });//发短信

        ImageButton button2 = (ImageButton) findViewById(R.id.copy);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 从API11开始android推荐使用android.content.ClipboardManager
                // 为了兼容低版本我们这里使用旧版的android.text.ClipboardManager，虽然提示deprecated，但不影响使用。
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                // 将文本内容放到系统剪贴板里。
                cm.setText(tvMsg.getText());
                Toast toast = makeText(getApplicationContext(), "号码已复制成功", Toast.LENGTH_LONG);
                toast.show();

            }
        });//复制到剪贴板

        ImageButton button4 = (ImageButton) findViewById(R.id.info);
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ResultActivity.this, InfoActivity.class));
            }
        });//帮助

    }


    private void place(String str,Uri uri){
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
            myedittext.setText(str);
        }
        catch (FileNotFoundException e) {
            Log.e("Exception", e.getLocalizedMessage());
        }

    }

    private void setKeyBoardFragment(){
        FragmentManager fragmentManager=getFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        MyKeyBoard myKeyBoard=new MyKeyBoard();
        fragmentTransaction.replace(R.id.keyboard,myKeyBoard);
        fragmentTransaction.commit();
    }



    @Override
    protected int getLayoutResId() {
        return R.layout.after_reco;
    }




}
