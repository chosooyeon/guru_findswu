package com.example.user.finalproject;

import android.Manifest;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.user.finalproject.bean.LostBean;
import com.example.user.finalproject.bean.SaveBean;
import com.example.user.finalproject.bean.Util;
import com.example.user.finalproject.tab.TabMainActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LostUpdateActivity extends AppCompatActivity {

    private EditText mEdtTitle;
    private EditText mEdtContent;

    private ImageView mImgCamera;
    private LostBean mLostBean;

    private Uri mCaptureUri;
    private String mPhotoPath;
    public static final int REQUEST_IMAGE_CAPTURE = 200;


    private Marker currentPositionMarker = null;
    private LatLng mCurPosLatLng;
    private MapFragment mMapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_update);

        mLostBean = (LostBean)getIntent().getSerializableExtra(LostBean.class.getName());

        mEdtTitle = findViewById(R.id.edtTitle);
        mEdtContent = findViewById(R.id.edtContent);
        mImgCamera = findViewById(R.id.imgCamera);

        mEdtTitle.setText(mLostBean.getTitle());
        mEdtContent.setText(mLostBean.getContent());

        if(mLostBean != null && mLostBean.getImgPath() != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(mLostBean.getImgPath());
            mImgCamera.setImageBitmap(bitmap);
        }

        //카메라를 사용하기 위한 퍼미션을 요청한다.
        ActivityCompat.requestPermissions(this, new String[] {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        }, 0);

        //카메라 버튼
        findViewById(R.id.btnPhoto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });

        Button btnUpdate = findViewById(R.id.btnUpdate);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    mLostBean.setTitle(mEdtTitle.getText().toString());
                    mLostBean.setContent(mEdtContent.getText().toString());
                    if(mPhotoPath != null) {
                        mLostBean.setImgPath(mPhotoPath);
                    }
                    if(currentPositionMarker != null) {
                        mLostBean.setLatitude(currentPositionMarker.getPosition().latitude);
                        mLostBean.setLongitude(currentPositionMarker.getPosition().longitude);
                    }

                    Gson gson = new Gson();
                    String jsonStr = Util.openFile(LostUpdateActivity.this, SaveBean.class.getName());
                    SaveBean saveBean = gson.fromJson(jsonStr, SaveBean.class);

                    // 저장
                    saveBean.getLostBeanList().set( mLostBean.getSelIdx(), mLostBean );

                    String jsonStr2 = gson.toJson(saveBean);
                    Util.saveFile(LostUpdateActivity.this, SaveBean.class.getName(), jsonStr2);

                    Intent i = new Intent(LostUpdateActivity.this, TabMainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(i);

            }
        });

        mMapFragment = ((MapFragment) getFragmentManager().findFragmentById(R.id.map));
        mMapFragment.getMapAsync(onMapReadyCallback);

    }

    private OnMapReadyCallback onMapReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(final GoogleMap googleMap) {
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            if(mLostBean.getLatitude() != 0) {
                MarkerOptions markerOptions = new MarkerOptions();
                mCurPosLatLng = new LatLng(mLostBean.getLatitude(), mLostBean.getLongitude());
                markerOptions.position(mCurPosLatLng);
                googleMap.addMarker(markerOptions);

                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {


                        googleMap.clear();
                        currentPositionMarker = googleMap.addMarker(new MarkerOptions()
                                .position(latLng).title("내가 클릭한 곳").snippet("여기"));

                    }
                });

            }

            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.6281894, 127.0897268), 18));
        }
    };


    private void takePicture() {
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            mCaptureUri = Uri.fromFile( getOutputMediaFile() );
        } else {
            mCaptureUri = FileProvider.getUriForFile(this,
                    "com.example.user.finalproject.fileprovider", getOutputMediaFile());
        }

        i.putExtra(MediaStore.EXTRA_OUTPUT, mCaptureUri);

        //Uri uri = FileProvider.getUriForFile(this, "", getOutputMediaFile());
        i.putExtra(MediaStore.EXTRA_OUTPUT, mCaptureUri);

        //내가 원하는 액티비티로 이동하고, 그 액티비티가 종료되면(finish)되면 다시 나의 액티비티의 onActivityResult() 메서드가 호출되는 구조이다.
        //내가 어떤 데이터를 받고 싶을때 상대 액티비티를 호출해주고 그 액티비티에서 호출한 나의 액티비티로 데이터를 넘겨주는 구조이다. 이때 호출되는 메서드가 onActivityResult()메서드이다.
        startActivityForResult(i, REQUEST_IMAGE_CAPTURE);
    }

    private File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "cameraDemo");
        if(!mediaStorageDir.exists()) {
            if(!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File file = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");

        mPhotoPath = file.getAbsolutePath();

        return file;
    }

    private void sendPicture() {
        Bitmap bitmap = BitmapFactory.decodeFile(mPhotoPath);
        Bitmap resizedBmp = getResizedBitmap(bitmap, 4, 100, 100);

        //사진이 캡쳐되서 들어오면 뒤집어져 있다. 이애를 다시 원상복구 시킨다.
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(mPhotoPath);
        }catch (Exception e) {
            e.printStackTrace();
        }
        int exifOrientation;
        int exifDegree;
        if(exif != null) {
            exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            exifDegree = exifOrienToDegree(exifOrientation);
        }else{
            exifDegree = 0;
        }
        Bitmap roatedBmp = roate(bitmap, exifDegree);
        mImgCamera.setImageBitmap(roatedBmp);
    }

    private int exifOrienToDegree(int exifOrientation) {
        if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        }
        else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_180){
            return 180;
        } else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    private Bitmap roate(Bitmap bmp, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
    }

    //비트맵의 사이즈를 줄여준다.
    public static Bitmap getResizedBitmap(Bitmap srcBmp, int size, int width, int height){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = size;
        Bitmap resized = Bitmap.createScaledBitmap(srcBmp, width, height, true);
        return resized;
    }

    public static Bitmap getResizedBitmap(Resources resources, int id, int size, int width, int height){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = size;
        Bitmap src = BitmapFactory.decodeResource(resources, id, options);
        Bitmap resized = Bitmap.createScaledBitmap(src, width, height, true);
        return resized;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //카메라로부터 오는 데이터를 취득한다.
        if(resultCode == RESULT_OK) {

            if(requestCode == REQUEST_IMAGE_CAPTURE) {
                sendPicture();
            }
        }
    }//end onActivityResult()


}
