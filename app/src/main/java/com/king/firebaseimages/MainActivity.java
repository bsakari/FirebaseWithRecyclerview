package com.king.firebaseimages;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.logging.Handler;

public class MainActivity extends AppCompatActivity {
private static final int PICK_IMAGE_REQUEST = 1;
private Button mButtonChooseImage,mButtonUpload;
private TextView mTextViewShowUploads;
private EditText mEditTextFileName;
private ImageView mImageView;
private ProgressBar mProgressBar;
private Uri mImageUri;
private StorageReference mStorageRef;
private DatabaseReference mDatabaseRef;
private StorageTask mUploadTask;

    @Override
    protected  void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mButtonChooseImage = findViewById(R.id.button_choose_image);
        mButtonUpload = findViewById(R.id.button_upload);
        mTextViewShowUploads = findViewById(R.id.text_view_show_uploads);
        mEditTextFileName = findViewById(R.id.edit_text_file_name);
        mImageView = findViewById(R.id.imageView);
        mProgressBar = findViewById(R.id.progress_bar);
        mProgressBar = findViewById(R.id.progress_bar);
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");


        //Chhosing an Image from Gallery
        mButtonChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            openFileChooser();
            }
        });
        //Uploading an Image
        mButtonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUploadTask !=null && mUploadTask.isInProgress()){
                    Toast.makeText(MainActivity.this, "Upload in Progress", Toast.LENGTH_LONG).show();
                }else {
                    uploadFile();
                }
            }
        });
        //Showing uploaded images
        mTextViewShowUploads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagesActivity();
            }
        });
    }

    public void openFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==PICK_IMAGE_REQUEST &&resultCode==
                RESULT_OK && data!=null && data.getData()!=null){
            mImageUri = data.getData();
            Picasso.with(this).load(mImageUri).into(mImageView);
            //You Can Also Use
            //mImageView.setImageURI(mImageUri);
        }
    }


    // Getting an extension from our file (eg. Jpeg,Png,Jpg etc)
    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile(){
    if (mImageUri !=null){
        StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                +"."+getFileExtension(mImageUri));

        mUploadTask =fileReference.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            //Success
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Thread delay = new Thread(){
                    @Override
                    public void run() {
                        try {
                            sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
                delay.start();
                Toast.makeText(MainActivity.this, "Upload Successful", Toast.LENGTH_LONG).show();
                Upload upload = new Upload(mEditTextFileName.getText().toString()
                .trim(),taskSnapshot.getDownloadUrl().toString());
                String uploadId = mDatabaseRef.push().getKey();
                mDatabaseRef.child(System.currentTimeMillis()+"").setValue(upload);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            //Failure
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            //Updating the Progress Bar
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                mProgressBar.setProgress((int) progress);
            }
        });

    }else {
        Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
    }

    }

    private void openImagesActivity(){
        Intent intent = new Intent(getApplicationContext(),ImagesActivity.class);
        startActivity(intent);
    }

}
