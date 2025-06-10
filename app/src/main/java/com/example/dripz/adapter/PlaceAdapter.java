package com.example.dripz.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.dripz.R;
import com.example.dripz.model.PhotoResponse;
import com.example.dripz.model.Place;
import com.example.dripz.network.FoursquareApi;
import com.example.dripz.network.RetrofitClient;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder> {
    private List<Place> placeList;
    private final String API_KEY = "fsq3a4FzRMpB8kLrNHnB8bJgY+nTbIDEOtk7088yl5pCI4A=";

    public PlaceAdapter(List<Place> placeList) {
        this.placeList = placeList;
    }

    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_place, parent, false);
        return new PlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int position) {
        Place place = placeList.get(position);
        holder.tvName.setText(place.name);
        holder.tvAddress.setText(place.location != null ? place.location.address : "-");
        holder.tvCategory.setText(
                place.categories != null && place.categories.size() > 0 ?
                        place.categories.get(0).name : "-"
        );
        holder.ivPhoto.setImageResource(R.drawable.placeholder);
        FoursquareApi api = RetrofitClient.getFoursquareApi();
        api.getPlacePhotos(API_KEY, place.fsq_id, 1)
                .enqueue(new Callback<List<PhotoResponse>>() {
                    @Override
                    public void onResponse(Call<List<PhotoResponse>> call, Response<List<PhotoResponse>> response) {
                        if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                            PhotoResponse photo = response.body().get(0);
                            String prefix = photo.prefix != null ? photo.prefix : "";
                            String suffix = photo.suffix != null ? photo.suffix : "";
                            // Pastikan prefix ada slash di akhir, suffix ada slash di awal (Foursquare biasanya sudah benar, tapi jaga-jaga)
                            if (!prefix.endsWith("/")) prefix += "/";
                            if (!suffix.startsWith("/")) suffix = "/" + suffix;
                            String photoUrl = prefix + "original" + suffix;
                            android.util.Log.d("PHOTO", "Photo URL: " + photoUrl);

                            // Cek context valid sebelum load Glide (optional, jaga-jaga)
                            if (holder.itemView.getContext() != null) {
                                Glide.with(holder.itemView.getContext())
                                        .load(photoUrl)
                                        .placeholder(R.drawable.placeholder)
                                        .error(R.drawable.placeholder)
                                        .into(holder.ivPhoto);
                            }
                        } else {
                            holder.ivPhoto.setImageResource(R.drawable.placeholder);
                            android.util.Log.e("PHOTO", "Photo API empty or not successful: " + response.message());
                        }
                    }
                    @Override
                    public void onFailure(Call<List<PhotoResponse>> call, Throwable t) {
                        holder.ivPhoto.setImageResource(R.drawable.placeholder);
                        android.util.Log.e("PHOTO", "API call failed: " + t.getMessage());
                    }
                });
        holder.itemView.setOnClickListener(v -> {
            Context ctx = v.getContext();
            Intent intent = new Intent(ctx, com.example.dripz.ui.detail.DetailPlaceActivity.class);
            intent.putExtra("fsq_id", place.fsq_id);
            ctx.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return placeList.size();
    }

    public void setData(List<Place> data) {
        this.placeList = data;
        notifyDataSetChanged();
    }

    public static class PlaceViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvAddress, tvCategory;
        ImageView ivPhoto;
        public PlaceViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.ivPhoto);
            tvName = itemView.findViewById(R.id.tvName);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvCategory = itemView.findViewById(R.id.tvCategory);
        }
    }
}