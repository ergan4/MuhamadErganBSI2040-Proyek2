package com.example.project2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Dasboard extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference documentReference;


    Button Profile, LihatWisata, TambahWisata,Logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dasboard);


        documentReference = db.collection("user").document("profile");

        Profile = findViewById(R.id.profile);
        LihatWisata = findViewById(R.id.lihatWisata);
        TambahWisata = findViewById(R.id.tambahWisata);
        Logout = findViewById(R.id.logout);



        TambahWisata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), com.example.project2.TambahWisata.class));
            }
        });

        LihatWisata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), com.example.project2.LihatWisata.class));
            }
        });
    }

    public void Logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(),Login.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(),Login.class));
        finish();
    }

    public void ShowProfile(View view) {

        documentReference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.getResult().exists()){
                            Intent intent = new Intent(Dasboard.this, ShowProfile.class);
                            startActivity(intent);

                        }else {
                            Intent intent = new Intent(Dasboard.this, com.example.project2.Profile.class);
                            startActivity(intent);
                        }
                    }
                });
    }

    public void DeleteProfile(View view) {
        showDialog();
    }

    private void showDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(Dasboard.this);
        builder.setTitle("Delete");
        builder.setMessage("Are you sure to delete profile");
        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                documentReference.delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                Toast.makeText(Dasboard.this, "Profile Deleted", Toast.LENGTH_SHORT).show();

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