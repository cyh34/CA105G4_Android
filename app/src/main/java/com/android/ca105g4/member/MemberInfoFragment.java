package com.android.ca105g4.member;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.ca105g4.R;
import com.android.ca105g4.main.Util;
import com.android.ca105g4.task.ImageTask;
import com.google.gson.JsonObject;

import java.util.concurrent.ExecutionException;

public class MemberInfoFragment extends Fragment {

    private static final String TAG = "MemberInfoActivity";
    private TextView tvMemName, tvMemBirth, tvMemEmail, tvMemTel, tvMemAddr, tvMemSex, tvMemSkill;
    private ImageView ivMemberImage;

    private MemberVO memberVO = null;

    private ImageTask getMemberImageTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {   // 載入layout, 何種容器, 傳遞資料用
        super.onCreate(savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_member_info, container, false);  // 建立畫面

//        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);   // 再貼回來的時候執行
//        toolbar.setTitle(R.string.member);

        //取得資料
        memberVO = (MemberVO) getArguments().getSerializable("memberVO");

        ivMemberImage =  view.findViewById(R.id.ivMemberImage);
        tvMemName =  view.findViewById(R.id.tvMemName);
        tvMemBirth =  view.findViewById(R.id.tvMemBirth);
        tvMemEmail =  view.findViewById(R.id.tvMemEmail);
        tvMemTel =  view.findViewById(R.id.tvMemTel);
        tvMemAddr =  view.findViewById(R.id.tvMemAddr);
        tvMemSex =  view.findViewById(R.id.tvMemSex);
        tvMemSkill =  view.findViewById(R.id.tvMemSkill);

        String name = getString(R.string.memName) + memberVO.getMemName();
        String birth = getString(R.string.memBirth) + memberVO.getMemBirth();
        String email = getString(R.string.memEmail) + memberVO.getMemEmail();
        String tel = getString(R.string.memTel) + memberVO.getMemTel();
        String addr = getString(R.string.memAddr) + memberVO.getMemAddr();
        String sex = getString(R.string.memSex) + ("M".equals(memberVO.getMemSex()) ? "男" : "女");
        String skill = getString(R.string.memSkill) + memberVO.getMemSkill();

        Bitmap bitmap = getMemberImage(memberVO.getMemID());

//        if(bitmap == null)
            ivMemberImage.setImageResource(R.drawable.default_person);
//        else
//            ivMemberImage.setImageBitmap(bitmap);

        tvMemName.setText(name);
        tvMemBirth.setText(birth);
        tvMemEmail.setText(email);
        tvMemTel.setText(tel);
        tvMemAddr.setText(addr);
        tvMemSex.setText(sex);
        tvMemSkill.setText(skill);

        return view;
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
