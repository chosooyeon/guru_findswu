package com.example.user.finalproject.bean;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

/**
 * Created by user on 2018-07-27.
 */

public class Util {
    // 내부앱에 파일로 저장
    public static void saveFile(Context context, String fileName, String contents){
        try {
            FileOutputStream outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            outputStream.write(contents.getBytes());
            outputStream.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    // 내부앱에서 파일을 읽어 들인다.
    public static String openFile(Context context, String fileName){
        // 파일에서 읽은 데이터를 저장하기 위해서 만든 변수
        StringBuffer data = new StringBuffer();
        try {
            FileInputStream fis = context.openFileInput(fileName); // 파일명
            BufferedReader buffer = new BufferedReader(new InputStreamReader(fis));
            String line;
            while ((line = buffer.readLine()) != null){
                data.append(line);
            }
            buffer.close();
        } catch (Exception e){
            e.printStackTrace();
        }
        return data.toString();
    }
}
