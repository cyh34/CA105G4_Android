package com.android.ca105g4.coupon;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.ca105g4.R;
import com.android.ca105g4.main.Util;
import com.android.ca105g4.task.CommonTask;
import com.android.ca105g4.task.ImageTask;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class CouponFragment extends Fragment {

    private static final String TAG = "CouponFragment";
    private RecyclerView rvCoupon;
//    private LayoutInflater inflater;

    private CommonTask getCouponTask, getCollectCouponTask;
    private ImageTask getCouponImageTask;

    private String memId;

    private ArrayAdapter<Coupon> couponAdapter;
    private List<Coupon> collectCouponList;    //已收藏的優惠券清單

    public CouponFragment(){

    }

    // 問題
    // 1.怎麼取context => 66 89
    // 2.updateUI => onStart要先執行一次, 下選單時可以再一次要資料, 或是直接拿之前的來判斷??
    // 3.recyclerView 要拆到外面去 onCreateView 無法取到 DB 資料
    // 4.onStart 一次, updateUI 還有 setAdapter => 與向servlet請求有關

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {   // 載入layout, 何種容器, 傳遞資料用

        View view = inflater.inflate(R.layout.fragment_coupon, container, false);  // 建立畫面

        Log.d(TAG, TAG);

        rvCoupon = view.findViewById(R.id.recyclerView);   // 當成實體變數 ???????
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());     // getActivity() 取得畫面物件, 建立 layout管理員
        rvCoupon.setLayoutManager(layoutManager);   // 將 recyclerView 設定 layout 管理員
//        rvCoupon.setAdapter(new CouponAdapter(inflater, couponList));   // 設定掌控ListView內容物的Adapter => layout和資料皆在一起

        SharedPreferences preferences = getActivity().getSharedPreferences(Util.PREF_FILE, getActivity().MODE_PRIVATE);  // 取得偏好設定
        memId = preferences.getString("memId", "");   // 取出 偏好設定裡的 login

        return view;      // 回傳整個 fragment 畫面
    }

    @Override
    public void onStart() {
        super.onStart();

        getMemberCollect();
        getAllCoupon();
    }

    public void getAllCoupon(){
        List<Coupon> couponList;

        if (Util.isNetworkConnected(getContext())) {     // 取得 context => fragment 無法直接取得
            String url = Util.URL + "coupon/Coupon.do";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAllCoupon");

            String jsonOut = jsonObject.toString();
            getCouponTask = new CommonTask(url, jsonOut);            // url, 輸出字串 => 送到 servlet, 比對是否為會員, 回傳會員編號
            try {
                String jsonIn = getCouponTask.execute().get();    // 取得 servlet 回傳資料

                Type listType = new TypeToken<List<Coupon>>(){
                }.getType();
                couponList = new Gson().fromJson(jsonIn, listType);       //優惠券清單
//                rvCoupon.setAdapter(new CouponAdapter(inflater, couponList));   // 設定掌控ListView內容物的Adapter => layout和資料皆在一
//                rvCoupon.setAdapter(couponAdapter);   // 設定掌控ListView內容物的Adapter => layout和資料皆在一起

                rvCoupon.setAdapter(new CouponAdapter(couponList));   // 設定掌控ListView內容物的Adapter => layout和資料皆在一起

            } catch (ExecutionException e) {
                Log.e(TAG, e.toString());
            } catch (InterruptedException e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Util.showToast(getContext(), R.string.msg_error);    //   ?????
        }
    }

    public class CouponAdapter extends RecyclerView.Adapter<CouponAdapter.ViewHolder>{

//        private LayoutInflater inflater;
        private List<Coupon> couponList;

        public CouponAdapter(List<Coupon> couponList){     // 建構子, 將要顯示的資料帶進來
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
                tvCpnQuantity = view.findViewById(R.id.tvCpnQuantity);
                tvState = view.findViewById(R.id.tvState);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {    // 創建畫面綁定資料, 在哪個畫面上 => 回傳資料
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_coupon, parent, false);
            // 設定畫面 = (取得是哪個容器).載入layout用(載入layout, 放在RecyclerView裡面, )

//            ViewHolder viewHolder = new ViewHolder(view);
//            return viewHolder;
            return new ViewHolder(view);   // 拆成上面兩行
        }

        @Override
        public void onBindViewHolder(final ViewHolder viewHolder,int position) {    // 連結(資料, 第幾個) =>  次一筆
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
            viewHolder.tvCpnQuantity.setText(getString(R.string.cpn_quantity) + coupon.getQuantity());

            clearState(viewHolder.tvState);               //清除
            changeState(coupon, viewHolder.tvState);      //優惠券已領取
            changeFinish(coupon, viewHolder.tvState);     //已發完

//            getAllCoupon();

            // itemView為ViewHolder內建屬性(指的就是每一列)
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {  //設定監聽器
                @Override
                public void onClick(View view) {       //點擊後發生
                    if(coupon.getQuantity() == 0){     //數量剩餘0時
                        Toast.makeText(view.getContext(), getString(R.string.cpn_finished), Toast.LENGTH_SHORT).show();
                    } else if((!memId.equals(collectCouponList.get(index).getMemID()))) {  // 檢查使否領取(用memId比對left join清單, 沒有在裡面可領取)        collectCouponList.get(index).getCpnState() != null &&
                        updateMemberCollect(coupon.getCpnID(), coupon.getQuantity() - 1);
                        Toast.makeText(view.getContext(), getString(R.string.cpn_received), Toast.LENGTH_SHORT).show();
                        viewHolder.tvState.setVisibility(View.VISIBLE);
                        viewHolder.tvState.setText(getString(R.string.cpn_received));
                        Log.d(TAG, collectCouponList.get(index).getCpnID());
                        changeBgColor(viewHolder.tvState);
                    } else {
                        Toast.makeText(view.getContext(), getString(R.string.cpn_received), Toast.LENGTH_SHORT).show();
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

    public void changeState(Coupon coupon, TextView textView) {     //判斷優惠券現在狀態

        for(int i = 0; i < collectCouponList.size(); i++) {       // 比對會員編號 和 已收藏清單和
            if (memId.equals(collectCouponList.get(i).getMemID()) && collectCouponList.get(i).getCpnID().equals(coupon.getCpnID())) {
                textView.setVisibility(View.VISIBLE);
                textView.setText(getString(R.string.cpn_received));
                Log.d(TAG, collectCouponList.get(i).getCpnID());
                changeBgColor(textView);
                getMemberCollect();
            }
        }
    }

    public void changeFinish(Coupon coupon, TextView textView){
        if(coupon.getQuantity() == 0){      //數量剩0時
            textView.setVisibility(View.VISIBLE);   //出現
            textView.setText(getString(R.string.cpn_finished));       //顯示提示字
            Log.d(TAG, coupon.getCpnID());
            changeBgColor(textView);
        }
    }

    public void changeBgColor(TextView textView){
        textView.setBackgroundColor(Color.argb(200, 153, 153, 153));   //背景色
    }

    @Override
    public void onStop() {      // 釋放資源
        super.onStop();
        if(getCouponTask != null){
            getCouponTask.cancel(true);
        }

        if(getCouponImageTask != null){
            getCouponImageTask.cancel(true);
        }
    }

    public void getMemberCollect(){

        if (Util.isNetworkConnected(getContext())) {     // 取得 context => fragment 無法直接取得
            String url = Util.URL + "coupon/Coupon.do";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getCollectCoupon");
//            jsonObject.addProperty("memId", memId);

            String jsonOut = jsonObject.toString();
            getCollectCouponTask = new CommonTask(url, jsonOut);            // url, 輸出字串 => 送到 servlet, 比對是否為會員, 回傳會員編號
            try {
                String jsonIn = getCollectCouponTask.execute().get();    // 取得 servlet 回傳資料

                Type listType = new TypeToken<List<Coupon>>(){
                }.getType();
                collectCouponList = new Gson().fromJson(jsonIn, listType);

            } catch (ExecutionException e) {
                Log.e(TAG, e.toString());
            } catch (InterruptedException e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Util.showToast(getContext(), R.string.msg_error);    //   ?????
        }
    }

    public boolean updateMemberCollect(String cpnId, int quantity){

        if (Util.isNetworkConnected(getContext())) {     // 取得 context => fragment 無法直接取得
            String url = Util.URL + "coupon/Coupon.do";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "memberGetCoupon");
            jsonObject.addProperty("memId", memId);
            jsonObject.addProperty("cpnId", cpnId);
            jsonObject.addProperty("quantity", quantity);

            String jsonOut = jsonObject.toString();
            getCollectCouponTask = new CommonTask(url, jsonOut);            // url, 輸出字串 => 送到 servlet, 比對是否為會員, 回傳會員編號
            try {
                String jsonIn = getCollectCouponTask.execute().get();    // 取得 servlet 回傳資料

                Log.d(TAG, jsonIn);
                return Boolean.valueOf(jsonIn);

            } catch (ExecutionException e) {
                Log.e(TAG, e.toString());
            } catch (InterruptedException e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Util.showToast(getContext(), R.string.msg_error);    //   ?????
        }
        return false;
    }

}
