package com.example.user.finalproject.tab;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.user.finalproject.FoundWriteActivity;
import com.example.user.finalproject.R;
import com.example.user.finalproject.adapter.FoundAdapter;
import com.example.user.finalproject.bean.FoundBean;
import com.example.user.finalproject.bean.SaveBean;
import com.example.user.finalproject.bean.Util;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TabFindFragment extends Fragment {

    //멤버변수
    private ListView mLstFindMember;
    private FoundAdapter mAdapter;
    public static List<FoundBean> mFoundList = new ArrayList<FoundBean>();
    private SaveBean mSaveBean;
    private EditText mEdtSearch;
    private ImageButton mBtnSearch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.find_fragment, container, false);

        //TODO 여기에 View를 찾고 이벤트를 등록하고 등등의 처리를 할 수 있다.
        //getView().findViewById(R.id.btn1).setOnClickListener();

        mLstFindMember = view.findViewById(R.id.lstFindMember);
        mEdtSearch = view.findViewById(R.id.edtSearch);
        mBtnSearch = view.findViewById(R.id.btnSearch);


        // 글 쓰기 버튼
        view.findViewById(R.id.btnPlus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), FoundWriteActivity.class);
                startActivity(i);
            }
        });


        mEdtSearch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // Enter key Action
                if ((event.getAction() == KeyEvent.ACTION_DOWN)
                        && (keyCode == KeyEvent.KEYCODE_ENTER))
                {
                    // Enter 키 눌렀을 때 처리
                    searchList();
                    return true;
                    // 리턴이 true일 경우에는 사용자가 입력한 엔터키의 이벤트를 os 단까지 보내지 않겠다는 뜻이다.
                    // 즉 사용자가 입력한 엔터키가 소면된다.
                }
                return false; // false일 경우, 사용자가 입력한 키값을 os에 다시 전달한다.
            }
        });

        // 검색버튼
        view.findViewById(R.id.btnSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchList();
            }
        });

        return view;
    }


    // 리스트에서 항목을 검색하는 메서드
    public void searchList(){
        // 검색어
        String search = mEdtSearch.getText().toString();

        List<FoundBean> tempList = new ArrayList<FoundBean>();
        List<FoundBean> allList = mSaveBean.getFoundBeanList();

        // 멤버리스트를 돌면서 하나씩 비교검색한다.
        for (int i=0; i<allList.size(); i++){
            FoundBean bean = allList.get(i);

            // 안에 찾고자 하는 검색어가 포함되어 있다면, 찾은 문자열의 index를 반환한다.
            if (bean.getTitle().indexOf(search) > -1){ // 찾았다.
                tempList.add(bean); // 찾은 객체를 add 시킨다.
            } else if(bean.getContent().indexOf(search) > -1){ // 찾았다.
                tempList.add(bean); // 찾은 객체를 add 시킨다.
            }
        } // end for

        // 입력한 검색어가 있고, 찾은 리스트가 있다면 기존 리스트를 찾은 리스트로 대체한다.
        if (search != null && search.length() > 0 && tempList.size() > 0){
            mAdapter = new FoundAdapter(getActivity(), tempList);
        }
        // 입력한 검색어가 있고, 찾은 리스트가 없다면 기존의 모든 리스트를 지운다.
        else if (search != null && search.length() > 0 && tempList.size() == 0){
            mAdapter = new FoundAdapter(getActivity(), tempList);
        }
        // 입력한 검색어가 없는 경우에는 원래의 리스트로 복원한다.
        else if (search == null || search.length() == 0){
            mAdapter = new FoundAdapter(getActivity(), allList);
        }

        mLstFindMember.setAdapter(mAdapter);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mEdtSearch.requestFocus(); // 포커스를 입력창에 다시 준다.
            }
        }, 700);

    } // end method

    public void onResume() {

        super.onResume();

        String jsonStr = Util.openFile(getActivity(), SaveBean.class.getName());
        Gson gson = new Gson();
        mSaveBean = gson.fromJson(jsonStr, SaveBean.class);

        if (mSaveBean != null){
            mAdapter = new FoundAdapter(getActivity(), mSaveBean.getFoundBeanList());
            mLstFindMember.setAdapter(mAdapter);
        }

    }

}

