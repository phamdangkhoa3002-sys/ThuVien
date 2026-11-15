package com.example.thuvien;

import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

public class Activity2 extends AppCompatActivity {

    Spinner spnLoaiTimKiem;
    EditText edtTuKhoa;
    RecyclerView rvKetQuaTimKiem;
    Button btnQuayLai;

    DatabaseHelper databaseHelper;

    ArrayList<Sach> danhSachSach = new ArrayList<>();
    TimKiemSachAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);

        // 1. Ánh xạ giao diện
        spnLoaiTimKiem = findViewById(R.id.spnLoaiTimKiem);
        edtTuKhoa = findViewById(R.id.edtTuKhoa);
        rvKetQuaTimKiem = findViewById(R.id.rvKetQuaTimKiem);
        btnQuayLai = findViewById(R.id.btnQuayLai);


        // 3. Nút quay lại
        if (btnQuayLai != null) {
            btnQuayLai.setOnClickListener(v -> finish());
        }

        // 4. Khởi tạo Database
        databaseHelper = new DatabaseHelper(this);

        // 5. Cấu hình Spinner
        String[] loaiTimKiem = {
                "Tên sách",
                "Tác giả"
        };

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                loaiTimKiem
        );

        spinnerAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spnLoaiTimKiem.setAdapter(spinnerAdapter);

        // 6. Cấu hình RecyclerView
        rvKetQuaTimKiem.setLayoutManager(
                new LinearLayoutManager(this)
        );

        adapter = new TimKiemSachAdapter(danhSachSach);
        rvKetQuaTimKiem.setAdapter(adapter);

        // 7. Tìm kiếm khi nhập nội dung
        edtTuKhoa.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count) {
                timKiemSach();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // 8. Tìm kiếm khi thay đổi tiêu chí
        spnLoaiTimKiem.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id) {
                        timKiemSach();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                }
        );

        // 9. Hiển thị danh sách ban đầu
        timKiemSach();
    }

    // Hàm tìm kiếm sách
    private void timKiemSach() {

        if (databaseHelper == null || adapter == null) {
            return;
        }

        String tuKhoa = edtTuKhoa.getText()
                .toString()
                .trim()
                .toLowerCase(Locale.ROOT);

        String loai = spnLoaiTimKiem.getSelectedItem() == null
                ? "Tên sách"
                : spnLoaiTimKiem.getSelectedItem().toString();

        danhSachSach.clear();

        Cursor cursor = databaseHelper.getAllSach();

        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {

                    String tenSach = cursor.getString(
                            cursor.getColumnIndexOrThrow("tenSach")
                    );

                    String tacGia = cursor.getString(
                            cursor.getColumnIndexOrThrow("tacGia")
                    );

                    String giaTriTimKiem;

                    if (loai.equals("Tên sách")) {
                        giaTriTimKiem = tenSach;
                    } else {
                        giaTriTimKiem = tacGia;
                    }

                    if (giaTriTimKiem != null &&
                            giaTriTimKiem.toLowerCase(Locale.ROOT)
                                    .contains(tuKhoa)) {

                        Sach sach = new Sach(
                                cursor.getString(cursor.getColumnIndexOrThrow("maSach")),
                                tenSach,
                                tacGia,
                                cursor.getString(cursor.getColumnIndexOrThrow("theLoai")),
                                cursor.getInt(cursor.getColumnIndexOrThrow("namXuatBan")),
                                cursor.getString(cursor.getColumnIndexOrThrow("ngayNhap")),
                                cursor.getString(cursor.getColumnIndexOrThrow("ngayMuon")),
                                cursor.getString(cursor.getColumnIndexOrThrow("ngayTra")),
                                cursor.getString(cursor.getColumnIndexOrThrow("trangThai"))
                        );

                        danhSachSach.add(sach);
                    }
                }

            } finally {
                cursor.close();
            }
        }

        adapter.notifyDataSetChanged();
    }

    // Adapter riêng dành cho màn hình tìm kiếm
    class TimKiemSachAdapter
            extends RecyclerView.Adapter<TimKiemSachAdapter.SachViewHolder> {

        ArrayList<Sach> danhSach;

        TimKiemSachAdapter(ArrayList<Sach> danhSach) {
            this.danhSach = danhSach;
        }

        class SachViewHolder extends RecyclerView.ViewHolder {

            TextView tvTenSach, tvMaSach, tvTacGia, tvTheLoai;
            TextView tvNamXuatBan, tvNgayNhap, tvNgayMuon;
            TextView tvNgayTra, tvTrangThai;

            ImageButton btnEditSach, btnDeleteSach;
            CheckBox cbChonSach;

            SachViewHolder(View itemView) {
                super(itemView);

                tvTenSach = itemView.findViewById(R.id.tvTenSach);
                tvMaSach = itemView.findViewById(R.id.tvMaSach);
                tvTacGia = itemView.findViewById(R.id.tvTacGia);
                tvTheLoai = itemView.findViewById(R.id.tvTheLoai);
                tvNamXuatBan = itemView.findViewById(R.id.tvNamXuatBan);
                tvNgayNhap = itemView.findViewById(R.id.tvNgayNhap);
                tvNgayMuon = itemView.findViewById(R.id.tvNgayMuon);
                tvNgayTra = itemView.findViewById(R.id.tvNgayTra);
                tvTrangThai = itemView.findViewById(R.id.tvTrangThai);

                btnEditSach = itemView.findViewById(R.id.btnEditSach);
                btnDeleteSach = itemView.findViewById(R.id.btnDeleteSach);
                cbChonSach = itemView.findViewById(R.id.cbChonSach);
            }
        }

        @Override
        public SachViewHolder onCreateViewHolder(
                ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_sach, parent, false);

            return new SachViewHolder(view);
        }

        @Override
        public void onBindViewHolder(
                SachViewHolder holder, int position) {

            Sach sach = danhSach.get(position);

            holder.tvTenSach.setText(hienThi(sach.tenSach));
            holder.tvMaSach.setText("Mã sách: " + hienThi(sach.maSach));
            holder.tvTacGia.setText("Tác giả: " + hienThi(sach.tacGia));
            holder.tvTheLoai.setText("Thể loại: " + hienThi(sach.theLoai));
            holder.tvNamXuatBan.setText("Năm xuất bản: " + sach.namXuatBan);
            holder.tvNgayNhap.setText("Ngày nhập: " + hienThi(sach.ngayNhap));
            holder.tvNgayMuon.setText("Ngày mượn: " + hienThi(sach.ngayMuon));
            holder.tvNgayTra.setText("Ngày trả: " + hienThi(sach.ngayTra));
            holder.tvTrangThai.setText(hienThi(sach.trangThai));

            // Ẩn các nút không cần thiết trên màn hình tìm kiếm
            holder.btnEditSach.setVisibility(View.GONE);
            holder.btnDeleteSach.setVisibility(View.GONE);
            holder.cbChonSach.setVisibility(View.GONE);
        }

        @Override
        public int getItemCount() {
            return danhSach.size();
        }

        private String hienThi(String giaTri) {
            if (giaTri == null || giaTri.trim().isEmpty()) {
                return "Chưa cập nhật";
            }

            return giaTri;
        }
    }
}
