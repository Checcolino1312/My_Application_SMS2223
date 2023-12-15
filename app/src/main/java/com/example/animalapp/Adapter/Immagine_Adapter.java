package com.example.animalapp.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.animalapp.Model.Immagine;
import com.example.animalapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class Immagine_Adapter extends RecyclerView.Adapter<Immagine_Adapter.ImageViewHolder> {
    private Context mContext;
    private List<Immagine> mImages;

    public Immagine_Adapter(Context context, List<Immagine> images){
        mContext=context;
        mImages=images;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(mContext).inflate(R.layout.image_item,parent,false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Immagine img = mImages.get(position);
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(img.imgPosition);
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(holder.imageView.getContext())
                        .load(uri)
                        /*.placeholder(com.firebase.ui.storage.R.drawable.common_google_signin_btn_icon_dark)
                        .circleCrop()
                        .error(com.firebase.ui.storage.R.drawable.common_google_signin_btn_icon_dark_normal)*/
                        .into(holder.imageView);
            }
        });
    }

    @Override
    public int getItemCount() {

        return mImages.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder{
        public ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.image_view_album_foto_animale);
        }
    }
}
