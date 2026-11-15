package com.example.thuvien;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import java.util.Calendar;
import java.util.Locale;
import android.database.Cursor;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

public class MainActivity extends AppCompatActivity {


    Toolbar toolbar;
    Spinner spnTrangThai;

    EditText edtMaSach, edtTenSach, edtTacGia;
    EditText edtTheLoai, edtNamXuatBan;
    EditText edtNgayNhap, edtNgayMuon, edtNgayTra;

    Button btnThem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Bật chế độ EdgeToEdge
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_main);

        // ÁNH XẠ CÁC Ô NHẬP THÔNG TIN SÁCH
        edtMaSach = findViewById(R.id.edtMaSach);
        edtTenSach = findViewById(R.id.edtTenSach);
        edtTacGia = findViewById(R.id.edtTacGia);
        edtTheLoai = findViewById(R.id.edtTheLoai);
        edtNamXuatBan = findViewById(R.id.edtNamXuatBan);

        edtNgayNhap = findViewById(R.id.edtNgayNhap);
        edtNgayMuon = findViewById(R.id.edtNgayMuon);
        edtNgayTra = findViewById(R.id.edtNgayTra);

// ÁNH XẠ NÚT THÊM SÁCH
        btnThem = findViewById(R.id.btnThem);

// XỬ LÝ SỰ KIỆN THÊM SÁCH

        btnThem.setOnClickListener(v -> {
            themSach();
        });
        rvDanhSachSach = findViewById(R.id.rvDanhSachSach);

        databaseHelper = new DatabaseHelper(this);

        danhSachSach = new ArrayList<>();

        rvDanhSachSach.setLayoutManager(
                new LinearLayoutManager(this)
        );

        taiDanhSachSach();

        // ÁNH XẠ SPINNER TRẠNG THÁI
        spnTrangThai = findViewById(R.id.spnTrangThai);

        // Danh sách trạng thái sách
        String[] trangThai = {
                "Có sẵn",
                "Đang được mượn",
                "Đã đặt trước",
                "Đã trả",
                "Quá hạn trả",
                "Bị mất",
                "Bị hư hỏng",
                "Ngừng lưu hành"
        };

        // Tạo Adapter cho Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                trangThai
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        // Gắn Adapter vào Spinner
        spnTrangThai.setAdapter(adapter);

        // ÁNH XẠ TOOLBAR
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // ĐIỀU CHỈNH KHOẢNG CÁCH THANH TRẠNG THÁI
        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {

                    Insets systemBars = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars()
                    );

                    v.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );

                    return insets;
                }
        );

        // ÁNH XẠ CÁC Ô CHỌN NGÀY
        edtNgayNhap = findViewById(R.id.edtNgayNhap);
        edtNgayMuon = findViewById(R.id.edtNgayMuon);
        edtNgayTra = findViewById(R.id.edtNgayTra);

        // CHỌN NGÀY NHẬP SÁCH
        edtNgayNhap.setOnClickListener(v ->
                chonNgay(edtNgayNhap)
        );

        // CHỌN NGÀY MƯỢN SÁCH
        edtNgayMuon.setOnClickListener(v ->
                chonNgay(edtNgayMuon)
        );

        // CHỌN NGÀY TRẢ SÁCH
        edtNgayTra.setOnClickListener(v ->
                chonNgay(edtNgayTra)
        );
    }

    // HIỂN THỊ MENU TRÊN TOOLBAR
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.toolbar_menu, menu);

        return true;
    }

    // XỬ LÝ CHỨC NĂNG MENU
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        // CHỨC NĂNG TÌM KIẾM
        if (id == R.id.menuTimKiem) {

            Intent intent = new Intent(
                    MainActivity.this,
                    Activity2.class
            );

            startActivity(intent);

            return true;
        }

        // CHỨC NĂNG LỌC DANH SÁCH
        else if (id == R.id.menuLoc) {

            Intent intent = new Intent(
                    MainActivity.this,
                    Activity3.class
            );

            startActivity(intent);

            return true;
        }

        // CHỨC NĂNG THOÁT
        else if (id == R.id.menuThoat) {

            xacNhanThoat();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private RecyclerView rvDanhSachSach;
    private ArrayList<Sach> danhSachSach;
    private SachAdapter sachAdapter;
    private DatabaseHelper databaseHelper;


    // CHỨC NĂNG THÊM SÁCH
    private void themSach() {

        // LẤY DỮ LIỆU TỪ CÁC Ô NHẬP
        String maSach = edtMaSach.getText().toString().trim();
        String tenSach = edtTenSach.getText().toString().trim();
        String tacGia = edtTacGia.getText().toString().trim();
        String theLoai = edtTheLoai.getText().toString().trim();
        String nam = edtNamXuatBan.getText().toString().trim();

        String ngayNhap = edtNgayNhap.getText().toString().trim();
        String ngayMuon = edtNgayMuon.getText().toString().trim();
        String ngayTra = edtNgayTra.getText().toString().trim();

        // KIỂM TRA THÔNG TIN BẮT BUỘC
        if (maSach.isEmpty() || tenSach.isEmpty()
                || tacGia.isEmpty() || theLoai.isEmpty()
                || nam.isEmpty() || ngayNhap.isEmpty()) {

            Toast.makeText(this,
                    "Vui lòng nhập đầy đủ thông tin sách!",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // KIỂM TRA NĂM XUẤT BẢN
        int namXuatBan;

        try {
            namXuatBan = Integer.parseInt(nam);

            if (namXuatBan <= 0) {
                Toast.makeText(this,
                        "Năm xuất bản không hợp lệ!",
                        Toast.LENGTH_SHORT).show();
                return;
            }

        } catch (NumberFormatException e) {

            Toast.makeText(this,
                    "Năm xuất bản phải là số!",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // LẤY TRẠNG THÁI SÁCH
        String trangThai = spnTrangThai.getSelectedItem().toString();

        // THÊM SÁCH VÀO DATABASE
        boolean ketQua = databaseHelper.themSach(
                maSach,
                tenSach,
                tacGia,
                theLoai,
                namXuatBan,
                ngayNhap,
                ngayMuon,
                ngayTra,
                trangThai
        );

        if (ketQua) {

            Toast.makeText(this,
                    "Thêm sách thành công!",
                    Toast.LENGTH_SHORT).show();

            // CẬP NHẬT DANH SÁCH SÁCH
            taiDanhSachSach();

            // XÓA DỮ LIỆU SAU KHI THÊM
            edtMaSach.setText("");
            edtTenSach.setText("");
            edtTacGia.setText("");
            edtTheLoai.setText("");
            edtNamXuatBan.setText("");

            edtNgayNhap.setText("");
            edtNgayMuon.setText("");
            edtNgayTra.setText("");

            // ĐƯA CON TRỎ VỀ Ô MÃ SÁCH
            edtMaSach.requestFocus();

        } else {

            Toast.makeText(this,
                    "Thêm sách thất bại! Mã sách có thể đã tồn tại.",
                    Toast.LENGTH_SHORT).show();
        }
    }


    // HIỂN THỊ HỘP THOẠI XÁC NHẬN THOÁT
    private void xacNhanThoat() {

        new AlertDialog.Builder(this)
                .setTitle("Xác nhận thoát")
                .setMessage("Bạn có chắc chắn muốn thoát chương trình không?")

                // NẾU CHỌN THOÁT
                .setPositiveButton("Thoát", (dialog, which) -> {

                    Toast.makeText(
                            MainActivity.this,
                            "Đã thoát chương trình",
                            Toast.LENGTH_SHORT
                    ).show();

                    finish();
                })

                // NẾU CHỌN HỦY
                .setNegativeButton("Hủy", (dialog, which) -> {

                    dialog.dismiss();
                })

                .show();
    }


    // HIỂN THỊ LỊCH CHỌN NGÀY
    private void chonNgay(EditText editText) {

        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,

                (view, selectedYear, selectedMonth, selectedDay) -> {

                    String ngay = String.format(
                            Locale.getDefault(),
                            "%02d/%02d/%04d",
                            selectedDay,
                            selectedMonth + 1,
                            selectedYear
                    );

                    editText.setText(ngay);
                },

                year,
                month,
                day
        );

        datePickerDialog.show();
    }
    private void hienThiSuaSach() {

        View view = LayoutInflater.from(this)
                .inflate(R.layout.item_fix, null);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();

        dialog.show();
    }
    // ĐỌC DANH SÁCH SÁCH TỪ DATABASE
    private void taiDanhSachSach() {

        danhSachSach.clear();

        Cursor cursor = databaseHelper.getAllSach();

        if (cursor != null) {

            while (cursor.moveToNext()) {

                Sach sach = new Sach(
                        cursor.getString(cursor.getColumnIndexOrThrow("maSach")),
                        cursor.getString(cursor.getColumnIndexOrThrow("tenSach")),
                        cursor.getString(cursor.getColumnIndexOrThrow("tacGia")),
                        cursor.getString(cursor.getColumnIndexOrThrow("theLoai")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("namXuatBan")),
                        cursor.getString(cursor.getColumnIndexOrThrow("ngayNhap")),
                        cursor.getString(cursor.getColumnIndexOrThrow("ngayMuon")),
                        cursor.getString(cursor.getColumnIndexOrThrow("ngayTra")),
                        cursor.getString(cursor.getColumnIndexOrThrow("trangThai"))
                );

                danhSachSach.add(sach);
            }

            cursor.close();
        }

        sachAdapter = new SachAdapter(
                this,
                danhSachSach,
                databaseHelper,
                this::taiDanhSachSach
        );

        rvDanhSachSach.setAdapter(sachAdapter);
    }


}
