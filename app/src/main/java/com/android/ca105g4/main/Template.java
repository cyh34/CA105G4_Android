package com.android.ca105g4.main;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.android.ca105g4.R;

public class Template {

    private Activity activity;

    public Template(Activity activity){
        this.activity = activity;
    }

    public void toolbar(Toolbar toolbar){
        toolbar.setLogo(R.mipmap.ic_logo_round);
        toolbar.setTitle(R.string.app_name);
    }

    public void drawerLayout(DrawerLayout drawerLayout, Toolbar toolbar){
        // 側邊欄選單
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(activity, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);   // 加入切換開關設定
        toggle.syncState();                       // 讓Drawer開關出現三條線
    }

    public void setImage(Bitmap bitmap, ImageView imageView, int imageResource){
        if(bitmap != null){            // 判斷是否有圖片
            imageView.setImageBitmap(bitmap);   // 設定
        } else{
            imageView.setImageResource(imageResource);    // 沒有, 使用預設的
        }
    }

    public void setNavBackground(ImageView imageView){
        int id = (int)(Math.random() * 6);
        imageView.setBackgroundResource(R.drawable.p1 + id);
        imageView.setAlpha(0.4f);
    }
}
