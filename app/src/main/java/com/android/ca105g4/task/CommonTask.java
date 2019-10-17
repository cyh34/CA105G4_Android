package com.android.ca105g4.task;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class CommonTask extends AsyncTask<String, Integer, String> {

    private final static String TAG = "CommonTask";
    private String url, outStr;

    public CommonTask(String url, String outStr){
        this.url = url;
        this.outStr = outStr;
    }

    @Override
    protected String doInBackground(String... strings) {
        return getRemoteData();
    }

    private String getRemoteData(){
        StringBuilder inStr = new StringBuilder();
        BufferedReader br = null;
        HttpURLConnection con = null;

        try {
            con = (HttpURLConnection) new URL(url).openConnection();
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setChunkedStreamingMode(0);  // 不知道請求內容時, 將請求內容分段傳輸, 0代表使用預設大小
            con.setUseCaches(false);
            con.setRequestMethod("POST");
            con.setRequestProperty("charset", "UTF-8");

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));
            bw.write(outStr);
            Log.d(TAG, "outStr = " + outStr);
            bw.close();

            int responseCode = con.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_OK) {     // HTTP_OK 成功
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String tempStr;
                while ((tempStr = br.readLine()) != null){
                    inStr.append(tempStr);
                }
//                Log.d(TAG, "response Code = " + responseCode);
            } else{
                Log.d(TAG, "response Code = " + responseCode);
            }

        } catch (IOException e) {
            Log.e(TAG, e.toString());
        } finally {
            if(br != null){
                try {
                    br.close();
                } catch (IOException e) {
                    Log.e(TAG, e.toString());
                }
            }

            if(con != null)
                con.disconnect();
        }
        Log.d(TAG, "inStr = " + inStr);
        return inStr.toString();
    }

}
