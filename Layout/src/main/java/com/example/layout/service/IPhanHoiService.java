package com.example.layout.service;

import java.util.List;
import java.util.Map;

public interface IPhanHoiService {
    List<Map<String, Object>> getFeedbackByCustomer(int customerId);
    Map<String, Object> getCustomersWithFeedback(int page, int size);
}
