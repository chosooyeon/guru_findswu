package com.example.user.finalproject.adapter;

/**
 * Created by user on 2018-07-27.
 * 찾은 분실물
 */

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.finalproject.FoundUpdateActivity;
import com.example.user.finalproject.PostViewActivity;
import com.example.user.finalproject.R;
import com.example.user.finalproject.bean.FoundBean;
import com.example.user.finalproject.bean.SaveBean;
import com.example.user.finalproject.bean.Util;
import com.google.gson.Gson;

import java.util.List;

/**
 * Created by user on 2018-07-27.
 */

public class FoundAdapter extends BaseAdapter {

    private Context mContext;
    private List<FoundBean> mList;
    private LayoutInflater mInflater;

    /** 인텐트에 컨텐츠 텍스트를 실어보내기 위한 키 값**/
    public static  final String KEY_FOUND_CONTENTS = "keyFoundContents";
    /** 인텐트에 ListData 클래스를 통째로 실어보내기 위한 키값**/
    public static final String KEY_FOUND_DATA_CLASS = "keyFoundDataClass";

    Integer selIdx = 0;

    public FoundAdapter(Context context, List<FoundBean> list) {
        mContext = context;
        mList = list;
        mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // 리스트 갯수
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        //고유의 데이터가 넘어감
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // 인플레이터로 뷰 가져옴
        convertView = mInflater.inflate(R.layout.list_view, null);

        final FoundBean bean = mList.get(position);

        bean.setSelIdx(position);

        TextView txtTitle = convertView.findViewById(R.id.txtTitle);
        TextView txtContent = convertView.findViewById(R.id.txtContent);

        txtTitle.setText(bean.getTitle());
        txtContent.setText(bean.getContent());

        ImageView imgPhoto = convertView.findViewById(R.id.imgPhoto);

        if(bean.getImgPath() != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(bean.getImgPath());
            imgPhoto.setImageBitmap(bitmap);
        }

        TextView txtDate = convertView.findViewById(R.id.txtDate);
        txtDate.setText(bean.getRegDate());

        Button btnUpdate = convertView.findViewById(R.id.btnUpdate);
        Button btnDel = convertView.findViewById(R.id.btnDel);

        btnUpdate.setTag(position);
        btnDel.setTag(position);

        // 수정 버튼
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selIdx = (Integer)v.getTag();
                pwdCustomDlg();
            }
        });


        // 삭제 버튼
        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 선택된 삭제 버튼의 메모된 인덱스 번호를 취득
                selIdx = (Integer) v.getTag();

                delCustomDlg();
            }
        });

        // 한 row에 클릭 이벤트 적용
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //FoundBean bean = mList.get(selIdx.intValue());
                //selIdx = (Integer)v.getTag();

                Intent i = new Intent(mContext, PostViewActivity.class);
                i.putExtra(KEY_FOUND_CONTENTS, bean.getContent());
                i.putExtra(KEY_FOUND_DATA_CLASS, bean);

                mContext.startActivity(i);

            }
        });


        return convertView;
    }

    private void pwdCustomDlg() {
        final Dialog dlg = new Dialog(mContext);
        dlg.setContentView(R.layout.view_upd_dlg);
        final EditText edtPwd = dlg.findViewById(R.id.edtPwd);
        Button btnCheckPwd = dlg.findViewById(R.id.btnCheckPwd);
        Button btnCancel = dlg.findViewById(R.id.btnCancel);
        dlg.show();

        btnCheckPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(selIdx != null) {
                    FoundBean bean = mList.get(selIdx.intValue());

                    // 선택된 행의 인덱스 값 보냄
                    bean.setSelIdx(selIdx.intValue());

                    // 가져온 정보로 입력한 id, pass가 일치하는지를 검사한다.
                    if (bean.getEdtPwd().equals(edtPwd.getText().toString())) {
                        Intent i = new Intent(mContext, FoundUpdateActivity.class);
                        i.putExtra(FoundBean.class.getName(), bean);
                        mContext.startActivity(i);
                        dlg.cancel();
                    } else if (!bean.getEdtPwd().equals(edtPwd.getText().toString())) {
                        Toast.makeText(mContext, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.cancel();
            }
        });

    }

    private void delCustomDlg() {
        final Dialog dlg = new Dialog(mContext);
        dlg.setContentView(R.layout.view_del_dlg);
        final EditText edtPwd = dlg.findViewById(R.id.edtPwd);
        Button btnDel = dlg.findViewById(R.id.btnDel);
        Button btnCancel = dlg.findViewById(R.id.btnCancel);
        dlg.show();

        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FoundBean foundBean = new FoundBean();
                Gson gson = new Gson();

                String jsonStr = Util.openFile(mContext, SaveBean.class.getName());
                SaveBean saveBean = gson.fromJson(jsonStr, SaveBean.class);

                if(selIdx != null) {
                    FoundBean bean = mList.get(selIdx.intValue());
                    bean.setSelIdx(selIdx.intValue());

                    if(bean.getEdtPwd().equals(edtPwd.getText().toString())) {
                        // 화면상 삭제
                        mList.remove(selIdx.intValue());

                        saveBean.getFoundBeanList().remove(selIdx.intValue());
                        String jsonStr2 = gson.toJson(saveBean);
                        Util.saveFile(mContext, SaveBean.class.getName(), jsonStr2);

                        FoundAdapter.this.notifyDataSetInvalidated(); // 어댑터 리프레쉬

                        dlg.cancel();
                        Toast.makeText(mContext, "해당 게시물이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                    } else if (!bean.getEdtPwd().equals(edtPwd.getText().toString())) {
                        Toast.makeText(mContext, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.cancel();
            }
        });

    }
}