package com.example.user.finalproject.tab;

import android.Manifest;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.widget.TabHost;

import com.example.user.finalproject.R;

public class TabMainActivity extends AppCompatActivity {

    private TabLayout mTabLayout;
    private ViewPager mPager;
    private PagerAdatper mPagerAdatper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 액티비티의 최상단 status bar를 숨긴다. (setContentView보다 먼저 나온다.)
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 액티비티의 타이틀을 숨긴다. (setContentView보다 먼저 나온다.)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_tab_main);

        // 권한 획득 코드
        ActivityCompat.requestPermissions(this, new String[] {
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        }, 0);

        mTabLayout = findViewById(R.id.tabLayout);
        mPager = findViewById(R.id.pager);

        mTabLayout.addTab(mTabLayout.newTab().setIcon(R.drawable.find));
        mTabLayout.addTab(mTabLayout.newTab().setIcon(R.drawable.lost));
        mTabLayout.addTab(mTabLayout.newTab().setIcon(R.drawable.map));

        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);//탭의 가로 전체 사이즈 지정
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);

        //프래그먼트를 관리할 PagerAdapter를 생성한다.
        mPagerAdatper = new PagerAdatper(getSupportFragmentManager(),mTabLayout.getTabCount());
        mPager.setAdapter(mPagerAdatper);

        //Tab 레이아웃과 ViewPager를 이벤트로 서로 연결시켜준다.
        //ViewPager가 움직였을 때, 탭이 바뀌게끔 한다.
        mPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));

        //탭레이아웃이 바뀌면 ViewPager의 Fragment도 바뀌는 작업을 연결한다.
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //현재 사용자가 바꾼 탭의 이벤트가 넘어온다.
                mPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
