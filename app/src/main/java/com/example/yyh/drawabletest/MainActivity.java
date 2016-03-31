package com.example.yyh.drawabletest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private DrawableView mQiQiu;
    private DrawableView mMeiNv;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mQiQiu = (DrawableView) findViewById(R.id.id_qiqiu);
        mMeiNv = (DrawableView) findViewById(R.id.id_meinv);
        mQiQiu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mQiQiu.setType(DrawableView.TYPE_ROUND);
            }
        });
        mMeiNv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMeiNv.setmBorderRadius(90);
            }
        });

    }
}
