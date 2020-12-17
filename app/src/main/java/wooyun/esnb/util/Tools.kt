package wooyun.esnb.util

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.text.TextUtils
import android.widget.Toast
import wooyun.esnb.R
import java.io.IOException
import java.util.*


@SuppressLint("Registered")
class Tools : Activity() {

    fun spGet(context: Context, fileName: String?): SharedPreferences {
        return context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
    }

    fun openBrowser(context: Context, url: String?) {
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        intent.data = Uri.parse(url)
        if (intent.resolveActivity(context.packageManager) != null) {
            val componentName = intent.resolveActivity(context.packageManager)
            println("componentName = " + componentName.className)
            context.startActivity(Intent.createChooser(intent, "请选择浏览器"))
        } else {
            Toast.makeText(context.applicationContext, "请下载浏览器", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * @param context     当前安装程序信息
     * @param packageName 当前安装程序包名信息
     * @return
     */
    fun is_Aible(context: Context, packageName: String?): Boolean {
        val packageManager = context.packageManager
        val installedPackages = packageManager.getInstalledPackages(0)
        val packageNames: MutableList<String> = ArrayList()
        if (installedPackages != null) {
            for (i in installedPackages.indices) {
                val packName = installedPackages[i].packageName
                packageNames.add(packName)
            }
        }
        return packageNames.contains(packageName)
    }

    companion object {
        /**
         * 获取屏幕宽度(px)
         */
        fun getScreenWidth(context: Context): Int {
            return context.resources.displayMetrics.widthPixels
        }

        /**
         * 判断某个界面是否在前台
         */
        fun isForeground(context: Context?, className: String?): Boolean {
            try {
                if (context == null || TextUtils.isEmpty(className)) return false
                val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                val list = am.getRunningTasks(1)
                for (taskInfo in list) {
                    if (taskInfo.topActivity.shortClassName.contains(className!!)) {
                        // 说明它已经启动了
                        return true
                    }
                }
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
            return false
        }

        /*发送邮件*/
        fun feedback(email: String?, context: Context) {
            val emailIntent = Intent(Intent.ACTION_SENDTO)
            emailIntent.data = Uri.parse(email)
            context.startActivity(Intent.createChooser(emailIntent, context.getString(R.string.Email_feedback)))
        }

        fun getNetStatus(): Int {
            var process: Process? = null
            var status = 0
            try {
                process = Runtime.getRuntime().exec("/system/bin/ping -c 1 -w 100 www.baid.com")
            } catch (e: IOException) {
                e.printStackTrace()
            }
            try {
                status = process!!.waitFor()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            return status
        }
    }


}


