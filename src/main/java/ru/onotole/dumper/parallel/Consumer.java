package ru.onotole.dumper.parallel;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

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
                consumer.accept(currentId, outputQueue);
            }
        } catch (InterruptedException e) {
            log.info("Stop thread: " + Thread.currentThread().getName());
        }
    }
}
