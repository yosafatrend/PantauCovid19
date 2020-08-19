package com.spect.pantaucovid19;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Provinsi {

    @SerializedName("attributes")
    @Expose
    private Attributes attributes;

    public Attributes getAttributes() {
        return attributes;
    }

    @Override
    public String toString() {
        return "Provinsi{" +
                "attributes=" + attributes +
                '}';
    }
}