package com.example.admin.visualgseg;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.Snackbar;



import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
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
import android.app.ProgressDialog;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.content.Context;

import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifyImagesOptions;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassification;

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

    public String pushToWatson (String path){
        VisualRecognition service = new VisualRecognition(VisualRecognition.VERSION_DATE_2016_05_20);
        service.setApiKey("145b047be11f5059687578f4ca85325d23e0cdf8");

        ClassifyImagesOptions options = null;
            options = new ClassifyImagesOptions.Builder()
                    .images(new File(path))//modified implementation as per SDK documentation.
                    .threshold(0.000001)
                    .classifierIds("Wastetype_2031632458")//This is required for our classifier to refect in its current state.
                    .build();
        VisualClassification result = service.classify(options).execute();
        return result.toString();
    }
    String photoPath;
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {//Callback for camera
        if (requestCode == 1 && resultCode == RESULT_OK){
            new action().execute();
        }
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

    
    private class action extends AsyncTask<Void,Void,String>{
        ProgressDialog pd;
        @Override
        protected void onPreExecute(){
            pd=ProgressDialog.show(MainActivity.this,"","Connecting to Watson...",false); 
        }
        
        @Override
        protected String doInBackground(Void... params) {
            String scores = pushToWatson(photoPath);
            return scores;
        }

        @Override
        protected void onPostExecute(String s) {
            pd.dismiss();
            TextView output = (TextView)findViewById(R.id.result);
            output.setText(s);
            try{
                Intent intent = new Intent(MainActivity.this, ResultActivity.class); //Start of code for activity transfer.
                intent.putExtra("PHOTO", photoPath);
                intent.putExtra("JSON",s);
                startActivity(intent);}
            catch(Exception e){e.printStackTrace();}
            
            
        }
    }

    public static Bitmap resizeBitMapImage(String filePath, int targetWidth, int targetHeight) {
        Bitmap bitMapImage = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, options);
            double sampleSize = 0;
            Boolean scaleByHeight = Math.abs(options.outHeight - targetHeight) >= Math.abs(options.outWidth
                    - targetWidth);
            if (options.outHeight * options.outWidth * 2 >= 1638) {
                sampleSize = scaleByHeight ? options.outHeight / targetHeight : options.outWidth / targetWidth;
                sampleSize = (int) Math.pow(2d, Math.floor(Math.log(sampleSize) / Math.log(2d)));
            }
            options.inJustDecodeBounds = false;
            options.inTempStorage = new byte[128];
            while (true) {
                try {
                    options.inSampleSize = (int) sampleSize;
                    bitMapImage = BitmapFactory.decodeFile(filePath, options);
                    break;
                } catch (Exception ex) {
                    try {
                        sampleSize = sampleSize * 2;
                    } catch (Exception ex1) {

                    }
                }
            }
        } catch (Exception ex) {

        }
        return bitMapImage;
    }// ignore this method,but keep it in code
    private File compress (File uncompressed){
        Bitmap bitmap = BitmapFactory.decodeFile(uncompressed.getAbsolutePath());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 50, out);                                       // The integer represents the percentage quality(100 is the most quality,least compression)
        Bitmap decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
        File compressed = new File(decoded+".png");
        return compressed;
    }                                                  // ignore this method,but keep it in code
    private File compress2(File uncon) throws IOException{
        File compressed = new Compressor(this).compressToFile(uncon);
        return compressed;
    }

}
