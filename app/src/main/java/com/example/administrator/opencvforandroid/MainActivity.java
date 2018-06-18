package com.example.administrator.opencvforandroid;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private final String TAG = getClass().getName();
    private Button bt1 = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        opencvLibLoader();
        bt1 = findViewById(R.id.covGray);
        bt1.setOnClickListener(this);

    }

    private void opencvLibLoader(){
        boolean load = OpenCVLoader.initDebug();
        if(load){
            Log.i(TAG,"Opencv CV Libraries loaded...");
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.covGray: convertToGery();
                break;
        }
    }

    private void convertToGery() {
        Mat src = new Mat();
        Mat temp = new Mat();
        Mat dst = new Mat();

        Bitmap image = BitmapFactory.decodeResource(this.getResources(),R.mipmap.ic_launcher);
        Utils.bitmapToMat(image,src);
        Imgproc.cvtColor(src,temp,Imgproc.COLOR_RGBA2BGR);//bitmap为四通道的RGBA mat为三通道
        Log.i(TAG,"image type :" + (temp.type() == CvType.CV_8UC3));
        Imgproc.cvtColor(temp,dst,Imgproc.COLOR_RGB2GRAY);
        Utils.matToBitmap(dst,image);
        ImageView imageView = findViewById(R.id.image);
        imageView.setImageBitmap(image);

        src.release();
        temp.release();
        dst.release();
    }
}
