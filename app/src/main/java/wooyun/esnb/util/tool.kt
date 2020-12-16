package wooyun.esnb.util

import java.io.IOException


object tool {

    @JvmStatic
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