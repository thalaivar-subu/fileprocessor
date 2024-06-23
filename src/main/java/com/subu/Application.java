package com.subu;

import com.subu.fileprocessor.common.SharedVariableManager;
import com.subu.fileprocessor.executor.Orchestator;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

@Slf4j
public class Application {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        String filePath = "C:\\Users\\Subramanian VE\\Workspace\\fileprocessor\\src\\main\\resources\\big.txt";
        String wordsToMatch = "James,John,Robert,Michael,William,David,Richard,Charles,Joseph,Thomas,Christopher,Daniel,Paul,Mark,Donal" +
                "d,George,Kenneth,Steven,Edward,Brian,Ronald,Anthony,Kevin,Jason,Matthew,Gary,Timothy,Jose,Larry,Jeffrey," +
                "Frank,Scott,Eric,Stephen,Andrew,Raymond,Gregory,Joshua,Jerry,Dennis,Walter,Patrick,Peter,Harold,Douglas,H" +
                "enry,Carl,Arthur,Ryan,Roger";
        Set<String> setOfWordsToMatch = Collections.synchronizedSet(new HashSet<>(Arrays.asList(wordsToMatch.split(","))));
        int matcherThreadCount = 10;
        int batchSize = matcherThreadCount * 100;
        int queueSize = matcherThreadCount * 2;
        SharedVariableManager sharedVariableManager = new SharedVariableManager(queueSize, setOfWordsToMatch);
        new Orchestator(filePath, matcherThreadCount, batchSize, sharedVariableManager).start();
        System.exit(1);
    }
}
