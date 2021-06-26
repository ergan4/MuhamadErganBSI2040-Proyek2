package com.example.project2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.HashMap;
import java.util.Map;

public class MyAdapter extends FirebaseRecyclerAdapter<Modelll,MyAdapter.myviewholder> {

    public MyAdapter(FirebaseRecyclerOptions<Modelll> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final myviewholder holder, final int position, @NonNull final Modelll model) {
        holder.nama.setText(model.getNama());
        holder.kategori.setText(model.getKategori());
        holder.alamat.setText(model.getAlamat());
        Glide.with(holder.img.getContext()).load(model.getPurl()).into(holder.img);

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DialogPlus dialogPlus = DialogPlus.newDialog(holder.img.getContext())
                        .setContentHolder(new ViewHolder(R.layout.dialogcontent))
                        .setExpanded(true, 1100)
                        .create();

                View myview = dialogPlus.getHolderView();
                final EditText purl = myview.findViewById(R.id.uimgurl);
                final EditText nama = myview.findViewById(R.id.uname);
                final EditText alamat = myview.findViewById(R.id.ualamat);
                final EditText kategori = myview.findViewById(R.id.ukategori);
                Button submit = myview.findViewById(R.id.usubmit);

                purl.setText(model.getPurl());
                nama.setText(model.getNama());
                alamat.setText(model.getAlamat());
                kategori.setText(model.getKategori());

                dialogPlus.show();

                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("purl", purl.getText().toString());
                        map.put("nama", nama.getText().toString());
                        map.put("kategori", kategori.getText().toString());
                        map.put("alamat", alamat.getText().toString());

                        FirebaseDatabase.getInstance().getReference().child("wisata")
                                .child(getRef(position).getKey()).updateChildren(map)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        dialogPlus.dismiss();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        dialogPlus.dismiss();
                                    }
                                });
                    }
                });


            }
        });


        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.img.getContext());
                builder.setTitle("Delete Panel");
                builder.setMessage("Delete...?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseDatabase.getInstance().getReference().child("wisata")
                                .child(getRef(position).getKey()).removeValue();
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                builder.show();
            }
        });

    } // End of OnBindViewMethod

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.template, parent, false);
        return new myviewholder(view);
    }


    class myviewholder extends RecyclerView.ViewHolder {
        ImageView edit, delete,img;
        TextView nama, kategori, alamat;

        public myviewholder(@NonNull View itemView) {
            super(itemView);
            nama= (TextView) itemView.findViewById(R.id.namatext);
            kategori = (TextView) itemView.findViewById(R.id.kategoritext);
            alamat = (TextView) itemView.findViewById(R.id.alamattext);
            img = (ImageView) itemView.findViewById(R.id.img);

            edit = (ImageView) itemView.findViewById(R.id.editicon);
            delete = (ImageView) itemView.findViewById(R.id.deleteicon);
        }
    }
}