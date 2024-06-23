package com.subu.fileprocessor.aggregator;

import com.subu.fileprocessor.models.Offset;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class Aggregator {
    public static void display(HashMap<String, ArrayList<Offset>> offsetMap) {
        log.info("OffsetMap Size: {}", offsetMap.size());
        StringBuilder location = new StringBuilder();
        location.append("\n");
        for (Map.Entry<String, ArrayList<Offset>> entry : offsetMap.entrySet()) {
            String word = entry.getKey();
            ArrayList<Offset> offsets = entry.getValue();
            location.append(word).append(" --> [");
            for (Offset offset : offsets) {
                location.append("[lineOffset=").append(offset.getLineOffset()).append(", charOffset=").append(offset.getCharOffset()).append("],");
            }
            location = new StringBuilder(location.substring(0, location.length() - 1));
            location.append("]");
            location.append("\n");
        }
        log.info(String.valueOf(location));
    }
}
