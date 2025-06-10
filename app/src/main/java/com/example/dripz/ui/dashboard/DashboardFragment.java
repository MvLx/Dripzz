package com.example.dripz.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dripz.R;
import com.example.dripz.adapter.PlaceAdapter;
import com.example.dripz.db.DBHelper;
import com.example.dripz.model.Place;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {
    private RecyclerView rvFavorite;
    private PlaceAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        rvFavorite = view.findViewById(R.id.rvFavorite);

        adapter = new PlaceAdapter(new ArrayList<>());
        rvFavorite.setAdapter(adapter);
        rvFavorite.setLayoutManager(new LinearLayoutManager(getContext()));

        loadFavorites();
        return view;
    }

    private void loadFavorites() {
        DBHelper dbHelper = new DBHelper(requireContext());
        List<Place> favList = dbHelper.getAllFavorites();
        adapter.setData(favList);
        if (favList.isEmpty()) {
            Toast.makeText(getContext(), "Tidak ada tempat favorit", Toast.LENGTH_SHORT).show();
        }
    }
}