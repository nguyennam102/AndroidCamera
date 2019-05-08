package com.example.demo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.provider.MediaStore;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    //khai báo
    private Button btnGetPixel,btnGetImgPixel;
    /**
     * Function onCreate
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        onClickBtnGetPixel();
        onClickBtnGetImgPixel();
    }

    /**
     * Function : Button Get Img
     */
    private void onClickBtnGetImgPixel() {
        btnGetImgPixel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),ActivityImgPixel.class);// Tạo Intent đến ActivityImgPixel
                MainActivity.this.startActivity(intent);// Start
            }
        });

    }

    /**
     * Function : Button Get Pixel
     */
    public void onClickBtnGetPixel(){
        btnGetPixel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),ActivityGetPixel.class); //Tạo Intent đến ActivityGetPixel
                MainActivity.this.startActivity(intent);// Start
            }
        });
    }


    /**
     * Function : ánh xạ
     */
    public void init(){
        btnGetPixel = findViewById(R.id.btnGetPixel);
        btnGetImgPixel = findViewById(R.id.btnGetImgPixel);
    }

}
