package com.cemetery.map.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

/** Returned by search.php — same shape as MarkerData but marker_id field name differs */
public class SearchResult implements Serializable {

    @SerializedName("marker_id")
    private Integer markerId;

    @SerializedName("marker_name")
    private String markerName;

    @SerializedName("x")
    private float x;

    @SerializedName("y")
    private float y;

    @SerializedName("z")
    private float z;

    @SerializedName("status")
    private String status;

    @SerializedName("burial_id")
    private int burialId;

    @SerializedName("deceased_name")
    private String deceasedName;

    @SerializedName("birth_date")
    private String birthDate;

    @SerializedName("death_date")
    private String deathDate;

    @SerializedName("burial_date")
    private String burialDate;

    @SerializedName("plot_number")
    private String plotNumber;

    @SerializedName("section_block")
    private String sectionBlock;

    @SerializedName("notes")
    private String notes;

    // Getters
    public Integer getMarkerId()    { return markerId; }
    public String getMarkerName()   { return markerName != null ? markerName : ""; }
    public float getX()             { return x; }
    public float getY()             { return y; }
    public float getZ()             { return z; }
    public String getStatus()       { return status != null ? status : "available"; }
    public int getBurialId()        { return burialId; }
    public String getDeceasedName() { return deceasedName != null ? deceasedName : ""; }
    public String getBirthDate()    { return birthDate != null ? birthDate : "—"; }
    public String getDeathDate()    { return deathDate != null ? deathDate : "—"; }
    public String getBurialDate()   { return burialDate != null ? burialDate : "—"; }
    public String getPlotNumber()   { return plotNumber != null ? plotNumber : "—"; }
    public String getSectionBlock() { return sectionBlock != null ? sectionBlock : "—"; }
    public String getNotes()        { return notes != null ? notes : ""; }

    public boolean hasMarker()      { return markerId != null && markerId > 0; }
}
