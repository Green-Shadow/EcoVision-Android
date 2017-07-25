package com.example.admin.visualgseg;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import java.io.File;
import android.widget.ImageView;
import android.widget.TextView;
import android.net.Uri;

public class ResultActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        ImageView resImgView = (ImageView)findViewById(R.id.res_imgview);
        TextView resTextView = (TextView)findViewById(R.id.res_textview);
        Intent intent = getIntent();
        String result = intent.getStringExtra("MainActivity.JSON");
        File image = (File)getIntent().getExtras().get("PHOTO");
        resImgView.setImageURI(Uri.fromFile(image));
        resTextView.setText(result);
 }
 }