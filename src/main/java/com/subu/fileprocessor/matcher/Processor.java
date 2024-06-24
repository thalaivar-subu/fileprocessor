package com.subu.fileprocessor.matcher;

import com.subu.fileprocessor.configuration.SharedVariableManager;
import com.subu.fileprocessor.models.Batch;
import com.subu.fileprocessor.models.Offset;
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

    @Override
    public synchronized HashMap<String, ArrayList<Offset>> call() {
        log.debug("Matcher Batch Number: {}, threadNumber: {}", batch.getNumber(), Thread.currentThread().getName());
        HashMap<String, ArrayList<Offset>> offsetMap = new HashMap<>();
        AtomicInteger lineNumber = new AtomicInteger((batch.getNumber() - 1) * 1000);
        batch.getList().parallelStream().forEach(line -> {
            lineNumber.getAndIncrement();
            if (!line.trim().isEmpty()) {
                sharedVariableManager.getInputTextMap().forEach(word -> {
                    int wordIndex = line.toLowerCase().indexOf(word);
                    if (wordIndex != -1) {
                        int lineOffset = lineNumber.get();
                        int charOffset = lineOffset + wordIndex;
                        Offset newOffset = new Offset(lineOffset, charOffset);
                        ArrayList<Offset> offset = offsetMap.containsKey(word) ? offsetMap.get(word) : new ArrayList<>();
                        offset.add(newOffset);
                        offsetMap.put(word, offset);
                    }
                });
            }
        });
        return offsetMap;
    }
}
