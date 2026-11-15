
package com.example.thuvien;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Activity3 extends AppCompatActivity {

    // Khai báo giao diện
    Spinner spnLoaiLoc, spnGiaTriLoc;
    Button btnQuayLai;
    RecyclerView rvKetQuaLoc;

    // Khai báo database
    DatabaseHelper databaseHelper;

    // Danh sách sách gốc và danh sách kết quả
    ArrayList<Sach> danhSachSach = new ArrayList<>();
    ArrayList<String> danhSachGiaTri = new ArrayList<>();

    // Adapter hiển thị kết quả lọc
    LocSachAdapter adapter;

    String loaiLoc = "Thể loại";

    // Danh sách trạng thái giống MainActivity
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_3);

        // Ánh xạ giao diện
        spnLoaiLoc = findViewById(R.id.spnLoaiLoc);
        spnGiaTriLoc = findViewById(R.id.spnGiaTriLoc);
        rvKetQuaLoc = findViewById(R.id.rvKetQuaLoc);
        btnQuayLai = findViewById(R.id.btnQuayLai);

        // Nút quay lại màn hình chính
        btnQuayLai.setOnClickListener(v -> finish());

        // Khởi tạo database
        databaseHelper = new DatabaseHelper(this);

        // Thiết lập RecyclerView
        rvKetQuaLoc.setLayoutManager(new LinearLayoutManager(this));

        adapter = new LocSachAdapter(new ArrayList<>());
        rvKetQuaLoc.setAdapter(adapter);

        // Đọc danh sách sách từ database
        docDanhSachSach();

        // Spinner chọn loại lọc
        String[] loaiLocArray = {
                "Thể loại",
                "Trạng thái"
        };

        ArrayAdapter<String> loaiAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                loaiLocArray
        );

        loaiAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spnLoaiLoc.setAdapter(loaiAdapter);

        // Khi chọn giá trị lọc
        spnGiaTriLoc.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id) {

                        locDanhSachSach();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                }
        );

        // Khi thay đổi loại lọc
        spnLoaiLoc.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id) {

                        loaiLoc = parent.getItemAtPosition(position).toString();

                        capNhatSpinnerGiaTri();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                }
        );
    }

    // Đọc danh sách sách từ database
    private void docDanhSachSach() {

        danhSachSach.clear();

        Cursor cursor = databaseHelper.getAllSach();

        if (cursor != null) {

            while (cursor.moveToNext()) {

                String maSach = cursor.getString(
                        cursor.getColumnIndexOrThrow("maSach"));

                String tenSach = cursor.getString(
                        cursor.getColumnIndexOrThrow("tenSach"));

                String tacGia = cursor.getString(
                        cursor.getColumnIndexOrThrow("tacGia"));

                String theLoai = cursor.getString(
                        cursor.getColumnIndexOrThrow("theLoai"));

                int namXuatBan = cursor.getInt(
                        cursor.getColumnIndexOrThrow("namXuatBan"));

                String ngayNhap = cursor.getString(
                        cursor.getColumnIndexOrThrow("ngayNhap"));

                String ngayMuon = cursor.getString(
                        cursor.getColumnIndexOrThrow("ngayMuon"));

                String ngayTra = cursor.getString(
                        cursor.getColumnIndexOrThrow("ngayTra"));

                String trangThaiSach = cursor.getString(
                        cursor.getColumnIndexOrThrow("trangThai"));

                Sach sach = new Sach(
                        maSach,
                        tenSach,
                        tacGia,
                        theLoai,
                        namXuatBan,
                        ngayNhap,
                        ngayMuon,
                        ngayTra,
                        trangThaiSach
                );

                danhSachSach.add(sach);
            }

            cursor.close();
        }
    }

    // Cập nhật Spinner giá trị theo loại lọc
    private void capNhatSpinnerGiaTri() {

        danhSachGiaTri.clear();

        if (loaiLoc.equals("Thể loại")) {

            // Lấy các thể loại có trong database
            for (Sach sach : danhSachSach) {

                if (sach.theLoai != null
                        && !sach.theLoai.trim().isEmpty()
                        && !danhSachGiaTri.contains(sach.theLoai)) {

                    danhSachGiaTri.add(sach.theLoai);
                }
            }

            if (danhSachGiaTri.isEmpty()) {
                danhSachGiaTri.add("Không có thể loại");
            }

        } else {

            // Lấy tám trạng thái giống MainActivity
            for (String trangThaiItem : trangThai) {
                danhSachGiaTri.add(trangThaiItem);
            }
        }

        ArrayAdapter<String> giaTriAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                danhSachGiaTri
        );

        giaTriAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spnGiaTriLoc.setAdapter(giaTriAdapter);
    }

    // Lọc danh sách sách
    private void locDanhSachSach() {

        if (spnGiaTriLoc.getSelectedItem() == null) {
            adapter.capNhatDanhSach(new ArrayList<>());
            return;
        }

        String giaTri = spnGiaTriLoc.getSelectedItem().toString();

        ArrayList<Sach> ketQua = new ArrayList<>();

        // Duyệt danh sách gốc, không xóa dữ liệu ban đầu
        for (Sach sach : danhSachSach) {

            if (loaiLoc.equals("Thể loại")) {

                if (sach.theLoai != null
                        && sach.theLoai.equalsIgnoreCase(giaTri)) {

                    ketQua.add(sach);
                }

            } else {

                if (sach.trangThai != null
                        && sach.trangThai.equalsIgnoreCase(giaTri)) {

                    ketQua.add(sach);
                }
            }
        }

        adapter.capNhatDanhSach(ketQua);
    }

    // Hiển thị giá trị an toàn, tránh chữ null
    private String hienThi(String giaTri) {
        return giaTri == null || giaTri.trim().isEmpty()
                ? "Chưa cập nhật"
                : giaTri;
    }

    // Adapter riêng cho màn hình lọc
    public class LocSachAdapter
            extends RecyclerView.Adapter<LocSachAdapter.SachViewHolder> {

        ArrayList<Sach> danhSach;

        public LocSachAdapter(ArrayList<Sach> danhSach) {
            this.danhSach = danhSach;
        }

        @NonNull
        @Override
        public SachViewHolder onCreateViewHolder(
                @NonNull ViewGroup parent,
                int viewType) {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_sach, parent, false);

            return new SachViewHolder(view);
        }

        @Override
        public void onBindViewHolder(
                @NonNull SachViewHolder holder,
                int position) {

            Sach sach = danhSach.get(position);

            holder.tvTenSach.setText(hienThi(sach.tenSach));
            holder.tvMaSach.setText("Mã sách: " + hienThi(sach.maSach));
            holder.tvTacGia.setText("Tác giả: " + hienThi(sach.tacGia));
            holder.tvTheLoai.setText("Thể loại: " + hienThi(sach.theLoai));

            holder.tvNamXuatBan.setText(
                    "Năm xuất bản: " + sach.namXuatBan);

            holder.tvNgayNhap.setText(
                    "Ngày nhập: " + hienThi(sach.ngayNhap));

            holder.tvNgayMuon.setText(
                    "Ngày mượn: " + hienThi(sach.ngayMuon));

            holder.tvNgayTra.setText(
                    "Ngày trả: " + hienThi(sach.ngayTra));

            holder.tvTrangThai.setText(
                    "Trạng thái: " + hienThi(sach.trangThai));

            // Ẩn checkbox, sửa và xóa vì đây là màn hình xem kết quả
            holder.cbChonSach.setVisibility(View.GONE);
            holder.btnEditSach.setVisibility(View.GONE);
            holder.btnDeleteSach.setVisibility(View.GONE);
        }

        @Override
        public int getItemCount() {
            return danhSach.size();
        }

        // Cập nhật danh sách kết quả lọc
        public void capNhatDanhSach(ArrayList<Sach> ketQua) {

            danhSach.clear();
            danhSach.addAll(ketQua);

            notifyDataSetChanged();
        }

        public class SachViewHolder extends RecyclerView.ViewHolder {

            TextView tvTenSach, tvMaSach, tvTacGia, tvTheLoai;
            TextView tvNamXuatBan, tvNgayNhap, tvNgayMuon;
            TextView tvNgayTra, tvTrangThai;

            CheckBox cbChonSach;
            ImageButton btnEditSach, btnDeleteSach;

            public SachViewHolder(@NonNull View itemView) {
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

                cbChonSach = itemView.findViewById(R.id.cbChonSach);
                btnEditSach = itemView.findViewById(R.id.btnEditSach);
                btnDeleteSach = itemView.findViewById(R.id.btnDeleteSach);
            }
        }
    }
}
