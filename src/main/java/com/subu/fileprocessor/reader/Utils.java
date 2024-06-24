package com.subu.fileprocessor.reader;

import com.subu.fileprocessor.configuration.SharedVariableManager;
import com.subu.fileprocessor.dao.Batch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class Utils {
    private final SharedVariableManager sharedVariableManager;

    public void sendPoisonPill() throws InterruptedException {
        log.debug("Sending Poison Pill");
        Batch batch = new Batch(null, null);
        sharedVariableManager.getMatcherQueue().put(batch);
    }
}
