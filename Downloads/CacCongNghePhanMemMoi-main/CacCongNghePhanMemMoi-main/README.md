# CNPMM_API - Chức Năng Đăng Ký (Task 1)

Dự án này tập trung xây dựng API hoàn chỉnh cho chức năng Đăng ký tài khoản (Register) với các tính năng bảo mật và xác thực thực tế.

## 🌟 Các chức năng đã hoàn thành trong Task 1

- **API Đăng ký (`/api/auth/register`)**: Nhận dữ liệu đầu vào, mã hóa mật khẩu và tạo tài khoản ở trạng thái chờ kích hoạt.
- **Xác thực dữ liệu (Validation)**: Sử dụng `express-validator` để kiểm tra định dạng email, độ dài mật khẩu, mật khẩu phải chứa chữ và số, xác nhận mật khẩu khớp nhau.
- **Chống Spam (Rate Limiting)**: Sử dụng `express-rate-limit` để giới hạn số lần gọi API đăng ký (5 lần/15 phút) và gửi OTP (3 lần/10 phút) trên cùng một địa chỉ IP.
- **Bảo mật mật khẩu**: Sử dụng `bcryptjs` để mã hóa (hash) mật khẩu trước khi lưu vào cơ sở dữ liệu.
- **Gửi OTP qua Email**: Tích hợp `nodemailer` tự động gửi email chứa 6 mã số OTP cực kỳ chuyên nghiệp ngay sau khi điền form đăng ký.
- **Xác thực OTP (`/api/auth/verify-otp`)**: Nhận mã OTP từ người dùng và so sánh. OTP có thời gian sống (TTL) là 5 phút. Lưu trữ OTP được tối ưu bằng bộ nhớ trong (`Map` Object) giúp hệ thống chạy nhanh, nhẹ và không bị phụ thuộc vào Redis.
- **Giao diện trực quan (UI)**: Hỗ trợ 2 trang giao diện Dark Mode + Glassmorphism tại `/register` và `/login` để test trải nghiệm mượt mà không cần dùng Postman.

## 🛠 Công nghệ sử dụng
- **Backend:** Node.js, Express.js
- **Database:** MongoDB (sử dụng Mongoose)
- **Bảo mật & Tiện ích:** bcryptjs, express-validator, express-rate-limit, nodemailer

## 🚀 Hướng dẫn cài đặt và chạy dự án

1. Clone repository về máy:
   ```bash
   git clone https://github.com/Tainguyen273/CNPMM_API.git
   ```
2. Cài đặt các gói thư viện Node.js:
   ```bash
   npm install
   ```
3. Đảm bảo bạn đang mở MongoDB Server trên máy (`mongodb://localhost:27017`).
4. Chạy lệnh khởi động Server:
   ```bash
   npm start
   ```
5. Mở trình duyệt và truy cập để trải nghiệm:
   - Giao diện Đăng ký: `http://localhost:6969/register`
   - Giao diện Đăng nhập: `http://localhost:6969/login`
