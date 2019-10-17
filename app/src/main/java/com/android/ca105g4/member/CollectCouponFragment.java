package com.android.ca105g4.member;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.ca105g4.R;
import com.android.ca105g4.coupon.Coupon;
import com.android.ca105g4.main.Util;
import com.android.ca105g4.qrCode.Contents;
import com.android.ca105g4.qrCode.QRCodeEncoder;
import com.android.ca105g4.task.CommonTask;
import com.android.ca105g4.task.ImageTask;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class CollectCouponFragment extends Fragment {

    private static final String TAG = "CollectCouponFragment";

    private RecyclerView rvCoupon;
    private ImageView ivQRCode;

    private CommonTask getCollectCouponTask;
    private ImageTask getCouponImageTask;

    private String memId;

    public CollectCouponFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_coupon, container, false);

        Log.d(TAG, TAG);

        rvCoupon = view.findViewById(R.id.recyclerView);   // 當成實體變數 ???????
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());     // getActivity() 取得畫面物件, 建立 layout管理員
        rvCoupon.setLayoutManager(layoutManager);   // 將 recyclerView 設定 layout 管理員
//        rvCoupon.setAdapter(new CouponAdapter(inflater, couponList));   // 設定掌控ListView內容物的Adapter => layout和資料皆在一起

        SharedPreferences preferences = getActivity().getSharedPreferences(Util.PREF_FILE, getActivity().MODE_PRIVATE);  // 取得偏好設定
        memId = preferences.getString("memId", "");   // 取出 偏好設定裡的 login

        ivQRCode = view.findViewById(R.id.ivQRCode);
        ivQRCode.setOnClickListener(new Listener());

        return view;      // 回傳整個 fragment 畫面
    }

    @Override
    public void onStart() {
        super.onStart();

        getCollectCoupon();
    }

    public void getCollectCoupon(){
        List<Coupon> couponList;

        if (Util.isNetworkConnected(getContext())) {     // 取得 context => fragment 無法直接取得
            String url = Util.URL + "coupon/Coupon.do";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getMemberCollect");
            jsonObject.addProperty("memId", memId);

            String jsonOut = jsonObject.toString();
            getCollectCouponTask = new CommonTask(url, jsonOut);            // url, 輸出字串 => 送到 servlet, 比對是否為會員, 回傳會員編號
            try {
                String jsonIn = getCollectCouponTask.execute().get();    // 取得 servlet 回傳資料

                Type listType = new TypeToken<List<Coupon>>(){
                }.getType();
                couponList = new Gson().fromJson(jsonIn, listType);       //優惠券清單
//                rvCoupon.setAdapter(new CouponAdapter(inflater, couponList));   // 設定掌控ListView內容物的Adapter => layout和資料皆在一起
                rvCoupon.setAdapter(new CollectCouponAdapter(couponList));   // 設定掌控ListView內容物的Adapter => layout和資料皆在一起

            } catch (ExecutionException e) {
                Log.e(TAG, e.toString());
            } catch (InterruptedException e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Util.showToast(getContext(), R.string.msg_error);    //   ?????
        }
    }

    public class CollectCouponAdapter extends RecyclerView.Adapter<CollectCouponAdapter.ViewHolder>{

        //        private LayoutInflater inflater;
        private List<Coupon> couponList;

        public CollectCouponAdapter(List<Coupon> couponList){     // 建構子, 將要顯示的資料帶進來
//        public CouponAdapter(LayoutInflater inflater, List<Coupon> couponList){     // 建構子, 將要顯示的資料帶進來
//            this.inflater = inflater;                      // 取得 layout
            this.couponList = couponList;                  // 取得用集合裝的資料
        }

        // 建立ViewHolder，藉由ViewHolder做元件綁定
        class ViewHolder extends RecyclerView.ViewHolder{

            private ImageView ivCpnImage;
            private TextView tvCpnQuantity, tvState, tvDiscount;
            private ViewHolder(View view){
                super(view);

                ivCpnImage = view.findViewById(R.id.ivCpnImage);        // 把元件附在 單筆的 layout上
                tvDiscount = view.findViewById(R.id.tvCpnDiscount);
                tvState = view.findViewById(R.id.tvState);
            }
        }

        @Override
        public CollectCouponAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int i) {    // 創建畫面綁定資料, 在哪個畫面上 => 回傳資料
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_coupon, parent, false);
            // 設定畫面 = (取得是哪個容器).載入layout用(載入layout, 放在RecyclerView裡面, )

//            ViewHolder viewHolder = new ViewHolder(view);
//            return viewHolder;
            return new CollectCouponAdapter.ViewHolder(view);   // 拆成上面兩行
        }

        @Override
        public void onBindViewHolder(CollectCouponAdapter.ViewHolder viewHolder, int position) {    // 連結(資料, 第幾個) =>  次一筆
            //將資料注入到View裡
            final Coupon coupon = couponList.get(position);        // 因為被匿名類別哪來用, 所以加上 final => 取得第幾個VO
            final int index = position;

            int imageSize = getResources().getDisplayMetrics().widthPixels / 4;
            String url = Util.URL + "coupon/Coupon.do";
            String cpnId = coupon.getCpnID();
            Bitmap bitmap = null;

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getCouponImage");
            jsonObject.addProperty("cpnId", cpnId);
            jsonObject.addProperty("imageSize", imageSize);
            getCouponImageTask = new ImageTask(url, jsonObject.toString());
            try {
                bitmap = getCouponImageTask.execute().get();
            } catch (ExecutionException e) {
                Log.e(TAG, e.toString());
            } catch (InterruptedException e) {
                Log.e(TAG, e.toString());
            }

            if (bitmap != null){
                viewHolder.ivCpnImage.setImageBitmap(bitmap);
            }

            viewHolder.tvDiscount.setText("折" + coupon.getDiscount() + "元");


            clearState(viewHolder.tvState);               //清除
//            changeState(coupon, viewHolder.tvState);      //優惠券已領取
            changeFinish(coupon, viewHolder.tvState);     //已發完

            // itemView為ViewHolder內建屬性(指的就是每一列)
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {  //設定監聽器
                @Override
                public void onClick(View view) {       //點擊後發生
                    if(coupon.getCpnState() == 1){
                        Toast.makeText(view.getContext(), getString(R.string.cpn_used), Toast.LENGTH_SHORT).show();
                    } else if(coupon.getCpnState() == 0){
                        ivQRCode.setVisibility(View.VISIBLE);         //顯示 QR Code image
                        rvCoupon.setVisibility(View.INVISIBLE);       //清單 隱藏

                        JsonObject jsonInfo = new JsonObject();
                        jsonInfo.addProperty("action", "useCoupon");
                        jsonInfo.addProperty("memId", memId);                //放入資料
                        jsonInfo.addProperty("cpnId", coupon.getCpnID());
                        jsonInfo.addProperty("discount", coupon.getDiscount());
                        int smallerDimension = getDimension();   //所小尺寸
                        QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(jsonInfo.toString(), null, Contents.Type.TEXT,
                                BarcodeFormat.QR_CODE.toString(), smallerDimension);
                        try {
                            Bitmap qrCodeBitmap = qrCodeEncoder.encodeAsBitmap();
                            ivQRCode.setImageBitmap(qrCodeBitmap);
                        } catch (WriterException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }

        @Override
        public int getItemCount() {      // ListView總列數
            return couponList.size();    // 取得資料數量
        }
    }

    public void clearState(TextView textView){
        textView.setVisibility(View.GONE);      //消失
    }

    public void changeFinish(Coupon coupon, TextView textView){        //未完待續
        if(coupon.getCpnState() == 1){      //狀態為1時, 代表已使用
            textView.setVisibility(View.VISIBLE);   //出現
            textView.setText(getString(R.string.cpn_used));       //顯示提示字
            Log.d(TAG, String.valueOf(coupon.getCpnState()));
            changeBgColor(textView);
        }
    }

    public void changeBgColor(TextView textView){
        textView.setBackgroundColor(Color.argb(200, 153, 153, 153));   //背景色
    }

    class Listener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            ivQRCode.setVisibility(View.GONE);        //QR Code 消失
            rvCoupon.setVisibility(View.VISIBLE);     //清單 出現
            getCollectCoupon();                       //取得收藏的優惠券
        }
    }

    private int getDimension() {   //得到尺寸  => QR Code 用
        WindowManager manager = (WindowManager)getActivity().getSystemService(getActivity().WINDOW_SERVICE);         //取得視窗管理員
        // 取得螢幕尺寸
        Display display = manager.getDefaultDisplay();    // 為了取得螢幕
        // API 13列為deprecated，但為了支援舊版手機仍採用
        int width = display.getWidth();
        int height = display.getHeight();

        // 產生的QR code圖形尺寸(正方形)為螢幕較短一邊的1/2長度
        int smallerDimension = width < height ? width : height;       //寬小於高, 直立
        smallerDimension = smallerDimension / 2;

        // API 13開始支援
//                Display display = manager.getDefaultDisplay();
//                Point point = new Point();
//                display.getSize(point);
//                int width = point.x;
//                int height = point.y;
//                int smallerDimension = width < height ? width : height;
//                smallerDimension = smallerDimension / 2;
        return smallerDimension;         //縮小尺寸
    }
}
