package ru.onotole.dumper.parallel;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import ru.onotole.dumper.parallel.iterators.ProcessStatus;

import java.text.NumberFormat;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;

import static java.lang.Thread.sleep;

/**
 * Created by onotole on 28/05/2017.
 */
@Slf4j
@RequiredArgsConstructor
public class Informer extends TimerTask {
    private int lastId;
    private final BlockingQueue<Integer> queue;
    private final BlockingQueue<?> idsQueue;
    private final ProcessStatus processStatus;
    private final NumberFormat percentFormatter = NumberFormat.getPercentInstance();

    {
        percentFormatter.setMinimumFractionDigits(2);
        percentFormatter.setMaximumFractionDigits(2);
    }

    @Override
    @SneakyThrows
    public void run() {
        int currentId;
        try {
            currentId = queue.peek();
        } catch (NullPointerException e) {
            return;
        }
        if (lastId > 0) {
            log.info(String.format(
                    "Process: %s\tSpeed: %4d\tcurrentId:%d\tids queue size: %d, work queue size: %d",
                    percentFormatter.format(processStatus.getProcess()),
                    currentId - lastId,
                    currentId,
                    idsQueue.size(),
                    queue.size()));
        }
        lastId = currentId;
    }
}
