package com.elliott.a18350.irecognizer

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.view.KeyEvent
import kotlinx.android.synthetic.main.about.*


/**
 * Created by 18350 on 2017/5/25 0025.
 * to kotlin 2017/6/6
 */

class InfoActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.about)
        hyperlink.autoLinkMask = Linkify.ALL
        hyperlink.movementMethod = LinkMovementMethod.getInstance()
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
