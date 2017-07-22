package com.example.admin.visualgseg;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifyImagesOptions;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassification;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    VisualClassification pushToWatson (String filePath){                                            //filePath would store image location (eg:-"src/test/resources/visual_recognition/car.png")
        VisualRecognition service = new VisualRecognition(VisualRecognition.VERSION_DATE_2016_05_20);
        service.setApiKey("145b047be11f5059687578f4ca85325d23e0cdf8");

        ClassifyImagesOptions options = new ClassifyImagesOptions.Builder()
                .images(new File(filePath))                                                         //passes filePath to analyze
                .build();
        VisualClassification result = service.classify(options).execute();
        return result;
    }
}
