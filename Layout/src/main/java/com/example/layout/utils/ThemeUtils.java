package com.example.layout.utils;

import org.springframework.stereotype.Component;

@Component
public class ThemeUtils {
    
    private static final String[] COLORS = {
        "bg-blue-500", "bg-green-500", "bg-red-500", 
        "bg-yellow-500", "bg-indigo-500", "bg-teal-500", "bg-pink-500"
    };

    public String getInitials(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "?";
        }

        String[] words = name.trim().split("\\s+");
        if (words.length == 1) {
            return words[0].substring(0, 1).toUpperCase();
        } else {
            return (words[0].substring(0, 1) + words[words.length - 1].substring(0, 1)).toUpperCase();
        }
    }

    public String getColorFromName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return COLORS[0];
        }

        int hash = 0;
        for (char c : name.toCharArray()) {
            hash = c + ((hash << 5) - hash);
        }
        
        int index = Math.abs(hash % COLORS.length);
        return COLORS[index];
    }
}
