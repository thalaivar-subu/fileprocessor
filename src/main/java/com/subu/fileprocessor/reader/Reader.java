package com.subu.fileprocessor.reader;

import com.subu.fileprocessor.configuration.SharedVariableManager;
import com.subu.fileprocessor.dao.Batch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class Reader implements Runnable {
    @Value("${file.path}")
    private final String filePath;
    @Value("${batch.size}")
    private final Integer batchSize;
    private final SharedVariableManager sharedVariableManager;
    private final Utils readerUtils;

    @Override
    public void run() {
        log.info("Started Reading File");
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            ArrayList<String> eachBatch = new ArrayList<>();
            int batchNumber = 0;
            long charOffset = 0;
            String line;
            while ((line = reader.readLine()) != null) {
                eachBatch.add(line);
                if (eachBatch.size() % batchSize == 0) {
                    Batch batch = new Batch(++batchNumber, eachBatch, charOffset);
                    log.debug("Batch No: {}", batchNumber);
                    sharedVariableManager.getMatcherQueue().put(batch);
                    eachBatch = new ArrayList<>();
                }
                charOffset += line.length();
            }
            // Process any remaining lines (last batch)
            if (!eachBatch.isEmpty()) {
                Batch batch = new Batch(++batchNumber, eachBatch, charOffset);
                sharedVariableManager.getMatcherQueue().put(batch);
            }
            reader.close();
            log.info("EOF Reached. Batch Count: {}", batchNumber);
            readerUtils.sendPoisonPill();
        } catch (Exception e) {
            log.error("Exception while reading file: {}", e.getMessage());
        }
    }
}
