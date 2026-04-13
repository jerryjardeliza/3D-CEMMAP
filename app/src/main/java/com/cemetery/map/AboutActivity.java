package com.cemetery.map;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.cemetery.map.utils.Constants;

import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("About Us");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ProgressBar progress = findViewById(R.id.progress_bar);
        TextView tvContent   = findViewById(R.id.tv_content);
        progress.setVisibility(View.VISIBLE);

        new Thread(() -> {
            String content = "";
            try {
                String base = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE)
                        .getString(Constants.PREF_BASE_URL, Constants.BASE_URL);
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(8, java.util.concurrent.TimeUnit.SECONDS)
                        .readTimeout(8, java.util.concurrent.TimeUnit.SECONDS)
                        .build();
                Response res = client.newCall(
                        new Request.Builder().url(base + "about.php").build()).execute();
                if (res.isSuccessful() && res.body() != null) {
                    JSONObject json = new JSONObject(res.body().string());
                    content = json.optString("content", "");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            final String finalContent = content.isEmpty()
                    ? "Hda. Estrella Cemetery\nBarangay XIV, Victorias City\n\nA 3D digital mapping system for cemetery plot management and burial records."
                    : content;

            runOnUiThread(() -> {
                progress.setVisibility(View.GONE);
                tvContent.setText(finalContent);
            });
        }).start();
    }

    @Override public boolean onSupportNavigateUp() { finish(); return true; }
}
