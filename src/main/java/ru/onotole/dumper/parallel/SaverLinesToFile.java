package ru.onotole.dumper.parallel;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import ru.onotole.dumper.utils.QueueToFile;

import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;

/**
 * Created by onotole on 28/05/2017.
 */
@Slf4j
@RequiredArgsConstructor
public class SaverLinesToFile extends TimerTask {
    private final BlockingQueue<String> queue;
    private final QueueToFile queueToFile;
    private static final int BUNCH_SIZE = 1000;

    @Override
    @SneakyThrows
    public void run() {
        while (queue != null && queue.size() > BUNCH_SIZE) {
            queueToFile.save(BUNCH_SIZE);
        }
    }
}
