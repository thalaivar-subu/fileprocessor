package com.subu.fileprocessor.executor;

import com.subu.fileprocessor.Aggregator;
import com.subu.fileprocessor.Matcher;
import com.subu.fileprocessor.common.SharedVariableManager;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class Orchestator {
    private final String filePath;
    private final int batchSize;
    private final int matcherThreadCount;
    private final ExecutorService readerExecutor;
//    private final ExecutorService matcherExecutor;
    private final SharedVariableManager sharedVariableManager;

    public Orchestator(String filePath, int matcherThreadCount, int batchSize, SharedVariableManager sharedVariableManager) {
        this.filePath = filePath;
        this.batchSize = batchSize;
        this.sharedVariableManager = sharedVariableManager;
        this.readerExecutor = Executors.newSingleThreadExecutor();
        this.matcherThreadCount = matcherThreadCount;
    }

    public void start() throws InterruptedException, ExecutionException {
        readerExecutor.submit(new com.subu.fileprocessor.Reader(filePath, batchSize, sharedVariableManager));
//        List<Future<?>> futures = new ArrayList<>();
//        IntStream.range(1, matcherThreadCount).forEach(_ -> {
//            futures.add(matcherExecutor.submit(new Matcher(sharedVariableManager)));
//        });
//        for(Future<?> future : futures) future.get();
//        readerFuture.get();
        new Matcher(sharedVariableManager, matcherThreadCount).run();
        Aggregator.display(sharedVariableManager.getOffsetMap());
        log.info("OffsetMap Size: {}", sharedVariableManager.getOffsetMap().size());
        Cleanup.start(readerExecutor);
    }
}
