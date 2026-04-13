package com.cemetery.map.utils;

public class Constants {

    // ── CHANGE THIS to match your setup ──────────────────────────────────────
    // Android Emulator  → use 10.0.2.2
    // Real device (WiFi)→ use your PC's LAN IP (run ipconfig in CMD to find it)
    // Both phone and PC must be on the SAME WiFi network
    public static final String BASE_URL = "http://192.168.12.79/cemetery/api/mobile/";

    // Endpoints (relative to BASE_URL)
    public static final String ENDPOINT_MARKERS = "markers.php";
    public static final String ENDPOINT_SEARCH  = "search.php";
    public static final String ENDPOINT_ASSETS  = "assets.php";

    // Marker status
    public static final String STATUS_OCCUPIED  = "occupied";
    public static final String STATUS_AVAILABLE = "available";
    public static final String STATUS_RESERVED  = "reserved";

    // Marker colors (ARGB int)
    public static final int COLOR_OCCUPIED  = 0xFFEF4444;
    public static final int COLOR_AVAILABLE = 0xFF22C55E;
    public static final int COLOR_RESERVED  = 0xFFF59E0B;

    // SQLite
    public static final String DB_NAME    = "cemetery_cache.db";
    public static final int    DB_VERSION = 1;

    // SharedPreferences
    public static final String PREFS_NAME    = "cemetery_prefs";
    public static final String PREF_BASE_URL = "base_url";
    public static final String PREF_DARK_MODE = "dark_mode";
}
