package com.dylan.circleimageview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CircleImageView circleImageView = (CircleImageView) findViewById(R.id.random_icon);
        circleImageView.drawRandomBackground();

        CircleImageView circleImageView1 = (CircleImageView) findViewById(R.id.random_icon1);
        circleImageView1.drawRandomBackground();
    }
}
