package ru.onotole.dumper;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import ru.onotole.dumper.parallel.*;
import ru.onotole.dumper.parallel.iterators.IdsBanchIterator;
import ru.onotole.dumper.utils.APIProcessor;
import ru.onotole.dumper.utils.QueueToFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Timer;
import java.util.concurrent.*;

import static ru.onotole.dumper.parallel.ExceptionLogging.withExceptionLogging;

/**
 * Created by onotole on 26/05/2017.
 */
@Slf4j
public class ValidIds {

    @SneakyThrows
    public static void main(String[] args) {
        String filename = "accIds20M100M.txt";
        Path file = Paths.get(filename);
        Files.write(file, new ArrayList<String>(), StandardOpenOption.APPEND);

        APIProcessor apiProcessor = new APIProcessor();
        Timer timer = new Timer();
        BlockingQueue<Integer> queue = new LinkedBlockingQueue<>(50);
        BlockingQueue<String> availableIds = new LinkedBlockingQueue<>(5000);
        QueueToFile queueToFile = new QueueToFile(availableIds, file);
        int startId =  77380700;
        int finishId = 100000000;
        int threadsCount = 5;
        int step = 100;
        IdsBanchIterator iterator = new IdsBanchIterator(startId, finishId, step);
        Producer producer = new Producer(iterator, queue);
        Finalizer finalizer = new Finalizer(queueToFile, iterator);

        Thread tp = new Thread(producer);
        tp.start();

        ExecutorService executor = Executors.newFixedThreadPool(threadsCount);
        for (int i = 0; i < threadsCount; i++) {
            Consumer consumer = new Consumer<>(queue, availableIds, apiProcessor::getValidAccountIdsPutToQueue);
            executor.execute(withExceptionLogging(consumer));
        }
        timer.schedule(new Informer(queue, availableIds, iterator), 1000,10_000);
        timer.schedule(new SaverLinesToFile(availableIds, queueToFile), 5000, 5000);
        timer.schedule(finalizer, 10_000, 10_000);
        finalizer.add(tp);
        finalizer.add(timer);
        finalizer.add(executor);
    }
}
