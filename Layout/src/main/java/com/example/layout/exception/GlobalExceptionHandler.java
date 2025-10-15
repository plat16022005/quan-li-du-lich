package com.example.layout.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Component
@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public Object handleAllExceptions(Exception ex, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        logger.error("Unhandled exception for request {} {}", request.getMethod(), request.getRequestURI(), ex);

        String accept = request.getHeader("Accept");
        String requestedWith = request.getHeader("X-Requested-With");

        // If it's an AJAX/Fetch request, return JSON with error details (safe message)
        if ((accept != null && accept.contains(MediaType.APPLICATION_JSON_VALUE)) || "XMLHttpRequest".equalsIgnoreCase(requestedWith)) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            return new ResponseEntity<>(Map.of("error", "Đã xảy ra lỗi nội bộ. Vui lòng thử lại sau."), headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // For browser form navigations, set a flash attribute and redirect back to referer if present
        String referer = request.getHeader("Referer");
        redirectAttributes.addFlashAttribute("errorMessage", "Đã xảy ra lỗi nội bộ. Vui lòng thử lại sau.");
        if (referer != null && !referer.isEmpty()) {
            // Redirect to referer path (strip origin if necessary)
            return "redirect:" + referer;
        }
        return new ResponseEntity<>("Đã xảy ra lỗi nội bộ.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
