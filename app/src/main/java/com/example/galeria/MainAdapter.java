package com.example.galeria;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MainAdapter extends RecyclerView.Adapter {

    MainActivity mainActivity;
    List<String> photos;

    public MainAdapter(MainActivity mainActivity, List<String> photos){
        this.mainActivity = mainActivity;
        this.photos = photos;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mainActivity);
        View v = layoutInflater.inflate(R.layout.list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ImageView imPhoto = holder.itemView.findViewById(R.id.imItem);
        int w = (int) mainActivity.getResources().getDimension(R.dimen.itemWidth);
        int h = (int) mainActivity.getResources().getDimension(R.dimen.itemHeight);
        Bitmap bitmap = Utils.getScaledBitmap(photos.get(position), w, h);
        imPhoto.setImageBitmap(bitmap);
        imPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.startPhotoActivity(photos.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }
}
