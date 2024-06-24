package com.subu.fileprocessor.configuration;

import com.subu.fileprocessor.executor.Orchestrator;
import com.subu.fileprocessor.reader.Reader;
import com.subu.fileprocessor.reader.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Slf4j
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
        return new Orchestrator(sharedVariableManager, Executors.newFixedThreadPool(matcherThreadCount), reader, batchSize);
    }

    @Bean
    public SharedVariableManager sharedVariableManager(
            @Value("${queue.size}") Integer queueSize,
            @Value("${words.to.match}") String wordsToMatch
    ) {
        Set<String> inputTextMap = Arrays.stream(
                        wordsToMatch.split(","))
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
        log.debug("Input Text Map Size: {}", inputTextMap.size());
        return new SharedVariableManager(
                queueSize,
                inputTextMap
        );
    }

    @Bean
    public Utils readerUtils(SharedVariableManager sharedVariableManager) {
        return new Utils(sharedVariableManager);
    }
}