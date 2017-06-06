package com.elliott.a18350.irecognizer
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler

/**
 * Created by 18350 on 2017/5/24 0024.
 * to kotlin 2017/6/6
 */

class LaunchActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val time = 800    //设置等待时间，单位为毫秒,时间缩短为1秒
        val handler = Handler()
        //当计时结束时，跳转至主界面
        handler.postDelayed({
            startActivity(Intent(this@LaunchActivity, MainActivity::class.java))
            finish()
        }, time.toLong())
    }

}
