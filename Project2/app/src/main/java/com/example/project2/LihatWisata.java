package com.example.project2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.ColorSpace;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;

public class LihatWisata extends AppCompatActivity {

    RecyclerView recview_list;
    MyAdapter adapter;
    Button Mfaad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lihat_wisata);
        setTitle("Cari Disini");

        Mfaad = findViewById(R.id.faad);

        recview_list = findViewById(R.id.recview);
        recview_list.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<Modelll> options =
                new FirebaseRecyclerOptions.Builder<Modelll>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("wisata"), Modelll.class)
                        .build();

        adapter = new MyAdapter(options);
        recview_list.setAdapter(adapter);

        Mfaad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),TambahWisata.class));
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.searchmenu,menu);

        MenuItem item=menu.findItem(R.id.search);

        SearchView searchView=(SearchView)item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String w) {

                processsearch(w);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String w) {
                processsearch(w);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void processsearch(String w) {
        FirebaseRecyclerOptions<Modelll> options =
                new FirebaseRecyclerOptions.Builder<Modelll>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("wisata").orderByChild("alamat").startAt(w).endAt(w + "\uf8ff"), Modelll.class)
                        .build();

        adapter = new MyAdapter(options);
        adapter.startListening();
        recview_list.setAdapter(adapter);
    }
}