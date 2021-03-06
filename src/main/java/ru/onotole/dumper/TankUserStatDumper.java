package ru.onotole.dumper;

import lombok.SneakyThrows;
import ru.onotole.dumper.model.TankPerUserStat;
import ru.onotole.dumper.parallel.*;
import ru.onotole.dumper.parallel.iterators.ReadFromFileIterator;
import ru.onotole.dumper.utils.APIProcessor;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Timer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static ru.onotole.dumper.parallel.ExceptionLogging.withExceptionLogging;

public class TankUserStatDumper {
    private final String filenameWithIds = "accIds.txt";
    private final Path fileWithIds = Paths.get(filenameWithIds);
    private final APIProcessor apiProcessor = new APIProcessor();
    private final BlockingQueue<Integer> idsQueue = new ArrayBlockingQueue<>(100);
    private final BlockingQueue<TankPerUserStat> statsDataQueue = new ArrayBlockingQueue<>(1000);
    private final ReadFromFileIterator iterator = new ReadFromFileIterator(fileWithIds);
    private final Timer timer = new Timer();
    private final int threadsCount = 8;

    private final Producer producer = new Producer(iterator, idsQueue);
//    private Consumer consumer;// = new Consumer<>(idsQueue, statsDataQueue, apiProcessor::getTankUserStatById);
    private final Informer informer = new Informer(idsQueue, statsDataQueue, iterator);
    private final Thread tp = new Thread(producer);
    private final Executor executor = Executors.newFixedThreadPool(threadsCount);
//    private final Finalizer finalizer = new Finalizer(que, iterator);

    @SneakyThrows
    public static void main(String[] args) {
        TankUserStatDumper tankUserStatDumper = new TankUserStatDumper();
        tankUserStatDumper.tp.start();
        Thread.sleep(1000);
        for (int i = 0; i < tankUserStatDumper.threadsCount; i++) {
            Consumer<TankPerUserStat> consumer = new Consumer<>(tankUserStatDumper.idsQueue,
                    tankUserStatDumper.statsDataQueue, tankUserStatDumper.apiProcessor::getTankUserStatById);
            tankUserStatDumper.executor.execute(withExceptionLogging(consumer));
        }

        tankUserStatDumper.timer.schedule(tankUserStatDumper.informer, 1000, 1000);
        tankUserStatDumper.timer.schedule(new SaverToDB(tankUserStatDumper.statsDataQueue), 1000, 1000);
    }


}
