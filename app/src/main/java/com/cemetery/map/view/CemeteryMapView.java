package com.cemetery.map.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.cemetery.map.model.MarkerData;
import com.google.gson.Gson;

import java.util.List;

/**
 * WebView-based 3D map view.
 * Loads a local HTML page that uses Three.js to render the GLB and markers.
 * Communicates with Java via a JavaScript interface.
 */
public class CemeteryMapView extends WebView {

    public interface OnMarkerClickListener {
        void onMarkerClick(MarkerData marker);
    }

    private OnMarkerClickListener clickListener;
    private boolean               pageReady = false;
    private List<MarkerData>      pendingMarkers;
    private String                pendingGlbUrl;

    public CemeteryMapView(Context context) {
        super(context);
        init(context);
    }

    public CemeteryMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    private void init(Context context) {
        WebSettings s = getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setAllowFileAccessFromFileURLs(true);
        s.setAllowUniversalAccessFromFileURLs(true);
        s.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        s.setCacheMode(WebSettings.LOAD_DEFAULT);

        setWebChromeClient(new WebChromeClient());
        setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                pageReady = true;
                // Flush any pending calls
                if (pendingGlbUrl != null)    loadGlb(pendingGlbUrl);
                if (pendingMarkers != null)   setMarkers(pendingMarkers);
            }
        });

        // JS → Java bridge
        addJavascriptInterface(new JsBridge(), "Android");

        // Load the bundled map HTML from assets
        loadUrl("file:///android_asset/map.html");
    }

    public void setOnMarkerClickListener(OnMarkerClickListener l) {
        this.clickListener = l;
    }

    public void loadGlb(String url) {
        if (!pageReady) { pendingGlbUrl = url; return; }
        pendingGlbUrl = null;
        post(() -> evaluateJavascript("loadGlb('" + url + "')", null));
    }

    public void setMarkers(List<MarkerData> markers) {
        if (!pageReady) { pendingMarkers = markers; return; }
        pendingMarkers = null;
        String json = new Gson().toJson(markers);
        // Escape for JS string
        json = json.replace("\\", "\\\\").replace("'", "\\'");
        final String js = "setMarkers('" + json + "')";
        post(() -> evaluateJavascript(js, null));
    }

    public void mapZoomIn()      { post(() -> evaluateJavascript("mapZoom(1)", null)); }
    public void mapZoomOut()     { post(() -> evaluateJavascript("mapZoom(-1)", null)); }
    public void mapResetCamera() { post(() -> evaluateJavascript("resetCamera()", null)); }

    /** Called from JavaScript when a marker is tapped */
    private class JsBridge {
        @JavascriptInterface
        public void onMarkerClick(String markerJson) {
            if (clickListener == null) return;
            MarkerData marker = new Gson().fromJson(markerJson, MarkerData.class);
            post(() -> clickListener.onMarkerClick(marker));
        }
    }
}
