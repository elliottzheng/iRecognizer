package com.elliott.a18350.irecognizer

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import kh.android.updatecheckerlib.UpdateChecker
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
        check(UpdateChecker.Market.MARKET_COOLAPK,packageName,getVersionName())

    }


    private fun check(market: UpdateChecker.Market, pkg: String,now_versionName:String) {
        Thread(Runnable {
            try {
                val info = UpdateChecker.check(market, pkg)
                if(info.versionName>now_versionName) {
                    runOnUiThread {
                        val builder = AlertDialog.Builder(this@InfoActivity)
                        builder.setTitle("发现新版本,请在酷安检查更新")
                                .setMessage(info.versionName + "\n" + info.changeLog)
                                .show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }).start()
    }

    fun getVersionName(): String {
        val manager = packageManager
        try {
            val packageInfo = manager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            return packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return ""
    }
}
