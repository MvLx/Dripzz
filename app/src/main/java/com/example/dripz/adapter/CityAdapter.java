package com.example.dripz.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.dripz.model.City;
import com.example.dripz.R;
import java.util.List;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.CityViewHolder> {
    private List<City> cityList;
    private final OnCityClickListener listener;

    public interface OnCityClickListener {
        void onCityClick(City city);
    }

    public CityAdapter(List<City> cityList, OnCityClickListener listener) {
        this.cityList = cityList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_city, parent, false);
        return new CityViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CityViewHolder holder, int position) {
        City city = cityList.get(position);
        holder.tvCity.setText(city.name);
        holder.itemView.setOnClickListener(v -> listener.onCityClick(city));
    }

    @Override
    public int getItemCount() { return cityList.size(); }

    public void setData(List<City> data) {
        this.cityList = data;
        notifyDataSetChanged();
    }

    static class CityViewHolder extends RecyclerView.ViewHolder {
        TextView tvCity;
        CityViewHolder(View itemView) { super(itemView); tvCity = itemView.findViewById(R.id.tvCity);}
    }
}