package com.example.admin.visualgseg;

import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.app.AlertDialog.Builder;
import android.widget.TextView;

import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifyImagesOptions;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassification;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public String pushToWatson (File photoFile){
        VisualRecognition service = new VisualRecognition(VisualRecognition.VERSION_DATE_2016_05_20);
        service.setApiKey("145b047be11f5059687578f4ca85325d23e0cdf8");

        ClassifyImagesOptions options = null;
            options = new ClassifyImagesOptions.Builder()
                    .images(photoFile)//calls takePhoto() to launch camera app
                    .build();
        VisualClassification result = service.classify(options).execute();
        return result.toString();
    }
    private File takePhoto() throws IOException{ //Consolidated both image handling methods for code hygeine
    String timeStamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ").format(new Date());
    String imageFileName = "JPEG_" + timeStamp + "_";
    File image = File.createTempFile(imageFileName,".jpg",getExternalFilesDir(Environment.DIRECTORY_PICTURES));
    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
        if (image != null) {
            Uri photoURI = Uri.fromFile(image);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePictureIntent, 1);
            return image;
        }
    }
    return null;
}
    File image = null;
    void onClick (View view){
        new action().execute();
        
    }

    
    private class action extends AsyncTask<Void,Void,String>{
        @Override
        protected void onPreExecute() {
            try {
                image  = takePhoto();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            String scores = pushToWatson(image);
            return scores;
        }

        @Override
        protected void onPostExecute(String s) {
            TextView output = (TextView)findViewById(R.id.result);
            output.setText(s);
            try{
                Intent intent = new Intent(MainActivity.this, ResultActivity.class); //Start of code for activity transfer.
                intent.putExtra("PHOTO", image);
                intent.putExtra("JSON",s);
                startActivity(intent);}
            catch(Exception e){e.printStackTrace();}
            
            
        }
    }

}
