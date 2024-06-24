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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class Matcher implements Callable<List<Future<HashMap<String, ArrayList<Offset>>>>> {
    private final SharedVariableManager sharedVariableManager;
    private final ExecutorService matcherExecutor;

    @Override
    public List<Future<HashMap<String, ArrayList<Offset>>>> call() {
        List<Future<HashMap<String, ArrayList<Offset>>>> offsetMapListFuture = new ArrayList<>();
        try {
            while (true) {
                Batch batch = sharedVariableManager.getMatcherQueue().take();
                if (isPoisonPill(batch)) break;
                offsetMapListFuture.add(matcherExecutor.submit(new Processor(batch, sharedVariableManager)));
            }
        } catch (Exception e) {
            log.error("Exception while matching file: {} ", e.getMessage());
            Thread.currentThread().interrupt();
        }
        return offsetMapListFuture;
    }

    private boolean isPoisonPill(Batch batch) {
        boolean flag = batch.getList() == null && batch.getNumber() == null;
        log.debug("Poison Pill Received");
        return flag;
    }
}
