package com.android.ca105g4.roomType;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.ca105g4.R;
import com.android.ca105g4.main.Util;
import com.android.ca105g4.order.OrdersVO;
import com.android.ca105g4.order.ReservationFragment;
import com.android.ca105g4.task.ImageTask;
import com.google.gson.JsonObject;

import java.util.concurrent.ExecutionException;

public class RoomTypeInfoFragment extends Fragment {

    private static final String TAG = "RoomTypeInfoFragment";
    private final static String TAGFragment = "fragment";

//    private LinearLayout linearLayout;
    private TextView tvRtName, tvRtPrice, tvRtIntroMain;
    private ImageView ivRtImage;
    private Button btnRes;

    private RoomTypeBra roomTypeBra;
    private ImageTask getRoomTypeImageTask;

    private String braName, checkinDate, checkoutDate;
    private OrdersVO ordersVO;

    public RoomTypeInfoFragment(){
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_room_type_info, container, false);

        roomTypeBra = (RoomTypeBra) getArguments().getSerializable("roomTypeBra");

        findViews(view);

        String price = getString(R.string.rt_holidayPrice) + roomTypeBra.getHolidayPrice();
        tvRtName.setText(roomTypeBra.getRtName());
        tvRtPrice.setText(price);
        tvRtIntroMain.setText(roomTypeBra.getRtIntro());
//        tvRtIntroMain.setText("我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我");

        braName = getArguments().getString("braName");
        checkinDate = getArguments().getString("checkinDate");
        checkoutDate = getArguments().getString("checkoutDate");
        ordersVO = (OrdersVO)getArguments().getSerializable("ordersVO");

        boolean isSearch = getArguments().getBoolean("isSearch");

//        if("全部".equals(braName) || "請選擇日期".equals(checkinDate) || "請選擇日期".equals(checkoutDate)){
        if(!isSearch){     //有查詢嗎, 有才能按訂房
            btnRes.setVisibility(View.GONE);
        } else {
            btnRes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentManager manager = getActivity().getSupportFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();
                    ReservationFragment reservationFragment = new ReservationFragment();

                    //帶到下一頁
                    Bundle bundle = new Bundle();
                    bundle.putString("braName", braName);
                    bundle.putString("checkinDate", checkinDate);
                    bundle.putString("checkoutDate", checkoutDate);
                    bundle.putSerializable("ordersVO", ordersVO);
                    bundle.putSerializable("roomTypeBra", roomTypeBra);
                    reservationFragment.setArguments(bundle);             // 傳遞資料

                    transaction.replace(R.id.frameLayout, reservationFragment, TAGFragment);   // 換一個新的, 也可以直接建立一個新的 => 放在哪個容器, 哪個fragmennt  => 找不到fragment
                    transaction.commit();
                }
            });
        }

        return view;
    }

    public void onStart() {
        super.onStart();

        int imageSize = getResources().getDisplayMetrics().widthPixels / 4;
        String url = Util.URL + "roomType/RoomType.do";
        String rtId = roomTypeBra.getRtID();
        Bitmap bitmap = null;

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "getRoomTypeImage");
        jsonObject.addProperty("rtId", rtId);
        jsonObject.addProperty("imageSize", imageSize);
        getRoomTypeImageTask = new ImageTask(url, jsonObject.toString());
        try {
            bitmap = getRoomTypeImageTask.execute().get();
        } catch (ExecutionException e) {
            Log.e(TAG, e.toString());
        } catch (InterruptedException e) {
            Log.e(TAG, e.toString());
        }

        if(bitmap != null)
            ivRtImage.setImageBitmap(bitmap);
    }

    public void findViews(View view){
//        linearLayout = view.findViewById(R.id.main_roomType);   // homeActivity
        tvRtName = view.findViewById(R.id.tvRtName);
        ivRtImage = view.findViewById(R.id.ivRtImage);
        tvRtPrice = view.findViewById(R.id.tvRtPrice);
        tvRtIntroMain = view.findViewById(R.id.tvRtIntroMain);
        btnRes = view.findViewById(R.id.btnRes);
    }
}