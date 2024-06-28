package com.subu.fileprocessor.aggregator;

import com.subu.fileprocessor.configuration.SharedVariableManager;
import com.subu.fileprocessor.dao.Offset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

@Slf4j
@RequiredArgsConstructor
public class Aggregator {

  private final List<Future<HashMap<String, ArrayList<Offset>>>> matcherFutures;
  private final HashMap<String, ArrayList<Offset>> offsetMap = new HashMap<>();
  private final SharedVariableManager sharedVariableManager;
  private final StringBuilder nonMatches = new StringBuilder();
  private StringBuilder matches = new StringBuilder();

  public Aggregator aggregate() throws ExecutionException, InterruptedException {
    for (Future<HashMap<String, ArrayList<Offset>>> future : matcherFutures) {
      HashMap<String, ArrayList<Offset>> currentOffsetMap = future.get();
      currentOffsetMap.forEach((k, v) -> {
        ArrayList<Offset> currentOffset =
            offsetMap.containsKey(k) ? offsetMap.get(k) : new ArrayList<>();
        currentOffset.addAll(v);
        offsetMap.put(k, currentOffset);
      });
    }
    return this;
  }

  public Aggregator logMatchesCount() {
    AtomicInteger count = new AtomicInteger(0);
    sharedVariableManager.getInputTextMap().forEach(x -> {
      if (offsetMap.containsKey(x)) {
        count.getAndIncrement();
      }
    });
    matches.append("Matches (").append(count.get()).append(")\n");
    return this;
  }

  public Aggregator logMatches() {
    log.info("OffsetMap Size: {}", offsetMap.size());
    for (Map.Entry<String, ArrayList<Offset>> entry : offsetMap.entrySet()) {
      String word = entry.getKey();
      ArrayList<Offset> offsets = entry.getValue();
      matches.append(StringUtils.capitalize(word)).append(" --> [");
      for (Offset offset : offsets) {
        matches.append("[lineOffset=").append(offset.getLineOffset()).append(", charOffset=")
            .append(offset.getCharOffset()).append("],");
      }
      matches = new StringBuilder(matches.substring(0, matches.length() - 1));
      matches.append("]");
      matches.append("\n");
    }
    log.info(String.valueOf(matches));
    return this;
  }

  public void logNonMatches() {
    nonMatches.append("Non Matches ");
    AtomicInteger count = new AtomicInteger();
    ArrayList<String> listOfNonMatches = new ArrayList<>();
    sharedVariableManager.getInputTextMap().forEach(x -> {
      if (!offsetMap.containsKey(x)) {
        listOfNonMatches.add(StringUtils.capitalize(x));
        count.getAndIncrement();
      }
    });
    nonMatches.append("(")
        .append(count.get())
        .append(")")
        .append(" --> ")
        .append(String.join(", ", listOfNonMatches));
    log.info(String.valueOf(nonMatches));
  }

}
