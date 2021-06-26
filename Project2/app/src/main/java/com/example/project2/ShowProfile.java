package com.example.project2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class ShowProfile extends AppCompatActivity {

    UploadTask uploadTask;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference documentReference;
    ImageView imageView;
    Button edit;

    TextView nameEt, bioEt, emailEt, alamatEt, ageEt;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_profile);

        edit = findViewById(R.id.floatingbtn_sp);
        nameEt = findViewById(R.id.name_tv_sp);
        ageEt = findViewById(R.id.age_tv_sp);
        emailEt = findViewById(R.id.email_tv_sp);
        bioEt = findViewById(R.id.bio_tv_sp);
        alamatEt = findViewById(R.id.alamat_tv_sp);
        imageView = findViewById(R.id.imageView_sp);
        documentReference = db.collection("user").document("profile");
        storageReference = firebaseStorage.getInstance().getReference("profile image");

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowProfile.this, UpdateUser.class);
                startActivity(intent);
            }
        });

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
                            nameEt.setText(name_result);
                            ageEt.setText(age_result);
                            bioEt.setText(bio_result);
                            emailEt.setText(email_result);
                            alamatEt.setText(alamat_result);

                        }else {
                            Toast.makeText(ShowProfile.this, "No Profile Exist", Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    public void DeleteProfile(View view) {showDialog(); }

    private void showDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ShowProfile.this);
        builder.setTitle("Delete");
        builder.setMessage("Are you sure to delete profile");
        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                documentReference.delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                Toast.makeText(ShowProfile.this, "Profile Deleted", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ShowProfile.this, Dasboard.class);
                                startActivity(intent);

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });

            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}