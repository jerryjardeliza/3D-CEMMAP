package com.cemetery.map.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/** Generic wrapper matching { "success": true, "data": [...] } */
public class ApiResponse<T> {

    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private List<T> data;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message != null ? message : "Unknown error"; }
    public List<T> getData()   { return data; }
}
