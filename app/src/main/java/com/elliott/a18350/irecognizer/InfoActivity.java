package com.elliott.a18350.irecognizer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.KeyEvent;
import android.widget.TextView;

/**
 * Created by 18350 on 2017/5/25 0025.
 */

public class InfoActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        TextView textView = (TextView) findViewById(R.id.hyperlink);
        textView.setAutoLinkMask(Linkify.ALL);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            startActivity(new Intent(InfoActivity.this, MainActivity.class));
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
