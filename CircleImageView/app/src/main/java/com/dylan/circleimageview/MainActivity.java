package com.dylan.circleimageview;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CircleImageView circleImageView = (CircleImageView) findViewById(R.id.random_icon);
        circleImageView.drawRandomBackground();

        CircleImageView circleImageView1 = (CircleImageView) findViewById(R.id.random_icon1);
        circleImageView1.drawRandomBackground();

        List<CircleImageViewBean> list2 = new ArrayList<>();
        List<CircleImageViewBean> list3 = new ArrayList<>();
        List<CircleImageViewBean> list4 = new ArrayList<>();
        List<CircleImageViewBean> list5 = new ArrayList<>();
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.test);
        CircleImageViewBean bean1 = new CircleImageViewBean(bitmap, -1, "");
        CircleImageViewBean bean2 = new CircleImageViewBean(null, R.color.colorGreen, "A");
        CircleImageViewBean bean3 = new CircleImageViewBean(bitmap, R.color.colorGreen, "A");
        CircleImageViewBean bean4 = new CircleImageViewBean(null, R.color.colorAccent, "B");
        CircleImageViewBean bean5 = new CircleImageViewBean(bitmap, R.color.colorGreen, "A");

        list2.add(bean1);
        list2.add(bean2);
        CircleImageView circleImageView2 = (CircleImageView) findViewById(R.id.icon_two);
        circleImageView2.setImageBitmaps(list2);

        list3.add(bean3);
        list3.addAll(list2);
        CircleImageView circleImageView3 = (CircleImageView) findViewById(R.id.icon_three);
        circleImageView3.setImageBitmaps(list3);

        list4.add(bean4);
        list4.addAll(list3);
        CircleImageView circleImageView4 = (CircleImageView) findViewById(R.id.icon_four);
        circleImageView4.setImageBitmaps(list4);

        list5.add(bean5);
        list5.addAll(list4);
        CircleImageView circleImageView5 = (CircleImageView) findViewById(R.id.icon_five);
        circleImageView5.setImageBitmaps(list5);
    }
}
