package com.example.dripz.ui.detail;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.dripz.R;
import com.example.dripz.db.DBHelper;
import com.example.dripz.model.PlaceDetailResponse;
import com.example.dripz.model.PlaceDetailResponse.Photo;
import com.example.dripz.model.Place;
import com.example.dripz.model.Category;
import com.example.dripz.model.Hours;
import com.example.dripz.model.Location;
import com.example.dripz.network.FoursquareApi;
import com.example.dripz.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailPlaceActivity extends AppCompatActivity {

    private TextView tvName, tvAddress, tvCategory, tvHours;
    private ImageView ivPhoto;
    private Button btnFavorite;
    private DBHelper dbHelper;
    private boolean isFavorite = false;
    private PlaceDetailResponse placeDetailResponse;
    private Place favPlaceObj;
    private String namaKota = "";

    private final String API_KEY = "fsq3a4FzRMpB8kLrNHnB8bJgY+nTbIDEOtk7088yl5pCI4A=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_place);

        tvName = findViewById(R.id.tvName);
        tvAddress = findViewById(R.id.tvAddress);
        tvCategory = findViewById(R.id.tvCategory);
        tvHours = findViewById(R.id.tvHours);
        ivPhoto = findViewById(R.id.ivPhoto);
        btnFavorite = findViewById(R.id.btnFavorite);

        dbHelper = new DBHelper(this);

        String fsq_id = getIntent().getStringExtra("fsq_id");
        namaKota = getIntent().getStringExtra("nama_kota") != null ? getIntent().getStringExtra("nama_kota") : "";

        loadPlaceDetail(fsq_id);
    }

    private void loadPlaceDetail(String fsq_id) {
        isFavorite = dbHelper.isFavorite(fsq_id);

        FoursquareApi api = RetrofitClient.getFoursquareApi();
        api.getPlaceDetail(API_KEY, fsq_id).enqueue(new Callback<PlaceDetailResponse>() {
            @Override
            public void onResponse(Call<PlaceDetailResponse> call, Response<PlaceDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    placeDetailResponse = response.body();
                    showDetail(placeDetailResponse);
                    setFavoriteButton();
                } else {
                    Toast.makeText(DetailPlaceActivity.this, "Gagal mendapatkan detail tempat", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<PlaceDetailResponse> call, Throwable t) {
                Toast.makeText(DetailPlaceActivity.this, "Gagal koneksi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void showDetail(PlaceDetailResponse place) {
        tvName.setText(place.name != null ? place.name : "-");
        tvAddress.setText((place.location != null && place.location.address != null) ? place.location.address : "-");

        String categoryName = "-";
        if (place.categories != null && !place.categories.isEmpty() && place.categories.get(0) != null) {
            categoryName = place.categories.get(0).name != null ? place.categories.get(0).name : "-";
        }
        tvCategory.setText(categoryName);

        // Jam buka
        String jamBuka = "-";
        if (place.hours != null && place.hours.open != null && !place.hours.open.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (PlaceDetailResponse.Hours.Open open : place.hours.open) {
                sb.append((open.day != null ? open.day : "")).append(" ").append((open.rendered_time != null ? open.rendered_time : "")).append("\n");
            }
            jamBuka = sb.toString().trim();
        }
        tvHours.setText(jamBuka);

        // Load photo
        if (place.photos != null && !place.photos.isEmpty()) {
            Photo photo = place.photos.get(0);
            if (photo.prefix != null && photo.suffix != null) {
                String url = photo.prefix + "original" + photo.suffix;
                Glide.with(this)
                        .load(url)
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.placeholder)
                        .into(ivPhoto);
            } else {
                ivPhoto.setImageResource(R.drawable.placeholder);
            }
        } else {
            ivPhoto.setImageResource(R.drawable.placeholder);
        }

        favPlaceObj = new Place();
        favPlaceObj.fsq_id = place.fsq_id;
        favPlaceObj.name = place.name;

// Use Place.Location
        favPlaceObj.location = new Place.Location();
        if (place.location != null) {
            favPlaceObj.location.address = place.location.address;
        }

// Use Place.Category
        favPlaceObj.categories = new java.util.ArrayList<>();
        if (place.categories != null && !place.categories.isEmpty()) {
            Place.Category cat = new Place.Category();
            cat.name = place.categories.get(0).name;
            favPlaceObj.categories.add(cat);
        }

// Use Place.Hours
        favPlaceObj.hours = new Place.Hours();
        if (place.hours != null && place.hours.open != null && !place.hours.open.isEmpty()) {
            StringBuilder disp = new StringBuilder();
            for (PlaceDetailResponse.Hours.Open open : place.hours.open) {
                disp.append((open.day != null ? open.day : "")).append(" ").append((open.rendered_time != null ? open.rendered_time : "")).append("\n");
            }
            favPlaceObj.hours.display = disp.toString().trim();
        }

        favPlaceObj.offlineImagePath = null;

        btnFavorite.setOnClickListener(v -> {
            if (isFavorite) {
                dbHelper.removeFavorite(place.fsq_id);
                isFavorite = false;
                Toast.makeText(this, "Dihapus dari favorit", Toast.LENGTH_SHORT).show();
            } else {
                dbHelper.addFavorite(favPlaceObj, namaKota);
                isFavorite = true;
                Toast.makeText(this, "Ditambah ke favorit", Toast.LENGTH_SHORT).show();
            }
            setFavoriteButton();
        });
    }

    private void setFavoriteButton() {
        btnFavorite.setText(isFavorite ? "Unfavorite" : "Favorite");
    }
}