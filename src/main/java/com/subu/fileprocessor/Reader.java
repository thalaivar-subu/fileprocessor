package com.subu.fileprocessor;

import com.subu.fileprocessor.common.SharedVariableManager;
import com.subu.fileprocessor.models.Batch;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class Reader implements Runnable {
    private final String filePath;
    private final int batchSize;
    private final SharedVariableManager sharedVariableManager;


    public Reader(String filePath, int batchSize, SharedVariableManager sharedVariableManager) {
        this.filePath = filePath;
        this.batchSize = batchSize;
        this.sharedVariableManager = sharedVariableManager;
    }

    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            ArrayList<String> eachBatch = new ArrayList<>();
            int batchNumber = 0;
            String line;
            int emptyLines = 0;
            int validLines = 0;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    validLines++;
                    eachBatch.add(line);
                    if (eachBatch.size() % batchSize == 0) {
                        Batch batch = new Batch(++batchNumber, eachBatch);
                        log.debug("Batch No: {}", batchNumber);
                        sharedVariableManager.getMatcherQueue().put(batch);
                        eachBatch.clear();
                    }
                } else emptyLines++;
            }
            // Process any remaining lines (last batch)
            if (!eachBatch.isEmpty()) {
                Batch batch = new Batch(++batchNumber, eachBatch);
                sharedVariableManager.getMatcherQueue().put(batch);
                eachBatch.clear();
            }
            reader.close();
            sharedVariableManager.setEOF(new AtomicBoolean(true));
            log.info("EOF Reached. ValidLines:{}, EmptyLines:{}", validLines, emptyLines);
        } catch (Exception e) {
            log.error("Exception while reading file: {}", e.getMessage());
        }
    }
}
