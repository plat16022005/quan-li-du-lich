package com.example.layout.utils;

/**
 * Hằng số vai trò người dùng trong hệ thống.
 * Áp dụng OCP: thêm vai trò mới chỉ cần thêm constant ở đây,
 * không phải sửa rải rác logic kiểm tra trong nhiều service/controller.
 */
public final class VaiTroConstants {

    private VaiTroConstants() {
        // Utility class – không được khởi tạo
    }

    /** Quản lý (Admin tổng) */
    public static final int ADMIN = 1;

    /** Nhân viên quản lý tour */
    public static final int QUAN_LY_TOUR = 2;

    /** Hướng dẫn viên du lịch */
    public static final int HUONG_DAN_VIEN = 3;

    /** Khách hàng */
    public static final int KHACH_HANG = 4;

    /** Tài xế */
    public static final int TAI_XE = 5;
}
