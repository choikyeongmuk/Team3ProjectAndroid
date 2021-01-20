package com.kosmo.veve;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MyPage_Edit_Profile extends AppCompatActivity {

    private ImageView btn_back;
    private ImageView btnBack;
    private TextView textView8;
    private ImageView imageView3;
    private EditText editText;
    private TextView textView12;
    private ImageView imageView10;
    private ImageView imageView9;
    private ImageView imageView8;
    private ImageView imageView4;
    private ImageView imageView5;
    private ImageView imageView6;
    private ImageView imageView7;
    //private android.widget.view view3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page__edit__profile);


        initView();
    }

    private void initView() {
        btn_back = (ImageView) findViewById(R.id.btn_back);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btnBack = (ImageView) findViewById(R.id.btn_back);
        textView8 = (TextView) findViewById(R.id.textView8);
        imageView3 = (ImageView) findViewById(R.id.imageView3);
        editText = (EditText) findViewById(R.id.editText);
        textView12 = (TextView) findViewById(R.id.textView12);
        imageView10 = (ImageView) findViewById(R.id.imageView10);
        imageView9 = (ImageView) findViewById(R.id.imageView9);
        imageView8 = (ImageView) findViewById(R.id.imageView8);
        imageView4 = (ImageView) findViewById(R.id.imageView4);
        imageView5 = (ImageView) findViewById(R.id.imageView5);
        imageView6 = (ImageView) findViewById(R.id.imageView6);
        imageView7 = (ImageView) findViewById(R.id.imageView7);
        //view3 = (View)findViewById(R.id.view3);
    }
}