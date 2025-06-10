package com.example.dripz.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dripz.R;
import com.example.dripz.adapter.CityAdapter;
import com.example.dripz.adapter.PlaceAdapter;
import com.example.dripz.db.DBHelper;
import com.example.dripz.db.DBHelper.PlaceCache;
import com.example.dripz.model.City;
import com.example.dripz.model.GeocodingResponse;
import com.example.dripz.model.Place;
import com.example.dripz.model.Location;
import com.example.dripz.model.Category;
import com.example.dripz.model.Hours;
import com.example.dripz.model.PlacesResponse;
import com.example.dripz.network.FoursquareApi;
import com.example.dripz.network.GeocodingApi;
import com.example.dripz.network.RetrofitClient;
import com.example.dripz.util.ImageDownloader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private EditText etLocation;
    private Button btnSearch, btnRetryHome, btnToggleTheme;
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

    private SharedPreferences themePrefs;
    private static final String PREFS_NAME = "theme_pref";
    private static final String PREF_KEY_DARK = "is_dark";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        etLocation = view.findViewById(R.id.etLocation);
        btnSearch = view.findViewById(R.id.btnSearch);
        btnRetryHome = view.findViewById(R.id.btnRetryHome);
        btnToggleTheme = view.findViewById(R.id.btnToggleTheme);
        rvCities = view.findViewById(R.id.rvCities);
        rvPlaces = view.findViewById(R.id.rvPlaces);

        placeAdapter = new PlaceAdapter(new ArrayList<>());
        rvPlaces.setAdapter(placeAdapter);
        rvPlaces.setLayoutManager(new LinearLayoutManager(getContext()));

        cityAdapter = new CityAdapter(cityList, city -> {
            etLocation.setText(city.name);
            btnRetryHome.setVisibility(View.GONE);
            searchByLocationName(city.name);
        });
        rvCities.setAdapter(cityAdapter);
        rvCities.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        api = RetrofitClient.getFoursquareApi();
        geoApi = RetrofitClient.getGeocodingApi();

        themePrefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Set theme dari SharedPreferences saat fragment pertama kali dibuka
        applySavedTheme();

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
            if (!cityList.isEmpty()) {
                searchByLocationName(cityList.get(0).name);
            }
        });

        setThemeButtonAppearance();

        btnToggleTheme.setOnClickListener(v -> {
            int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            boolean isNowDark = currentNightMode == Configuration.UI_MODE_NIGHT_YES;
            if (isNowDark) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                themePrefs.edit().putBoolean(PREF_KEY_DARK, false).apply();
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                themePrefs.edit().putBoolean(PREF_KEY_DARK, true).apply();
            }
        });

        if (!cityList.isEmpty()) {
            searchByLocationName(cityList.get(0).name);
        }

        return view;
    }

    private void applySavedTheme() {
        boolean isDark = themePrefs.getBoolean(PREF_KEY_DARK, false);
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (isDark && currentNightMode != Configuration.UI_MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else if (!isDark && currentNightMode != Configuration.UI_MODE_NIGHT_NO) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setThemeButtonAppearance();
    }

    private void setThemeButtonAppearance() {
        if (btnToggleTheme == null || getContext() == null) return;
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            btnToggleTheme.setText("Ubah Mode");
            btnToggleTheme.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), android.R.color.transparent));
            btnToggleTheme.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white));
        } else {
            btnToggleTheme.setText("Ubah Mode");
            btnToggleTheme.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), android.R.color.white));
            btnToggleTheme.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black));
        }
    }

    private void searchByLocationName(String locationName) {
        geoApi.searchLocation(locationName, "json", 1)
                .enqueue(new Callback<List<GeocodingResponse>>() {
                    @Override
                    public void onResponse(Call<List<GeocodingResponse>> call, Response<List<GeocodingResponse>> response) {
                        if (!isAdded()) return;
                        if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                            GeocodingResponse geo = response.body().get(0);
                            String latlon = geo.lat + "," + geo.lon;
                            searchPlaces(latlon, DEFAULT_CATEGORY, 20, geo.display_name);
                        } else {
                            if (isAdded()) {
                                Toast.makeText(requireContext(), "Daerah tidak ditemukan", Toast.LENGTH_SHORT).show();
                                Log.e("Geo", "Geo response: " + response.body());
                                btnRetryHome.setVisibility(View.VISIBLE);
                                // Load offline jika gagal geo
                                loadPlacesFromDb();
                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<List<GeocodingResponse>> call, Throwable t) {
                        if (!isAdded()) return;
                        Toast.makeText(requireContext(), "Gagal mencari lokasi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("Geo", "Geo error: ", t);
                        btnRetryHome.setVisibility(View.VISIBLE);
                        // Load offline jika gagal geo
                        loadPlacesFromDb();
                    }
                });
    }

    // Perubahan: menambahkan param namaKota supaya bisa diisi ke DB
    private void searchPlaces(String latlon, String categories, int limit, String namaKota) {
        if (categories == null) categories = "";
        api.searchPlaces(API_KEY, latlon, categories, limit)
                .enqueue(new Callback<PlacesResponse>() {
                    @Override
                    public void onResponse(Call<PlacesResponse> call, Response<PlacesResponse> response) {
                        if (!isAdded()) return;
                        if (response.isSuccessful() && response.body() != null && response.body().results != null && !response.body().results.isEmpty()) {
                            List<Place> places = response.body().results;
                            placeAdapter.setData(places);
                            // Simpan ke SQLite dan download gambar
                            savePlacesToDb(places, namaKota);
                        } else {
                            if (isAdded()) {
                                Toast.makeText(requireContext(), "Data tidak ditemukan", Toast.LENGTH_SHORT).show();
                                Log.e("FSQ", "FSQ response: " + response.body());
                                btnRetryHome.setVisibility(View.VISIBLE);
                                loadPlacesFromDb();
                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<PlacesResponse> call, Throwable t) {
                        if (!isAdded()) return;
                        Toast.makeText(requireContext(), "Gagal load data: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("FSQ", "FSQ error: ", t);
                        btnRetryHome.setVisibility(View.VISIBLE);
                        // Load offline jika gagal API
                        loadPlacesFromDb();
                    }
                });
    }

    // Simpan places ke SQLite, download gambar satu per satu
    private void savePlacesToDb(List<Place> places, String namaKota) {
        Context ctx = getContext();
        if (ctx == null) return;
        DBHelper dbHelper = new DBHelper(ctx);
        dbHelper.clearPlaces(); // agar cache tidak menumpuk

        AtomicInteger done = new AtomicInteger(0);
        for (Place place : places) {
            final String namaTempat = place.name;
            final String alamatJalan = (place.location != null && place.location.address != null) ? place.location.address : "-";
            final String deskripsi = (place.categories != null && !place.categories.isEmpty() && place.categories.get(0) != null)
                    ? place.categories.get(0).name : "-";
            final String jamBuka = (place.hours != null && place.hours.display != null)
                    ? place.hours.display : "-";
            final String gambarUrl = (place.photos != null && !place.photos.isEmpty() && place.photos.get(0) != null)
                    ? place.photos.get(0).getUrl() : "";
            final String fileName = "offline_img_" + (System.currentTimeMillis() + done.get()) + ".jpg";
            final String finalNamaKota = namaKota; // agar bisa digunakan di callback

            ImageDownloader.download(ctx, gambarUrl, fileName, new ImageDownloader.DownloadCallback() {
                @Override
                public void onDownloaded(String filePath) {
                    dbHelper.insertPlace(namaTempat, finalNamaKota, alamatJalan, deskripsi, jamBuka, filePath);
                    if (done.incrementAndGet() == places.size()) {
                        Log.d("OfflineCache", "Selesai simpan semua ke DB");
                    }
                }

                @Override
                public void onFailed(Exception e) {
                    Log.e("OfflineCache", "Gagal download gambar: " + gambarUrl, e);
                    dbHelper.insertPlace(namaTempat, finalNamaKota, alamatJalan, deskripsi, jamBuka, "");
                    if (done.incrementAndGet() == places.size()) {
                        Log.d("OfflineCache", "Selesai simpan semua ke DB (beberapa gambar gagal)");
                    }
                }
            });
        }
    }

    // Load data dari SQLite dan tampilkan ke RecyclerView
    private void loadPlacesFromDb() {
        Context ctx = getContext();
        if (ctx == null) return;
        DBHelper dbHelper = new DBHelper(ctx);
        List<PlaceCache> placeCaches = dbHelper.getAllPlaces();

        if (placeCaches.isEmpty()) {
            Toast.makeText(ctx, "Tidak ada data offline", Toast.LENGTH_SHORT).show();
            placeAdapter.setData(new ArrayList<>()); // Kosongkan adapter
            return;
        }

        // Convert PlaceCache ke Place agar bisa dipakai di adapter
        List<Place> places = new ArrayList<>();
        for (PlaceCache pc : placeCaches) {
            Place place = new Place();
            place.name = pc.namaTempat;

            // Use Place.Location
            place.location = new Place.Location();
            place.location.address = pc.alamatJalan;

            // Use Place.Category
            place.categories = new ArrayList<>();
            Place.Category cat = new Place.Category();
            cat.name = pc.deskripsi;
            place.categories.add(cat);

            // Use Place.Hours
            place.hours = new Place.Hours();
            place.hours.display = pc.jamBuka;

            place.offlineImagePath = pc.gambarPath;
            places.add(place);
        }
        placeAdapter.setData(places);
        Toast.makeText(ctx, "Menampilkan data offline", Toast.LENGTH_SHORT).show();
    }
}