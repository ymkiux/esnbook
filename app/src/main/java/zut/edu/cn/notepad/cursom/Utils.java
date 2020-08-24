package zut.edu.cn.notepad.cursom;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import zut.edu.cn.notepad.R;


@SuppressLint("Registered")
public class Utils extends Activity {
    private static final int NETWORK_NONE = 1;/*无网络状态*/
    private static final int NETWORK_MOBILE = 0;/*移动网络状态*/
    private static final int NETWORW_WIFI = 2;/*无线网络状态*/

    //通过内部类实现清除缓存
    public void Clean_all(Context context) throws Exception {

        CacheDataManager.clearAllCache(context);

        Thread.sleep(3000);

        CacheDataManager.getTotalCacheSize(context);

    }

    /**
     * 获取屏幕高度(px)
     */
    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 获取屏幕宽度(px)
     */
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 检测vpn端口是否开启
     */
    public static boolean isVpnUsed() {
        try {
            Enumeration<NetworkInterface> niList = NetworkInterface.getNetworkInterfaces();
            if (niList != null) {
                for (NetworkInterface intf : Collections.list(niList)) {
                    if (!intf.isUp() || intf.getInterfaceAddresses().size() == 0) {
                        continue;
                    }
                    if ("tun0".equals(intf.getName()) || "ppp0".equals(intf.getName())) {
                        return true;
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断某个界面是否在前台
     */
    public static boolean isForeground(Context context, String className) {
        try {
            if (context == null || TextUtils.isEmpty(className))
                return false;
            ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
//        boolean flag=false;
            for (ActivityManager.RunningTaskInfo taskInfo : list) {
                if (taskInfo.topActivity.getShortClassName().contains(className)) { // 说明它已经启动了
                    //                flag = true;
                    return true;
                }
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return false;
    }

    /*发送邮件*/
    public static void feedback(String email, Context context) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse(email));
        context.startActivity(Intent.createChooser(emailIntent, context.getString(R.string.Email_feedback)));
    }

    /*测试打印日志*/
    public static void p(String print,String value) {
        System.out.println(print+value);
    }
}


