package ru.onotole.dumper.utils;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by onotole on 28/05/2017.
 */
@Slf4j
@RequiredArgsConstructor
public class QueueToFile {
    private final BlockingQueue<String> queue;
    private final Path file;

    @SneakyThrows
    public void save(int size) {
        List<String> bunchToSave = new ArrayList<>();
        size = Math.min(size, queue.size());
        if (size == 0) return;
        for (int i = 0; i < size; i++) {
            bunchToSave.add(queue.take());
        }
        log.info(String.format("saving to file ids from %s to %s, queue size: %s", bunchToSave.get(0),
                bunchToSave.get(bunchToSave.size() - 1), queue.size()));
        Files.write(file, bunchToSave, StandardOpenOption.APPEND);
    }
}
