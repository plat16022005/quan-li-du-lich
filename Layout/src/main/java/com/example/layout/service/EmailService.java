package com.example.layout.service;

public interface EmailService {
    void sendEmail(String toEmail, String subject, String body);
    String generateOTP(int length);
}
