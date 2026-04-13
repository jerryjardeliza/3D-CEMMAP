package com.cemetery.map;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.cemetery.map.db.CacheDbHelper;
import com.cemetery.map.model.ApiResponse;
import com.cemetery.map.model.MarkerData;
import com.cemetery.map.network.ApiClient;
import com.cemetery.map.utils.Constants;
import com.cemetery.map.view.CemeteryMapView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONObject;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;

public class MainActivity extends AppCompatActivity
        implements CemeteryMapView.OnMarkerClickListener {

    public static final String EXTRA_FOCUS_MARKER_ID   = "focus_marker_id";
    public static final String EXTRA_FOCUS_MARKER_NAME = "focus_marker_name";

    private CemeteryMapView mapView;
    private ProgressBar     progressBar;
    private TextView        tvError;
    private CacheDbHelper   cacheDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapView     = findViewById(R.id.map_view);
        progressBar = findViewById(R.id.progress_bar);
        tvError     = findViewById(R.id.tv_error);
        cacheDb     = new CacheDbHelper(this);

        mapView.setOnMarkerClickListener(this);

        // Handle "Find on Map" from SearchActivity
        handleFocusIntent(getIntent());

        BottomNavigationView nav = findViewById(R.id.bottom_nav);
        nav.setSelectedItemId(R.id.nav_map);
        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_search) {
                startActivity(new Intent(this, SearchActivity.class));
                return true;
            } else if (id == R.id.nav_legend) {
                showLegendDialog();
                return true;
            } else if (id == R.id.nav_about) {
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            }
            return true;
        });

        findViewById(R.id.btn_zoom_in).setOnClickListener(v -> mapView.mapZoomIn());
        findViewById(R.id.btn_zoom_out).setOnClickListener(v -> mapView.mapZoomOut());
        findViewById(R.id.btn_reset).setOnClickListener(v -> mapView.mapResetCamera());

        // Fetch GLB URL from XAMPP then load markers
        fetchAssetsAndLoad();
    }

    /**
     * Calls api/mobile/assets.php which reads site_assets table
     * and returns the GLB URL stored in XAMPP.
     */
    private void fetchAssetsAndLoad() {
        showLoading(true);

        new Thread(() -> {
            String glbUrl = "";
            try {
                SharedPreferences prefs = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
                String base = prefs.getString(Constants.PREF_BASE_URL, Constants.BASE_URL);
                // assets.php is in the same mobile/ folder
                String assetsUrl = base + "assets.php";

                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                        .readTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
                        .build();

                Request req  = new Request.Builder().url(assetsUrl).build();
                Response res = client.newCall(req).execute();

                if (res.isSuccessful() && res.body() != null) {
                    JSONObject json = new JSONObject(res.body().string());
                    if (json.optBoolean("success", false)) {
                        glbUrl = json.optString("glb_url", "");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            final String finalGlbUrl = glbUrl;
            runOnUiThread(() -> {
                if (!finalGlbUrl.isEmpty()) {
                    mapView.loadGlb(finalGlbUrl);
                }
                loadMarkers();
            });
        }).start();
    }

    private void loadMarkers() {
        ApiClient.getService(this)
                .getMarkers()
                .enqueue(new Callback<ApiResponse<MarkerData>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<MarkerData>> call,
                                           retrofit2.Response<ApiResponse<MarkerData>> response) {
                        showLoading(false);
                        if (response.isSuccessful() && response.body() != null
                                && response.body().isSuccess()) {
                            List<MarkerData> markers = response.body().getData();
                            cacheDb.saveMarkers(markers);
                            mapView.setMarkers(markers);
                        } else {
                            loadFromCache();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<MarkerData>> call, Throwable t) {
                        showLoading(false);
                        loadFromCache();
                        Toast.makeText(MainActivity.this,
                                "Offline — showing cached data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadFromCache() {
        List<MarkerData> cached = cacheDb.loadMarkers();
        if (!cached.isEmpty()) {
            mapView.setMarkers(cached);
        } else {
            tvError.setVisibility(View.VISIBLE);
            tvError.setText("Cannot connect to server.\nCheck your IP in Constants.java and ensure XAMPP is running.");
        }
    }

    @Override
    public void onMarkerClick(MarkerData marker) {
        showMarkerBottomSheet(marker);
    }

    private void showMarkerBottomSheet(MarkerData marker) {
        BottomSheetDialog sheet = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_marker, null);
        sheet.setContentView(view);

        ((TextView) view.findViewById(R.id.tv_plot_name)).setText(marker.getName());
        ((TextView) view.findViewById(R.id.tv_status)).setText(marker.getStatus().toUpperCase());
        ((TextView) view.findViewById(R.id.tv_deceased)).setText(
                marker.hasburial() ? marker.getDeceasedName() : "No burial record");
        ((TextView) view.findViewById(R.id.tv_plot_number)).setText(marker.getPlotNumber());
        ((TextView) view.findViewById(R.id.tv_section)).setText(marker.getSectionBlock());
        ((TextView) view.findViewById(R.id.tv_burial_date)).setText(marker.getBurialDate());

        int color;
        switch (marker.getStatus()) {
            case "occupied": color = 0xFFEF4444; break;
            case "reserved": color = 0xFFF59E0B; break;
            default:         color = 0xFF22C55E; break;
        }
        view.findViewById(R.id.status_dot).setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(color));

        view.findViewById(R.id.btn_view_details).setOnClickListener(v -> {
            sheet.dismiss();
            Intent intent = new Intent(this, BurialDetailActivity.class);
            intent.putExtra(BurialDetailActivity.EXTRA_MARKER, marker);
            startActivity(intent);
        });

        sheet.show();
    }

    private void showLegendDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Map Legend")
                .setMessage("🔴  Red    — Occupied plot\n\n🟢  Green — Available plot\n\n🟡  Yellow — Reserved plot\n\nTap any marker to view burial details.")
                .setPositiveButton("OK", null)
                .show();
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        tvError.setVisibility(View.GONE);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleFocusIntent(intent);
    }

    private void handleFocusIntent(Intent intent) {
        if (intent == null) return;
        int markerId = intent.getIntExtra(EXTRA_FOCUS_MARKER_ID, -1);
        String name  = intent.getStringExtra(EXTRA_FOCUS_MARKER_NAME);
        if (markerId > 0) {
            // Tell the WebView to fly camera to this marker
            final String js = "focusMarker(" + markerId + ")";
            mapView.post(() -> mapView.evaluateJavascript(js, null));
            if (name != null && !name.isEmpty()) {
                Toast.makeText(this, "Navigating to: " + name, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
