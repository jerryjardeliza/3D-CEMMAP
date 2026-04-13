package com.cemetery.map.utils;

/** Simple singleton to share the fetched video URL across activities */
public class VideoUrlCache {
    private static String videoUrl = "";

    public static void set(String url) { videoUrl = url != null ? url : ""; }
    public static String get()         { return videoUrl; }
    public static boolean hasUrl()     { return !videoUrl.isEmpty(); }
}
