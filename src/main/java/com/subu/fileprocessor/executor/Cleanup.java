package com.subu.fileprocessor.executor;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Cleanup {

  public static void start(List<ExecutorService> executorServices) {
    log.info("Executor Cleanup");
    for (ExecutorService executorService : executorServices) {
      executorService.shutdown();
      try {
        if (!executorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
          executorService.shutdownNow();
        }
      } catch (InterruptedException e) {
        executorService.shutdownNow();
      }
    }
  }
}
