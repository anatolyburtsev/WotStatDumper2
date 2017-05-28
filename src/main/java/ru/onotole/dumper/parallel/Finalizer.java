package ru.onotole.dumper.parallel;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import ru.onotole.dumper.utils.QueueToFile;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by onotole on 28/05/2017.
 */
@Slf4j
@RequiredArgsConstructor
public class Finalizer extends TimerTask{
    private final QueueToFile queueToFile;
    private final Iterator iterator;
    private final List<Thread> threads = new ArrayList<>();
    private Timer timer;
    private ExecutorService executor;

    @Override
    @SneakyThrows
    public void run() {
        if (! iterator.hasNext()) {
            log.info("Finishing");
            queueToFile.save(1_000_000);
            for (Thread t: threads) {
                t.interrupt();
            }
            if (executor != null) {
                executor.awaitTermination(3, TimeUnit.SECONDS);
                executor.shutdownNow();
            }
            if (timer != null) timer.cancel();
        }
    }

    public void add(Thread t) {
        threads.add(t);
    }
    public void add(Timer t) {timer = t;}
    public void add(ExecutorService service) {executor = service;}
}
