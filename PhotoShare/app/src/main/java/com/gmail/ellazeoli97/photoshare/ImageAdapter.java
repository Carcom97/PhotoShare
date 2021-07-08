package com.gmail.ellazeoli97.photoshare;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder>
{
    private Context context;
    private ArrayList<String> images;

    public ImageAdapter(Context context, ArrayList<String> uploads) {
        this.context = context;
        images = uploads;
    }

    @Override
    @NonNull
    public ImageAdapter.ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_image, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        Picasso.get()
                .load( images.get( position ))
                .into(holder.imageView); //carico l'immagine presa in una imageView (guarda riga 51)
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public ImageViewHolder(View itemView)
        {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view_upload);
        }
    }
}
