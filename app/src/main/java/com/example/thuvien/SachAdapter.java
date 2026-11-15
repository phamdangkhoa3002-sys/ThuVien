package com.example.thuvien;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class SachAdapter extends RecyclerView.Adapter<SachAdapter.SachViewHolder> {

    private Context context;
    private ArrayList<Sach> danhSachSach;
    private DatabaseHelper databaseHelper;
    private Runnable onDataChanged;

    public SachAdapter(Context context, ArrayList<Sach> danhSachSach,
                       DatabaseHelper databaseHelper, Runnable onDataChanged) {

        this.context = context;
        this.danhSachSach = danhSachSach;
        this.databaseHelper = databaseHelper;
        this.onDataChanged = onDataChanged;
    }

    // ÁNH XẠ GIAO DIỆN ITEM
    public static class SachViewHolder extends RecyclerView.ViewHolder {

        TextView tvTenSach, tvMaSach, tvTacGia, tvTheLoai;
        TextView tvNamXuatBan, tvNgayNhap, tvNgayMuon;
        TextView tvNgayTra, tvTrangThai;

        CheckBox cbChonSach;
        ImageButton btnEditSach, btnDeleteSach;

        public SachViewHolder(@NonNull View itemView) {
            super(itemView);

            cbChonSach = itemView.findViewById(R.id.cbChonSach);

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
        }
    }

    @NonNull
    @Override
    public SachViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_sach, parent, false);

        return new SachViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SachViewHolder holder, int position) {

        Sach sach = danhSachSach.get(position);

        holder.tvTenSach.setText(sach.tenSach);
        holder.tvMaSach.setText("Mã sách: " + sach.maSach);
        holder.tvTacGia.setText("Tác giả: " + sach.tacGia);
        holder.tvTheLoai.setText("Thể loại: " + sach.theLoai);
        holder.tvNamXuatBan.setText("Năm xuất bản: " + sach.namXuatBan);
        holder.tvNgayNhap.setText("Ngày nhập: " + sach.ngayNhap);
        holder.tvNgayMuon.setText("Ngày mượn: " + sach.ngayMuon);
        holder.tvNgayTra.setText("Ngày trả: " + sach.ngayTra);
        holder.tvTrangThai.setText("Trạng thái: " + sach.trangThai);

        // NHẤN ICON SỬA
        holder.btnEditSach.setOnClickListener(v -> {
            hienThiSuaSach(sach);
        });

        // NHẤN ICON XÓA
        holder.btnDeleteSach.setOnClickListener(v -> {
            xacNhanXoaSach(sach);
        });
    }

    @Override
    public int getItemCount() {
        return danhSachSach.size();
    }

    // HIỂN THỊ POPUP CHỈNH SỬA
    private void hienThiSuaSach(Sach sach) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_fix, null);

        EditText edtMaSach = view.findViewById(R.id.edtMaSach);
        EditText edtTenSach = view.findViewById(R.id.edtTenSach);
        EditText edtTacGia = view.findViewById(R.id.edtTacGia);
        EditText edtTheLoai = view.findViewById(R.id.edtTheLoai);
        EditText edtNamXuatBan = view.findViewById(R.id.edtNamXuatBan);

        EditText edtNgayNhap = view.findViewById(R.id.edtNgayNhap);
        EditText edtNgayMuon = view.findViewById(R.id.edtNgayMuon);
        EditText edtNgayTra = view.findViewById(R.id.edtNgayTra);

        Spinner spnTrangThai = view.findViewById(R.id.spnTrangThai);
        Button btnLuuThayDoi = view.findViewById(R.id.btnLuuThayDoi);

        // ĐIỀN THÔNG TIN SÁCH HIỆN TẠI
        edtMaSach.setText(sach.maSach);
        edtTenSach.setText(sach.tenSach);
        edtTacGia.setText(sach.tacGia);
        edtTheLoai.setText(sach.theLoai);
        edtNamXuatBan.setText(String.valueOf(sach.namXuatBan));

        edtNgayNhap.setText(sach.ngayNhap);
        edtNgayMuon.setText(sach.ngayMuon);
        edtNgayTra.setText(sach.ngayTra);

        // DANH SÁCH TRẠNG THÁI
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

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                context,
                android.R.layout.simple_spinner_item,
                trangThai
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spnTrangThai.setAdapter(adapter);

        int viTri = adapter.getPosition(sach.trangThai);

        if (viTri >= 0) {
            spnTrangThai.setSelection(viTri);
        }

        // CHỌN NGÀY
        edtNgayNhap.setOnClickListener(v -> chonNgay(edtNgayNhap));
        edtNgayMuon.setOnClickListener(v -> chonNgay(edtNgayMuon));
        edtNgayTra.setOnClickListener(v -> chonNgay(edtNgayTra));

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(view)
                .create();

        dialog.show();


        Button btnThoat = view.findViewById(R.id.btnThoat);

        btnThoat.setOnClickListener(v -> {
            dialog.dismiss();
        });

        // LƯU THAY ĐỔI
        btnLuuThayDoi.setOnClickListener(v -> {

            String tenSach = edtTenSach.getText().toString().trim();
            String tacGia = edtTacGia.getText().toString().trim();
            String theLoai = edtTheLoai.getText().toString().trim();
            String nam = edtNamXuatBan.getText().toString().trim();

            String ngayNhap = edtNgayNhap.getText().toString().trim();
            String ngayMuon = edtNgayMuon.getText().toString().trim();
            String ngayTra = edtNgayTra.getText().toString().trim();

            if (tenSach.isEmpty() || tacGia.isEmpty()
                    || theLoai.isEmpty() || nam.isEmpty()) {

                Toast.makeText(context,
                        "Vui lòng nhập đầy đủ thông tin!",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            int namXuatBan;

            try {
                namXuatBan = Integer.parseInt(nam);
            } catch (NumberFormatException e) {

                Toast.makeText(context,
                        "Năm xuất bản phải là số hợp lệ!",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            String trangThaiMoi = spnTrangThai.getSelectedItem().toString();

            boolean ketQua = databaseHelper.suaSach(
                    sach.maSach,
                    tenSach,
                    tacGia,
                    theLoai,
                    namXuatBan,
                    ngayNhap,
                    ngayMuon,
                    ngayTra,
                    trangThaiMoi
            );

            if (ketQua) {

                Toast.makeText(context,
                        "Cập nhật sách thành công!",
                        Toast.LENGTH_SHORT).show();

                dialog.dismiss();

                if (onDataChanged != null) {
                    onDataChanged.run();
                }

            } else {

                Toast.makeText(context,
                        "Cập nhật sách thất bại!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // CHỌN NGÀY
    private void chonNgay(EditText editText) {

        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                context,
                (datePicker, year, month, day) -> {

                    String ngay = String.format(
                            Locale.getDefault(),
                            "%02d/%02d/%04d",
                            day,
                            month + 1,
                            year
                    );

                    editText.setText(ngay);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.show();
    }

    // XÁC NHẬN XÓA SÁCH
    private void xacNhanXoaSach(Sach sach) {

        new AlertDialog.Builder(context)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa sách "
                        + sach.tenSach + " không?")
                .setPositiveButton("Xóa", (dialog, which) -> {

                    boolean ketQua = databaseHelper.xoaSach(sach.maSach);

                    if (ketQua) {

                        Toast.makeText(context,
                                "Đã xóa sách thành công!",
                                Toast.LENGTH_SHORT).show();

                        if (onDataChanged != null) {
                            onDataChanged.run();
                        }

                    } else {

                        Toast.makeText(context,
                                "Xóa sách thất bại!",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

}
