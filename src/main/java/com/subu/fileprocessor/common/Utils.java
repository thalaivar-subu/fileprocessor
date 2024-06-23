package com.subu.fileprocessor.common;

import com.subu.fileprocessor.models.Offset;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class Utils {
    public static Integer stringToInteger(String v) {
        Integer batchNumber = null;
        try {
            batchNumber = Integer.parseInt(v);
            // Use batchNumber for further processing
        } catch (NumberFormatException e) {
            // Handle the case where the first line cannot be parsed as an integer
            log.error("Failed to parse number: {}", v);
        }
        return batchNumber;
    }

    public static HashMap<String, ArrayList<Offset>> mergeMaps(List<ConcurrentHashMap<String, ArrayList<Offset>>> maps) {
        HashMap<String, ArrayList<Offset>> mergedMap = new HashMap<>();
        for (ConcurrentHashMap<String, ArrayList<Offset>> map : maps) {
            for (Map.Entry<String, ArrayList<Offset>> entry : map.entrySet()) {
                String key = entry.getKey();
                ArrayList<Offset> value = entry.getValue();

                // Merge the ArrayLists for the same key
                mergedMap.merge(key, value, (list1, list2) -> {
                    list1.addAll(list2);
                    return list1;
                });
            }
        }
        return mergedMap;
    }
}
