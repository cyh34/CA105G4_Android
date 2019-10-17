//package com.android.ca105g4.roomType;
//
//import android.app.DatePickerDialog;
//import android.app.Dialog;
//import android.graphics.Bitmap;
//import android.os.Bundle;
//import android.support.v4.app.DialogFragment;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentTransaction;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.DatePicker;
//import android.widget.ImageView;
//import android.widget.Spinner;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.android.ca105g4.R;
//import com.android.ca105g4.main.Util;
//import com.android.ca105g4.task.CommonTask;
//import com.android.ca105g4.task.ImageTask;
//import com.google.gson.Gson;
//import com.google.gson.JsonObject;
//import com.google.gson.reflect.TypeToken;
//
//import java.lang.reflect.Type;
//import java.util.Calendar;
//import java.util.List;
//import java.util.concurrent.ExecutionException;
//
//public class RoomTypeActivity extends AppCompatActivity {
//
//    private static final String TAG = "RoomTypeFragment";
//    private final static String TAGFfragment = "fragment";
//    private RecyclerView rvRoomType;
//    private TextView tvBraName, tvDate;
//
//    private static TextView tvResultLocation, tvResultCheckin, tvResultCheckout, tvResultServlet;
//    private static Button btnCheckinDate, btnCheckoutDate, btnSearch;
//    private Spinner spBranch;
//
//    private static int year, month, day;
//
//    private CommonTask getRoomTypeTask, getRoomTypeByBraNameTask;
//    private ImageTask getRoomTypeImageTask;
//
//    private List<RoomTypeBra> roomTypeList;
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {   // 載入layout, 何種容器, 傳遞資料用
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_room_type);
//
//        findViews();
//        showRightNow();
//
//        rvRoomType = findViewById(R.id.recyclerView);
//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);   // getActivity() 取得畫面物件
//        rvRoomType.setLayoutManager(layoutManager);
//
////        tvBraName = findViewById(R.id.tvBraName);
////        tvDate = findViewById(R.id.tvDate);
//
////        tvBraName.setText("全部房型");
////        tvDate.setText("2018-12-15～2018-12-30");
//
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//
//        if (Util.isNetworkConnected(this)) {     // 取得 context => fragment 無法直接取得
//            String url = Util.URL + "roomType/RoomType.do";
////            Log.d(TAG, url);
//            JsonObject jsonObject = new JsonObject();
//            jsonObject.addProperty("action", "getAllRoomType");
//
//            String jsonOut = jsonObject.toString();
//            getRoomTypeTask = new CommonTask(url, jsonOut);            // url, 輸出字串 => 送到 servlet, 比對是否為會員, 回傳會員編號
//            try {
//                String jsonIn = getRoomTypeTask.execute().get();    // 取得 servlet 回傳資料
//
//                Type listType = new TypeToken<List<RoomTypeBra>>(){
//                }.getType();
//                roomTypeList = new Gson().fromJson(jsonIn, listType);
//                rvRoomType.setAdapter(new RoomTypeAdapter(roomTypeList));   // 設定掌控ListView內容物的Adapter => layout和資料皆在一起
//
//            } catch (ExecutionException e) {
//                Log.e(TAG, e.toString());
//            } catch (InterruptedException e) {
//                Log.e(TAG, e.toString());
//            }
//        } else {
//            Util.showToast(this, R.string.msg_error);    //   ?????
//        }
//
//    }
//
//    public void showRightNow(){
//        Calendar cal = Calendar.getInstance();
//        year = cal.get(Calendar.YEAR);
//        month = cal.get(Calendar.MONTH);
//        day = cal.get(Calendar.DAY_OF_MONTH);
//        updateInfo(btnCheckinDate);
//        updateInfo(btnCheckoutDate);
//    }
//
//    public static void updateInfo(Button button){
//        StringBuilder sb = new StringBuilder();
//        String str = sb.append(year).append("-").append(month + 1).append("-").append(day).toString();
//        button.setText(str);
//    }
//
//    public void findViews(){
//        spBranch = findViewById(R.id.spLocation);
//        btnCheckinDate = findViewById(R.id.btnCheckinDate);
//        btnCheckoutDate = findViewById(R.id.btnCheckoutDate);
//        btnSearch = findViewById(R.id.btnSearch);
//
//        String[] braName = {"新竹市", "花蓮市", "屏東市"};
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, braName);
//
//        spBranch.setAdapter(adapter);
//        spBranch.setSelection(0, true);
//
//        ItemListener itemListener = new ItemListener();
//        spBranch.setOnItemSelectedListener(itemListener);    // 下拉選單
//
//        Listener listener = new Listener();
//        btnCheckinDate.setOnClickListener(listener);   // 選擇入住日期
//        btnCheckoutDate.setOnClickListener(listener);  // 選擇退房日期
//        btnSearch.setOnClickListener(listener);
//    }
//
//    private class ItemListener implements Spinner.OnItemSelectedListener{
//        @Override
//        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//            tvResultLocation.setText(parent.getItemAtPosition(position).toString());
//        }
//
//        @Override
//        public void onNothingSelected(AdapterView<?> parent) {
//
//        }
//    }
//
//
//    private class Listener implements View.OnClickListener{
//        @Override
//        public void onClick(View view) {
//            switch(view.getId()){
//                case R.id.btnCheckinDate:
//                    DatePickerFragment datePickerFragmentCheckIn = new DatePickerFragment();
//                    FragmentManager fm1 = getSupportFragmentManager();
//                    datePickerFragmentCheckIn.show(fm1, "datePickerCheckin");
//                    break;
//                case R.id.btnCheckoutDate:
//                    DatePickerFragment datePickerFragmentCheckOut = new DatePickerFragment();
//                    FragmentManager fm2 = getSupportFragmentManager();
//                    datePickerFragmentCheckOut.show(fm2, "datePickerCheckout");
//                    break;
//                case R.id.btnSearch:
//                    // 判斷日期是否合理
//                    if(Util.isNetworkConnected(RoomTypeActivity.this)){
//                        String url = Util.URL + "roomType/RoomType.do";
//                        JsonObject jsonObject = new JsonObject();
//
//                        String selectBraName = tvBraName.getText().toString();
//                        String checkinDate = tvResultCheckin.getText().toString();
//                        String checkoutDate = tvResultCheckout.getText().toString();
//
//                        jsonObject.addProperty("action", "getRoomTypeForBraName");
//                        jsonObject.addProperty("select", selectBraName);
//                        jsonObject.addProperty("checkinDate", checkinDate);
//                        jsonObject.addProperty("checkoutDate", checkoutDate);
//
//                        String jsonOut = jsonObject.toString();
//                        getRoomTypeByBraNameTask = new CommonTask(url, jsonOut);
//                        try{
//                            String jsonIn = getRoomTypeByBraNameTask.execute().get();
//                            Type listType = new TypeToken<List<RoomTypeBra>>() {
//                            }.getType();
//
//                            roomTypeList = new Gson().fromJson(jsonIn, listType);
//                        } catch (ExecutionException e) {
//                            Log.e(TAG, e.toString());
//                        } catch (InterruptedException e) {
//                            Log.e(TAG, e.toString());
//                        }
//                    } else
//                        Toast.makeText(RoomTypeActivity.this, "連線失敗", Toast.LENGTH_SHORT).show();
//                    break;
//            }
//        }
//    }
//
//
//    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
//
//        @Override
//        public Dialog onCreateDialog(Bundle savedInstanceState) {
//
//            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), DatePickerFragment.this, year, month, day);
//
//            long minDate = System.currentTimeMillis();
//            long maxDate = System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 30L;
////            Log.d("show", "millis = " + minDate);
////            Log.d("show", "millis = " + maxDate);
//            datePickerDialog.getDatePicker().setMinDate(minDate);   // 只能選當天之後的
//            datePickerDialog.getDatePicker().setMaxDate(maxDate);   // 加上30天
//
//            return datePickerDialog;
//        }
//
//        @Override
//        public void onDateSet(DatePicker datePicker, int y, int m, int d) {
//            year = y;
//            month = m;
//            day = d;
////
//            switch(getTag()){
//                case "datePickerCheckin":
//                    updateInfo(btnCheckinDate);
//                    break;
//                case "datePickerCheckout":
//                    updateInfo(btnCheckoutDate);
//                    break;
//            }
//        }
//    }
//
//    public class RoomTypeAdapter extends RecyclerView.Adapter<RoomTypeAdapter.ViewHolder>{
//
//        private List<RoomTypeBra> roomTypeList;
//
//        public RoomTypeAdapter(List<RoomTypeBra> roomTypeList){     // 建構子, 將要顯示的資料帶進來
//            this.roomTypeList = roomTypeList;                  // 取得用集合裝的資料
//        }
//
//        // 建立ViewHolder，藉由ViewHolder做元件綁定
//        class ViewHolder extends RecyclerView.ViewHolder{
//
//            private ImageView ivRtImage;
//            private TextView tvRtName, tvRtQuantity;
//            private ViewHolder(View view){
//                super(view);
//
//                ivRtImage = view.findViewById(R.id.ivRtImage);        // 把元件附在 單筆的 layout上
//                tvRtName = view.findViewById(R.id.tvRtName);
//                tvRtQuantity = view.findViewById(R.id.tvRtQuantity);
//            }
//        }
//
//        @Override
//        public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {    // 創建畫面綁定資料, 在哪個畫面上 => 回傳資料
//            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_room_type, parent, false);
//            // 設定畫面 = (取得是哪個容器).載入layout用(載入layout, 放在RecyclerView裡面, )
//
////            ViewHolder viewHolder = new ViewHolder(view);
////            return viewHolder;
//            return new ViewHolder(view);   // 可拆成上面兩行
//        }
//
//        @Override
//        public void onBindViewHolder(ViewHolder viewHolder, int position) {    // 連結(資料, 第幾個) =>  次一筆
//            //將資料注入到View裡
//            final RoomTypeBra roomTypeBra = roomTypeList.get(position);        // 因為被匿名類別哪來用, 所以加上 final => 取得第幾個VO
//            int imageSize = getResources().getDisplayMetrics().widthPixels / 4;
//            String url = Util.URL + "roomType/RoomType.do";
//            String rtId = roomTypeBra.getRtID();
//            Bitmap bitmap = null;
//
//            JsonObject jsonObject = new JsonObject();
//            jsonObject.addProperty("action", "getRoomTypeImage");
//            jsonObject.addProperty("rtId", rtId);
//            jsonObject.addProperty("imageSize", imageSize);
//            getRoomTypeImageTask = new ImageTask(url, jsonObject.toString());
//            try {
//                bitmap = getRoomTypeImageTask.execute().get();
//            } catch (ExecutionException e) {
//                Log.e(TAG, e.toString());
//            } catch (InterruptedException e) {
//                Log.e(TAG, e.toString());
//            }
//            viewHolder.tvRtName.setText(roomTypeBra.getRtName());
////            viewHolder.tvRtQuantity.setText(getString(R.string.rt_quantity) + roomTypeBra.);
//            if(bitmap != null)
//                viewHolder.ivRtImage.setImageBitmap(bitmap);
//
//            // itemView為ViewHolder內建屬性(指的就是每一列)
//            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {  //設定監聽器
//                @Override
//                public void onClick(View view) {       //點擊後發生
//                    replaceFragment(roomTypeBra);
//                }
//            });
//        }
//
//        @Override
//        public int getItemCount() {      // ListView總列數
//            return roomTypeList.size();    // 取得資料數量
//        }
//    }
//
//    public void replaceFragment(RoomTypeBra roomTypeBra){       // 先remove再add
//        FragmentManager manager = getSupportFragmentManager();
//        FragmentTransaction transaction = manager.beginTransaction();
//        RoomTypeInfoFragment fragmennt = new RoomTypeInfoFragment();
//        Bundle bundle = new Bundle();
//        bundle.putSerializable("roomTypeBra", roomTypeBra);
//        fragmennt.setArguments(bundle);                 // 傳遞資料
//
//        transaction.replace(R.id.frameLayout, fragmennt, TAGFfragment);   // 換一個新的, 也可以直接建立一個新的 => 放在哪個容器, 哪個fragmennt  => 找不到fragment
//        transaction.commit();                                              // 一定要有
//    }
//
//    @Override
//    public void onStop() {      // 釋放資源
//        super.onStop();
//        if(getRoomTypeTask != null){
//            getRoomTypeTask.cancel(true);
//        }
//
//        if(getRoomTypeImageTask != null){
//            getRoomTypeImageTask.cancel(true);
//        }
//
//        if(getRoomTypeByBraNameTask != null){
//            getRoomTypeByBraNameTask.cancel(true);
//        }
//    }
//}
//
