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
        File image = (File)getIntent().getExtras().get("PHOTO");
        resImgView.setImageURI(Uri.fromFile(image));
        try {
            resTextView.setText(JSONParse());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String JSONParse() throws JSONException {
        Intent intent = getIntent();
        String result = intent.getStringExtra("MainActivity.JSON");
        JSONObject watson = null;
        String wasteType=null;
        int highestScore=0;
        try {
            watson = new JSONObject(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONArray classifiers = watson.getJSONArray("classifiers");
        for (int i=0; i < classifiers.length(); i+=1){
            JSONObject classifier = classifiers.getJSONObject(i);
            String classifier_id = classifier.getString("classifier_id");
            if (classifier_id==" "){                                                                //Add classifier_id here
                JSONArray classes = classifier.getJSONArray("classes");
                for (int j=0; j < classifiers.length(); j+=1){
                    JSONObject Class = classes.getJSONObject(j);
                    String class_name = Class.getString("class");
                    int score = Class.getInt("score");
                    if (j==0){
                        highestScore=score;
                        wasteType=class_name;
                    }
                    if (score>highestScore){
                        highestScore=score;
                        wasteType=class_name;
                    }
                }
            }

        }
        return wasteType;
    }


}