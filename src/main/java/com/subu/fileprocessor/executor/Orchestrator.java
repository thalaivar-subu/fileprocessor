package com.subu.fileprocessor.executor;

import com.subu.fileprocessor.aggregator.Aggregator;
import com.subu.fileprocessor.configuration.SharedVariableManager;
import com.subu.fileprocessor.matcher.Matcher;
import com.subu.fileprocessor.models.Offset;
import com.subu.fileprocessor.reader.Reader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class Orchestrator {
    private final SharedVariableManager sharedVariableManager;
    private final ExecutorService matcherExecutor;
    private final Reader reader;
    private final ExecutorService matcherParentExecutor = Executors.newSingleThreadExecutor();
    private final ExecutorService readerExecutor = Executors.newSingleThreadExecutor();

    public void start() throws ExecutionException, InterruptedException {
        Future<?> readerFuture = readerExecutor.submit(reader);
        List<Future<HashMap<String, ArrayList<Offset>>>> matcherFutures = matcherParentExecutor.submit(
                new Matcher(sharedVariableManager, matcherExecutor)
        ).get();
        readerFuture.get();

        HashMap<String, ArrayList<Offset>> offsetMap = new HashMap<>();
        for (Future<HashMap<String, ArrayList<Offset>>> future : matcherFutures) {
            HashMap<String, ArrayList<Offset>> currentOffsetMap = future.get();
            currentOffsetMap.forEach((k, v) -> {
                ArrayList<Offset> currentOffset = offsetMap.containsKey(k) ? offsetMap.get(k) : new ArrayList<>();
                currentOffset.addAll(v);
                offsetMap.put(k, currentOffset);
            });
        }
        Aggregator.display(offsetMap);
        log.info("OffsetMap Size: {}", offsetMap.size());
        Cleanup.start(readerExecutor);
        Cleanup.start(matcherExecutor);
    }
}
