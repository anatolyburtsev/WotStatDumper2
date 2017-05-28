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

public class TankUserStatDumper {
    private final String filenameWithIds = "accIds10M20M.txt";
    private final Path fileWithIds = Paths.get(filenameWithIds);
    private final APIProcessor apiProcessor = new APIProcessor();
    private final BlockingQueue<Integer> idsQueue = new ArrayBlockingQueue<>(100);
    private final BlockingQueue<TankPerUserStat> statsDataQueue = new ArrayBlockingQueue<>(1000);
    private final ReadFromFileIterator iterator = new ReadFromFileIterator(fileWithIds);
    private final Timer timer = new Timer();
    private final int threadsCount = 10;

    private final Producer producer = new Producer(iterator, idsQueue);
    private final Consumer consumer = new Consumer<>(idsQueue, statsDataQueue, apiProcessor::getTankUserStatById);
    private final Informer informer = new Informer(idsQueue, statsDataQueue, iterator);
    private final Thread tp = new Thread(producer);
    private final Executor executor = Executors.newFixedThreadPool(threadsCount);

    @SneakyThrows
    public static void main(String[] args) {
        TankUserStatDumper tankUserStatDumper = new TankUserStatDumper();
        tankUserStatDumper.tp.start();
        Thread.sleep(1000);
        for (int i = 0; i < tankUserStatDumper.threadsCount; i++) {
            tankUserStatDumper.executor.execute(tankUserStatDumper.consumer);
        }

        tankUserStatDumper.timer.schedule(tankUserStatDumper.informer, 1000, 1000);
        tankUserStatDumper.timer.schedule(new SaverToDB(tankUserStatDumper.statsDataQueue), 1000, 1000);
    }


}
