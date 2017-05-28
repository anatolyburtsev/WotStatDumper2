package ru.onotole.dumper.parallel;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.util.Iterator;
import java.util.concurrent.BlockingQueue;

/**
 * Created by onotole on 28/05/2017.
 */
@RequiredArgsConstructor
public class Producer implements Runnable {
    private final Iterator<Integer> iterator;

    @Getter
    private final BlockingQueue<Integer> queue;

    @Override
    @SneakyThrows
    public void run() {
        while (iterator.hasNext()) {
            queue.put(iterator.next());
        }
    }
}
