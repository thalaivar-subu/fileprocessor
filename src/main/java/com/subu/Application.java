package com.subu;

import com.subu.fileprocessor.executor.Orchestrator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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
        orchestrator.start();
    }
}
