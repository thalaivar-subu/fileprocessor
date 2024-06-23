package com.subu.fileprocessor.configuration;

import com.subu.fileprocessor.models.Batch;
import com.subu.fileprocessor.models.Offset;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

@Getter
@Setter
public class SharedVariableManager {
    private LinkedBlockingQueue<Batch> MatcherQueue;
    private Set<String> InputTextMap;
    private ConcurrentHashMap<String, ArrayList<Offset>> OffsetMap;

    public SharedVariableManager(int capacity, Set<String> InputTextMap) {
        this.MatcherQueue = new LinkedBlockingQueue<>(capacity);
        this.InputTextMap = InputTextMap;
        this.OffsetMap = new ConcurrentHashMap<>();
    }
}
