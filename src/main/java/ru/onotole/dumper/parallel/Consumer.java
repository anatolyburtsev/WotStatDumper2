package ru.onotole.dumper.parallel;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.BlockingQueue;
import java.util.function.BiConsumer;

/**
 * Created by onotole on 28/05/2017.
 */
@Slf4j
@RequiredArgsConstructor
public class Consumer<T> implements Runnable{
    private final BlockingQueue<Integer> queue;
    private final BlockingQueue<T> outputQueue;
    private final BiConsumer<Integer, BlockingQueue<T>> consumer;

    @Override
    public void run() {
        Integer currentId;
        try {
            while (!Thread.currentThread().isInterrupted() && (currentId = queue.take()) != null) {
                LocalDateTime dt = LocalDateTime.now();
                consumer.accept(currentId, outputQueue);
                if (currentId % 1000 == 0) log.info("Thread: "  + Thread.currentThread().getName() + " alive. spent "
                        + Duration.between(dt, LocalDateTime.now()).toMillis() + " ms on work");
            }
        } catch (InterruptedException e) {
            log.info("Stop thread: " + Thread.currentThread().getName());
        }
    }
}
