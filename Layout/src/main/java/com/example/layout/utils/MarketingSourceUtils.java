package com.example.layout.utils;

/**
 * Utility class cung cấp nhãn hiển thị cho nguồn khách hàng.
 * Áp dụng DRY: tập trung logic chuyển đổi key → label tại một nơi,
 * tránh trùng lặp giữa ExcelExportServiceImpl và PdfExportServiceImpl.
 */
public final class MarketingSourceUtils {

    private MarketingSourceUtils() {
        // Utility class – không được khởi tạo
    }

    /**
     * Trả về nhãn tiếng Việt cho nguồn khách hàng.
     *
     * @param key khóa nguồn (vd: "friend", "facebook"...)
     * @return chuỗi nhãn hiển thị tiếng Việt
     */
    public static String getLabel(String key) {
        return switch (key) {
            case "friend"   -> "Người quen giới thiệu";
            case "facebook" -> "Facebook";
            case "tiktok"   -> "TikTok";
            case "google"   -> "Tìm kiếm trên Google";
            case "youtube"  -> "Quảng cáo YouTube";
            case "website"  -> "Website/Blog khác";
            default         -> "Khác";
        };
    }
}
