package com.example.admin.visualgseg;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.widget.ArrayAdapter;
import android.widget.ImageView;



import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.app.ProgressDialog;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.content.Context;

import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifyImagesOptions;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassification;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import id.zelory.compressor.Compressor;

public class MainActivity extends AppCompatActivity {
    boolean isConnected;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if (!isConnected){
            Snackbar snackbar = Snackbar
                .make(findViewById(android.R.id.content), "No internet connection", Snackbar.LENGTH_LONG);
 
            snackbar.show();
        }
    }
    String photoPath;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {//Callback for camera
        if (requestCode == 1 && resultCode == RESULT_OK){
            new action().execute();
        }
    }
    private class action extends AsyncTask<Void,Void,String>{
        ProgressDialog pd;
        @Override
        protected void onPreExecute(){
            pd=ProgressDialog.show(MainActivity.this,"","Connecting to Watson...",false); 
        }
        
        @Override
        protected String doInBackground(Void... params) {
            String scores = null;
            try {
                scores = pushToWatson(compress2(new File(photoPath)));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return scores;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                JSONParse(s);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            pd.dismiss();
            ImageView resImgView = (ImageView)findViewById(R.id.res_imgview);
            resImgView.setImageURI(Uri.fromFile(new File(photoPath)));
        }
    }

    private File compress2(File uncon) throws IOException{
        return new Compressor(this).compressToFile(uncon);
    }
    private void JSONParse (String JSON) throws JSONException {
        JSONObject watson=null;
        double highestScore=0.0;
        String wasteType=null;
        try {
            watson = new JSONObject(JSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONArray images = watson.getJSONArray("images");
        JSONObject image_1 = images.getJSONObject(0);
        JSONArray classifiers = image_1.getJSONArray("classifiers");
        JSONObject classifier_wasteType = classifiers.getJSONObject(0);
        JSONArray classes = classifier_wasteType.getJSONArray("classes");
        for (int i = 0; i < classes.length(); i+=1){
            String class_name= classes.getJSONObject(i).getString("class");
            double score= classes.getJSONObject(i).getDouble("score");
            if (i==0){
                highestScore=score;
                wasteType=class_name;
            }
            if (score>highestScore){
                highestScore=score;
                wasteType=class_name;
            }
        }
        TextView res_class_textview = (TextView) findViewById(R.id.res_class_textview);
        TextView res_confidence_textview = (TextView) findViewById(R.id.res_confidence_textview);
        res_class_textview.setText(wasteType);
        String final_score = String.valueOf(highestScore);
        res_confidence_textview.setText(final_score);
        if (highestScore<=0.4){
            res_class_textview.setTextColor(Color.RED);
            res_confidence_textview.setTextColor(Color.RED);
        }else if ((highestScore>0.4) && (highestScore<0.6) ){
            res_class_textview.setTextColor(Color.YELLOW);
            res_confidence_textview.setTextColor(Color.YELLOW);
        }else if (highestScore>=0.6){
            res_class_textview.setTextColor(Color.GREEN);
            res_confidence_textview.setTextColor(Color.GREEN);
        }
    }
    private void takePhoto() throws IOException{ //Consolidated both image handling methods for code hygeine
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            String timeStamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File image = File.createTempFile(imageFileName,".jpg",getExternalFilesDir(Environment.DIRECTORY_PICTURES));
            photoPath = image.getAbsolutePath();
            if (image != null) {
                Uri photoURI = Uri.fromFile(image);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, 1);
            }
        }
    }
    public String pushToWatson (File data){
        VisualRecognition service = new VisualRecognition(VisualRecognition.VERSION_DATE_2016_05_20);
        service.setApiKey("145b047be11f5059687578f4ca85325d23e0cdf8");

        ClassifyImagesOptions options = new ClassifyImagesOptions
                .Builder()
                .images(data)//modified implementation as per SDK documentation.
                .threshold(0.000001)
                .classifierIds("Wastetype_2031632458")//This is required for our classifier to refect in its current state.
                .build();
        VisualClassification result = service.classify(options).execute();
        return result.toString();
    }
    public void onClick (View view){
        if (isConnected){
            try {
                takePhoto();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            Snackbar snackbar = Snackbar
                    .make(findViewById(android.R.id.content), "No internet connection", Snackbar.LENGTH_LONG);

            snackbar.show();
        }

    }



}
