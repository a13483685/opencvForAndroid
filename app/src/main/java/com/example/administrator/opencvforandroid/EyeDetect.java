package com.example.administrator.opencvforandroid;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.WindowManager;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.core.Core;
import org.opencv.core.Mat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class EyeDetect extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2{
    private int option = 0;
    private JavaCameraView javaCameraView = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eye_detect);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        javaCameraView = findViewById(R.id.eye_cameraView_id);
        javaCameraView.setVisibility(SurfaceView.VISIBLE);
        javaCameraView.setCvCameraViewListener(this);
        javaCameraView.setCameraIndex(1);
        javaCameraView.enableFpsMeter();
        javaCameraView.enableView();
        try {
            initNativeDetector();
            initNativeDetector();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void initNativeDetector() throws IOException {
        InputStream inputStream = getResources().openRawResource(R.raw.lbpcascade_frontalcatface);
        File cascade = this.getDir("cascade", Context.MODE_PRIVATE);
        File file = new File(cascade.getAbsolutePath(),"lbpcascade_frontalface.xml");
        FileOutputStream outputStream = new FileOutputStream(file);
        byte[] buf = new byte[1024];
        int len = 0;
        while ((len = inputStream.read(buf))!=-1){
            outputStream.write(buf,0,len);
        }
        inputStream.close();
        outputStream.close();
        cascade.delete();
    }
    private void initEyeDetector() throws IOException {
        InputStream inputStream = getResources().openRawResource(R.raw.haarcascade_eye_tree_eyeglasses);
        File cascade = this.getDir("cascade",Context.MODE_PRIVATE);
        File file = new File(cascade.getAbsolutePath(),"haarcascade_eye_tree_eyeglasses.xml");
        FileOutputStream outputStream = new FileOutputStream(file);
        byte[] buf = new byte[1024];
        int len;
        while ((len=inputStream.read(buf))!=-1){
            outputStream.write(buf);
        }
        inputStream.close();
        outputStream.close();
        cascade.delete();
    }


    @Override
    protected void onPause() {
        if(javaCameraView!=null){
            javaCameraView.disableView();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if(javaCameraView!=null){
            javaCameraView.disableView();
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.eye_track_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.facedetector:
                option = 1;
                break;
            case R.id.eyearea:
                option = 2;
                break;
            case R.id.eye_ball:
                option = 3;
                break;
            case R.id.eye_render:
                option = 4;
                break;
            case R.id.preview:
                option = 0;
                break;
            default:
                option = 0;
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Mat frame = inputFrame.rgba();
        Core.flip(frame,frame,1);
        if(this.getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
            Core.rotate(frame,frame,Core.ROTATE_90_CLOCKWISE);
        }
        process(frame);
        return frame;
    }

    private void process(Mat frame) {
        if(option<1){
            return;
        }

    }
}
