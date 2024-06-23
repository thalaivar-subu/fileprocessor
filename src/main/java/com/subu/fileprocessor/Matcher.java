package com.subu.fileprocessor;

import com.subu.fileprocessor.common.SharedVariableManager;
import com.subu.fileprocessor.models.Batch;
import com.subu.fileprocessor.models.Offset;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class Matcher {
    private final SharedVariableManager sharedVariableManager;
    private final ExecutorService matcherExecutor;

    public Matcher(SharedVariableManager sharedVariableManager, int matcherThreadCount) {
        this.sharedVariableManager = sharedVariableManager;
        this.matcherExecutor = Executors.newFixedThreadPool(matcherThreadCount);
    }

//    @Override
    public void run() {
        try {
            while (isNotEOF()) {
                Batch batch = sharedVariableManager.getMatcherQueue().take();
                if (!isNull(batch) && isNotEOF()) {
                    matcherExecutor.submit(() -> {
                        int batchNumber = batch.getNumber();
                        List<String> lines = batch.getList();
                        AtomicInteger lineOffset = new AtomicInteger((batchNumber - 1) * 1000);
                        Set<String> textToMatch = sharedVariableManager.getInputTextMap();
                        log.debug("Batch No in Matcher: {}, threadNumber: {}", batch.getNumber(), Thread.currentThread().getName());
                        lines.stream().filter(Objects::nonNull).forEach(line -> {
                            lineOffset.getAndIncrement();
                            for (String word : textToMatch) {
                                int wordIndex = line.indexOf(word);
                                if (wordIndex != -1) {
                                    int charOffset = lineOffset.get() + wordIndex;
                                    Offset newOffset = new Offset(lineOffset.get(), charOffset);
                                    updateOffsetMap(word, newOffset);
                                }
                            }
                        });
                    });
                }
            }
            System.out.println("AMERAIC");
        } catch (Exception e) {
            log.error("Exception while matching file: {} ", e.getStackTrace());
            Thread.currentThread().interrupt();
        }
    }

    public synchronized void updateOffsetMap(String word, Offset newOffset) {
        sharedVariableManager.getOffsetMap().computeIfAbsent(word, _ -> new ArrayList<>()).add(newOffset);
    }

    public boolean isNotEOF() {
        return !sharedVariableManager.getEOF().get();
    }

    public boolean isNull(Batch batch) {
        return (batch == null || batch.getList().isEmpty());
    }
}
