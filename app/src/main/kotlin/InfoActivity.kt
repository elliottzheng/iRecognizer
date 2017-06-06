package com.elliott.a18350.irecognizer

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.view.KeyEvent
import android.widget.TextView

/**
 * Created by 18350 on 2017/5/25 0025.
 */

class InfoActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.about)
        //setContentView(R.layout.new_main);
        val textView = findViewById(R.id.hyperlink) as TextView
        textView.autoLinkMask = Linkify.ALL
        textView.movementMethod = LinkMovementMethod.getInstance()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.repeatCount == 0) {
            startActivity(Intent(this@InfoActivity, MainActivity::class.java))
            finish()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

}
