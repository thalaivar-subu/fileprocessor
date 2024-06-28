package com.subu.fileprocessor.matcher;

import com.subu.fileprocessor.configuration.SharedVariableManager;
import com.subu.fileprocessor.dao.Batch;
import com.subu.fileprocessor.dao.Offset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j

@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class Processor implements Callable<HashMap<String, ArrayList<Offset>>> {

  private final Batch batch;
  private final Integer batchSize;
  private final SharedVariableManager sharedVariableManager; // Synchronize

  @Override
  public HashMap<String, ArrayList<Offset>> call() {
    HashMap<String, ArrayList<Offset>> offsetMap = new HashMap<>();
    log.debug("Matcher Batch Number: {}, threadNumber: {}", batch.getNumber(),
        Thread.currentThread().getName());
    List<String> lines = batch.getList();
    long lineCount = ((long) (batch.getNumber() - 1) * batchSize);
    long charCount = batch.getPreviousBatchCharOffset();
    for (String line : lines) {
      lineCount++;
      processLine(line, offsetMap, lineCount, charCount);
      charCount += line.length();
    }
    return offsetMap;
  }

  private void processLine(String line, HashMap<String, ArrayList<Offset>> offsetMap,
      long lineOffset, long charCount) {
    if (!line.isEmpty()) {
      sharedVariableManager.getInputTextMap().forEach(word -> {
        long wordIndex = line.toLowerCase().indexOf(word);
        if (wordIndex != -1 && isExactMatch(line, wordIndex, word)) {
          long charOffset = charCount + wordIndex;
          Offset newOffset = new Offset(lineOffset, charOffset);
          ArrayList<Offset> offset = offsetMap.computeIfAbsent(word, _ -> new ArrayList<>());
          offset.add(newOffset);
        }
      });
    }
  }

  private boolean isExactMatch(String line, long index, String word) {
    boolean beforeBoundary = (index == 0 || !Character.isLetterOrDigit(
        line.charAt((int) index - 1)));
    boolean afterBoundary = ((index + word.length()) == line.length() || !Character.isLetterOrDigit(
        line.charAt((int) (index + word.length()))));
    return beforeBoundary && afterBoundary;
  }
}
