package com.android.ca105g4.order;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.ca105g4.R;
import com.android.ca105g4.main.Util;
import com.android.ca105g4.roomType.RoomTypeBra;
import com.android.ca105g4.task.CommonTask;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class OrderDetailFragment extends Fragment {

    private static final String TAG = "OrderDetailFragment";
    private final static String TAGFragment = "fragment";

    private RecyclerView rvRoomType;
    private Button btnOdSubmit;

    //Dialog
    private Dialog checkDialog;
    private Button btnOK, btnCancel;

    private CommonTask getRoomTypeByBraNameTask, sendOrderTask;

    private List<RoomTypeBra> rtList;          //向servlet請求來的 (加上複合查詢後的VO)
    private List<OrderDetailData> orderDetailList;      //訂單明細

    private OrderTransactionVO orderTransactionVO;
    private OrdersVO ordersVO;

    private String braName;
    private String checkinDate;
    private String checkoutDate;
    private boolean isRoom = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_order_detail, container, false);

        orderTransactionVO = new OrderTransactionVO();
        orderDetailList = new ArrayList<OrderDetailData>();

        braName = getArguments().getString("braName");
        checkinDate = getArguments().getString("checkinDate");
        checkoutDate = getArguments().getString("checkoutDate");

        rvRoomType = view.findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());   // getActivity() 取得畫面物件
        rvRoomType.setLayoutManager(layoutManager);
        getRoomTypeByBraName();

        btnOdSubmit = view.findViewById(R.id.btnOdSubmit);
        btnOdSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getActivity().getSupportFragmentManager();
                final FragmentTransaction transaction = manager.beginTransaction();
                final OrderSendFragment orderSendFragment = new OrderSendFragment();

                braName = getArguments().getString("braName");
                checkinDate = getArguments().getString("checkinDate");
                checkoutDate = getArguments().getString("checkoutDate");
                ordersVO  = (OrdersVO)getArguments().getSerializable("ordersVO");

                for(int i = 0; i < orderDetailList.size(); i++){
                    Log.d(TAG, String.valueOf(i));
                    if(orderDetailList.get(i).getRooms() == 0){
                        isRoom = false;
//                        Log.d(TAG, String.valueOf(isRoom));
                    } else{
                        isRoom = true;         //有間數
//                        Log.d(TAG, String.valueOf(isRoom));
                        break;                //跳離
                    }
                }
                if(!isRoom) {
                    Toast.makeText(getActivity(), "請選擇間數", Toast.LENGTH_SHORT).show();

                    return;                   //離開方法
                }

                //Dialog
                checkDialog = new Dialog(getActivity());
                checkDialog.setCancelable(true);
                checkDialog.setContentView(R.layout.dialog_ok_cancel);
                Window dialogWindow = checkDialog.getWindow();
                dialogWindow.setGravity(Gravity.CENTER);
                WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                lp.width = 1000;
                lp.alpha = 1.0f;
                dialogWindow.setAttributes(lp);

                // 取得自訂對話視窗上的所有元件都需透過myDialog才能findViewById
                btnOK = checkDialog.findViewById(R.id.btnOK);
                btnOK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {       //按下確定

                        for(int i = 0; i < orderDetailList.size(); i++){
                            orderDetailList.get(i).setCheckIn(Date.valueOf(checkinDate));
                            orderDetailList.get(i).setCheckOut(Date.valueOf(checkoutDate));
                        }

                        orderTransactionVO = new OrderTransactionVO();
                        orderTransactionVO.setAction("sendOrder");
                        orderTransactionVO.setOrdersVO(ordersVO);
                        orderTransactionVO.setOrderDetailList(orderDetailList);

                        Bundle bundle = new Bundle();
                        bundle.putString("braName", braName);
                        bundle.putString("checkinDate", checkinDate);
                        bundle.putString("checkoutDate", checkoutDate);
                        bundle.putString("sendOrderToServlet", sendOrderToServlet());  //發送訊息, 傳遞到下一頁
                        orderSendFragment.setArguments(bundle);

                        transaction.replace(R.id.frameLayout, orderSendFragment, TAGFragment);   // 換一個新的, 也可以直接建立一個新的 => 放在哪個容器, 哪個fragmennt  => 找不到fragment
                        transaction.commit();

                        Toast.makeText(getActivity(), "確定送出", Toast.LENGTH_SHORT).show();

                        initHomeActivity();           //初始化, 回到預設狀態

                        checkDialog.cancel();  // 關閉對話視窗  視窗取消
                    }
                });

                btnCancel = checkDialog.findViewById(R.id.btnCancel);  // 對話視窗裡的元件id
                btnCancel.setOnClickListener(new View.OnClickListener() {  // 設定點擊監聽器
                    @Override
                    public void onClick(View view) {      //按下取消
                        Toast.makeText(getActivity(), "取消視窗", Toast.LENGTH_SHORT).show();
                        checkDialog.cancel();  //視窗取消
                    }
                });
                checkDialog.show();     // 小心！一定要記得show() 一定要有

//                for(int i = 0; i < orderDetailList.size(); i++){
//                    orderDetailList.get(i).setCheckIn(Date.valueOf(checkinDate));
//                    orderDetailList.get(i).setCheckOut(Date.valueOf(checkoutDate));
//                }
//
//                orderTransactionVO = new OrderTransactionVO();
//                orderTransactionVO.setAction("sendOrder");
//                orderTransactionVO.setOrdersVO(ordersVO);
//                orderTransactionVO.setOrderDetailList(orderDetailList);

//                Bundle bundle = new Bundle();
//                bundle.putString("braName", braName);
//                bundle.putString("checkinDate", checkinDate);
//                bundle.putString("checkoutDate", checkoutDate);
//                bundle.putSerializable("ordersVO", ordersVO);
//                orderSendFragment.setArguments(bundle);             // 傳遞資料

//                Bundle bundle = new Bundle();
//                if(sendOrderToServlet()){
//                    bundle.putBoolean("success", true);
//                } else {
//                    bundle.putBoolean("success", false);
//                }
//                orderSendFragment.setArguments(bundle);
//
//                transaction.replace(R.id.frameLayout, orderSendFragment, TAGFragment);   // 換一個新的, 也可以直接建立一個新的 => 放在哪個容器, 哪個fragmennt  => 找不到fragment
//                transaction.commit();
            }
        });

        return view;
    }

    public void initHomeActivity(){   //初始化, 回到預設狀態
        Spinner spBranch = getActivity().findViewById(R.id.spBranch);
        Button btnCheckinDate = getActivity().findViewById(R.id.btnCheckinDate);
        Button btnCheckoutDate = getActivity().findViewById(R.id.btnCheckoutDate);

        spBranch.setSelection(0);
        btnCheckinDate.setText("請選擇日期");
        btnCheckoutDate.setText("請選擇日期");
    }

    public void getRoomTypeByBraName(){
        if(Util.isNetworkConnected(getActivity())){
            String url = Util.URL + "roomType/RoomType.do";
            JsonObject jsonObject = new JsonObject();

            jsonObject.addProperty("action", "getAllRoomType");
            jsonObject.addProperty("braName", braName);
            jsonObject.addProperty("checkinDate", checkinDate);
            jsonObject.addProperty("checkoutDate", checkoutDate);

            String jsonOut = jsonObject.toString();
            getRoomTypeByBraNameTask = new CommonTask(url, jsonOut);
            try{
                String jsonIn = getRoomTypeByBraNameTask.execute().get();
                Type listType = new TypeToken<List<RoomTypeBra>>() {
                }.getType();

                rtList = new Gson().fromJson(jsonIn, listType);
                rvRoomType.setAdapter(new RoomTypeAdapter(rtList));   // 設定掌控ListView內容物的Adapter => layout和資料皆在一起

            } catch (ExecutionException e) {
                Log.e(TAG, e.toString());
            } catch (InterruptedException e) {
                Log.e(TAG, e.toString());
            }
        } else
            Toast.makeText(getActivity(), "連線失敗", Toast.LENGTH_SHORT).show();
    }

    public class RoomTypeAdapter extends RecyclerView.Adapter<RoomTypeAdapter.ViewHolder>{

        private List<RoomTypeBra> roomTypeList;          // RecyclerView 用的(加上複合查詢後的VO 與 上方DB的連線來要來的一樣)

        public RoomTypeAdapter(List<RoomTypeBra> roomTypeList){     // 建構子, 將要顯示的資料帶進來
            this.roomTypeList = roomTypeList;                  // 取得用集合裝的資料
        }

        // 建立ViewHolder，藉由ViewHolder做元件綁定
        class ViewHolder extends RecyclerView.ViewHolder{

            private Spinner spNumOfRooms;
//            private Spinner spExtraBed;
            private TextView tvRoomTypeName;
            private ViewHolder(View view){
                super(view);

                tvRoomTypeName = view.findViewById(R.id.tvRoomTypeName);
                spNumOfRooms = view.findViewById(R.id.spNumOfRooms);        // 把元件附在 單筆的 layout上
//                spExtraBed = view.findViewById(R.id.spExtraBed);
            }
        }

        @Override
        public RoomTypeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int i) {    // 創建畫面綁定資料, 在哪個畫面上 => 回傳資料
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_order_detail, parent, false);
            // 設定畫面 = (取得是哪個容器).載入layout用(載入layout, 放在RecyclerView裡面, )

//            ViewHolder viewHolder = new ViewHolder(view);
//            return viewHolder;
            return new RoomTypeAdapter.ViewHolder(view);   // 可拆成上面兩行
        }

        @Override
        public void onBindViewHolder(final RoomTypeAdapter.ViewHolder viewHolder, int position) {    // 連結(資料, 第幾個) =>  次一筆

//            Log.d(TAG, roomTypeList.get(position).getRtName());


            String rtName = roomTypeList.get(position).getRtName();
            viewHolder.tvRoomTypeName.setText(rtName);

            List<String> numOfRooms = new ArrayList<String>();
            int minRooms = roomTypeList.get(position).getRooms();      // 動態下拉選單
            for(int i = 0; i <= minRooms; i++){
//                Log.d(TAG, String.valueOf(i));
                numOfRooms.add(String.valueOf(i));
//                Log.d(TAG, numOfRooms.toString());
            }

            ArrayAdapter<String> NumOfRoomsAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, numOfRooms);
            viewHolder.spNumOfRooms.setAdapter(NumOfRoomsAdapter);
            viewHolder.spNumOfRooms.setSelection(0, true);

//            String[] extraBed = {"不加床", "加床"};      // 靜態下拉選單
//            ArrayAdapter<String> extraBedAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, extraBed);
//            viewHolder.spExtraBed.setAdapter(extraBedAdapter);
//            viewHolder.spExtraBed.setSelection(0, true);

            ItemListenerRooms itemListenerRooms = new ItemListenerRooms(position);
            viewHolder.spNumOfRooms.setOnItemSelectedListener(itemListenerRooms);

//            ItemListenerSpecial itemListenerSpecial = new ItemListenerSpecial(position);
//            viewHolder.spExtraBed.setOnItemSelectedListener(itemListenerSpecial);

            int rooms = viewHolder.spNumOfRooms.getSelectedItemPosition();
//            int special = viewHolder.spExtraBed.getSelectedItemPosition();

            OrderDetailData odDate = new OrderDetailData();
            odDate.setRtID(roomTypeList.get(position).getRtID());   //先放入所有房型
            odDate.setRooms(rooms);                                 //設定房型預設值
//            odDate.setSpecial(special);                             //設定不加床為預設值
            orderDetailList.add(odDate);                            //放入 集合
        }

        @Override
        public int getItemCount() {      // ListView總列數
            return roomTypeList.size();    // 取得資料數量
        }

        class ItemListenerRooms implements Spinner.OnItemSelectedListener{

            private int index;

            private ItemListenerRooms(int index){
                this.index = index;
            }

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                orderDetailList.get(index).setRooms(position);     //改變間數
                Log.d(TAG, "間數 index(" + index + ") = " + String.valueOf(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        }

//        class ItemListenerSpecial implements Spinner.OnItemSelectedListener{
//
//            private int index;
//
//            private ItemListenerSpecial(int index){
//                this.index = index;
//            }
//
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//                orderDetailList.get(index).setSpecial(position);     //改變加不加床的需求
//                Log.d(TAG, "需求 index(" + index + ") = " + String.valueOf(position));
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        }
    }

    public String sendOrderToServlet(){

        String jsonIn = null;

        if (Util.isNetworkConnected(getActivity())) {
            String url = Util.URL + "orders/Orders.do";

            JsonObject jsonObjectOrder = new JsonObject();
            jsonObjectOrder.addProperty("memId", ordersVO.getMemID());
            jsonObjectOrder.addProperty("braId", ordersVO.getBraID());
            jsonObjectOrder.addProperty("orderType", ordersVO.getOrdType());
            jsonObjectOrder.addProperty("numOfGuest", ordersVO.getNumOfGuest());
            jsonObjectOrder.addProperty("payment", ordersVO.getPayment());

            JsonArray jsonArrayData = new JsonArray();
            for(int i = 0; i < orderDetailList.size(); i++) {
                JsonObject jsonObjectData = new JsonObject();
                jsonObjectData.addProperty("rtId", orderDetailList.get(i).getRtID());
                jsonObjectData.addProperty("checkinDate", String.valueOf(orderDetailList.get(i).getCheckIn()));    //sql.date日期轉為字串
                jsonObjectData.addProperty("checkoutDate", String.valueOf(orderDetailList.get(i).getCheckOut()));
                jsonObjectData.addProperty("rooms", orderDetailList.get(i).getRooms());
//                jsonObjectData.addProperty("special", orderDetailList.get(i).getSpecial());

                jsonArrayData.add(jsonObjectData);
            }

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "sendOrder");
            jsonObject.add("ordersVO", jsonObjectOrder);
            jsonObject.add("orderDetailList", jsonArrayData);

            String jsonOut = jsonObject.toString();
            Log.d(TAG, "jsonOut = " + jsonOut);

            sendOrderTask = new CommonTask(url, jsonOut);      // url, 輸出字串 => 送到 servlet, 找出會員資料
            try {
                jsonIn = sendOrderTask.execute().get();    // 取得 servlet 回傳資料

            } catch (ExecutionException e) {
                Log.e(TAG, e.toString());
            } catch (InterruptedException e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Util.showToast(getActivity(), R.string.msg_error);
        }
        return jsonIn;
    }
}
