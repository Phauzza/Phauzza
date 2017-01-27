package com.pahuza.pahuza.views;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;

import com.pahuza.pahuza.R;

public class ResultActivity extends AppCompatActivity {

    private ImageView locationImg;
    private TextView descTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        locationImg = (ImageView) findViewById(R.id.location_Img);
        descTxt = (TextView) findViewById(R.id.description_txt);

        String byteArray = getIntent().getStringExtra("BitmapImage");
        String desc = getIntent().getStringExtra("Description");

//        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        byte[] decodedString = Base64.decode(byteArray, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        locationImg.setImageBitmap(decodedByte);
        descTxt.setText(desc);
    }
}
