package com.example.project2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Document;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Profile extends AppCompatActivity {

    EditText et_name, et_age, et_bio, et_email, et_alamat;
    Button button;
    ProgressBar progressBar;
    private Uri imageUri;
    private static final int PICK_IMAGE = 1;
    UploadTask uploadTask;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference documentReference;
    ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        imageView = findViewById(R.id.imageView_cp);
        et_name = findViewById(R.id.name_et_cp);
        et_age = findViewById(R.id.age_et_cp);
        et_bio = findViewById(R.id.bio_et_cp);
        et_email = findViewById(R.id.email_et_cp);
        et_alamat = findViewById(R.id.alamat_et_cp);
        et_name = findViewById(R.id.name_et_cp);
        button = findViewById(R.id.save_profile_btn_cp);
        progressBar = findViewById(R.id.progressbar_cp);

        documentReference = db.collection("user").document("profile");
        storageReference = firebaseStorage.getInstance().getReference("profile image");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadData();
            }
        });


    }

    public void ChooseImage(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE || resultCode == RESULT_OK || data != null || data.getData() != null){

            imageUri = data.getData();

            Picasso.get().load(imageUri).into(imageView);

        }
    }

    private String getFileExt(Uri uri){

        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }

    private void UploadData(){

        String name = et_name.getText().toString();
        String age = et_age.getText().toString();
        String bio = et_bio.getText().toString();
        String alamat = et_alamat.getText().toString();
        String email = et_email.getText().toString();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(age) || TextUtils.isEmpty(bio)
                || TextUtils.isEmpty(alamat) || TextUtils.isEmpty(email) || imageUri != null){

            progressBar.setVisibility(View.VISIBLE);
            final StorageReference reference = storageReference.child(System.currentTimeMillis() + "." + getFileExt(imageUri));

            uploadTask = reference.putFile(imageUri);

            Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull  Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }

                    return reference.getDownloadUrl();
                }
            })
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull  Task<Uri> task) {

                            if (task.isSuccessful()){
                                Uri downloadUri = task.getResult();
                                Map<String,String> profile = new HashMap<>();
                                profile.put("name", name);
                                profile.put("age", age);
                                profile.put("bio", bio);
                                profile.put("email", email);
                                profile.put("alamat", alamat);
                                profile.put("url", downloadUri.toString());

                                documentReference.set(profile)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                progressBar.setVisibility(View.INVISIBLE);
                                                Toast.makeText(Profile.this, "Profile Created", Toast.LENGTH_SHORT).show();

                                                Intent intent = new Intent(Profile.this, ShowProfile.class);
                                                startActivity(intent);

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull  Exception e) {

                                                Toast.makeText(Profile.this, "failed", Toast.LENGTH_SHORT).show();

                                            }
                                        });


                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });

        }else {
            Toast.makeText(this, "All Files Required", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        documentReference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.getResult().exists()){
                            String name_result = task.getResult().getString("name");
                            String age_result = task.getResult().getString("age");
                            String bio_result = task.getResult().getString("bio");
                            String email_result = task.getResult().getString("email");
                            String alamat_result = task.getResult().getString("alamat");
                            String url = task.getResult().getString("url");


                            Picasso.get().load(url).into(imageView);
                            et_name.setText(name_result);
                            et_age.setText(age_result);
                            et_bio.setText(bio_result);
                            et_email.setText(email_result);
                            et_alamat.setText(alamat_result);

                        }else {
                            Toast.makeText(Profile.this, "No Profile Exist", Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
}

