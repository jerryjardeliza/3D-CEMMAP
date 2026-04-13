package com.cemetery.map.network;

import com.cemetery.map.model.ApiResponse;
import com.cemetery.map.model.MarkerData;
import com.cemetery.map.model.SearchResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    /** All plot markers with burial info — from site_assets + plot_markers tables */
    @GET("markers.php")
    Call<ApiResponse<MarkerData>> getMarkers();

    /** Search deceased by name */
    @GET("search.php")
    Call<ApiResponse<SearchResult>> searchBurials(@Query("name") String name);
}
