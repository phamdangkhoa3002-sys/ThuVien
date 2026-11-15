package com.example.thuvien;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // THÔNG TIN DATABASE
    private static final String DATABASE_NAME = "QuanLySach.db";
    private static final int DATABASE_VERSION = 1;

    // TÊN BẢNG
    private static final String TABLE_SACH = "Sach";

    // TÊN CÁC CỘT
    private static final String COL_MASACH = "maSach";
    private static final String COL_TENSACH = "tenSach";
    private static final String COL_TACGIA = "tacGia";
    private static final String COL_THELOAI = "theLoai";
    private static final String COL_NAMXUATBAN = "namXuatBan";
    private static final String COL_NGAYNHAP = "ngayNhap";
    private static final String COL_NGAYMUON = "ngayMuon";
    private static final String COL_NGAYTRA = "ngayTra";
    private static final String COL_TRANGTHAI = "trangThai";

    // KHỞI TẠO DATABASE
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // TẠO BẢNG SÁCH
    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql = "CREATE TABLE " + TABLE_SACH + " (" +
                COL_MASACH + " TEXT PRIMARY KEY, " +
                COL_TENSACH + " TEXT, " +
                COL_TACGIA + " TEXT, " +
                COL_THELOAI + " TEXT, " +
                COL_NAMXUATBAN + " INTEGER, " +
                COL_NGAYNHAP + " TEXT, " +
                COL_NGAYMUON + " TEXT, " +
                COL_NGAYTRA + " TEXT, " +
                "soLuongTong INTEGER, " +
                "soLuongCon INTEGER, " +
                COL_TRANGTHAI + " TEXT)";

        db.execSQL(sql);
    }

    // NÂNG CẤP DATABASE
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Chưa có thay đổi cấu trúc bảng nên không cần xóa dữ liệu.
        // Khi thay đổi cấu trúc database, thực hiện nâng cấp tại đây.
    }

    // THÊM SÁCH
    public boolean themSach(String maSach, String tenSach,
                            String tacGia, String theLoai,
                            int namXuatBan, String ngayNhap,
                            String ngayMuon, String ngayTra,
                            String trangThai) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COL_MASACH, maSach);
        values.put(COL_TENSACH, tenSach);
        values.put(COL_TACGIA, tacGia);
        values.put(COL_THELOAI, theLoai);
        values.put(COL_NAMXUATBAN, namXuatBan);
        values.put(COL_NGAYNHAP, ngayNhap);
        values.put(COL_NGAYMUON, ngayMuon);
        values.put(COL_NGAYTRA, ngayTra);
        values.put(COL_TRANGTHAI, trangThai);

        long result = db.insert(TABLE_SACH, null, values);

        return result != -1;
    }

    // CẬP NHẬT SÁCH
    public boolean suaSach(String maSach, String tenSach,
                           String tacGia, String theLoai,
                           int namXuatBan, String ngayNhap,
                           String ngayMuon, String ngayTra,
                           String trangThai) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COL_TENSACH, tenSach);
        values.put(COL_TACGIA, tacGia);
        values.put(COL_THELOAI, theLoai);
        values.put(COL_NAMXUATBAN, namXuatBan);
        values.put(COL_NGAYNHAP, ngayNhap);
        values.put(COL_NGAYMUON, ngayMuon);
        values.put(COL_NGAYTRA, ngayTra);
        values.put(COL_TRANGTHAI, trangThai);

        int result = db.update(
                TABLE_SACH,
                values,
                COL_MASACH + " = ?",
                new String[]{maSach}
        );

        return result > 0;
    }


    // XÓA SÁCH
    public boolean xoaSach(String maSach) {

        SQLiteDatabase db = this.getWritableDatabase();

        int result = db.delete(
                TABLE_SACH,
                COL_MASACH + " = ?",
                new String[]{maSach}
        );

        return result > 0;
    }

    // LẤY TẤT CẢ SÁCH
    public Cursor getAllSach() {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM " + TABLE_SACH,
                null
        );
    }

    // TÌM KIẾM SÁCH THEO MÃ, TÊN HOẶC TÁC GIẢ
    public Cursor timKiemSach(String tuKhoa) {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM " + TABLE_SACH +
                        " WHERE " + COL_MASACH + " LIKE ?" +
                        " OR " + COL_TENSACH + " LIKE ?" +
                        " OR " + COL_TACGIA + " LIKE ?",

                new String[]{
                        "%" + tuKhoa + "%",
                        "%" + tuKhoa + "%",
                        "%" + tuKhoa + "%"
                }
        );
    }

    // LỌC SÁCH THEO TRẠNG THÁI
    public Cursor locSach(String trangThai) {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM " + TABLE_SACH +
                        " WHERE " + COL_TRANGTHAI + " = ?",

                new String[]{trangThai}
        );
    }
}
