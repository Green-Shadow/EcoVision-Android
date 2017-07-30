package com.example.admin.visualgseg;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import java.io.File;
import android.widget.ImageView;
import android.widget.TextView;
import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ResultActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        ImageView resImgView = (ImageView)findViewById(R.id.res_imgview);
        TextView resTextView = (TextView)findViewById(R.id.res_textview);
        Intent intent = getIntent();
        String result = "no data";
        result = intent.getStringExtra("MainActivity.WASTE_TYPE");
        String imagePath = intent.getStringExtra("MainActivity.PHOTO");
        resImgView.setImageURI(Uri.fromFile(new File(imagePath)));
        resTextView.setText(result);
    }
        
    }
