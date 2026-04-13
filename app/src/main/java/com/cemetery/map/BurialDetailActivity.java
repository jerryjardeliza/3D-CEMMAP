package com.cemetery.map;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.cemetery.map.model.MarkerData;
import com.cemetery.map.model.SearchResult;

public class BurialDetailActivity extends AppCompatActivity {

    public static final String EXTRA_MARKER        = "extra_marker";
    public static final String EXTRA_SEARCH_RESULT = "extra_search_result";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_burial_detail);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Burial Details");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Accept either MarkerData or SearchResult
        if (getIntent().hasExtra(EXTRA_MARKER)) {
            MarkerData m = (MarkerData) getIntent().getSerializableExtra(EXTRA_MARKER);
            if (m != null) populate(
                    m.getDeceasedName(), m.getBirthDate(), m.getDeathDate(),
                    m.getBurialDate(), m.getPlotNumber(), m.getSectionBlock(),
                    m.getStatus(), m.getNotes(), m.getName());
        } else if (getIntent().hasExtra(EXTRA_SEARCH_RESULT)) {
            SearchResult s = (SearchResult) getIntent().getSerializableExtra(EXTRA_SEARCH_RESULT);
            if (s != null) populate(
                    s.getDeceasedName(), s.getBirthDate(), s.getDeathDate(),
                    s.getBurialDate(), s.getPlotNumber(), s.getSectionBlock(),
                    s.getStatus(), s.getNotes(), s.getMarkerName());
        }
    }

    private void populate(String name, String birth, String death, String burial,
                          String plot, String section, String status, String notes,
                          String markerName) {
        set(R.id.tv_name,        name);
        set(R.id.tv_birth_date,  birth);
        set(R.id.tv_death_date,  death);
        set(R.id.tv_burial_date, burial);
        set(R.id.tv_plot_number, plot);
        set(R.id.tv_section,     section);
        set(R.id.tv_status,      status.toUpperCase());
        set(R.id.tv_notes,       notes.isEmpty() ? "No notes." : notes);
        set(R.id.tv_marker_name, markerName);

        // Status color
        int color;
        switch (status) {
            case "occupied": color = 0xFFEF4444; break;
            case "reserved": color = 0xFFF59E0B; break;
            default:         color = 0xFF22C55E; break;
        }
        findViewById(R.id.tv_status).setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(color));
    }

    private void set(int id, String text) {
        ((TextView) findViewById(id)).setText(text);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
