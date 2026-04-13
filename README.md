# Cemetery Map — Android App

## Setup Instructions

### 1. Place the GLB file
Copy your `cemmap_sketchup.glb` into:
```
app/src/main/res/raw/cemetery_model.glb
```
Create the `raw/` folder if it doesn't exist.

### 2. Set your server IP
Open `utils/Constants.java` and update `BASE_URL`:

```java
// Android Emulator → use 10.0.2.2 (maps to your PC's localhost)
public static final String BASE_URL = "http://10.0.2.2/cemetery/api/mobile/";

// Real device on same WiFi → use your PC's LAN IP
public static final String BASE_URL = "http://192.168.1.XXX/cemetery/api/mobile/";
```

### 3. Make sure XAMPP is running
- Apache + MySQL must be running
- The PHP backend must be accessible at the URL above

### 4. Build & Run
Open the `android/` folder in Android Studio, sync Gradle, then Run.

---

## Project Structure

```
app/src/main/java/com/cemetery/map/
├── MainActivity.java          — 3D map screen
├── SearchActivity.java        — Search burials
├── BurialDetailActivity.java  — Full burial details
├── adapter/
│   └── SearchAdapter.java     — RecyclerView adapter
├── model/
│   ├── MarkerData.java        — Plot marker + burial info
│   ├── SearchResult.java      — Search API response item
│   ├── BurialRecord.java      — Burial record model
│   └── ApiResponse.java       — Generic API wrapper
├── network/
│   ├── ApiClient.java         — Retrofit singleton
│   └── ApiService.java        — API endpoint definitions
├── db/
│   └── CacheDbHelper.java     — SQLite offline cache
├── view/
│   ├── CemeteryMapView.java   — Custom SurfaceView (touch handling)
│   └── CemeteryRenderer.java  — Rajawali 3D renderer
└── utils/
    └── Constants.java         — URLs, colors, config
```

## Gestures
| Gesture | Action |
|---|---|
| Single finger drag | Orbit / rotate |
| Pinch | Zoom in/out |
| Two-finger drag | Pan |
| Tap marker | Show burial info |

## API Endpoints Used
| Endpoint | Description |
|---|---|
| `api/mobile/markers.php` | All plot markers with burial info |
| `api/mobile/search.php?name=` | Search by deceased name |
| `api/mobile/assets.php` | GLB + video URLs |

okay
