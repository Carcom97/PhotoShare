package com.gmail.ellazeoli97.photoshare;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class BrowsingFoto extends AppCompatActivity {
        private static RecyclerView recycler_view;
        private ImageAdapter adapter;
        private ProgressBar progressBar;
        private ArrayList<String> images;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_browsingfoto);

            recycler_view = findViewById(R.id.recycler_view);
            recycler_view.setHasFixedSize(true);
            recycler_view.setLayoutManager(new LinearLayoutManager(this));
            progressBar = findViewById(R.id.progressbar_browsing);
            images = new ArrayList<>();

            adapter = new ImageAdapter(this, images);
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();

            progressBar.setVisibility(View.VISIBLE );
            storageReference.listAll().addOnSuccessListener( new OnSuccessListener<ListResult>() {
                @Override
                public void onSuccess(ListResult listResult) {
                    for (StorageReference sr : listResult.getItems())
                    {sr.getDownloadUrl().addOnSuccessListener( new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                images.add( uri.toString());
                                Log.d("item", uri.toString());
                            }
                        } ).addOnSuccessListener( new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri)
                            {
                                recycler_view.setAdapter( adapter );
                                progressBar.setVisibility( View.GONE );
                            }
                        } );
                    }
                }
            } );
        }
        @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate( R.menu.menu, menu); //MenuInflater mi pernette di riempire menu con quello che ho creato
        return true;
        }
    //Se il menu item viene cliccato si torna indietro alla MainActivity class
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.item_back)
            startActivity( new Intent( getApplicationContext(), MainActivity.class ));
        return super.onOptionsItemSelected( item );
    }
}
