package com.subu.fileprocessor.executor;

import com.subu.fileprocessor.aggregator.Aggregator;
import com.subu.fileprocessor.configuration.SharedVariableManager;
import com.subu.fileprocessor.dao.Offset;
import com.subu.fileprocessor.matcher.Matcher;
import com.subu.fileprocessor.reader.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class Orchestrator {

  private final SharedVariableManager sharedVariableManager;
  private final ExecutorService matcherExecutor;
  private final Reader reader;
  private final ExecutorService matcherParentExecutor = Executors.newSingleThreadExecutor();
  private final ExecutorService readerExecutor = Executors.newSingleThreadExecutor();
  @Value("${batch.size}")
  private final Integer batchSize;

  public void start() throws ExecutionException, InterruptedException {
    Future<?> readerFuture = readerExecutor.submit(reader);
    List<Future<HashMap<String, ArrayList<Offset>>>> matcherFutures = matcherParentExecutor.submit(
        new Matcher(sharedVariableManager, matcherExecutor, batchSize)
    ).get();
    readerFuture.get();
    new Aggregator(matcherFutures, sharedVariableManager)
        .aggregate()
        .logMatchesCount()
        .logMatches()
        .logNonMatches();
    Cleanup.start(List.of(readerExecutor, matcherExecutor));
  }
}
