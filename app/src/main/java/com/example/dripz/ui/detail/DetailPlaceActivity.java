package com.example.dripz.ui.detail;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import com.bumptech.glide.Glide;
import com.example.dripz.R;
import com.example.dripz.model.PhotoResponse;
import com.example.dripz.model.PlaceDetailResponse;
import com.example.dripz.network.FoursquareApi;
import com.example.dripz.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailPlaceActivity extends AppCompatActivity {
    private TextView tvName, tvAddress, tvCategory, tvDesc, tvWebsite, tvJamBuka;
    private ImageView ivPhoto;
    private ProgressBar progressBar;
    private Button btnRetry;

    private final String API_KEY = "fsq3a4FzRMpB8kLrNHnB8bJgY+nTbIDEOtk7088yl5pCI4A=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_place);

        tvName = findViewById(R.id.tvName);
        tvAddress = findViewById(R.id.tvAddress);
        tvCategory = findViewById(R.id.tvCategory);
        tvDesc = findViewById(R.id.tvDesc);
        tvWebsite = findViewById(R.id.tvWebsite);
        tvJamBuka = findViewById(R.id.tvJamBuka);
        ivPhoto = findViewById(R.id.ivPhoto);
        progressBar = findViewById(R.id.progressBar);
        btnRetry = findViewById(R.id.btnRetry);

        String fsqId = getIntent().getStringExtra("fsq_id");
        if (fsqId == null) {
            finish();
            return;
        }

        loadDetail(fsqId);

        btnRetry.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            btnRetry.setVisibility(View.GONE);
            loadDetail(fsqId);
        });
    }

    private void loadDetail(String fsqId) {
        progressBar.setVisibility(View.VISIBLE);
        btnRetry.setVisibility(View.GONE);

        FoursquareApi api = RetrofitClient.getFoursquareApi();
        api.getPlaceDetail(API_KEY, fsqId)
            .enqueue(new Callback<PlaceDetailResponse>() {
                @Override
                public void onResponse(Call<PlaceDetailResponse> call, Response<PlaceDetailResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        PlaceDetailResponse detail = response.body();

                        tvName.setText(detail.name != null ? detail.name : "-");
                        tvAddress.setText(detail.location != null && detail.location.address != null ? detail.location.address : "-");
                        tvCategory.setText(
                            detail.categories != null && !detail.categories.isEmpty() && detail.categories.get(0).name != null
                                ? detail.categories.get(0).name : "-"
                        );
                        tvDesc.setText(detail.description != null ? detail.description : "Deskripsi tidak tersedia dari API");
                        tvWebsite.setText(detail.website != null ? detail.website : "Website tidak tersedia dari API");
                        // Jam buka: tampilkan jam buka jika ada, jika tidak tampilkan "-"
                        if (detail.hours != null && detail.hours.open != null && !detail.hours.open.isEmpty()) {
                            StringBuilder jamBuka = new StringBuilder();
                            for (PlaceDetailResponse.Hours.Open open : detail.hours.open) {
                                jamBuka.append(open.day != null ? open.day + ": " : "");
                                jamBuka.append(open.rendered_time != null ? open.rendered_time : "-");
                                jamBuka.append("\n");
                            }
                            tvJamBuka.setText(jamBuka.toString().trim());
                        } else if (detail.hours != null && detail.hours.is_open) {
                            tvJamBuka.setText("Buka");
                        } else {
                            tvJamBuka.setText("-");
                        }
                    }
                    // Setelah detail, lanjut ambil foto
                    loadPhoto(fsqId);
                }

                @Override
                public void onFailure(Call<PlaceDetailResponse> call, Throwable t) {
                    // Jika gagal, tetap coba ambil foto, tampilkan tombol retry
                    loadPhoto(fsqId);
                    btnRetry.setVisibility(View.VISIBLE);
                }
            });
    }

    private void loadPhoto(String fsqId) {
        FoursquareApi api = RetrofitClient.getFoursquareApi();
        api.getPlacePhotos(API_KEY, fsqId, 1)
            .enqueue(new Callback<List<PhotoResponse>>() {
                @Override
                public void onResponse(Call<List<PhotoResponse>> call, Response<List<PhotoResponse>> response) {
                    progressBar.setVisibility(View.GONE);
                    if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                        PhotoResponse photo = response.body().get(0);
                        String prefix = photo.prefix != null ? photo.prefix : "";
                        String suffix = photo.suffix != null ? photo.suffix : "";
                        if (!prefix.endsWith("/")) prefix += "/";
                        if (!suffix.startsWith("/")) suffix = "/" + suffix;
                        String url = prefix + "original" + suffix;
                        Glide.with(DetailPlaceActivity.this)
                                .load(url)
                                .placeholder(R.drawable.placeholder)
                                .error(R.drawable.placeholder)
                                .into(ivPhoto);
                    } else {
                        ivPhoto.setImageResource(R.drawable.placeholder);
                    }
                }
                @Override
                public void onFailure(Call<List<PhotoResponse>> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    ivPhoto.setImageResource(R.drawable.placeholder);
                    btnRetry.setVisibility(View.VISIBLE);
                }
            });
    }
}