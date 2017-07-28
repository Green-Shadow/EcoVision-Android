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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.SimpleMultiPartRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    String BASE_URL = "https://gateway-a.watsonplatform.net/visual-recognition/api/v3/classify?api_key=145b047be11f5059687578f4ca85325d23e0cdf8&version=2016-05-20";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public String pushToWatson (String path){
        SimpleMultiPartRequest smr = new SimpleMultiPartRequest(Request.Method.POST, BASE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response){
                        try {
                            JSONObject score = new JSONObject(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        smr.addFile("images_file",image.getAbsolutePath());
        VolleyHelper.getInstance().addToRequestQueue(smr);
        return score.toString();
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
                image = takePhoto();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            String scores = pushToWatson(image.getAbsolutePath());
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
