package com.example.project2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class TambahWisata extends AppCompatActivity {

    EditText nama_ws,kategori_ws,alamat_ws;
    Button tambah_ws;
    FirebaseStorage firebaseStorage2;
    StorageReference storageReference2;
    private static final int PICK_IMAGE = 1;
    private Uri imageuri;
    ImageView gambar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_wisata);

        gambar = findViewById(R.id.gambar_tb);
        nama_ws = findViewById(R.id.name_tb_ws);
        kategori_ws = findViewById(R.id.kategori_tb_ws);
        alamat_ws = findViewById(R.id.alamat_tb_ws);
        tambah_ws = findViewById(R.id.Tambah_wisata);
        nama_ws = findViewById(R.id.name_tb_ws);




        tambah_ws.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processinsert();
            }
        });
    }

    private void processinsert() {
        Map<String,Object> map=new HashMap<>();
        map.put("nama",nama_ws.getText().toString());
        map.put("kategori",kategori_ws.getText().toString());
        map.put("alamat",alamat_ws.getText().toString());
        FirebaseDatabase.getInstance().getReference().child("wisata").push()
                .setValue(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        nama_ws.setText("");
                        kategori_ws.setText("");
                        alamat_ws.setText("");
                        Toast.makeText(getApplicationContext(),"Inserted Successfully",Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        Toast.makeText(getApplicationContext(),"Could not insert",Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void PilihGambar(View view) {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE || resultCode == RESULT_OK || data != null || data.getData() != null){

            imageuri = data.getData();

            Picasso.get().load(imageuri).into(gambar);

        }
    }

    private String getFileExt(Uri uri){

        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }




}