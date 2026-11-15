package com.example.thuvien;

public class Sach {

    public String maSach;
    public String tenSach;
    public String tacGia;
    public String theLoai;
    public int namXuatBan;
    public String ngayNhap;
    public String ngayMuon;
    public String ngayTra;
    public String trangThai;

    public Sach(String maSach, String tenSach,
                String tacGia, String theLoai,
                int namXuatBan, String ngayNhap,
                String ngayMuon, String ngayTra,
                String trangThai) {

        this.maSach = maSach;
        this.tenSach = tenSach;
        this.tacGia = tacGia;
        this.theLoai = theLoai;
        this.namXuatBan = namXuatBan;
        this.ngayNhap = ngayNhap;
        this.ngayMuon = ngayMuon;
        this.ngayTra = ngayTra;
        this.trangThai = trangThai;
    }
}