package com.example.sample_drawcircle;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.sample_drawcircle.view.CircleView;

public class MainActivity extends AppCompatActivity {
    private CircleView cv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cv = (CircleView) findViewById(R.id.cv_test);
    }
}
