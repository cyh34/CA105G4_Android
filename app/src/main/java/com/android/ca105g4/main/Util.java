package com.android.ca105g4.main;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

public class Util {

    // 連上Tomcat
    public static String URL = "http://54.249.67.197:8081/CA105G4/";  //AWS
//    public static String URL = "http://10.0.2.2:8081/CA105G4/";      // 模擬器
//    public static String URL = "http://172.20.10.14:8081/CA105G4/";  // 實機 - 手機
//    public static String URL = "http://192.168.2.211:8081/CA105G4/";   // 實機 - 牛
//    public static String URL = "http://192.168.196.248:8081/CA105G4/";  // 實機 - 教室 wifi

    // 偏好設定檔案名稱
    public final static String PREF_FILE = "preference";


    public static boolean isNetworkConnected(Context context){
        ConnectivityManager conManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);   // 取得連線的系統管理員
        NetworkInfo info = conManager.getActiveNetworkInfo();

        return info != null && info.isConnected();             // info 不是空值 且 是連線狀態
    }

    public static void showToast(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(Context context, int messageResId){
        Toast.makeText(context, messageResId, Toast.LENGTH_SHORT).show();
    }

    public static void log_d(String tag, String message){
        Log.d(tag, message);
    }
}
