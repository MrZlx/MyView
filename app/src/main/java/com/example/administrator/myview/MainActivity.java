package com.example.administrator.myview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    CircleBarView cicleBarProgress;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        textView = findViewById(R.id.textView);
        cicleBarProgress = findViewById(R.id.cicleBarProgress);
        cicleBarProgress.setProgressNum(100,3000);
        cicleBarProgress.setProgressText(new AnimatorProgressListener() {
            @Override
            public void changeTextListener(float progress) {
                textView.setText("当前进度："+progress);
            }
        });
    }
}
