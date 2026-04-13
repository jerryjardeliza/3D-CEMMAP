package com.cemetery.map;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.cemetery.map.utils.Constants;
import com.cemetery.map.utils.VideoUrlCache;
import com.cemetery.map.view.FullscreenVideoView;

import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SplashActivity extends AppCompatActivity {

    private FullscreenVideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Full screen
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        setContentView(R.layout.activity_splash);

        videoView = findViewById(R.id.video_bg);

        // Fetch video URL from XAMPP site_assets table
        fetchAndPlayVideo();

        // Enter → MainActivity
        findViewById(R.id.btn_enter).setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });
    }

    private void fetchAndPlayVideo() {
        new Thread(() -> {
            String videoUrl = "";
            try {
                String base = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE)
                        .getString(Constants.PREF_BASE_URL, Constants.BASE_URL);
                String assetsUrl = base + "assets.php";

                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(5, java.util.concurrent.TimeUnit.SECONDS)
                        .readTimeout(5, java.util.concurrent.TimeUnit.SECONDS)
                        .build();
                Response res = client.newCall(
                        new Request.Builder().url(assetsUrl).build()).execute();
                if (res.isSuccessful() && res.body() != null) {
                    JSONObject json = new JSONObject(res.body().string());
                    videoUrl = json.optString("video_url", "");
                    VideoUrlCache.set(videoUrl); // cache for other activities
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            final String url = videoUrl;
            runOnUiThread(() -> {
                if (!url.isEmpty()) videoView.setVideoUrl(url);
            });
        }).start();
    }

    @Override protected void onPause()  { super.onPause();  videoView.pause(); }
    @Override protected void onResume() { super.onResume(); videoView.resume(); }
    @Override protected void onDestroy(){ super.onDestroy(); videoView.release(); }
}
