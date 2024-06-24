package com.subu.fileprocessor.configuration;

import com.subu.fileprocessor.dao.Batch;
import lombok.Data;

import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

@Data
public class SharedVariableManager {
    private final AtomicInteger overallLineOffset = new AtomicInteger(0);
    private final AtomicInteger overallCharOffset = new AtomicInteger(0);
    private LinkedBlockingQueue<Batch> MatcherQueue;
    private Set<String> InputTextMap;


    public SharedVariableManager(int capacity, Set<String> InputTextMap) {
        this.MatcherQueue = new LinkedBlockingQueue<>(capacity);
        this.InputTextMap = InputTextMap;
    }
}
