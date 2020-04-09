package com.example.dumps;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.tensorflow.lite.Interpreter;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;


public class CaptureImage extends AppCompatActivity {


    Interpreter tflite;

    ImageView imageView;
    Button button;
    File photoFile = null;
    static final int CAPTURE_IMAGE_REQUEST = 1;

    String mCurrentPhotoPath;
    private static final String IMAGE_DIRECTORY_NAME = "DUMPS";
    private int pic_clicked=0;
    Button add_details;
    private static final int GET_DETAILS_REQUEST_CODE =96;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_image);

        imageView = (ImageView) findViewById(R.id.imageView);
        button = findViewById(R.id.btnCaptureImage);
        add_details = (Button) findViewById(R.id.add_details_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    pic_clicked = captureImage();
                    //predict();
                } else {
                    pic_clicked = captureImage2();
                }
            }
        });
        add_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pic_clicked == 1) {
                    displayMessage(getBaseContext(), "Great picture !!! ");
                    //button.setOnClickListener(null);
                     /*final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        get_details();
                    }}, 2000);
                    pic_clicked=0;*/
                    Intent intent2=new Intent(CaptureImage.this,GetDetails.class);
                    CaptureImage.this.startActivity(intent2);
                    //startActivityForResult(intent2, GET_DETAILS_REQUEST_CODE);
                    finish();
                }
                else
                {
                    displayMessage(getApplicationContext(),"Please click a picture of the dump");
                }
            }
        });
    }


/*
    private void predict() {
        try{
            tflite = new Interpreter(loadModelFile());
        }
        catch (Exception e){
            displayMessage(getBaseContext(),e.toString());
        }


        tflite.run(inputVal,outputVal); //both arguments are arrays

    }
*/
    private int captureImage2() {

        try {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            photoFile = createImageFile4();
            if (photoFile != null) ;
            {
                displayMessage(getBaseContext(), photoFile.getAbsolutePath());
                Log.i("Dumps", photoFile.getAbsolutePath());
                Uri photoUri = Uri.fromFile(photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(cameraIntent, CAPTURE_IMAGE_REQUEST);
                return 1;
            }

        } catch (Exception e) {
            displayMessage(getBaseContext(), "Camera is not available" + e.toString());
            return 0;
        }
    }

    private int captureImage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Note to self : this below code asks for permission, try including it at start and see if it asks for permission by default
            // Never mind the above comment, onrequestpermissionresult calls captureimage if permissions are granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        } else {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                //Creating the file where the photo should go
                try {
                    photoFile = createImageFile();
                    displayMessage(getBaseContext(), photoFile.getAbsolutePath());
                    Log.i("Dumps", photoFile.getAbsolutePath());
                    //Continue only if the photoFile was crated
                    if (photoFile != null) {
                        //displayMessage(getBaseContext(),"photofile not null");
                        Uri photoUri = FileProvider.getUriForFile(this, "com.example.dumps.fileprovider", photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                        startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST);
                        return 1;
                    }
                } catch (Exception e) {
                    displayMessage(getBaseContext(), e.getMessage().toString());
                    return 0;
                }
            } else {
                displayMessage(getBaseContext(), "NULL, something went wrong ");
                return 0;
            }
        }
    return 0;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAPTURE_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Bitmap img_bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
            imageView.setImageBitmap(img_bitmap);
        } else {
            displayMessage(getBaseContext(), "Image Request cancelled");
        }
    }

    private File createImageFile4() {
        //External SD Card location
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), IMAGE_DIRECTORY_NAME);

        //Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                displayMessage(getBaseContext(), "Unable to create directory");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");

        return mediaFile;

    }

    private File createImageFile() throws IOException {
        //Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,//prefix
                ".jpg",//suffix
                storageDir   //directory
        );

        //Save a file : path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;

    }

    private void displayMessage(Context context, String message)
    {
        Toast.makeText(context,message,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==0){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED && grantResults[1]==PackageManager.PERMISSION_GRANTED){
                captureImage();
            }
        }
    }
    /*
    private MappedByteBuffer loadModelFile() throws IOException{
        //Open model using input stream and memory map it to load
        AssetFileDescriptor fileDescriptor=this.getAssets().openFd("converted_model.tflite");
        FileInputStream inputStream =new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel =inputStream.getChannel();
        long startOffset=fileDescriptor.getStartOffset();
        long declaredLength=fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY,startOffset,declaredLength);
    }

     */


}



