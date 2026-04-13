package com.cemetery.map;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cemetery.map.adapter.SearchAdapter;
import com.cemetery.map.model.ApiResponse;
import com.cemetery.map.model.MarkerData;
import com.cemetery.map.model.SearchResult;
import com.cemetery.map.network.ApiClient;
import com.cemetery.map.utils.VideoUrlCache;
import com.cemetery.map.view.FullscreenVideoView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity
        implements SearchAdapter.OnResultClickListener {

    private RecyclerView       recyclerView;
    private ProgressBar        progressBar;
    private TextView           tvEmpty, tvCount;
    private SwipeRefreshLayout swipeRefresh;
    private SearchAdapter      adapter;
    private FullscreenVideoView videoView;
    private final List<SearchResult> allRecords = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Search Burials");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        videoView    = findViewById(R.id.video_bg);
        recyclerView = findViewById(R.id.recycler_view);
        progressBar  = findViewById(R.id.progress_bar);
        tvEmpty      = findViewById(R.id.tv_empty);
        tvCount      = findViewById(R.id.tv_count);
        swipeRefresh = findViewById(R.id.swipe_refresh);

        // Play cached video in background
        if (VideoUrlCache.hasUrl()) videoView.setVideoUrl(VideoUrlCache.get());

        adapter = new SearchAdapter(new ArrayList<>(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Filter as user types — no API call needed
        ((com.google.android.material.textfield.TextInputEditText)
                findViewById(R.id.et_search))
                .addTextChangedListener(new TextWatcher() {
                    @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
                    @Override public void afterTextChanged(Editable s) {}
                    @Override public void onTextChanged(CharSequence s, int st, int b, int c) {
                        filterRecords(s.toString().trim());
                    }
                });

        swipeRefresh.setOnRefreshListener(this::loadAllRecords);

        // Load all records immediately on open
        loadAllRecords();
    }

    /** Fetch all markers (with burial info) from the API once */
    private void loadAllRecords() {
        progressBar.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);

        ApiClient.getService(this).getMarkers()
                .enqueue(new Callback<ApiResponse<MarkerData>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<MarkerData>> call,
                                           Response<ApiResponse<MarkerData>> response) {
                        progressBar.setVisibility(View.GONE);
                        swipeRefresh.setRefreshing(false);
                        if (response.isSuccessful() && response.body() != null
                                && response.body().isSuccess()) {
                            allRecords.clear();
                            for (MarkerData m : response.body().getData()) {
                                if (m.hasburial()) allRecords.add(toSearchResult(m));
                            }
                            filterRecords(""); // show all
                        } else {
                            showEmpty("Failed to load records.");
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<MarkerData>> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        swipeRefresh.setRefreshing(false);
                        showEmpty("Network error. Check your connection.");
                    }
                });
    }

    /** Filter the local list — instant, no network */
    private void filterRecords(String query) {
        List<SearchResult> filtered = new ArrayList<>();
        String q = query.toLowerCase();
        for (SearchResult r : allRecords) {
            if (q.isEmpty()
                    || r.getDeceasedName().toLowerCase().contains(q)
                    || r.getPlotNumber().toLowerCase().contains(q)
                    || r.getSectionBlock().toLowerCase().contains(q)) {
                filtered.add(r);
            }
        }
        adapter.updateData(filtered);
        tvCount.setText(filtered.size() + " record" + (filtered.size() == 1 ? "" : "s"));
        tvEmpty.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
        tvEmpty.setText(query.isEmpty() ? "No burial records found." : "No results for \"" + query + "\"");
    }

    /** Convert MarkerData to SearchResult for the adapter */
    private SearchResult toSearchResult(MarkerData m) {
        // Build a SearchResult from MarkerData fields using Gson round-trip
        com.google.gson.JsonObject obj = new com.google.gson.JsonObject();
        obj.addProperty("marker_id",    m.getId());
        obj.addProperty("marker_name",  m.getName());
        obj.addProperty("x",            m.getX());
        obj.addProperty("y",            m.getY());
        obj.addProperty("z",            m.getZ());
        obj.addProperty("status",       m.getStatus());
        obj.addProperty("burial_id",    m.getBurialId() != null ? m.getBurialId() : 0);
        obj.addProperty("deceased_name",m.getDeceasedName());
        obj.addProperty("birth_date",   m.getBirthDate());
        obj.addProperty("death_date",   m.getDeathDate());
        obj.addProperty("burial_date",  m.getBurialDate());
        obj.addProperty("plot_number",  m.getPlotNumber());
        obj.addProperty("section_block",m.getSectionBlock());
        obj.addProperty("notes",        m.getNotes());
        return new com.google.gson.Gson().fromJson(obj, SearchResult.class);
    }

    @Override
    public void onDetailsClick(SearchResult result) {
        Intent intent = new Intent(this, BurialDetailActivity.class);
        intent.putExtra(BurialDetailActivity.EXTRA_SEARCH_RESULT, result);
        startActivity(intent);
    }

    @Override
    public void onFindOnMapClick(SearchResult result) {
        // Go back to MainActivity and tell it to focus this marker
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.EXTRA_FOCUS_MARKER_ID, result.getMarkerId());
        intent.putExtra(MainActivity.EXTRA_FOCUS_MARKER_NAME, result.getDeceasedName());
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    private void showEmpty(String msg) {
        tvEmpty.setText(msg);
        tvEmpty.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onSupportNavigateUp() { finish(); return true; }

    @Override protected void onPause()  { super.onPause();  if (videoView != null) videoView.pause(); }
    @Override protected void onResume() { super.onResume(); if (videoView != null) videoView.resume(); }
    @Override protected void onDestroy(){ super.onDestroy(); if (videoView != null) videoView.release(); }
}
