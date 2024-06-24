package com.subu.fileprocessor.matcher;

import com.subu.fileprocessor.configuration.SharedVariableManager;
import com.subu.fileprocessor.dao.Batch;
import com.subu.fileprocessor.dao.Offset;
import jakarta.annotation.PostConstruct;
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
    private final SharedVariableManager sharedVariableManager;
    private final AtomicInteger charNumber = new AtomicInteger(0);
    private final AtomicInteger lineNumber = new AtomicInteger(0);
    ;

    @PostConstruct
    private void init() {
        lineNumber.addAndGet((batch.getNumber() - 1) * 1000);
    }

    @Override
    public synchronized HashMap<String, ArrayList<Offset>> call() {
        log.debug("Matcher Batch Number: {}, threadNumber: {}", batch.getNumber(), Thread.currentThread().getName());
        HashMap<String, ArrayList<Offset>> offsetMap = new HashMap<>();
        batch.getList().forEach(line -> {
            if (!line.trim().isEmpty()) {
                sharedVariableManager.getInputTextMap().forEach(word -> {
                    int wordIndex = line.toLowerCase().indexOf(word);
                    if (wordIndex != -1) {
                        int lineOffset = sharedVariableManager.getOverallLineOffset().get() + lineNumber.incrementAndGet();
                        int charOffset = sharedVariableManager.getOverallCharOffset().get() + wordIndex;
                        Offset newOffset = new Offset(lineOffset, charOffset);
                        ArrayList<Offset> offset = offsetMap.containsKey(word) ? offsetMap.get(word) : new ArrayList<>();
                        offset.add(newOffset);
                        offsetMap.put(word, offset);
                    }
                });
            }
            lineNumber.getAndIncrement();
            charNumber.addAndGet(line.length());
        });
        sharedVariableManager.getOverallLineOffset().addAndGet(lineNumber.get());
        sharedVariableManager.getOverallCharOffset().addAndGet(charNumber.get());
        return offsetMap;
    }
}
