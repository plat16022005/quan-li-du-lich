package com.example.layout.utils;

/**
 * Utility class để kiểm tra trạng thái chuyến du lịch.
 * Áp dụng OCP: logic tập trung tại một nơi, các service chỉ cần gọi helper này.
 */
public final class TripStatusChecker {

    private TripStatusChecker() {
        // Utility class – không được khởi tạo
    }

    /**
     * Kiểm tra xem trạng thái chuyến đi có phải "đã hoàn thành" hay không.
     */
    public static boolean isCompleted(String trangThai) {
        if (trangThai == null) return false;
        return trangThai.contains("hoàn")
                || trangThai.equalsIgnoreCase("Finished")
                || trangThai.equalsIgnoreCase("Đã hoàn thành");
    }

    /**
     * Kiểm tra xem trạng thái có phải là đang diễn ra hoặc đã kết thúc (không thể hủy) không.
     */
    public static boolean isNonCancellable(String trangThai) {
        return "Đang diễn ra".equals(trangThai)
                || "Đã hoàn thành".equals(trangThai)
                || "Đã kết thúc".equals(trangThai);
    }
}
