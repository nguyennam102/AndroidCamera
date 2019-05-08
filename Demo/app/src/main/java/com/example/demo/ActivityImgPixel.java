package com.example.demo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;

public class ActivityImgPixel extends AppCompatActivity{
    ImageView imgProject;
    ImageButton ibtnCamera, btnChonhinh;
    TextView tvResult;
    Uri imgUri;
    Bitmap imgBitmap;
    BaseLoaderCallback baseLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i("OpenCV", "OpenCV loaded successfully");
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_img_pixel);
        init();
        btnCameraOnClick();
        btnThumucOnClick();
    }

    /**
     * function : hàm lấy thư mục
     */
    private void btnThumucOnClick() {
        btnChonhinh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                thumuc();
            }
        });
    }
    private void thumuc() {

            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 111);
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (OpenCVLoader.initDebug()) {
            Log.i("OpenCV", "OpenCV was load successfull!");
            baseLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
            Log.i("Tiến Trình", "Ở đây là onResume");
        } else {
            Log.i("OpenCV", "OpenCV was load failed!");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, baseLoaderCallback);
        }
    }

    /**
     * function : mở camera
     */
    public void btnCameraOnClick(){
        ibtnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenCamera();
            }
        });
    }

    /**
     * function : ánh xạ
     */
    public void init(){
        btnChonhinh = findViewById(R.id.btnChonhinh);
        imgProject = findViewById(R.id.imgProject);
        ibtnCamera = findViewById(R.id.ibtnCamera);
        tvResult = findViewById(R.id.tvResult);
    }
    public void OpenCamera(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,986);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**
         * trả ảnh từ camera
         */
        if(requestCode == 986 && resultCode == RESULT_OK && data != null){
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            Mat m = new Mat();
            Bitmap bmp32 = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            Utils.bitmapToMat(bmp32,m);
            double[] get = new double[3];
            double R = 0, G = 0, B = 0;
            for(int i = 0; i < m.rows(); i++){
                for(int j = 0; j < m.cols();j++){
                    get = m.get(i,j);
                    R += get[0];
                    G += get[1];
                    B += get[2];
                }
            }
            double size = m.rows()*m.cols();
            R = R/size;
            G = G/size;
            B = B/size;
            tvResult.setText("Red : "+(int)R+" Green :"+(int)G +" Blue : "+(int)B);
            imgProject.setImageBitmap(bitmap);
            
        }
        /**
         * trả ảnh từ thư mục
         */
        if(requestCode == 111 && resultCode == RESULT_OK && data != null){
            imgUri = data.getData();
            try {
                imgBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imgUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Mat n = new Mat();
            Bitmap bmp32 = imgBitmap.copy(Bitmap.Config.ARGB_8888, true);
            Utils.bitmapToMat(imgBitmap, n);
            double[] get = new double[3];
            double R = 0, G = 0, B = 0;
            for (int i = 0; i < n.rows(); i++) {
                for (int j = 0; j < n.cols(); j++) {
                    get = n.get(i, j);
                    R += get[0];
                    G += get[1];
                    B += get[2];
                }
            }
            double size = n.rows() * n.cols();
            R = R / size;
            G = G / size;
            B = B / size;
            tvResult.setText(" Red : " + (int) R + "\n Green :" + (int) G + "\n Blue : " + (int) B);
            imgProject.setImageBitmap(imgBitmap);
        }
    }
}
