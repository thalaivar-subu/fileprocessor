package com.subu;

import com.subu.fileprocessor.executor.Orchestrator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static com.subu.fileprocessor.utils.Memory.getUsedMemory;

@Slf4j
@SpringBootApplication
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class Application implements CommandLineRunner {
    private final Orchestrator orchestrator;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args).close();
        System.exit(1);
    }

    @Override
    public void run(String... args) throws Exception {
        long startTime = System.currentTimeMillis();
        long memoryBeforeOrchestator = getUsedMemory();
        orchestrator.start();
        log.info("Memory used by orchestator: {} mb", (getUsedMemory() - memoryBeforeOrchestator));
        log.info("Total time taken by orchestator: {} ms", System.currentTimeMillis() - startTime);
    }
}
