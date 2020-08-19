package com.spect.pantaucovid19;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Attributes {
    @SerializedName("FID")
    @Expose
    private String fID;
    @SerializedName("Kode_Provi")
    @Expose
    private String kodeProvi;
    @SerializedName("Provinsi")
    @Expose
    private String provinsi;
    @SerializedName("Kasus_Posi")
    @Expose
    private String kasusPosi;
    @SerializedName("Kasus_Semb")
    @Expose
    private String kasusSemb;
    @SerializedName("Kasus_Meni")
    @Expose
    private String kasusMeni;

    public String getfID() {
        return fID;
    }

    public String getKodeProvi() {
        return kodeProvi;
    }

    public String getProvinsi() {
        return provinsi;
    }

    public String getKasusPosi() {
        return kasusPosi;
    }

    public String getKasusSemb() {
        return kasusSemb;
    }

    public String getKasusMeni() {
        return kasusMeni;
    }

    @Override
    public String toString() {
        return "Attributes{" +
                "fID=" + fID +
                ", kodeProvi=" + kodeProvi +
                ", provinsi='" + provinsi + '\'' +
                ", kasusPosi=" + kasusPosi +
                ", kasusSemb=" + kasusSemb +
                ", kasusMeni=" + kasusMeni +
                '}';
    }
}
