package com.android.ca105g4.main;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.ca105g4.R;
import com.android.ca105g4.coupon.CouponFragment;
import com.android.ca105g4.member.CollectCouponFragment;
import com.android.ca105g4.member.LoginActivity;
import com.android.ca105g4.member.MemberInfoFragment;
import com.android.ca105g4.member.MemberVO;
import com.android.ca105g4.order.OrdersVO;
import com.android.ca105g4.roomType.RoomTypeBra;
import com.android.ca105g4.roomType.RoomTypeInfoFragment;
import com.android.ca105g4.roomType.RoomTypeVO;
import com.android.ca105g4.task.CommonTask;
import com.android.ca105g4.task.ImageTask;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.sql.Date;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

// style主題：注意
// MainActivity：onCreateOptionsMenu創建一定要有
// manifest：修改, 不能同使存在兩條ActionBar


public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private static final String TAG = "HomeActivity";
    private final static String TAGFragment = "fragment";

    private LinearLayout linearLayout;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;                    // 側邊欄
    private Toolbar toolbar;
    private ImageView ivNavBackgroundImage;
    private TextView tvMemberName;
    private ImageView ivNavMemberImage;

    private BottomSheetBehavior mBottomSheetBehavior;
    private View bottomSheet;

    // 連servlet
    private CommonTask getMemberTask;
    private ImageTask getMemberImageTask;

    private MemberVO memberVO;

    //RoomType

    private final static String TAGFfragment = "fragment";
    private RecyclerView rvRoomType;
    private TextView tvBraName, tvDate;

    private static TextView tvResultLocation, tvResultCheckin, tvResultCheckout, tvResultServlet;
    private static Button btnCheckinDate, btnCheckoutDate, btnSearch;
    private Spinner spBranch;
    private boolean isSearch = false;

    private static DatePickerDialog datePickerDialog;
    private static DateVO dateVO = new DateVO();
    private static long minDate, maxDate;
    private static int year, month, day;

    private CommonTask getRoomTypeTask, getRoomTypeByBraNameTask, getBraNameTask;
    private ImageTask getRoomTypeImageTask;

    private List<RoomTypeBra> roomTypeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Bundle bundle = getIntent().getExtras();        // 取出跳轉後的資料
        final String memId = bundle.getString("memId");

        // 工具列設定
        toolbar = findViewById(R.id.toolbar);
        toolbar.setLogo(R.mipmap.ic_logo_round);
        toolbar.setTitle(R.string.app_name);            // 再貼被回來的時候, 要再執行 => 切換標題      ?????
//        setSupportActionBar(toolbar);            // 在toolbar上加入menu
//        // Menu item click的監聽事件一樣要設定在setSupportActionBar之後才有作用
//        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {      // 設定 toolbar icon 按鈕監聽器
//            @Override
//            public boolean onMenuItemClick(MenuItem menuItem) {
//                switch(menuItem.getItemId()){s
//                    case R.id.toolbar_search:
//                        Toast.makeText(HomeActivity.this, "search", Toast.LENGTH_SHORT).show();
//                        break;
//                    case R.id.toolbar_map:
//                        Toast.makeText(HomeActivity.this, "Map", Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(HomeActivity.this, MapActivity.class);
//                        startActivity(intent);
//                        break;
//                }
//                return true;
//            }
//        });

        // 側邊欄選單
        drawerLayout = findViewById(R.id.drawerLayout);   //    (這個畫面, 選單要放在[drawerLayout]裡面, 放在toolbar上, , )
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(HomeActivity.this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);   // 加入切換開關設定
        toggle.syncState();                       // 讓Drawer開關出現三條線

        // 側邊欄 找出會員資料
        navigationView = findViewById(R.id.navigationView);                      // 先取得 側邊欄 物件     => 可改成 實體變數
        View nav = navigationView.getHeaderView(0);                        // 回傳畫面, 索引值0(header)
        navigationView.setNavigationItemSelectedListener(HomeActivity.this);     // 設定 側邊欄 監聽器
        ivNavBackgroundImage = nav.findViewById(R.id.ivNavBackgroundImage);
        ivNavMemberImage = nav.findViewById(R.id.ivNavMemberImage);
        tvMemberName = nav.findViewById(R.id.tvMemberName);

        Template template = new Template(this);
        memberVO = getMemberInfo(memId);                   // 到servlet找會員資料
//        Bitmap bitmap = getMemberImage(memId);             // 到servlet找會員圖片
//        tvMemberName.setText(memberVO.getMemName());
//        template.setImage(bitmap, ivNavMemberImage, R.drawable.default_member);   // 到servlet找會員圖片
        template.setNavBackground(ivNavBackgroundImage);
        //                取得圖片,             , 元件         , 預設圖片
//        getMemberImage(memId);                        // 另一種方式


//        FragmentManager manager = getSupportFragmentManager();
//        FragmentTransaction transaction = manager.beginTransaction();
//        RoomTypeFragment fragmennt = new RoomTypeFragment();
////        transaction.replace(R.id.frameLayout, fragmennt, TAGFragment);
//        transaction.replace(R.id.frameLayout, fragmennt, TAGRtFragment);
//        // 換一個新的, 也可以直接建立一個新的 => 放在哪個容器, 哪個fragmennt  => 找不到fragment
//        transaction.commit();

//        ivBackgroundImage.setBackgroundResource(imageId);
//        ivNavMemberImage.setImageResource(imageId);

//        ivMemberImage.setImageBitmap(bitmap);

//        if(bitmap != null){            // 判斷是否有圖片
//            ivMemberImage.setImageBitmap(bitmap);   // 設定
//        } else{
//            ivMemberImage.setImageResource(R.drawable.default_person);    // 沒有, 使用預設的
//        }

//        Log.d(TAG, memberVO.getMemName());
        SharedPreferences preferences = getSharedPreferences(Util.PREF_FILE, MODE_PRIVATE);    // 設定為私有的
        preferences.edit().putString("name", memberVO.getMemName()).apply();

        linearLayout = findViewById(R.id.main_roomType);
        linearLayout.setVisibility(View.VISIBLE);

        findViews();
        showRightNow();

        rvRoomType = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);   // getActivity() 取得畫面物件
        rvRoomType.setLayoutManager(layoutManager);

    }

    @Override
    public void onBackPressed() {                    // 設定返回鍵功能 => 如何設計 ??    %%
        FragmentManager manager = getSupportFragmentManager();          //拆下來 製成方法
        FragmentTransaction transaction = manager.beginTransaction();
        Fragment fragment = manager.findFragmentByTag(TAGFragment);

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (fragment != null) {   // 移除
            linearLayout.setVisibility(View.VISIBLE);

            isSearch = false;      //初始化, 旗標
            initHomeActivity();    //初始化, 回到預設狀態

            transaction.remove(fragment);   //移除
            transaction.commit();
        } else {
            finish();
        }
    }

    public void initHomeActivity(){   //初始化, 回到預設狀態
        Spinner spBranch = findViewById(R.id.spBranch);
        Button btnCheckinDate = findViewById(R.id.btnCheckinDate);
        Button btnCheckoutDate = findViewById(R.id.btnCheckoutDate);

        spBranch.setSelection(0);
        btnCheckinDate.setText("請選擇日期");
        btnCheckoutDate.setText("請選擇日期");
    }

    public List<String> getBraName(){

        List<String> braNameList = null;

        if (Util.isNetworkConnected(this)) {     // 取得 context => fragment 無法直接取得
            String url = Util.URL + "branch/Branch.do";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAllBraName");

            String jsonOut = jsonObject.toString();
            getBraNameTask = new CommonTask(url, jsonOut);            // url, 輸出字串 => 送到 servlet, 比對是否為會員, 回傳會員編號
            try {
                String jsonIn = getBraNameTask.execute().get();    // 取得 servlet 回傳資料
                Log.d(TAG, "jsonIn = " + jsonIn);
                Type listType = new TypeToken<List<String>>(){
                }.getType();

                braNameList = new Gson().fromJson(jsonIn, listType);
            } catch (ExecutionException e) {
                Log.e(TAG, e.toString());
            } catch (InterruptedException e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Util.showToast(this, R.string.msg_error);    //   ?????
        }
        return braNameList;
    }

    @Override
    public void onStart() {
        super.onStart();

        getRoomType();
    }

    public void getRoomType() {
        if (Util.isNetworkConnected(this)) {     // 取得 context => fragment 無法直接取得
            String url = Util.URL + "roomType/RoomType.do";
            String select = (String)spBranch.getSelectedItem();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAllRoomType");
            jsonObject.addProperty("braName", select);

            String jsonOut = jsonObject.toString();
            getRoomTypeTask = new CommonTask(url, jsonOut);            // url, 輸出字串 => 送到 servlet, 比對是否為會員, 回傳會員編號
            try {
                String jsonIn = getRoomTypeTask.execute().get();    // 取得 servlet 回傳資料

                Type listType = new TypeToken<List<RoomTypeBra>>(){
                }.getType();
                roomTypeList = new Gson().fromJson(jsonIn, listType);
                rvRoomType.setAdapter(new RoomTypeAdapter(roomTypeList));   // 設定掌控ListView內容物的Adapter => layout和資料皆在一起

            } catch (ExecutionException e) {
                Log.e(TAG, e.toString());
            } catch (InterruptedException e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Util.showToast(this, R.string.msg_error);    //   ?????
        }
    }

    public void showRightNow(){
        Calendar cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);

        btnCheckinDate.setText("請選擇日期");
        btnCheckoutDate.setText("請選擇日期");

//        updateInfo(btnCheckinDate);
//        updateInfo(btnCheckoutDate);
    }

    public static void updateInfo(Button button){

        StringBuilder sb = new StringBuilder();
//        month = month + 1;

        sb.append(year).append("-").append((month + 1) < 10 ? "0" + (month + 1) : (month + 1)).append("-").append((day) < 10 ? "0" + day : day);
        button.setText(sb.toString());

//        button.setText(new StringBuilder().append(year).append("-").append(month + 1).append("-").append(day));
    }

    public void findViews(){
        spBranch = findViewById(R.id.spBranch);
        btnCheckinDate = findViewById(R.id.btnCheckinDate);
        btnCheckoutDate = findViewById(R.id.btnCheckoutDate);
        btnSearch = findViewById(R.id.btnSearch);

        List<String> branch = getBraName();      // 動態下拉選單
//        String[] branch = {"全部", "福翔", "麻翔"};      // 靜態態下拉選單
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, branch);

        spBranch.setAdapter(adapter);
        spBranch.setSelection(0, true);
        ItemListener itemListener = new ItemListener();
        spBranch.setOnItemSelectedListener(itemListener);

//        HomeActivity.ItemListener itemListener = new HomeActivity.ItemListener();
//        spBranch.setOnItemSelectedListener(itemListener);    // 下拉選單

        Listener listener = new Listener();
        btnCheckinDate.setOnClickListener(listener);   // 選擇入住日期
        btnCheckoutDate.setOnClickListener(listener);  // 選擇退房日期
        btnSearch.setOnClickListener(listener);

    }

    private class ItemListener implements Spinner.OnItemSelectedListener{
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if(spBranch.getSelectedItemPosition() == 0){
                isSearch = false;     //看到不訂房按鈕

                btnCheckinDate.setText("請選擇日期");
                btnCheckoutDate.setText("請選擇日期");
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    private class Listener implements View.OnClickListener{
        @Override
        public void onClick(View view) {

            switch(view.getId()){
                case R.id.btnCheckinDate:     //入住日期
                    isSearch = false;     //看到不訂房按鈕

//                    changeMinMaxDate("max");

                    HomeActivity.DatePickerFragment datePickerFragmentCheckIn = new HomeActivity.DatePickerFragment();
                    FragmentManager fm1 = getSupportFragmentManager();
                    datePickerFragmentCheckIn.show(fm1, "datePickerCheckin");
                    break;
                case R.id.btnCheckoutDate:   //退房日期
                    isSearch = false;     //看到不訂房按鈕

//                    changeMinMaxDate("min");

                    HomeActivity.DatePickerFragment datePickerFragmentCheckOut = new HomeActivity.DatePickerFragment();
                    FragmentManager fm2 = getSupportFragmentManager();
                    datePickerFragmentCheckOut.show(fm2, "datePickerCheckout");
                    break;
                case R.id.btnSearch:
                    // 判斷日期是否合理
                    if(Util.isNetworkConnected(HomeActivity.this)){
                        String url = Util.URL + "roomType/RoomType.do";
                        JsonObject jsonObject = new JsonObject();

                        String selectBraName = (String)spBranch.getSelectedItem();
                        String checkinDate = btnCheckinDate.getText().toString();
                        String checkoutDate = btnCheckoutDate.getText().toString();

//                        if(!"全部".equals(selectBraName)) {
//                            Toast.makeText(HomeActivity.this, "請選擇分店", Toast.LENGTH_SHORT).show();
//                        } else


                        long checkinLong = Date.valueOf(btnCheckinDate.getText().toString()).getTime();
                        long checkoutLong = Date.valueOf(btnCheckoutDate.getText().toString()).getTime();

                        if(!"全部".equals(selectBraName) && ("請選擇日期".equals(checkinDate) || "請選擇日期".equals(checkoutDate))){
                            isSearch = false;  //看到不訂房按鈕

                            Toast.makeText(HomeActivity.this, "請選擇日期", Toast.LENGTH_SHORT).show();
                            return;
                        } else if("全部".equals(selectBraName)){
                            isSearch = false;

                            Toast.makeText(HomeActivity.this, "搜尋全部房型", Toast.LENGTH_SHORT).show();
                        } else if(checkoutLong - checkinLong <= 0){    //判斷入住時間
                            isSearch = false;

                            Toast.makeText(HomeActivity.this, "請確認住房時間", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            isSearch = true;    //旗標, 按下search才能按訂房

                            Toast.makeText(HomeActivity.this, "搜尋" + selectBraName + "分店的房型", Toast.LENGTH_SHORT).show();
                        }

                        Log.d(TAG, String.valueOf(isSearch));

                        jsonObject.addProperty("action", "getAllRoomType");
                        jsonObject.addProperty("braName", selectBraName);
                        jsonObject.addProperty("checkinDate", checkinDate);
                        jsonObject.addProperty("checkoutDate", checkoutDate);

                        String jsonOut = jsonObject.toString();
                        getRoomTypeByBraNameTask = new CommonTask(url, jsonOut);
                        try{
                            String jsonIn = getRoomTypeByBraNameTask.execute().get();
                            Type listType = new TypeToken<List<RoomTypeBra>>() {
                            }.getType();

                            roomTypeList = new Gson().fromJson(jsonIn, listType);
                            rvRoomType.setAdapter(new RoomTypeAdapter(roomTypeList));   // 設定掌控ListView內容物的Adapter => layout和資料皆在一起
                        } catch (ExecutionException e) {
                            Log.e(TAG, e.toString());
                        } catch (InterruptedException e) {
                            Log.e(TAG, e.toString());
                        }
                    } else
                        Toast.makeText(HomeActivity.this, "連線失敗", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            datePickerDialog = new DatePickerDialog(getActivity(), DatePickerFragment.this, year, month, day);

//            Log.d("show", "millis = " + minDate);
//            Log.d("show", "millis = " + maxDate);
            datePickerDialog.getDatePicker().setMinDate(dateVO.getInitMinDate());   // 只能選當天之後的
            datePickerDialog.getDatePicker().setMaxDate(dateVO.getInitMaxDate());   // 加上30天

            return datePickerDialog;
        }

        @Override
        public void onDateSet(DatePicker datePicker, int y, int m, int d) {
            year = y;
            month = m;
            day = d;

            String dateTag = getTag();

            switch(dateTag){
                case "datePickerCheckin":
                    updateInfo(btnCheckinDate);
                    Log.d(TAG, "IN");
                    break;
                case "datePickerCheckout":
                    updateInfo(btnCheckoutDate);
                    Log.d(TAG, "OUT");
                    break;
            }
        }
    }

    public void changeMinMaxDate(String str){

        String dateStr = null;

        try {
            if("min".equals(str)) {
                dateStr = btnCheckinDate.getText().toString();
                dateVO.setMinDate(Date.valueOf(dateStr).getTime());                 //無法轉型, 進入catch
                datePickerDialog.getDatePicker().setMinDate(dateVO.getMinDate());   //設定最小時間
                datePickerDialog.getDatePicker().setMaxDate(dateVO.getInitMaxDate());
                Log.d(TAG, "設定最小值");
            } else if("max".equals(str)) {
                dateStr = btnCheckoutDate.getText().toString();
                dateVO.setMaxDate(Date.valueOf(dateStr).getTime());                 //無法轉型, 進入catch
                datePickerDialog.getDatePicker().setMinDate(dateVO.getInitMinDate());
                datePickerDialog.getDatePicker().setMaxDate(dateVO.getMaxDate());   //設定最大時間
                Log.d(TAG, "設定最大值");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class RoomTypeAdapter extends RecyclerView.Adapter<RoomTypeAdapter.ViewHolder>{

        private List<RoomTypeBra> roomTypeList;

        public RoomTypeAdapter(List<RoomTypeBra> roomTypeList){     // 建構子, 將要顯示的資料帶進來
            this.roomTypeList = roomTypeList;                  // 取得用集合裝的資料
        }

        // 建立ViewHolder，藉由ViewHolder做元件綁定
        class ViewHolder extends RecyclerView.ViewHolder{

            private ImageView ivRtImage;
            private TextView tvRtName, tvRtHolidayPrice, tvRtQuantity;
            private ViewHolder(View view){
                super(view);

                ivRtImage = view.findViewById(R.id.ivRtImage);        // 把元件附在 單筆的 layout上
                tvRtName = view.findViewById(R.id.tvRtName);
                tvRtHolidayPrice = view.findViewById(R.id.tvRtHolidayPrice);
                tvRtQuantity = view.findViewById(R.id.tvRtQuantity);
            }
        }

        @Override
        public RoomTypeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int i) {    // 創建畫面綁定資料, 在哪個畫面上 => 回傳資料
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_room_type, parent, false);
            // 設定畫面 = (取得是哪個容器).載入layout用(載入layout, 放在RecyclerView裡面, )

//            ViewHolder viewHolder = new ViewHolder(view);
//            return viewHolder;
            return new RoomTypeAdapter.ViewHolder(view);   // 可拆成上面兩行
        }

        @Override
        public void onBindViewHolder(RoomTypeAdapter.ViewHolder viewHolder, int position) {    // 連結(資料, 第幾個) =>  次一筆
            //將資料注入到View裡
            final RoomTypeBra roomTypeBra = roomTypeList.get(position);        // 因為被匿名類別哪來用, 所以加上 final => 取得第幾個VO
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
                viewHolder.ivRtImage.setImageBitmap(bitmap);

            viewHolder.tvRtName.setText(roomTypeBra.getRtName());
//            viewHolder.tvRtQuantity.setText(getString(R.string.rt_quantity) + roomTypeBra.);

            viewHolder.tvRtHolidayPrice.setText(getString(R.string.rt_holidayPrice) + roomTypeBra.getHolidayPrice());

            viewHolder.tvRtQuantity.setVisibility(View.INVISIBLE);   // 隱藏
            String select = (String)spBranch.getSelectedItem();
            if(!"全部".equals(select)){
                viewHolder.tvRtQuantity.setVisibility(View.VISIBLE);  //消失
                viewHolder.tvRtQuantity.setText(getString(R.string.rt_quantity) + roomTypeBra.getRooms());
            }

            // itemView為ViewHolder內建屬性(指的就是每一列)
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {  //設定監聽器
                @Override
                public void onClick(View view) {       //點擊後發生
                    linearLayout.setVisibility(View.GONE);          //未選擇日期時, 看不到訂房按鈕
                    replaceFragment(roomTypeBra);
                }
            });
        }

        @Override
        public int getItemCount() {      // ListView總列數
            return roomTypeList.size();    // 取得資料數量
        }
    }

    public void replaceFragment(RoomTypeBra roomTypeBra){       // 先remove再add
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        RoomTypeInfoFragment fragment = new RoomTypeInfoFragment();

        Bundle bundle = new Bundle();
        OrdersVO ordersVO = new OrdersVO();
        ordersVO.setMemID(memberVO.getMemID());     //存會員編號
        ordersVO.setBraID(roomTypeBra.getBraID());  //存分店編號

        bundle.putSerializable("ordersVO", ordersVO);
        bundle.putString("braName", spBranch.getSelectedItem().toString());
        bundle.putString("checkinDate", btnCheckinDate.getText().toString());
        bundle.putString("checkoutDate", btnCheckoutDate.getText().toString());
        bundle.putBoolean("isSearch", isSearch);         //放入isSearch 旗標 boolean

        bundle.putSerializable("roomTypeBra", roomTypeBra);
        fragment.setArguments(bundle);                 // 傳遞資料

        transaction.replace(R.id.frameLayout, fragment, TAGFragment);   // 換一個新的, 也可以直接建立一個新的 => 放在哪個容器, 哪個fragmennt  => 找不到fragment
        transaction.commit();                                              // 一定要有
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {       //創建menu 一定要有
        // 為了讓Toolbar的 Menu有作用，這邊的程式不可以拿掉
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {      // 側邊欄的按鈕
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Intent intent;
        Bundle bundle;
        switch (item.getItemId()) {
            case R.id.nav_home:
                linearLayout.setVisibility(View.VISIBLE);

                manager = getSupportFragmentManager();
                transaction = manager.beginTransaction();
                Fragment fragment = manager.findFragmentByTag(TAGFragment);
                if (fragment != null) {     //有的話
                    transaction.remove(fragment);   //移除
                    transaction.commit();
                }
                break;
            case R.id.nav_getCoupon:               // 優惠券
//                manager = getSupportFragmentManager();
//                transaction = manager.beginTransaction();
                linearLayout.setVisibility(View.GONE);
                CouponFragment couponFragment = new CouponFragment();
                bundle = new Bundle();
                bundle.putSerializable("memberVO", memberVO);
                couponFragment.setArguments(bundle);                 // 傳遞資料

                transaction.replace(R.id.frameLayout, couponFragment, TAGFragment);   // 換一個新的, 也可以直接建立一個新的 => 放在哪個容器, 哪個fragmennt  => 找不到fragment
                transaction.commit();
                break;
            case R.id.nav_member:               // 會員資料
//                manager = getSupportFragmentManager();
//                transaction = manager.beginTransaction();
                linearLayout.setVisibility(View.GONE);

                MemberInfoFragment memberInfoFragment = new MemberInfoFragment();
                bundle = new Bundle();
                bundle.putSerializable("memberVO", memberVO);
                memberInfoFragment.setArguments(bundle);                 // 傳遞資料

                transaction.replace(R.id.frameLayout, memberInfoFragment, TAGFragment);   // 換一個新的, 也可以直接建立一個新的 => 放在哪個容器, 哪個fragmennt  => 找不到fragment
                transaction.commit();
                break;
            case R.id.nav_collect_coupon:   //收藏優惠券
//                manager = getSupportFragmentManager();
//                transaction = manager.beginTransaction();
                linearLayout.setVisibility(View.GONE);
                CollectCouponFragment collectCouponFragment = new CollectCouponFragment();
                transaction.replace(R.id.frameLayout, collectCouponFragment, TAGFragment);   // 換一個新的, 也可以直接建立一個新的 => 放在哪個容器, 哪個fragmennt  => 找不到fragment
                transaction.commit();
                break;
//            case R.id.nav_collectRoomType:      // 收藏房型
////                intent = new Intent(HomeActivity.this, MemberInfoActivity.class);
////                bundle = new Bundle();
////                bundle.putSerializable("memberVO", memberVO);
////                intent.putExtras(bundle);
////                startActivity(intent);
//                break;
//            case R.id.nav_orderHistory:         // 訂單紀錄
////                intent = new Intent(HomeActivity.this, MemberInfoActivity.class);
////                bundle = new Bundle();
////                bundle.putSerializable("memberVO", memberVO);
////                intent.putExtras(bundle);
////                startActivity(intent);
//                break;
            case R.id.nav_logout:               // 登出
                SharedPreferences preferences = getSharedPreferences(Util.PREF_FILE, MODE_PRIVATE);
                preferences.edit().putBoolean("login", false).apply();
                intent = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);                     // 加這行點了才會關閉
        return true;
    }

    public void replaceFragment(RoomTypeVO roomTypeVO){       // 先remove再add
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        RoomTypeInfoFragment fragmennt = new RoomTypeInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("roomTypeVO", roomTypeVO);
        fragmennt.setArguments(bundle);                 // 傳遞資料

        transaction.replace(R.id.frameLayout, fragmennt, TAGFragment);   // 換一個新的, 也可以直接建立一個新的 => 放在哪個容器, 哪個fragmennt  => 找不到fragment
//        transaction.replace(R.id.frameLayout, fragmennt, TAGRtFragment);   // 換一個新的, 也可以直接建立一個新的 => 放在哪個容器, 哪個fragmennt  => 找不到fragment
        transaction.commit();                                              // 一定要有
    }

    @Override
    protected void onStop(){              // 畫面消失, 釋放資源
        super.onStop();
        if(getMemberTask != null){
            getMemberTask.cancel(true);
        }

        if(getMemberImageTask != null){
            getMemberImageTask.cancel(true);
        }

        if(getRoomTypeTask != null){
            getRoomTypeTask.cancel(true);
        }

        if(getRoomTypeImageTask != null){
            getRoomTypeImageTask.cancel(true);
        }

        if(getRoomTypeByBraNameTask != null){
            getRoomTypeByBraNameTask.cancel(true);
        }
    }

    public List<RoomTypeVO> getAllRoomType(){

        List<RoomTypeVO> roomTypeList = null;
        String select = "all";

        String jsonIn;

        if (Util.isNetworkConnected(this)) {
            String url = Util.URL + "roomType/RoomType.do";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAllRoomType");
            jsonObject.addProperty("select", select);

            String jsonOut = jsonObject.toString();
            getMemberTask = new CommonTask(url, jsonOut);      // url, 輸出字串 => 送到 servlet, 找出會員資料
            try {
                jsonIn = getMemberTask.execute().get();    // 取得 servlet 回傳資料
//                Gson gson = new GsonBuilder().setDateFormat("yyyy-mm-dd").create();   // 日期格式設定 => client servlet 都要寫
                Gson gson = new Gson();   // 日期格式設定 => client servlet 都要寫

                Type listType = new TypeToken<List<RoomTypeVO>>(){
                }.getType();
                roomTypeList = gson.fromJson(jsonIn, listType);
            } catch (ExecutionException e) {
                Log.e(TAG, e.toString());
            } catch (InterruptedException e) {
                Log.e(TAG, e.toString());
            }
            if(roomTypeList == null){
                Log.d(TAG, getString(R.string.msg_error));
            }
        } else {
            Util.showToast(HomeActivity.this, R.string.msg_error);
        }
        return roomTypeList;    // 回傳結果, 房型資料
    }

    public MemberVO getMemberInfo(String memId){                   // 送到servlet, 用會員編號找出會員資料
        MemberVO memberVO = null;
        String jsonIn;

        if (Util.isNetworkConnected(this)) {
            String url = Util.URL + "member/Member.do";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getMemberInfoByMemId");
            jsonObject.addProperty("memId", memId);

            String jsonOut = jsonObject.toString();
            getMemberTask = new CommonTask(url, jsonOut);      // url, 輸出字串 => 送到 servlet, 找出會員資料
            try {
                jsonIn = getMemberTask.execute().get();    // 取得 servlet 回傳資料

                Gson gson = new GsonBuilder().setDateFormat("yyyy-mm-dd").create();   // 日期格式設定 => client servlet 都要寫
                memberVO = gson.fromJson(jsonIn, MemberVO.class);
            } catch (ExecutionException e) {
                Log.e(TAG, e.toString());
            } catch (InterruptedException e) {
                Log.e(TAG, e.toString());
            }

            if(memberVO == null){
                Log.d(TAG, getString(R.string.msg_error));
            }
        } else {
            Util.showToast(HomeActivity.this, R.string.msg_error);
        }
        return memberVO;    // 回傳結果, 會員資料
    }

    public Bitmap getMemberImage(String memId){          // 取得會員圖片
        String url = Util.URL + "member/Member.do";
        int imageSize = getResources().getDisplayMetrics().widthPixels / 3;    // 計算出 縮圖比例 => 螢幕寬度除以3當作將圖的尺寸
        Bitmap bitmap = null;

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "getMemberImageByMemId");
        jsonObject.addProperty("memId", memId);
        jsonObject.addProperty("imageSize", imageSize);

        getMemberImageTask = new ImageTask(url, jsonObject.toString());  // %% 要宣告
        try {
            bitmap = getMemberImageTask.execute().get();
        } catch (ExecutionException e) {
            Log.e(TAG, e.toString());
        } catch (InterruptedException e) {
            Log.e(TAG, e.toString());
        }

        return bitmap;
    }
}
