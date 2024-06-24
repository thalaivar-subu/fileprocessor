package com.subu.fileprocessor.matcher;

import com.subu.fileprocessor.configuration.SharedVariableManager;
import com.subu.fileprocessor.dao.Batch;
import com.subu.fileprocessor.dao.Offset;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

@Slf4j

@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class Processor implements Callable<HashMap<String, ArrayList<Offset>>> {
    private final Batch batch;
    private final Integer batchSize;
    private final SharedVariableManager sharedVariableManager; // Synchronize

    @Override
    public HashMap<String, ArrayList<Offset>> call() {
        HashMap<String, ArrayList<Offset>> offsetMap = new HashMap<>();
        log.debug("Matcher Batch Number: {}, threadNumber: {}", batch.getNumber(), Thread.currentThread().getName());
        List<String> lines = batch.getList();
        long lineCount = ((long) (batch.getNumber() - 1) * batchSize);
        long charCount = sharedVariableManager.getOverallCharOffset().get();
        for (String line : lines) {
            lineCount++;
            processLine(line, offsetMap, lineCount, charCount);
            charCount += line.length();
        }
        updateSharedState(charCount);
        return offsetMap;
    }

    private void processLine(String line, HashMap<String, ArrayList<Offset>> offsetMap, long lineOffset, long charCount) {
        if (!line.isEmpty()) {
            sharedVariableManager.getInputTextMap().forEach(word -> {
                int wordIndex = line.toLowerCase().indexOf(word);
                if (wordIndex != -1 && isExactMatch(line, wordIndex, word)) {
                    long charOffset = charCount + wordIndex;
                    Offset newOffset = new Offset(lineOffset, charOffset);
                    ArrayList<Offset> offset = offsetMap.computeIfAbsent(word, _ -> new ArrayList<>());
                    offset.add(newOffset);
                }
            });
        }
    }

    private void updateSharedState(long charCount) {
        sharedVariableManager.getOverallCharOffset().getAndUpdate(v -> v + charCount);
    }

    private boolean isExactMatch(String line, int index, String word) {
        boolean beforeBoundary = (index == 0 || !Character.isLetterOrDigit(line.charAt(index - 1)));
        boolean afterBoundary = (index + word.length() == line.length() || !Character.isLetterOrDigit(line.charAt(index + word.length())));
        return beforeBoundary && afterBoundary;
    }
}
