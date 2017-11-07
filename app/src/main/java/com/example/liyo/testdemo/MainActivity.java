package com.example.liyo.testdemo;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.example.liyo.testdemo.view.CircleView;
import com.example.liyo.testdemo.view.BloodOxygenView;

import java.util.Random;


public class MainActivity extends AppCompatActivity {
    private Context mContext;
    private CircleView mCircleView;
    private BloodOxygenView bloodOxygenView;
    private boolean isAnimFiniShed = true;
    private TextView tvssy;
    private TextView tvszy;
    private String dw = "mmHg";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        mCircleView = (CircleView) findViewById(R.id.cv_test);

//        tvssy = (TextView) findViewById(R.id.tv_ssy);
//        tvszy = (TextView) findViewById(R.id.tv_szy);
////        mCircleView = (CircleView) findViewById(R.id.cv_test);
//        bloodOxygenView = (BloodOxygenView) findViewById(R.id.cv_test);
//        bloodOxygenView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Random r = new Random();
//                int ml = r.nextInt(150);
//                System.out.println(ml);
//                bloodOxygenView.setPulse_rate(ml);
//
//            }
//        });



    }




}
