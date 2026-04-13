package com.cemetery.map.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class BurialRecord implements Serializable {

    @SerializedName("burial_id")
    private int id;

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
    public int getId()             { return id; }
    public String getDeceasedName(){ return deceasedName != null ? deceasedName : ""; }
    public String getBirthDate()   { return birthDate != null ? birthDate : "—"; }
    public String getDeathDate()   { return deathDate != null ? deathDate : "—"; }
    public String getBurialDate()  { return burialDate != null ? burialDate : "—"; }
    public String getPlotNumber()  { return plotNumber != null ? plotNumber : "—"; }
    public String getSectionBlock(){ return sectionBlock != null ? sectionBlock : "—"; }
    public String getNotes()       { return notes != null ? notes : ""; }
}
