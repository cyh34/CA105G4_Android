package com.android.ca105g4.member;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.ca105g4.R;
import com.android.ca105g4.main.HomeActivity;
import com.android.ca105g4.main.Util;
import com.android.ca105g4.task.CommonTask;
import com.google.gson.JsonObject;

import java.util.concurrent.ExecutionException;

public class  LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private TextInputLayout titleAccount, titlePassword;
    private EditText inputId, inputPassword;
    private Button btnSubmit;
    private ImageView ivLogo;
    private CommonTask isMemberTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final int[] count = {0};   // 設為 final 不可改變值, 所以變成陣列, 操作記憶體位置的值
        findViews();
        btnSubmit.setOnClickListener(new Listener());     // submit

        ivLogo.setOnClickListener(new View.OnClickListener() {      // 一鍵生成
            @Override
            public void onClick(View v) {
                if(count[0] == 0){
                    inputId.setText("peter1");
                    inputPassword.setText("abc123");
                    count[0]++;
                } else if(count[0] == 1) {
                    inputId.setText("tomcat");
                    inputPassword.setText("ABCDE");
                    count[0]++;
                } else {
                    inputId.setText("aaaaa");
                    inputPassword.setText("aaaaa");
                    count[0] = 0;
                }
            }
        });
    }

    private void findViews()
    {
        ivLogo = findViewById(R.id.ivLogo);
        titleAccount = findViewById(R.id.titleAccount);
        titlePassword = findViewById(R.id.titlePassword);
        inputId = findViewById(R.id.inputId);
        inputPassword = findViewById(R.id.inputPassword);
        btnSubmit = findViewById(R.id.btnSubmit);
    }

    class Listener implements View.OnClickListener
    {
        private String account, password;

        @Override
        public void onClick(View v)
        {
            account = inputId.getText().toString().trim();
            password = inputPassword.getText().toString().trim();

            if (account.isEmpty())                        // 空白判斷
                titleAccount.setError("此欄位不得為空");
            else
                titleAccount.setError(null);

            if (password.isEmpty())                 // 空白判斷
                titlePassword.setError("此欄位不得為空");
            else
                titlePassword.setError(null);

            String result = isMember(account, password);      // 回傳會員編號, 連上servlet
            if (!"nothing".equals(result)) {                  // 不是回傳 nothing 的都可登入
                Util.showToast(LoginActivity.this, R.string.msg_LoginSuccess);

                // 放入偏號設定, 紀錄狀態
                SharedPreferences preferences = getSharedPreferences(Util.PREF_FILE, MODE_PRIVATE);    // 設定為私有的
                preferences.edit().putBoolean("login", true)
                        .putString("memId", result)
                        .putString("account", account)
                        .putString("password", password).apply();

                loginToHome(result);
            } else {
                Util.showToast(LoginActivity.this, R.string.msg_LoginError);
            }
        }
    }

    public String isMember(String account, String password) {    // 送到servlet, 判斷是否為會員
        String isMember = "nothing";
        if (Util.isNetworkConnected(this)) {
            String url = Util.URL + "member/Member.do";
//            Log.d(TAG, url);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "isMember");
            jsonObject.addProperty("account", account);
            jsonObject.addProperty("password", password);

            String jsonOut = jsonObject.toString();
            isMemberTask = new CommonTask(url, jsonOut);            // url, 輸出字串 => 送到 servlet, 比對是否為會員, 回傳會員編號
            try {
                isMember = isMemberTask.execute().get();    // 取得 servlet 回傳資料
            } catch (ExecutionException e) {
                Log.e(TAG, e.toString());
            } catch (InterruptedException e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Util.showToast(LoginActivity.this, R.string.msg_error);
        }

        return isMember;    // 回傳結果, nothing 為找不到會員
    }

    protected void loginToHome(String result){
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        Bundle bundle = new Bundle();
        // 夾帶資料跳轉頁面
        bundle.putString("memId", result);   // 帶著會員編號 跳轉頁面
        intent.putExtras(bundle);            // 放入
        startActivity(intent);               // 帶著資料跳到下一個頁面    %%
        finish();                            // 畫面結束
    }

    protected void onStart(){             // 畫面回來
        super.onStart();
        SharedPreferences preferences = getSharedPreferences(Util.PREF_FILE, MODE_PRIVATE);  // 取得偏好設定
        boolean login = preferences.getBoolean("login", false);   // 取出 偏好設定裡的 login
        if(login){     // login 為true 的話
            String account = preferences.getString("account", "");
            String password = preferences.getString("password", "");

            String result = isMember(account, password);      // 回傳會員編號, 連上servlet
            if (!"nothing".equals(result)) {                  // 不是回傳 nothing 的都可登入
                loginToHome(result);
            }
        }
    }

    protected void onStop(){              // 畫面消失
        super.onStop();
        if(isMemberTask != null){         // 釋放資源
            isMemberTask.cancel(true);
        }
    }
}

