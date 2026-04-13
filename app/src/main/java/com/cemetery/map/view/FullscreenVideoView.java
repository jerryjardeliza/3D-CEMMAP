package com.cemetery.map.view;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;

/**
 * A TextureView that plays a video scaled to fill (cover) the entire view,
 * just like CSS background-size: cover.
 */
public class FullscreenVideoView extends TextureView
        implements TextureView.SurfaceTextureListener {

    private MediaPlayer mediaPlayer;
    private String      videoUrl;
    private boolean     surfaceReady = false;

    public FullscreenVideoView(Context context) {
        super(context);
        setSurfaceTextureListener(this);
    }

    public FullscreenVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setSurfaceTextureListener(this);
    }

    public void setVideoUrl(String url) {
        this.videoUrl = url;
        if (surfaceReady) startVideo();
    }

    private void startVideo() {
        if (videoUrl == null || videoUrl.isEmpty()) return;
        try {
            if (mediaPlayer != null) { mediaPlayer.release(); }
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(getContext(), Uri.parse(videoUrl));
            mediaPlayer.setSurface(new Surface(getSurfaceTexture()));
            mediaPlayer.setLooping(true);
            mediaPlayer.setVolume(0f, 0f);
            mediaPlayer.setOnVideoSizeChangedListener((mp, w, h) -> adjustScale(w, h));
            mediaPlayer.setOnPreparedListener(mp -> {
                mp.start();
                adjustScale(mp.getVideoWidth(), mp.getVideoHeight());
            });
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Scale the TextureView so video covers the full screen (like object-fit: cover) */
    private void adjustScale(int videoW, int videoH) {
        if (videoW == 0 || videoH == 0) return;
        post(() -> {
            int viewW = getWidth();
            int viewH = getHeight();
            if (viewW == 0 || viewH == 0) return;

            float scaleX = 1f, scaleY = 1f;
            float videoAspect = (float) videoW / videoH;
            float viewAspect  = (float) viewW  / viewH;

            if (videoAspect > viewAspect) {
                // Video is wider — scale by height, crop sides
                scaleX = videoAspect / viewAspect;
            } else {
                // Video is taller — scale by width, crop top/bottom
                scaleY = viewAspect / videoAspect;
            }

            Matrix matrix = new Matrix();
            matrix.setScale(scaleX, scaleY, viewW / 2f, viewH / 2f);
            setTransform(matrix);
        });
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture st, int w, int h) {
        surfaceReady = true;
        if (videoUrl != null) startVideo();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture st, int w, int h) {
        if (mediaPlayer != null)
            adjustScale(mediaPlayer.getVideoWidth(), mediaPlayer.getVideoHeight());
    }

    @Override public boolean onSurfaceTextureDestroyed(SurfaceTexture st) {
        if (mediaPlayer != null) { mediaPlayer.release(); mediaPlayer = null; }
        surfaceReady = false;
        return true;
    }
    @Override public void onSurfaceTextureUpdated(SurfaceTexture st) {}

    public void pause()  { if (mediaPlayer != null && mediaPlayer.isPlaying()) mediaPlayer.pause(); }
    public void resume() { if (mediaPlayer != null && !mediaPlayer.isPlaying()) mediaPlayer.start(); }
    public void release(){ if (mediaPlayer != null) { mediaPlayer.release(); mediaPlayer = null; } }
}
