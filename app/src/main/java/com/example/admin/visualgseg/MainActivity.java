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

    VisualClassification pushToWatson (File photoFile){                                            //filePath would store image location (eg:-"src/test/resources/visual_recognition/car.png")
        VisualRecognition service = new VisualRecognition(VisualRecognition.VERSION_DATE_2016_05_20);
        service.setApiKey("145b047be11f5059687578f4ca85325d23e0cdf8");

        ClassifyImagesOptions options = new ClassifyImagesOptions.Builder()
                .images(photoFile)                                                         //passes filePath to analyze
                .build();
        VisualClassification result = service.classify(options).execute();
        return result;
    }
    private File createImageFile() throws IOException {
    // Create an image file name
    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    String imageFileName = "JPEG_" + timeStamp + "_";
    File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    File image = File.createTempFile(
        imageFileName,  /* prefix */
        ".jpg",         /* suffix */
        storageDir      /* directory */
    );

    // Save a file: path for use with ACTION_VIEW intents
    //mCurrentPhotoPath = image.getAbsolutePath();
    return image;
}
private File takePhoto() {
    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
        File photoFile = null;
        try {photoFile = createImageFile();} 
        catch (IOException ex) {
           ex.printStackTrace();
        }
        if (photoFile != null) {
            Uri photoURI = Uri.fromFile(photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePictureIntent, 1);
            return photoFile;
        }
    }
    return null;
}
}
