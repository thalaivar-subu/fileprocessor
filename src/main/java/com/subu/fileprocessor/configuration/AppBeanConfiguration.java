package com.subu.fileprocessor.configuration;

import com.subu.fileprocessor.executor.Orchestrator;
import com.subu.fileprocessor.reader.Reader;
import com.subu.fileprocessor.reader.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.Executors;

@Configuration
@RequiredArgsConstructor
public class AppBeanConfiguration {
    @Bean
    public Reader reader(
            @Value("${file.path}") String filePath,
            @Value("${batch.size}") Integer batchSize,
            SharedVariableManager sharedVariableManager,
            Utils readerUtils) {
        return new Reader(filePath, batchSize, sharedVariableManager, readerUtils);
    }

    @Bean
    public Orchestrator orchestrator(
            @Value("${file.path}") String filePath,
            @Value("${batch.size}") Integer batchSize,
            @Value("${matcher.thread.count}") Integer matcherThreadCount,
            SharedVariableManager sharedVariableManager,
            Reader reader) {
        return new Orchestrator(sharedVariableManager, Executors.newFixedThreadPool(matcherThreadCount), reader);
    }

    @Bean
    public SharedVariableManager sharedVariableManager(
            @Value("${queue.size}") Integer queueSize,
            @Value("${words.to.match}") String wordsToMatch
    ) {
        return new SharedVariableManager(
                queueSize,
                Collections.synchronizedSet(new HashSet<>(Arrays.asList(wordsToMatch.split(","))))
        );
    }

    @Bean
    public Utils readerUtils(SharedVariableManager sharedVariableManager) {
        return new Utils(sharedVariableManager);
    }
}