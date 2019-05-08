package com.example.demo;

import android.app.Application;
import android.content.Intent;
import android.graphics.Canvas;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Range;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.List;

public class ActivityGetPixel extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {
    //Declare item
    JavaCameraView javaCameraView;
    ImageButton ibtnPlus, ibtnMinus;
    //Declare var
    Mat mRGBCam;
    int x = -1, y = -1, min = 50;
    public static final String TAG = "ActivityGetPixel";
    BaseLoaderCallback baseLoaderCallback = new BaseLoaderCallback(this) {
        /**
         * Function : Quản lí kết nối với Opencv
         * @param status
         */
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case BaseLoaderCallback.SUCCESS: {
                    javaCameraView.enableView();//Cho phép camera hoạt động
                    break;
                }
                default: {
                    super.onManagerConnected(status);
                    break;
                }
            }
        }
    };
    /**
     * Function onCreate
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_pixel);
        init();
        javaCameraView.setVisibility(SurfaceView.VISIBLE);
        javaCameraView.setCvCameraViewListener(this);
        javaCameraView.enableFpsMeter();
        btnClick();
    }
    /**
     * Function : Khởi tạo
     */
    public void init() {
        javaCameraView = findViewById(R.id.Camera);
        ibtnPlus = findViewById(R.id.ibtnPlus);
        ibtnMinus = findViewById(R.id.ibtnMinus);
    }
    /**
     * Function : Tăng giảm giá trị của min
     */
    public void btnClick() {
        ibtnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (min > 50) {
                    min = min - 25;
                } else if (min <= 50) {
                    min = 50;
                } else {
                    min = 50;
                }
            }
        });
        ibtnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (min < 200) {
                    min = min + 25;
                } else if (min >= 200) {
                    min = 200;
                } else {
                    min = 50;
                }
            }
        });
    }
    /**
     * Function : Trạng thái Pause
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (javaCameraView != null) { // Nếu camera khác rỗng
            javaCameraView.disableView();//Tắt camera
        }
    }
    /**
     * Function : Trạng thái Destroy
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (javaCameraView != null) {//Nếu camera khác rỗng
            javaCameraView.disableView(); // Tắt camera
        }
    }
    /**
     * Function : Trạng thái Resume
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (OpenCVLoader.initDebug()) {// Khởi tạo thành công
            baseLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS); //Kết nối và trả về SUCCESS
        } else { // Khởi tạo thất bại
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, baseLoaderCallback); // Khởi tạo đồng bộ OpenCV 3_4_0
        }
    }
    /**
     * Function : Khởi tạo camera
     * @param width -  the width of the frames that will be delivered
     * @param height - the height of the frames that will be delivered
     */
    @Override
    public void onCameraViewStarted(int width, int height) {
        mRGBCam = new Mat(height, width, CvType.CV_8UC4);
    }
    /**
     * Function : Dừng camera
     */
    @Override
    public void onCameraViewStopped() {
        mRGBCam.release(); // hủy mRGBCam
    }
    /**
     * Function : Khung camera
     * @param inputFrame
     * @return
     */
    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRGBCam = inputFrame.rgba();
        try {
            if (x == -1 && y == -1) {
                Imgproc.putText(mRGBCam, "Click for process", new Point(10, 90), Core.FONT_HERSHEY_COMPLEX, 1.2, new Scalar(5, 255, 255), 2, 8, false);
            }
            Mat mROI;
            int rows = mRGBCam.rows();//y
            int cols = mRGBCam.cols();//x
            Rect ROI = new Rect(x, y, min, min);
            mROI = mRGBCam.submat(ROI);
            if (x != -1 && y != -1) {
                processMatROI(mROI);
                Imgproc.rectangle(mRGBCam, new Point(x, y), new Point(x + min, y + min), new Scalar(5, 255, 255), 3, 3);
            }
            Imgproc.putText(mRGBCam, "Rows :" + rows + "    " + "Cols :" + cols, new Point(10, 52), Core.FONT_HERSHEY_COMPLEX, 1.2, new Scalar(5, 255, 255), 2, 8, false);
        } catch (Exception e) {
            e.getMessage();
        }
        return mRGBCam;
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        x = (int) event.getX() - 220;
        y = (int) event.getY() - 220;
        return super.onTouchEvent(event);
    }
    public void processMatROI(Mat m) {
        double R = 0, G = 0, B = 0;
        double[] data = new double[3];
        for (int i = 0; i < m.rows(); i++) {
            for (int j = 0; j < m.cols(); j++) {
                data = m.get(i, j);
                R = R + data[0];
                G = G + data[1];
                B = B + data[2];
            }
        }
        double size = m.rows() * m.cols();
        R = R / size;
        G = G / size;
        B = B / size;
        Imgproc.putText(mRGBCam, "Red : " + (int) R + " Green : " + (int) G + " Blue : " + (int) B, new Point(10, 90), Core.FONT_HERSHEY_COMPLEX, 1.2, new Scalar(5, 255, 255), 2, 8, false);
    }
}



