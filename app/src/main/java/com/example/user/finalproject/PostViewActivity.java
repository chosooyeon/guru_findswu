package com.example.user.finalproject;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.user.finalproject.adapter.ChatAdapter;
import com.example.user.finalproject.adapter.ChatLostAdapter;
import com.example.user.finalproject.adapter.FoundAdapter;
import com.example.user.finalproject.adapter.LostAdapter;
import com.example.user.finalproject.bean.FCBean;
import com.example.user.finalproject.bean.FoundBean;
import com.example.user.finalproject.bean.LCBean;
import com.example.user.finalproject.bean.LostBean;
import com.example.user.finalproject.bean.SaveBean;
import com.example.user.finalproject.bean.Util;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PostViewActivity extends AppCompatActivity {

    private MapFragment mapFragment;
    private ScrollView mScrollView;
    private ListView mLstChat;
    private ChatAdapter mAdapter;
    public static List<FoundBean> mFoundBeanList = new ArrayList<FoundBean>();

    private FoundBean mFoundBean;
    private Button mBtnWrite;
    private EditText mEdtWrite;
    private SaveBean mSaveBean;

    private FoundBean mSelFoundBean;
    private Marker currentPositionMarker = null;
    private LatLng mCurPosLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_view);

        mSelFoundBean = (FoundBean) getIntent().getSerializableExtra(FoundAdapter.KEY_FOUND_DATA_CLASS);

        mScrollView = findViewById(R.id.scrollView);
        TextView txtTitle = findViewById(R.id.txtTitle);
        TextView txtContent = findViewById(R.id.txtContent);
        ImageView imgPhoto = findViewById(R.id.imgPhoto);

        mLstChat = findViewById(R.id.lstChat);
        mBtnWrite = findViewById(R.id.btnWrite);
        mEdtWrite = findViewById(R.id.edtWrite);

        FragmentManager fragmentManager = getFragmentManager();
        mapFragment = (MapFragment) fragmentManager.findFragmentById(R.id.map);
        mapFragment.getMapAsync(mapReadyCallback);

        mBtnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveComment();

            }
        });

        if(mSelFoundBean != null) {
            txtTitle.setText(mSelFoundBean.getTitle());
            txtContent.setText(mSelFoundBean.getContent());
        }

        if(mSelFoundBean != null && mSelFoundBean.getImgPath() != null ) {
            Bitmap bitmap = BitmapFactory.decodeFile( mSelFoundBean.getImgPath() );
            imgPhoto.setImageBitmap(bitmap);
        }

    }

    private void saveComment() {
        //Toast.makeText(this,edtText.getText().toString()+", ",Toast.LENGTH_SHORT).show();
        FCBean fcBean = new FCBean();
        fcBean.setComment(mEdtWrite.getText().toString());

        // 날짜
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        fcBean.setRegDate(timeStamp);

        Gson gson = new Gson();

        String jsonStr = Util.openFile(this, SaveBean.class.getName());
        SaveBean saveBean = gson.fromJson(jsonStr, SaveBean.class);

        FoundBean foundBean = saveBean.getFoundBeanList().get( mSelFoundBean.getSelIdx() );
        foundBean.getFcBeanList().add( fcBean );

        //저장
        String jsonStr2 = gson.toJson(saveBean);
        Util.saveFile(PostViewActivity.this, SaveBean.class.getName(), jsonStr2);

        // Adapter를  생성한다. (Adapter에 리스트 데이터를 넘겨준다.)
        mAdapter = new ChatAdapter(PostViewActivity.this, mSelFoundBean.getSelIdx(), foundBean.getFcBeanList());

        // 최종적으로 Adapter를 ListView에 세팅한다.
        mLstChat.setAdapter(mAdapter);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mEdtWrite.setText("");
                mEdtWrite.requestFocus();
            }
        }, 400);
    }

    private OnMapReadyCallback mapReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {

            googleMap.getUiSettings().setZoomControlsEnabled(true);

            if(mSelFoundBean.getLatitude() != 0){

                float hue = 270;
                MarkerOptions markerOptions = new MarkerOptions();
                mCurPosLatLng = new LatLng(mSelFoundBean.getLatitude(), mSelFoundBean.getLongitude());
                markerOptions.position(mCurPosLatLng).icon(BitmapDescriptorFactory.defaultMarker(hue));
                googleMap.addMarker(markerOptions);

                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mCurPosLatLng, 17));
            }

        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        // Adapter를  생성한다. (Adapter에 리스트 데이터를 넘겨준다.)
        mAdapter =  new ChatAdapter(PostViewActivity.this, mSelFoundBean.getSelIdx(), mSelFoundBean.getFcBeanList());
        
        // 최종적으로 Adapter를 ListView에 세팅한다.
        mLstChat.setAdapter(mAdapter);


        mLstChat.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mScrollView.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });


    }
}
