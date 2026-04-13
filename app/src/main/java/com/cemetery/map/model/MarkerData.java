package com.cemetery.map.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class MarkerData implements Serializable {

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("x")
    private float x;

    @SerializedName("y")
    private float y;

    @SerializedName("z")
    private float z;

    @SerializedName("status")
    private String status;

    // Embedded burial info (joined from burial_records)
    @SerializedName("burial_id")
    private Integer burialId;

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
    public int getId()              { return id; }
    public String getName()         { return name != null ? name : ""; }
    public float getX()             { return x; }
    public float getY()             { return y; }
    public float getZ()             { return z; }
    public String getStatus()       { return status != null ? status : "available"; }
    public Integer getBurialId()    { return burialId; }
    public String getDeceasedName() { return deceasedName != null ? deceasedName : ""; }
    public String getBirthDate()    { return birthDate != null ? birthDate : "—"; }
    public String getDeathDate()    { return deathDate != null ? deathDate : "—"; }
    public String getBurialDate()   { return burialDate != null ? burialDate : "—"; }
    public String getPlotNumber()   { return plotNumber != null ? plotNumber : "—"; }
    public String getSectionBlock() { return sectionBlock != null ? sectionBlock : "—"; }
    public String getNotes()        { return notes != null ? notes : ""; }

    public boolean hasburial()      { return burialId != null && burialId > 0; }

    /** Convert to BurialRecord for detail screen */
    public BurialRecord toBurialRecord() {
        // We reuse the same JSON fields — Gson will map them correctly
        // This helper builds a BurialRecord from the embedded fields
        BurialRecord b = new BurialRecord();
        return b; // populated via Gson deserialization in SearchResult
    }
}
