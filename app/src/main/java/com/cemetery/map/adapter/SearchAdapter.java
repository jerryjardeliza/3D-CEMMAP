package com.cemetery.map.adapter;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cemetery.map.R;
import com.cemetery.map.model.SearchResult;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    public interface OnResultClickListener {
        void onDetailsClick(SearchResult result);
        void onFindOnMapClick(SearchResult result);
    }

    private List<SearchResult>    data;
    private OnResultClickListener listener;

    public SearchAdapter(List<SearchResult> data, OnResultClickListener listener) {
        this.data     = data;
        this.listener = listener;
    }

    public void updateData(List<SearchResult> newData) {
        this.data = newData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_result, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        SearchResult r = data.get(position);
        h.tvName.setText(r.getDeceasedName());
        h.tvPlot.setText("Plot: " + r.getPlotNumber() + "  ·  " + r.getSectionBlock());
        h.tvDates.setText("Buried: " + r.getBurialDate());

        int color;
        switch (r.getStatus()) {
            case "occupied": color = 0xFFEF4444; break;
            case "reserved": color = 0xFFF59E0B; break;
            default:         color = 0xFF22C55E; break;
        }
        h.statusDot.setBackgroundTintList(ColorStateList.valueOf(color));

        // Tap row = details
        h.itemView.setOnClickListener(v -> listener.onDetailsClick(r));

        // Map icon = find on map
        h.btnFindMap.setVisibility(r.hasMarker() ? View.VISIBLE : View.GONE);
        h.btnFindMap.setOnClickListener(v -> listener.onFindOnMapClick(r));
    }

    @Override
    public int getItemCount() { return data.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView    tvName, tvPlot, tvDates;
        View        statusDot;
        ImageButton btnFindMap;

        ViewHolder(View v) {
            super(v);
            tvName    = v.findViewById(R.id.tv_name);
            tvPlot    = v.findViewById(R.id.tv_plot);
            tvDates   = v.findViewById(R.id.tv_dates);
            statusDot = v.findViewById(R.id.status_dot);
            btnFindMap= v.findViewById(R.id.btn_find_map);
        }
    }
}
