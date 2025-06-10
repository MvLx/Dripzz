package com.example.dripz.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.dripz.R;
import com.example.dripz.model.City;
import com.example.dripz.model.PlacesResponse;
import com.example.dripz.model.GeocodingResponse;
import com.example.dripz.adapter.CityAdapter;
import com.example.dripz.adapter.PlaceAdapter;
import com.example.dripz.network.FoursquareApi;
import com.example.dripz.network.GeocodingApi;
import com.example.dripz.network.RetrofitClient;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private EditText etLocation;
    private Button btnSearch, btnRetryHome;
    private RecyclerView rvCities, rvPlaces;
    private PlaceAdapter placeAdapter;
    private CityAdapter cityAdapter;
    private FoursquareApi api;
    private GeocodingApi geoApi;
    private final String API_KEY = "fsq3a4FzRMpB8kLrNHnB8bJgY+nTbIDEOtk7088yl5pCI4A=";
    private final String DEFAULT_CATEGORY = "16000,13000,13065,14000,17000,18000,13027,17069,16013,19014";

    private final List<City> cityList = Arrays.asList(
            new City("Jakarta", "Jakarta"),
            new City("Bandung", "Bandung"),
            new City("Surabaya", "Surabaya"),
            new City("Bali", "Bali"),
            new City("Makassar", "Makassar")
    );

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        etLocation = view.findViewById(R.id.etLocation);
        btnSearch = view.findViewById(R.id.btnSearch);
        btnRetryHome = view.findViewById(R.id.btnRetryHome);
        rvCities = view.findViewById(R.id.rvCities);
        rvPlaces = view.findViewById(R.id.rvPlaces);

        placeAdapter = new PlaceAdapter(new ArrayList<>());
        rvPlaces.setAdapter(placeAdapter);
        rvPlaces.setLayoutManager(new LinearLayoutManager(getContext())); // satu kolom

        cityAdapter = new CityAdapter(cityList, city -> {
            etLocation.setText(city.name);
            searchByLocationName(city.name);
        });
        rvCities.setAdapter(cityAdapter);
        rvCities.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        api = RetrofitClient.getFoursquareApi();
        geoApi = RetrofitClient.getGeocodingApi();

        btnSearch.setOnClickListener(v -> {
            String locationName = etLocation.getText().toString().trim();
            if (locationName.isEmpty()) {
                etLocation.setError("Isi nama daerah dulu");
                return;
            }
            btnRetryHome.setVisibility(View.GONE);
            searchByLocationName(locationName);
        });

        btnRetryHome.setOnClickListener(v -> {
            btnRetryHome.setVisibility(View.GONE);
            // Default: cari kota pertama di daftar
            if (!cityList.isEmpty()) {
                searchByLocationName(cityList.get(0).name);
            }
        });

        // Optional: load default city on start
        if (!cityList.isEmpty()) {
            searchByLocationName(cityList.get(0).name);
        }

        return view;
    }

    private void searchByLocationName(String locationName) {
        geoApi.searchLocation(locationName, "json", 1)
                .enqueue(new Callback<List<GeocodingResponse>>() {
                    @Override
                    public void onResponse(Call<List<GeocodingResponse>> call, Response<List<GeocodingResponse>> response) {
                        if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                            GeocodingResponse geo = response.body().get(0);
                            String latlon = geo.lat + "," + geo.lon;
                            searchPlaces(latlon, DEFAULT_CATEGORY, 20);
                        } else {
                            Toast.makeText(getContext(), "Daerah tidak ditemukan", Toast.LENGTH_SHORT).show();
                            Log.e("Geo", "Geo response: " + response.body());
                            btnRetryHome.setVisibility(View.VISIBLE);
                        }
                    }
                    @Override
                    public void onFailure(Call<List<GeocodingResponse>> call, Throwable t) {
                        Toast.makeText(getContext(), "Gagal mencari lokasi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("Geo", "Geo error: ", t);
                        btnRetryHome.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void searchPlaces(String latlon, String categories, int limit) {
        if (categories == null) categories = "";
        api.searchPlaces(API_KEY, latlon, categories, limit)
                .enqueue(new Callback<PlacesResponse>() {
                    @Override
                    public void onResponse(Call<PlacesResponse> call, Response<PlacesResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().results != null && !response.body().results.isEmpty()) {
                            placeAdapter.setData(response.body().results);
                        } else {
                            Toast.makeText(getContext(), "Data tidak ditemukan", Toast.LENGTH_SHORT).show();
                            Log.e("FSQ", "FSQ response: " + response.body());
                            btnRetryHome.setVisibility(View.VISIBLE);
                        }
                    }
                    @Override
                    public void onFailure(Call<PlacesResponse> call, Throwable t) {
                        Toast.makeText(getContext(), "Gagal load data: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("FSQ", "FSQ error: ", t);
                        btnRetryHome.setVisibility(View.VISIBLE);
                    }
                });
    }
}