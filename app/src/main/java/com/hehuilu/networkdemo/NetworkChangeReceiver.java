package com.hehuilu.networkdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

import java.io.IOException;

/**
 * @Author lhh
 * @ClasssName NetworkChangeReceiver
 * @Description
 * @UpdateDate 2020/10/27 17:59
 */
public class NetworkChangeReceiver extends BroadcastReceiver {
    private String pingUrl = "www.baidu.com";
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        boolean canUse = false;
        boolean canConn = false;

        if (info != null && info.isAvailable()){
            if (info.isConnected()){
                canUse = canUse(manager);
                canConn = true;
            }
            Netinfo.Net_state.ISCONNECT.setCanUse(canUse);
            Netinfo.Net_state.ISCANUSER.setCanUse(canConn);

            Netinfo.NetMessage.MESSAGE.setMsg("网络连接成功");
            Netinfo.NetMessage.MESSAGE.setIndex(1);
            int netType = info.getType();
            if (netType == ConnectivityManager.TYPE_MOBILE){
                Netinfo.NetType.TYPE_NAME.setType_name("手机网络");
                Netinfo.NetType.TYPE_NAME.setIndex(1);
            }else if (netType == ConnectivityManager.TYPE_WIFI){
                Netinfo.NetType.TYPE_NAME.setType_name("WIFI网络");
                Netinfo.NetType.TYPE_NAME.setIndex(2);
            }
        }else{
            Netinfo.NetMessage.MESSAGE.setMsg("网络断开连接");
            Netinfo.NetMessage.MESSAGE.setIndex(2);
            Netinfo.Net_state.ISCONNECT.setCanUse(canUse);
            Netinfo.Net_state.ISCANUSER.setCanUse(canConn);
            Netinfo.NetType.TYPE_NAME.setType_name("无");
            Netinfo.NetType.TYPE_NAME.setIndex(3);
        }
        if (onCheckNetworkCallBack != null){
            onCheckNetworkCallBack.onChecked();
        }
    }


    public interface CheckNetworkCallBack{
        void onChecked();
    }
    private CheckNetworkCallBack onCheckNetworkCallBack;
    public void setOnCheckNetworkCallBack(CheckNetworkCallBack onCheckNetworkCallBack) {
        this.onCheckNetworkCallBack = onCheckNetworkCallBack;
    }

    /**
     *  判断网络可用（能否上网）
     *  @return
     */
    public boolean canUse(ConnectivityManager manager) {
        /**
         * android 6.0新增了获取网络状态的方法
         * 有心之人可以用   360免费WIFI软件分享WIFI给手机用，然后监控下这个 if 和 else
         * 里面的日志内容
         * 验证步骤：
         *   1.连接360免费WIFI不验证
         *   2.验证后把电脑设置错误的 MAC地址
         *   3 在验证后连接正常上网情况下
         **/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            NetworkCapabilities capabilities = manager.getNetworkCapabilities(manager.getActiveNetwork());
            Log.i("Avalible", "NetworkCapabilities:"+capabilities.toString());
            return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
        }else{
            Runtime runtime = Runtime.getRuntime();
            try {
                Process ipProcess = runtime.exec("ping -c 3 "+pingUrl);
                int exitValue = ipProcess.waitFor();
                Log.i("Avalible", "Process:"+exitValue);
                return (exitValue == 0);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    public void addPingUrl(String url){
        this.pingUrl = url;
    }
}