package com.example.administrator.opencvforandroid;

import android.Manifest;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.Toast;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;

import Permision.PermissionHelper;
import Permision.PermissionInterface;

import static org.opencv.imgproc.Imgproc.resize;

public class CamreaViewActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2,View.OnClickListener {
    private JavaCameraView mCamreaView;
    private PermissionHelper mPermissionHelper;
    private RadioButton rbPreCamrea = null;
    private RadioButton rbBackCamrea = null;
    private int camreaIndex = 0;
    private int screemHeight =0;
    private int screemWeight = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camrea_view);
        initView();
        initData();

//        mPermissionHelper = new PermissionHelper(this, this);
//        mPermissionHelper.requestPermissions();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mCamreaView!=null)
        {
            mCamreaView.disableView();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mCamreaView!=null)
        {
            mCamreaView.disableView();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mCamreaView!=null)
        {
            mCamreaView.enableView();
        }
    }

    private void initView(){
        rbPreCamrea = findViewById(R.id.preCamrea);
        rbBackCamrea = findViewById(R.id.backCamrea);
        rbPreCamrea.setOnClickListener(this);
        rbBackCamrea.setOnClickListener(this);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        screemHeight= displayMetrics.heightPixels;
        screemWeight = displayMetrics.widthPixels;
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Mat mat = inputFrame.rgba();
        //判断横竖屏
        if(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT == this.getResources().getConfiguration().orientation){
            Core.rotate(mat,mat,Core.ROTATE_90_CLOCKWISE);
        }
        resize(mat,mat,new Size(screemWeight,screemHeight));
        return mat;
    }

    //    @Override
//    public int getPermissionsRequestCode() {
//        return 10000;
//    }
//
//    @Override
//    public String[] getPermissions() {
//        return new String[]{
//                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                Manifest.permission.READ_PHONE_STATE,
//                Manifest.permission.CAMERA
//        };
//    }
//
//    @Override
//    public void requestPermissionsSuccess() {
//            initViewAndData();
//    }
    private void initData() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permission();//动态权限认证
        }else {
            initViewAndData();
        }
    }

    private void permission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            //没有授权
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        }else {
            initViewAndData();
        }
    }

    private void initViewAndData() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        mPermissionHelper = new PermissionHelper(this, this);
//        mPermissionHelper.requestPermissions();
        mCamreaView = findViewById(R.id.camrea_view);
        mCamreaView.setOnClickListener(this);
        mCamreaView.setVisibility(SurfaceView.VISIBLE);
        mCamreaView.setCameraIndex(0);
        mCamreaView.enableView();
    }

//    @Override
//    public void requestPermissionsFail() {
//        finish();
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if(mPermissionHelper.requestPermissionsResult(requestCode, permissions, grantResults)){
//            //权限请求结果，并已经处理了该回调
//            return;
//        }
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    //这里已经获取到了摄像头的权限，想干嘛干嘛了可以
                    initViewAndData();
                }else {
                    //这里是拒绝给APP摄像头权限，给个提示什么的说明一下都可以。
                    Toast.makeText(this,"请手动打开相机权限",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.preCamrea:
                camreaIndex = 1;
                break;
            case R.id.backCamrea:
                camreaIndex = 0;
                break;
        }
        mCamreaView.setCameraIndex(camreaIndex);
        if(mCamreaView!=null)
        {
            mCamreaView.disableView();
            mCamreaView.enableView();
        }

    }
}
