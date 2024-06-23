package com.subu.fileprocessor.common;

import com.subu.fileprocessor.models.Batch;
import com.subu.fileprocessor.models.Offset;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Getter
@Setter
public class SharedVariableManager {
    private LinkedBlockingQueue<Batch> MatcherQueue ;
    private AtomicBoolean EOF;
    private Set<String> InputTextMap;
    private ConcurrentHashMap<String, ArrayList<Offset>> OffsetMap;

    public SharedVariableManager(int capacity, Set<String> InputTextMap) {
        this.MatcherQueue = new LinkedBlockingQueue<>(capacity);
        this.EOF = new AtomicBoolean();
        this.InputTextMap = InputTextMap;
        this.OffsetMap = new ConcurrentHashMap<>();
    }
}
