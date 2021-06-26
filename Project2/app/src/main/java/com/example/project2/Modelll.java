package com.example.project2;
public class Modelll {
    String nama, kategori, alamat, purl;

    Modelll(){}

    public Modelll(String nama, String kategori, String alamat, String purl) {
        this.nama = nama;
        this.kategori = kategori;
        this.alamat = alamat;
        this.purl = purl;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getPurl() {
        return purl;
    }

    public void setPurl(String purl) {
        this.purl = purl;
    }
}
