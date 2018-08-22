package com.king.firebaseimages;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailsActivity extends AppCompatActivity {
    ImageView mImgDetail;
    TextView mTvDetail;
    String ImgUrl,Txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        mImgDetail = findViewById(R.id.imgDetail);
        mTvDetail = findViewById(R.id.tvDetail);
        ImgUrl = getIntent().getStringExtra("url");
        Txt = getIntent().getStringExtra("txt");
        Picasso.with(this).load(ImgUrl).into(mImgDetail);
        mTvDetail.setText(Txt);
    }
}
