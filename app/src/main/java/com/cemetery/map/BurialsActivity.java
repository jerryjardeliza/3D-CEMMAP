package com.cemetery.map;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cemetery.map.adapter.BurialsAdapter;
import com.cemetery.map.model.ApiResponse;
import com.cemetery.map.model.MarkerData;
import com.cemetery.map.network.ApiClient;
import com.cemetery.map.utils.VideoUrlCache;
import com.cemetery.map.view.FullscreenVideoView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BurialsActivity extends AppCompatActivity
        implements BurialsAdapter.OnBurialClickListener {

    private RecyclerView      recyclerView;
    private ProgressBar       progressBar;
    private TextView          tvEmpty;
    private SwipeRefreshLayout swipeRefresh;
    private BurialsAdapter    adapter;
    private FullscreenVideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_burials);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Burial Records");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        videoView    = findViewById(R.id.video_bg);
        recyclerView = findViewById(R.id.recycler_view);
        progressBar  = findViewById(R.id.progress_bar);
        tvEmpty      = findViewById(R.id.tv_empty);
        swipeRefresh = findViewById(R.id.swipe_refresh);

        // Play cached video in background
        if (VideoUrlCache.hasUrl()) videoView.setVideoUrl(VideoUrlCache.get());

        adapter = new BurialsAdapter(new ArrayList<>(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        swipeRefresh.setOnRefreshListener(this::loadBurials);
        loadBurials();
    }

    private void loadBurials() {
        progressBar.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);

        // Reuse markers endpoint — it contains all burial info
        ApiClient.getService(this).getMarkers()
                .enqueue(new Callback<ApiResponse<MarkerData>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<MarkerData>> call,
                                           Response<ApiResponse<MarkerData>> response) {
                        progressBar.setVisibility(View.GONE);
                        swipeRefresh.setRefreshing(false);
                        if (response.isSuccessful() && response.body() != null
                                && response.body().isSuccess()) {
                            // Filter only markers that have a burial record
                            List<MarkerData> all = response.body().getData();
                            List<MarkerData> withBurials = new ArrayList<>();
                            for (MarkerData m : all) {
                                if (m.hasburial()) withBurials.add(m);
                            }
                            adapter.updateData(withBurials);
                            tvEmpty.setVisibility(withBurials.isEmpty() ? View.VISIBLE : View.GONE);
                        } else {
                            showError("Failed to load records.");
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<MarkerData>> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        swipeRefresh.setRefreshing(false);
                        showError("Network error. Check your connection.");
                    }
                });
    }

    @Override
    public void onBurialClick(MarkerData marker) {
        Intent intent = new Intent(this, BurialDetailActivity.class);
        intent.putExtra(BurialDetailActivity.EXTRA_MARKER, marker);
        startActivity(intent);
    }

    private void showError(String msg) {
        tvEmpty.setText(msg);
        tvEmpty.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onSupportNavigateUp() { finish(); return true; }

    @Override protected void onPause()  { super.onPause();  if (videoView != null) videoView.pause(); }
    @Override protected void onResume() { super.onResume(); if (videoView != null) videoView.resume(); }
    @Override protected void onDestroy(){ super.onDestroy(); if (videoView != null) videoView.release(); }
}
