package com.cemetery.map.adapter;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cemetery.map.R;
import com.cemetery.map.model.MarkerData;

import java.util.List;

public class BurialsAdapter extends RecyclerView.Adapter<BurialsAdapter.ViewHolder> {

    public interface OnBurialClickListener {
        void onBurialClick(MarkerData marker);
    }

    private List<MarkerData>     data;
    private OnBurialClickListener listener;

    public BurialsAdapter(List<MarkerData> data, OnBurialClickListener listener) {
        this.data     = data;
        this.listener = listener;
    }

    public void updateData(List<MarkerData> newData) {
        this.data = newData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_burial_record, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        MarkerData m = data.get(position);
        h.tvName.setText(m.getDeceasedName());
        h.tvPlot.setText("Plot: " + m.getPlotNumber() + "  ·  " + m.getSectionBlock());
        h.tvDates.setText("Buried: " + m.getBurialDate());
        h.tvMarker.setText(m.getName());

        int color;
        switch (m.getStatus()) {
            case "occupied": color = 0xFFEF4444; break;
            case "reserved": color = 0xFFF59E0B; break;
            default:         color = 0xFF22C55E; break;
        }
        h.statusDot.setBackgroundTintList(ColorStateList.valueOf(color));
        h.itemView.setOnClickListener(v -> listener.onBurialClick(m));
    }

    @Override
    public int getItemCount() { return data.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPlot, tvDates, tvMarker;
        View     statusDot;
        ViewHolder(View v) {
            super(v);
            tvName    = v.findViewById(R.id.tv_name);
            tvPlot    = v.findViewById(R.id.tv_plot);
            tvDates   = v.findViewById(R.id.tv_dates);
            tvMarker  = v.findViewById(R.id.tv_marker);
            statusDot = v.findViewById(R.id.status_dot);
        }
    }
}
