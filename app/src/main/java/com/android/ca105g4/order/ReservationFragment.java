package com.android.ca105g4.order;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.ca105g4.R;
import com.android.ca105g4.main.Util;

public class ReservationFragment extends Fragment {

    private static final String TAG = "ReservationFragment";
    private final static String TAGFragment = "fragment";
    private TextView tvOdcheckin, tvOdcheckout, tvOdBraName, tvOdMemName;
    private EditText etOdNumber;
    private Button btnAddOrderDetail;
    private Spinner spPayment;

    private String braName;
    private String checkinDate;
    private String checkoutDate;

    private OrdersVO ordersVO;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_reservation, container, false);

        SharedPreferences preferences = getActivity().getSharedPreferences(Util.PREF_FILE, getActivity().MODE_PRIVATE);  // 取得偏好設定
        String memName = preferences.getString("name", "");   // 取出 偏好設定裡的 login

        braName = getArguments().getString("braName");
        checkinDate = getArguments().getString("checkinDate");
        checkoutDate = getArguments().getString("checkoutDate");
        ordersVO = (OrdersVO)getArguments().getSerializable("ordersVO");

        findViews(view);

        tvOdcheckin.setText(getString(R.string.tvOdcheckin) + checkinDate);
        tvOdcheckout.setText(getString(R.string.tvOdcheckout) + checkoutDate);
        tvOdBraName.setText(getString(R.string.orderBranch) + braName);
        tvOdMemName.setText(getString(R.string.odMemName) + memName);

        String[] payment = {"現金", "信用卡"};      // 靜態態下拉選單
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, payment);
        spPayment.setAdapter(adapter);
        spPayment.setSelection(0, true);

        Listener listener = new Listener();
        btnAddOrderDetail.setOnClickListener(listener);

        return view;
    }

    private void findViews(View view){
        tvOdcheckin = view.findViewById(R.id.tvOdcheckin);
        tvOdcheckout = view.findViewById(R.id.tvOdcheckout);
        tvOdBraName = view.findViewById(R.id.tvOdBraName);
        tvOdMemName = view.findViewById(R.id.tvOdMemName);
        etOdNumber = view.findViewById(R.id.etOdNumber);
        spPayment = view.findViewById(R.id.spOdPayment);
        btnAddOrderDetail = view.findViewById(R.id.btnAddOrderDetail);
    }

    private class Listener implements View.OnClickListener{
        @Override
        public void onClick(View view) {

            FragmentManager manager = getActivity().getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            OrderDetailFragment orderDetailFragment = new OrderDetailFragment();

            int numOfGuest = 0;
            try {
                numOfGuest = Integer.valueOf(etOdNumber.getText().toString());
            } catch (NumberFormatException e) {      //轉型失敗, 被捕捉
                Toast.makeText(getActivity(), "請填寫入住人數", Toast.LENGTH_SHORT).show();
                e.printStackTrace();

                return;     //離開
            }

            ordersVO.setOrdType(0);                      //存訂單種類 => 線上
            ordersVO.setNumOfGuest(numOfGuest);          //存入住人數
            ordersVO.setPayment(spPayment.getSelectedItemPosition());     //存付款方式

            Bundle bundle = new Bundle();
            bundle.putString("braName", braName);
            bundle.putString("checkinDate", checkinDate);
            bundle.putString("checkoutDate", checkoutDate);
            bundle.putSerializable("ordersVO", ordersVO);

            orderDetailFragment.setArguments(bundle);                 // 傳遞資料
            transaction.replace(R.id.frameLayout, orderDetailFragment, TAGFragment);   // 換一個新的, 也可以直接建立一個新的 => 放在哪個容器, 哪個fragmennt  => 找不到fragment
            transaction.commit();
        }
    }
}
