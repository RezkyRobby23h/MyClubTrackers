package com.example.myclubtrackers.utils;

import androidx.room.TypeConverter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class StringListConverter {
    @TypeConverter
    public static String fromList(List<String> list) {
        return list == null ? "" : String.join(";", list);
    }

    @TypeConverter
    public static List<String> toList(String data) {
        if (data == null || data.isEmpty()) return Collections.emptyList();
        return Arrays.asList(data.split(";"));
    }
}