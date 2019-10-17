package com.android.ca105g4.task;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.android.ca105g4.R;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

/*
    AsyncTask 泛型三種型態
    1.執行 前 可以傳入的參數型態
    2.執行 中 任務進度的資料型態
    3.執行 後 任務回傳的資料型態
*/
public class ImageTask extends AsyncTask<Object, Integer, Bitmap> {

    private final static String TAG = "ImageTask";
    private String url, outStr;

    private WeakReference<ImageView> imageViewWeakReference;    // 處理圖片 => 效能問題處理(以會用到)

    public ImageTask(String url, String outStr){
        this(url, outStr, null);
    }

    public ImageTask(String url, String outStr, ImageView imageView){   //
        this.url = url;
        this.outStr = outStr;
        this.imageViewWeakReference = new WeakReference<>(imageView);
    }

    @Override
    protected Bitmap doInBackground(Object... objects) {
        return getRemoteImage();
    }

    @Override
    protected void onPostExecute(Bitmap bitmap){      // 圖片處理
        ImageView imageView = imageViewWeakReference.get();       // ??

        if(isCancelled() || imageView == null){   // 取消或沒有圖  ??
            return;
        }

        if(bitmap != null){        // 有圖片回來                   ??
            imageView.setImageBitmap(bitmap);   //有, 設定
            Log.d(TAG, "bitmap");
        } else{
            imageView.setImageResource(R.drawable.default_image);  // 沒有, 圖片使用預設的
        }
    }

    private Bitmap getRemoteImage(){
        HttpURLConnection con = null;
        Bitmap bitmap = null;       // 圖片處理用

        try {
            con = (HttpURLConnection)new URL(url).openConnection();
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);
            con.setRequestMethod("POST");
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));
            bw.write(outStr);
            Log.d(TAG, "output = " + outStr);
            bw.close();         // 一定要關閉              ??   %%

            int responseCode = con.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_OK){
                bitmap = BitmapFactory.decodeStream(new BufferedInputStream(con.getInputStream()));  // 解碼
//                Log.d(TAG, "response Code = " + responseCode);
            } else {
                Log.d(TAG, "response Code = " + responseCode);
            }
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        } finally {
            if(con != null){
                con.disconnect();
            }
        }
        return bitmap;
    }


}
