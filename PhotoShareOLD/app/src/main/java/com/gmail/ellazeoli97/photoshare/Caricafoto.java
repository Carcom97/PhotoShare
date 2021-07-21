package com.gmail.ellazeoli97.photoshare;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import static android.provider.MediaStore.*;

public class Caricafoto extends AppCompatActivity {

    ImageView image;
    Button b_fotocamera, b_galleria, b_indietro, upload_img;
    private static final int CAMERA_CODE = 101;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int GALLERY_IMG_REQUEST = 2;
    EditText img_name;
    ProgressBar progressBarHorizontal;
    private Uri uri_img;

    private StorageReference storage;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caricafoto);

        image = findViewById(R.id.imgLoad);
        b_fotocamera = findViewById(R.id.b_fotocamera);
        b_galleria = findViewById(R.id.b_galleria);
        b_indietro = findViewById(R.id.indietro);
        upload_img = findViewById(R.id.caricafoto);
        img_name = findViewById(R.id.nomefoto);
        progressBarHorizontal = findViewById(R.id.progressBarUploadfoto);

        storage = FirebaseStorage.getInstance().getReference();
        database = FirebaseDatabase.getInstance().getReference();

        b_indietro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish(); //chiudi activity
            }
        });
        b_fotocamera.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCameraPermission();
            }
        } );

        b_galleria.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFile();
            }
        } );

        upload_img.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload();
            }
        } );

    }

    //SCEGLI FOTO DALLA GALLERIA

    private void openFile() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT ); //Insieme al Type 'image/*' mostra tutti i dati che possono essere essere scelti dall'utente
        startActivityForResult( intent, GALLERY_IMG_REQUEST );
    }
    //SCATTA FOTO CON LA FOTOCAMERA
    private void getCameraPermission() {
        //If not have the permission then request it
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_CODE );
        } else {
            dispatchTakePictureIntent();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText( this, "Camera Permission is necessary ", Toast.LENGTH_LONG ).show();
            }
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK){
            File f = new File(currentPhotoPath);
            image.setImageURI( Uri.fromFile( f ) );
            uri_img = Uri.fromFile( f );

        }else if (requestCode == GALLERY_IMG_REQUEST && resultCode == RESULT_OK && data != null) {
            //Take photo from the gallery
            uri_img = data.getData();
            image.setImageURI( uri_img );
        }
    }
    String currentPhotoPath;
    //Image Path for a taken picture
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "photo_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",   /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.gmail.ellazeoli97.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra( EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }
    //Upload the File image
    public void upload() {
        if (uri_img != null) {
            StorageReference photoStorageReference = storage.child(uri_img.getLastPathSegment());

            photoStorageReference.putFile( uri_img )
                    .addOnFailureListener( new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            Toast.makeText(getApplicationContext(), "Upload Failure", Toast.LENGTH_LONG).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Handler h = new Handler();
       //Causes the Runnable to be added to the message queue, to be run after the specified amount of time elapses.
                    h.postDelayed( new Runnable() {
                        @Override
                        public void run() {
                            progressBarHorizontal.setProgress(0);
                        }
                    }, 5000);
                    Image img = new Image(img_name.getText().toString().trim(), taskSnapshot.getUploadSessionUri().toString());
                    String id = database.push().getKey();
                    database.child(id).setValue(img);

                    Toast.makeText(getApplicationContext(), "File uploaded", Toast.LENGTH_LONG).show();
                }
            } ).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    progressBarHorizontal.setProgress((int) progress);
                }
            });
        }else {
            Toast.makeText(getApplicationContext(), "No file selected!", Toast.LENGTH_SHORT).show();
        }
    }
}

