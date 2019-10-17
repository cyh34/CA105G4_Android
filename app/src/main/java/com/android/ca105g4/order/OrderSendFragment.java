package com.android.ca105g4.order;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.ca105g4.R;
import com.android.ca105g4.main.Util;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class OrderSendFragment extends Fragment {

    private static final String TAG = "OrderSendFragment";
    private final static String TAGFragment = "fragment";

    OrdersVO ordersVO = new OrdersVO();

    private LinearLayout linearLayout;
    private TextView tvOrderState, tvOrderId, tvOrdMemName, tvOrdBraName, tvOrdRooms, tvOrdCheckin, tvOrdCheckout, tvOrdAmount, tvOrdBond;
    private Button btnReturnHome;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_order_send, container, false);

        SharedPreferences preferences = getActivity().getSharedPreferences(Util.PREF_FILE, getActivity().MODE_PRIVATE);  // 取得偏好設定
        String memName = preferences.getString("name", "");   // 取出 偏好設定裡的 login
        Log.d(TAG, memName);

        String braName = getArguments().getString("braName");
        String checkinDate = getArguments().getString("checkinDate");
        String checkoutDate = getArguments().getString("checkoutDate");

        String orderInfo = getArguments().getString("sendOrderToServlet");
        Log.d("orderInfo = ", orderInfo);

        Gson gson = new GsonBuilder().setDateFormat("yyyy-mm-dd").create();
        ordersVO = gson.fromJson(orderInfo, OrdersVO.class);

        linearLayout = getActivity().findViewById(R.id.main_roomType);
        tvOrderState = view.findViewById(R.id.tvOrderState);
        btnReturnHome = view.findViewById(R.id.btnReturnHome);
        tvOrderId = view.findViewById(R.id.tvOrderId);
        tvOrdMemName = view.findViewById(R.id.tvOrdMemName);
        tvOrdBraName = view.findViewById(R.id.tvOrdBraName);
        tvOrdRooms = view.findViewById(R.id.tvOrdRooms);
        tvOrdCheckin = view.findViewById(R.id.tvOrdCheckin);
        tvOrdCheckout = view.findViewById(R.id.tvOrdCheckout);
        tvOrdAmount = view.findViewById(R.id.tvOrdAmount);
        tvOrdBond = view.findViewById(R.id.tvOrdBond);

        Log.d(TAG, "success = " + orderInfo);
        if("error".equals(orderInfo)){              //失敗
            tvOrderState.setText("訂房失敗");
        } else{
            tvOrderState.setText("訂房成功");
            tvOrderId.setText(getString(R.string.final_ordersId) + "：" + ordersVO.getOrdID());
            tvOrdMemName.setText(getString(R.string.final_memName) + "：" + memName);
            tvOrdBraName.setText(getString(R.string.final_braName) + "：" + braName);
            tvOrdRooms.setText(getString(R.string.final_rooms) + "：" + ordersVO.getNumOfRoom());
            tvOrdCheckin.setText(getString(R.string.final_checkin) + "：" + checkinDate);
            tvOrdCheckout.setText(getString(R.string.final_checkout) + "：" + checkoutDate);
            tvOrdAmount.setText(getString(R.string.final_amount) + "：" + ordersVO.getAmount());
            tvOrdBond.setText(getString(R.string.final_bond) + "：" + ordersVO.getBond());
        }

        btnReturnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                Fragment fragment = manager.findFragmentByTag(TAGFragment);
                if (fragment != null) {     //有的話
                    linearLayout.setVisibility(View.VISIBLE);
                    transaction.remove(fragment);   //移除
                    transaction.commit();
                }
            }
        });

        return view;
    }
}
