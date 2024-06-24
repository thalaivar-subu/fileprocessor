package com.subu.fileprocessor.matcher;

import com.subu.fileprocessor.configuration.SharedVariableManager;
import com.subu.fileprocessor.dao.Batch;
import com.subu.fileprocessor.dao.Offset;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j

@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class Processor implements Callable<HashMap<String, ArrayList<Offset>>> {
    private final Batch batch;
    private final AtomicInteger lineNumber = new AtomicInteger(0);
    private final AtomicInteger charNumber = new AtomicInteger(0);
    private final HashMap<String, ArrayList<Offset>> offsetMap = new HashMap<>();
    private final SharedVariableManager sharedVariableManager; // Synchronize

    @Override
    public HashMap<String, ArrayList<Offset>> call() {
        synchronized (sharedVariableManager) {
            log.debug("Matcher Batch Number: {}, threadNumber: {}", batch.getNumber(), Thread.currentThread().getName());
            batch.getList().forEach(line -> {
                lineNumber.incrementAndGet();
                charNumber.addAndGet(line.length());
                if (!line.isEmpty()) {
                    sharedVariableManager.getInputTextMap().forEach(word -> {
                        int wordIndex = line.toLowerCase().indexOf(word);
                        if (wordIndex != -1 && isExactMatch(line, wordIndex, word)) {
                            int lineOffset = sharedVariableManager.getOverallLineOffset().get() + lineNumber.get();
                            int charOffset = sharedVariableManager.getOverallCharOffset().get() + wordIndex;
                            Offset newOffset = new Offset(lineOffset, charOffset);
                            ArrayList<Offset> offset = offsetMap.containsKey(word) ? offsetMap.get(word) : new ArrayList<>();
                            offset.add(newOffset);
                            offsetMap.put(word, offset);
                        }
                    });
                }
            });
            sharedVariableManager.getOverallLineOffset().addAndGet(lineNumber.get());
            sharedVariableManager.getOverallCharOffset().addAndGet(charNumber.get());
            return offsetMap;
        }
    }

    private boolean isExactMatch(String line, int index, String word) {
        boolean beforeBoundary = (index == 0 || !Character.isLetterOrDigit(line.charAt(index - 1)));
        boolean afterBoundary = (index + word.length() == line.length() || !Character.isLetterOrDigit(line.charAt(index + word.length())));
        return beforeBoundary && afterBoundary;
    }
}
