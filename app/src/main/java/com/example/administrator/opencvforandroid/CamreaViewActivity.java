package com.example.administrator.opencvforandroid;

import android.Manifest;
import android.content.Context;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.Toast;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import Permision.PermissionHelper;
import Permision.PermissionInterface;

import static org.opencv.imgproc.Imgproc.resize;

public class CamreaViewActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2, View.OnClickListener {
    private JavaCameraView mCamreaView;
    private PermissionHelper mPermissionHelper;
    private RadioButton rbPreCamrea = null;
    private RadioButton rbBackCamrea = null;
    private int camreaIndex = 0;
    private int screemHeight = 0;
    private int screemWeight = 0;
    private int option = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.camrea_view);
        initView();
        initData();
        try {
            initFaceDetectorData();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        mPermissionHelper = new PermissionHelper(this, this);
//        mPermissionHelper.requestPermissions();
    }

    private void initFaceDetectorData() throws IOException {
//        System.loadLibrary("haar_detect-lib");
        InputStream in = getResources().openRawResource(R.raw.lbpcascade_frontalface_improved);
        File cascadeDir = this.getDir("cascade", Context.MODE_PRIVATE);
        File file = new File(cascadeDir.getAbsolutePath() , "haarcascade_frontalface_alt_tree.xml");
        FileOutputStream out = new FileOutputStream(file);
        byte[] buf = new byte[1024];
        int len = 0;
        while ((len = in.read(buf)) != -1) {
            out.write(buf, 0, len);
        }
        out.close();
        in.close();
        initLoad(file.getAbsolutePath());
        file.delete();
        cascadeDir.delete();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCamreaView != null) {
            mCamreaView.disableView();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCamreaView != null) {
            mCamreaView.disableView();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCamreaView != null) {
            mCamreaView.enableView();
        }
    }


    @Override
    public MenuInflater getMenuInflater() {
        return super.getMenuInflater();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_camrea_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.origin:
                option = 0;
                break;
            case R.id.invert:
                option = 1;
                break;
            case R.id.edge:
                option = 2;
                break;
            case R.id.sobel:
                option = 3;
                break;
            case R.id.boxblue:
                option = 4;
                break;
            case R.id.face_detect:
                option = 5;
                break;
            default: {
                option = 0;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        rbPreCamrea = findViewById(R.id.preCamrea);
        rbBackCamrea = findViewById(R.id.backCamrea);
        rbPreCamrea.setOnClickListener(this);
        rbBackCamrea.setOnClickListener(this);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        screemHeight = displayMetrics.heightPixels;
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
        if(camreaIndex == 1){
            Core.flip(mat,mat,1);
        }
        //判断横竖屏
//        if(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT == this.getResources().getConfiguration().orientation){
//            Core.rotate(mat,mat,Core.ROTATE_90_CLOCKWISE);
//        }
//        resize(mat,mat,new Size(screemWeight,screemHeight));
        process(option, mat);
        return mat;
    }

    private void process(int option, Mat mat) {
        switch (option) {
            case 0:
                break;
            case 1:
                Core.bitwise_not(mat, mat);
                //反转
                break;
            case 2:
                //边沿
//                Toast.makeText(CamreaViewActivity.this,"边沿",Toast.LENGTH_SHORT).show();
                Mat edges = new Mat();
                Imgproc.Canny(mat, edges, 100, 200, 3, false);
                Mat result = Mat.zeros(mat.size(), mat.type());
                mat.copyTo(result, edges);
                result.copyTo(mat);

                edges.release();
                result.release();
                break;
            case 3:
                //梯度
//                Toast.makeText(CamreaViewActivity.this,"梯度",Toast.LENGTH_SHORT).show();
                Mat gradx = new Mat();
                Imgproc.Sobel(mat, gradx, CvType.CV_32F, 1, 0);
                Core.convertScaleAbs(gradx, gradx);
                gradx.copyTo(mat);
                gradx.release();
                break;
            case 4:
                //模糊
//                Toast.makeText(CamreaViewActivity.this,"模糊",Toast.LENGTH_SHORT).show();
                Mat temp = new Mat();
                Imgproc.blur(mat, temp, new Size(15, 15));
                temp.copyTo(mat);
                temp.release();
                break;
            case 5:
                faceDetect(mat.getNativeObjAddr());
        }
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
        } else {
            initViewAndData();
        }
    }

    private void permission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            //没有授权
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        } else {
            initViewAndData();
        }
    }

    private void initViewAndData() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        mPermissionHelper = new PermissionHelper(this, this);
//        mPermissionHelper.requestPermissions();
        mCamreaView = findViewById(R.id.camrea_view);
//        mCamreaView.setOnClickListener(this);
        mCamreaView.setCvCameraViewListener(this);
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
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //这里已经获取到了摄像头的权限，想干嘛干嘛了可以
                    initViewAndData();
                } else {
                    //这里是拒绝给APP摄像头权限，给个提示什么的说明一下都可以。
                    Toast.makeText(this, "请手动打开相机权限", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.preCamrea:
                camreaIndex = 1;
                break;
            case R.id.backCamrea:
                camreaIndex = 0;
                break;
        }
        mCamreaView.setCameraIndex(camreaIndex);
        if (mCamreaView != null) {
            mCamreaView.disableView();
            mCamreaView.enableView();
        }
    }
    public native void initLoad(String haarFilePath);
    public native void faceDetect(long address);
}
