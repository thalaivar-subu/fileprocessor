package com.subu.fileprocessor.configuration;

import com.subu.fileprocessor.dao.Batch;
import lombok.Data;

import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

@Data
public class SharedVariableManager {
    private final AtomicLong overallCharOffset = new AtomicLong(0);
    private LinkedBlockingQueue<Batch> MatcherQueue;
    private Set<String> InputTextMap;


    public SharedVariableManager(int capacity, Set<String> InputTextMap) {
        this.MatcherQueue = new LinkedBlockingQueue<>(capacity);
        this.InputTextMap = InputTextMap;
    }
}
