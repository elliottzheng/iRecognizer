package com.elliott.a18350.irecognizer;

/**
 * Created by 18350 on 2017/6/19 0019.
 * 拨号键盘代码来自 点滴之水
 * https://www.cnblogs.com/yoyohong/p/5687337.html
 */

import android.app.Activity;
import android.app.Fragment;
import android.app.Instrumentation;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class MyKeyBoard extends Fragment implements OnClickListener{
    Activity mActivity;
    View rootView;

    private Button button_1,button_2,button_3,button_4,button_5,button_6,button_7,button_8,button_9,button_0,button_del,button_point;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        mActivity=getActivity();
        rootView=inflater.inflate(R.layout.keyboard_layout, container, false);
        initView();
        return rootView;
    }

    private void initView() {
        button_0=(Button)rootView.findViewById(R.id.button_0);
        button_1=(Button)rootView.findViewById(R.id.button_1);
        button_2=(Button)rootView.findViewById(R.id.button_2);
        button_3=(Button)rootView.findViewById(R.id.button_3);
        button_4=(Button)rootView.findViewById(R.id.button_4);
        button_5=(Button)rootView.findViewById(R.id.button_5);
        button_6=(Button)rootView.findViewById(R.id.button_6);
        button_7=(Button)rootView.findViewById(R.id.button_7);
        button_8=(Button)rootView.findViewById(R.id.button_8);
        button_9=(Button)rootView.findViewById(R.id.button_9);
        button_point=(Button)rootView.findViewById(R.id.button_point);
        button_del=(Button)rootView.findViewById(R.id.button_del);
        button_0.setOnClickListener(this);
        button_1.setOnClickListener(this);
        button_2.setOnClickListener(this);
        button_3.setOnClickListener(this);
        button_4.setOnClickListener(this);
        button_5.setOnClickListener(this);
        button_6.setOnClickListener(this);
        button_7.setOnClickListener(this);
        button_8.setOnClickListener(this);
        button_9.setOnClickListener(this);
        button_point.setOnClickListener(this);
        button_del.setOnClickListener(this);
        button_del.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // TODO Auto-generated method stub
                performKeyDown(KeyEvent.KEYCODE_CLEAR);
                return false;
            }
        });
    }
    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        switch (arg0.getId()) {
            case R.id.button_0:
                performKeyDown(KeyEvent.KEYCODE_0);
                break;
            case R.id.button_1:
                performKeyDown(KeyEvent.KEYCODE_1);
                break;
            case R.id.button_2:
                performKeyDown(KeyEvent.KEYCODE_2);
                break;
            case R.id.button_3:
                performKeyDown(KeyEvent.KEYCODE_3);
                break;
            case R.id.button_4:
                performKeyDown(KeyEvent.KEYCODE_4);
                break;
            case R.id.button_5:
                performKeyDown(KeyEvent.KEYCODE_5);
                break;
            case R.id.button_6:
                performKeyDown(KeyEvent.KEYCODE_6);
                break;
            case R.id.button_7:
                performKeyDown(KeyEvent.KEYCODE_7);
                break;
            case R.id.button_8:
                performKeyDown(KeyEvent.KEYCODE_8);
                break;
            case R.id.button_9:
                performKeyDown(KeyEvent.KEYCODE_9);
                break;
            case R.id.button_point:
                final com.elliott.a18350.irecognizer.ClearEditText  tvMsg = (com.elliott.a18350.irecognizer.ClearEditText) rootView.findViewById(R.id.editText);
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + tvMsg.getText()));
                startActivity(intent);
                break;
            case R.id.button_del:
                performKeyDown(KeyEvent.KEYCODE_DEL);
                break;
            default:
                break;
        }

    }
    //模拟键盘输入
    public void performKeyDown(final int keyCode) {
        new Thread() {
            public void run() {
                try {
                    Instrumentation inst = new Instrumentation();
                    inst.sendKeyDownUpSync(keyCode);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
