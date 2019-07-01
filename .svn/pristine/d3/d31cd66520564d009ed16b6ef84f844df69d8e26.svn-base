package com.example.user.finalproject.tab;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * 뷰 페이져 어댑터 이다.
 * @since 2018.07.13
 * @author 조수연
 */

public class PagerAdatper extends FragmentStatePagerAdapter {

    //탭의 갯수를 저장하고 있는 멤버변수
    private int mNumOfTabs;
    private TabFindFragment tab1;
    private TabLostFragment tab2;
    private TabMapFragment tab3;

    public PagerAdatper(FragmentManager fm, int numOfTabs) {
        super(fm);
        mNumOfTabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        //BaseAdapter에서 getView()메서드에 해당하는 메서드로써,
        //position 값이 곧! 현재 선택된 Tab의 Index번호를 나타낸다.

        switch (position){
            case 0:
                if(tab1 == null) {
                    tab1 = new TabFindFragment();
                }
                return tab1;
            case 1:
                if(tab2 == null) {
                    tab2 = new TabLostFragment();
                }
                return tab2;
            case 2:
                if(tab3 == null) {
                    tab3 = new TabMapFragment();
                }
                return tab3;

        }
        return null;
    }

    @Override
    public int getCount() {
        //전체 탭의 갯수를 넘긴다.
        return mNumOfTabs;
    }
}
